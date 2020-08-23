package clientPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.swing.JOptionPane;

import networking.GenericMessage;
import networking.*;

public class Connection {
	
	// class members //
	private Socket clientSocket;
	private String username;
	
	ObjectOutputStream objOut = null;
	ObjectInputStream objIn = null;
	
	boolean playAsGuest;
	boolean isConnected;
	boolean loggedIn;
	
	// Constructor
	public Connection() {
		
		this.playAsGuest = false;
		this.isConnected = false;
		this.loggedIn = false;
		
		// Pass global configuration vars
		this.isConnected = this.connectToServer(NetworkConfig.serverAddress, NetworkConfig.serverPort);
		
		// Display connection status
		if(this.isConnected)  {
			// JOptionPane.showMessageDialog(null, "Connected to the game server");
			System.out.println("Socket is now connected to server");
		} else {
			JOptionPane.showMessageDialog(null,  "Failed to connect to the game server!");
			System.err.println("Failed establish connection to server!");
			return;
		}
		
		// Ask the user for login credentials
		this.username = JOptionPane.showInputDialog("Type in your username (leave empty for guest)", null);
		String password = "";
		if(username.length() < 1) {
			this.playAsGuest = true;
		} else {
			password = JOptionPane.showInputDialog("Type in your password");
		}
		
		// Before the player can enter the main menue he must identify himself
		try {
			this.loggedIn = this.login(username, password);
		} catch(NoSuchAlgorithmException e) {
			System.err.println("Failed to generate SHA-512 Hash of the password");
		}
		
		// Show the login status to the user
		if(!this.loggedIn) {
			System.err.println("Could not login to the game network!");
			JOptionPane.showMessageDialog(null, "Login Failed");
			return;
		} else {
			System.out.println("Logged in successfully");
			JOptionPane.showMessageDialog(null, "Login Successfull");
		}
		
		// If everything went well then launch the thread for continous message processing
		// ...
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
	
	// Method that handles the login procedure
	private boolean login(String user, String pwd) throws NoSuchAlgorithmException {
		
		// Generate a random salt to harden hash reverse engineering 
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		
		// Run the SHA-512 hash function with salt on the plain text password
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		byte[] hash = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
		
		// Start communication by sending login reuqest message
		MsgLogin loginMsg = new MsgLogin(user, hash);
		this.sendMessageToServer(loginMsg);
		System.out.println("Sent login message to the server");
		
		// Create message buffer to store a received message
		GenericMessage recvBuffer = null;
		
		// Define how long to wait for a response (socket timeout)
		try {
			this.clientSocket.setSoTimeout(3000);
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
			JOptionPane.showMessageDialog(null, "Login Failed");
			return false;
		}
		
		// Then parse message into desired format and check the content 
		MsgLoginStatus statusMsg = (MsgLoginStatus) recvBuffer;
		if(statusMsg.success() == false) {
			JOptionPane.showMessageDialog(null, "Login Data was incorrect");
			return false;
		}
		
		return true;
	}
	
	// Method for sending Message objects to the server
	public void sendMessageToServer(GenericMessage msg) {
		
		// Only try if the client is connected
		if(this.clientSocket != null && this.clientSocket.isConnected()) 
		{
			// Serialize and write the message object to the socket output stream
			try {
				this.objOut.writeObject(msg);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Public method for closing the connection to the game server
	public void closeConnection() {
		if(this.clientSocket != null && this.clientSocket.isConnected()) 
		{
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to close connection and socket properly!"); 
				return;
			}
			
			System.out.println("Closed connection");
		}
	}
}
