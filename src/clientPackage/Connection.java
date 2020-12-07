package clientPackage;

/*
 * written by Elias Geiger
 * 
 * This class represents the connection endpoint of a player client
 * It handles most essential operations for communication including connecting,
 * login authentification, receiving and parsing network messages, sending messages, etc
 * 
 * This class also encapsulates a separate thread of execution, so that things can be processed in the background 
 * without interfering the main thread where all the user interaction, game logic and GUI animation happens.
 * 
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import Stage.Commons;
import Stage.ProjectFrame;
import menueGui.GameState;
import networking.*;

public final class Connection extends Thread {
	
	// class members //
	private Socket clientSocket;
	private String username;
	
	// Data I/O streams for class (de)serialization
	private ObjectOutputStream objOut;
	private ObjectInputStream objIn;
	
	// State indicator variables
	private boolean playAsGuest;
	private boolean isConnected;
	private boolean loggedIn;
	private boolean stopOrder;
	private boolean sleepOrder;
	
	// Constructor attempts to connect to server
	public Connection() {
		// Initialize state indicators assuming worst case 
		this.playAsGuest = false;
		this.isConnected = false;
		this.loggedIn = false; 
		this.stopOrder = false;
		this.sleepOrder = false;
		
		// Use global configuration vars and store result in the class
		this.isConnected = this.connectToServer(NetworkConfig.serverAddress, NetworkConfig.serverPort);
		
		// Display connection status
		if(this.isConnected)  {
			System.out.println("Socket is now connected to server");
		} else {
			JOptionPane.showMessageDialog(null, "Failed to connect to the game server!");
			System.err.println("Failed establish connection to server!");
		}
	}
	
	// Finalizer that handles close up of the network connection
	@Override
	public void finalize() {
		if(this.clientSocket != null && this.clientSocket.isConnected()) 
		{
			// Send Logout message if the player was logged in
			if(this.loggedIn) {
				SignalMessage msg = new SignalMessage(GenericMessage.MSG_LOGOUT);
				this.sendMessageToServer(msg);
			}
			
			// Tell the listener thread to stop and wait until it has finished
			this.stopOrder = true;
			while(this.isAlive()) {}
			
			// Close the connection at last
			this.closeConnection();
		}
	}
	
	// Thread function that receives server messages in the background
	@Override
	public void run() 
	{
		System.out.println("Client listener thread launched");
		
		// Set the socket timeout before entering the loop
		try {
			this.clientSocket.setSoTimeout(NetworkConfig.clientSocketTimeout);
		} catch (SocketException e5) {
			System.err.println("[ClientThread] Failed to set the socket timeout");
			return;
		}
		
		while(!this.stopOrder)
		{
			// If the thread is supposed to sleep then simply wait a short time and
			// skip the network reading until
			if(this.sleepOrder) {
				try {
					Thread.sleep(NetworkConfig.threadSleepDelay);
				} catch (InterruptedException e) {}
				
				continue;
			}
			
			GenericMessage recvBuffer = null;
			
			// Check the input stream of the client socket for incoming messages
			try {
				// Read from the socket's input stream
				recvBuffer = (GenericMessage) this.objIn.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("[ClientThread] Class Not Found Exception thrown");
				System.err.println("[ClientThread] Failed to parse incoming message");
				continue;
			} catch(SocketTimeoutException e1) {
				continue;
			} catch (StreamCorruptedException e2) {
				System.err.println("[ClientThread] Stream corrupted exception thrown while reading");
				break;
			} catch(IOException e3) {
				System.err.println("[ClientThread] IOException thrown while reading");
				break;
			} catch (Exception e4) {
				System.err.println("[ClientThread] Unhandled Exception thrown while parsing incoming message!");
				break;
			}
			
			// Now handle and process the message from the server
			MessageHandler.handleMessage(recvBuffer);
		}
		
		System.out.println("Client listener thread closed");
	}
	
	// Method for connecting to the game server
	private boolean connectToServer(String addr, int port) {
		
		// Now attempt to connect to the game server
		try {
			this.clientSocket = new Socket(addr, port);
		} catch (UnknownHostException e) {
			System.err.println("DNS lookup failed!");
			return false;
		} catch (IOException e) {
			System.err.println("The server seems to be offline");
			return false;
		}
			
		// Prepare I/O streams for Object (de)serialization
		try {
			objOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
			objOut.flush();
			objIn = new ObjectInputStream(this.clientSocket.getInputStream());
		} catch (IOException e) {
			System.err.println("IO Exception thrown while initializing I/O streams");
			return false;
		}
		
		return true;
	}
	
	// Public method for closing the connection to the game server
	private void closeConnection() {
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to close connection and socket properly!"); 
			return;
		}
		
		System.out.println("Closed connection");
	}
	
	// Method that makes and attempt to create a new account
	public boolean registerAccount(String user, String mail, String pwd) {
		
		// create the message and send it so the server
		MsgRegister registerMsg = new MsgRegister(user, mail, pwd);
		this.sendMessageToServer(registerMsg);
		System.out.println("Sent registration message to the server");
		
		// Wait for the response
		GenericMessage recvBuffer = null;
		
		// Define how long to wait for a response (socket timeout)
		try {
			this.clientSocket.setSoTimeout(NetworkConfig.registerTimeout);
		} catch (SocketException e) {
			System.err.println("Failed to set socket timeout!");
			return false;
		}
		
		// Wait for a response to the register request
		try {
			recvBuffer = (GenericMessage) this.objIn.readObject();
		} catch(ClassNotFoundException e) {
			System.err.println("Class Not Found Exception thrown!");
			System.out.println("Failed to parse incoming message");
			return false;
		} catch(SocketTimeoutException e) {
			// JOptionPane.showMessageDialog(null, "Server did not answer the register request");
			GameState.registerStatusDescription = "timeout: server did not respond!";
			return false;
		} catch(StreamCorruptedException e1) {
			System.err.println("Stream corrupted Excption thrown while reading message!");
			return false;
		} catch(IOException e2) {
			System.err.println("IO Exception thrown while reading message!");
			return false;
		} catch(Exception e3) {
			System.err.println("Unknown exception thrown while parsing incoming message!");
			return false;
		}
		
		// Now checkout if the message is from the right type
		if(recvBuffer.getMessageID() != GenericMessage.MSG_REGISTER_STATUS) 
		{
			GameState.registerStatusDescription = "Wrong message type received!";
			return false;
		}
		
		// Coerce message into the desired format
		MsgRegisterStatus statusMsg = (MsgRegisterStatus)recvBuffer;
	
		// read success status
		GameState.registerStatusDescription = statusMsg.getDescription();
		return statusMsg.getSuccessStatus();
	}
	
	// Method that handles the login procedure for accounts
	public boolean loginWithAccount(String user, String pwd) {
		
		// Start communication by sending login reuqest message
		MsgLogin loginMsg = new MsgLogin(user, pwd);
		this.sendMessageToServer(loginMsg);
		System.out.println("Sent login message to the server");
		
		// Create message buffer to store a received message
		GenericMessage recvBuffer = null;
		
		// Define how long to wait for a response (socket timeout)
		try {
			this.clientSocket.setSoTimeout(NetworkConfig.loginTimeout);
		} catch (SocketException e) {
			System.err.println("Failed to set socket timeout!");
			return false;
		}
		
		// Wait for a response to the login request
		try {
			recvBuffer = (GenericMessage) this.objIn.readObject();
		} catch(ClassNotFoundException e) {
			System.err.println("Class Not Found Exception thrown!");
			System.out.println("Failed to parse incoming message");
			return false;
		} catch(SocketTimeoutException e) {
			JOptionPane.showMessageDialog(null, "Server did not answer the login request");
			return false;
		} catch(StreamCorruptedException e1) {
			System.err.println("Stream corrupted Excption thrown while reading message!");
			return false;
		} catch(IOException e2) {
			System.err.println("IO Exception thrown while reading message!");
			return false;
		} catch(Exception e3) {
			System.err.println("Unknown exception thrown while parsing incoming message!");
			return false;
		}
		
		// Now checkout if the message is from the right type
		if(recvBuffer.getMessageID() != GenericMessage.MSG_LOGIN_STATUS) 
		{
			return false;
		}
		
		// Then parse message into desired format and check the content 
		MsgLoginStatus statusMsg = (MsgLoginStatus) recvBuffer;
		
		// Save the account verification status globally in the Game State class
		GameState.userAccountVerified = statusMsg.isAccountVerified();
		
		// Evaluate success of the login request
		if(statusMsg.success() == false) {
			return false;
		} else {
			// When login was sucessfull then store the username in the class
			this.username = user;
			this.loggedIn = true;
		}
		
		// If everything went well then launch the thread for continous message processing
		// If the thread is already running then order it to exit sleep mode
		if(!this.isAlive()) {
			this.start();
		} else {
			this.setSleeping(false);
		}
		
		// Update the caption of the JFrame and append the playername
		ProjectFrame.f.setTitle(Commons.gameTitle + " - " + this.username);
		
		return true;
	}
	
	// Method that handles the login procedure for guest players
	public boolean loginAsGuest() {
		
		// Start communication by sending login reuqest message
		MsgLogin loginMsg = new MsgLogin();
		this.sendMessageToServer(loginMsg);
		System.out.println("Sent login message to the server");
		
		// Create message buffer to store a received message
		GenericMessage recvBuffer = null;
		
		// Define how long to wait for a response (socket timeout)
		try {
			this.clientSocket.setSoTimeout(NetworkConfig.loginTimeout);
		} catch (SocketException e) {
			System.err.println("Failed to set socket timeout!");
			return false;
		}
		
		// Wait for a response to the login request
		try {
			recvBuffer = (GenericMessage) this.objIn.readObject();
		} catch(ClassNotFoundException e) {
			System.err.println("Class Not Found Exception thrown!");
			System.out.println("Failed to parse incoming message");
			return false;
		} catch(SocketTimeoutException e) {
			JOptionPane.showMessageDialog(null, "Server did not answer the login request");
			return false;
		} catch(StreamCorruptedException e1) {
			System.err.println("Stream corrupted Excption throw while reading message!");
			return false;
		} catch(IOException e2) {
			System.err.println("IO Exception thrown while reading message!");
			return false;
		} catch(Exception e3) {
			System.err.println("Unknown exception thrown while parsing incoming message!");
			return false;
		}
		
		// Now checkout if the message is from the right type
		if(recvBuffer.getMessageID() != GenericMessage.MSG_LOGIN_STATUS) 
		{
			return false;
		}
		
		// Then parse message into desired format and check the content 
		MsgLoginStatus statusMsg = (MsgLoginStatus) recvBuffer;
		if(statusMsg.success() == false) {
			return false;
		}
		
		// Get the assigned playername and update the login state indicator
		this.username = statusMsg.getAssignedName();
		this.playAsGuest = true;
		this.loggedIn = true;
		
		// If everything went well then launch the thread for continous message processing
		// If the thread is already running then order it to exit sleep mode
		if(!this.isAlive()) {
			this.start();
		} else {
			this.setSleeping(false);
		}
		
		// Update the caption of the JFrame and append the playername
		ProjectFrame.f.setTitle(Commons.gameTitle + " - " + this.username);
		
		return true;
	}
	
	// Method for logout 
	public void logout() {
		// Only try if the player is logged in
		if(this.loggedIn && this.isAlive()) {
			// Send a logout message to the server
			SignalMessage logoutMessage = new SignalMessage(GenericMessage.MSG_LOGOUT);
			this.sendMessageToServer(logoutMessage);
			
			// Change the login status and reset player account info
			this.loggedIn = false;
			this.playAsGuest = false;
			this.username = "";
			
			// Put the client thread into sleep mode until the player gets logged in again
			this.setSleeping(true);
		}
		
		// Reset title of the JFrame to default
		ProjectFrame.f.setTitle(Commons.gameTitle);
	}
	
	// Method for sending Message objects to the server
	public void sendMessageToServer(GenericMessage msg) {
		
		// Only try if the client is connected
		if(this.clientSocket != null && this.clientSocket.isConnected() && msg != null) 
		{
			// Serialize and write the message object to the socket output stream
			try {
				this.objOut.writeObject(msg);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Method for sending messages anychronously to the server using SwingWorkers
	public void sendMessageAsync(GenericMessage msg) {
		
		// Only try if the client is connected
		if(this.clientSocket != null && this.clientSocket.isConnected() && msg != null) 
		{
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override protected Void doInBackground() throws Exception {
					
					// Serialize and write the message object to the socket output stream
					try {
						objOut.writeObject(msg);
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
				@Override protected void done() {
					return;
				}
			};
			worker.execute();
		}
	}
	
	// Helper methods for controlling the client thread
	private void setSleeping(boolean order) {
		// update the sleep order in the class
		this.sleepOrder = order;
		
		// Wait 2 seconds (= client socket timeout) to make sure the thread 
		// has recognized the updated sleep order
		if(order == true) {
			try {
				Thread.sleep(NetworkConfig.clientSocketTimeout + 20);
			} catch (InterruptedException e) {}
		} else {
			try {
				Thread.sleep(NetworkConfig.threadSleepDelay + 10);
			} catch (InterruptedException e) {}
		}
	}
	
	// Getters //
	
	public boolean isConnected() {
		return this.isConnected;
	}
	
	public boolean isLoggedIn() {
		return this.loggedIn;
	}
	
	public boolean isGuestPlayer() {
		return this.playAsGuest;
	}
	
	public String getUsername() {
		return this.username;
	}
}
