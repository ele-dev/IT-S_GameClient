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

import networking.*;

public class Connection extends Thread {
	
	// class members //
	private Socket clientSocket;
	private String team;		// cross or circle
	
	// Data I/O streams for class (de)serialization
	private ObjectOutputStream objOut = null;
	private ObjectInputStream objIn = null;
	
	// State indicator variables
	private boolean isConnected;
	private boolean stopOrder;
	
	// Constructor attempts to connect to server
	public Connection() {
		
		// Initialize state indicators assuming worst case 
		this.isConnected = false;
		this.stopOrder = false;
		
		// Use global configuration vars and store result in the class
		this.isConnected = this.connectToServer(NetworkConfig.serverAddress, NetworkConfig.serverPort);
		
		// Display connection status
		if(this.isConnected)  {
			System.out.println("Socket is now connected to server");
		} else {
			JOptionPane.showMessageDialog(null, "Failed to connect to the game server!");
			System.err.println("Failed establish connection to server!");
			return;
		}
		
		// If it worked, then launch the listener thread
		this.start();
	}
	
	// Finalizer that handles close up of the network connection
	@Override
	public void finalize() {
		if(this.clientSocket != null && this.clientSocket.isConnected()) 
		{	
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
			this.clientSocket.setSoTimeout(2000);
		} catch (SocketException e5) {
			System.err.println("Failed to set the socket timeout");
			return;
		}
		
		while(!this.stopOrder)
		{
			GenericMessage recvBuffer = null;
			
			// Check the input stream of the client socket for incoming messages
			try {
				recvBuffer = (GenericMessage) this.objIn.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("Class Not Found Exception thrown");
				System.err.println("Failed to parse incoming message");
				continue;
			} catch(SocketTimeoutException e1) {
				continue;
			} catch (StreamCorruptedException e2) {
				System.err.println("Stream corrupted exception thrown while reading");
				break;
			} catch(IOException e3) {
				System.err.println("IOException thrown while reading");
				break;
			} catch (Exception e4) {
				System.err.println("Unhandled Exception thrown while parsing incoming message!");
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
	
	public void chooseTeam() {
		String input = "";
		
		input = JOptionPane.showInputDialog("Which team? (type in cross or circle)");
		if(input != null) {
			this.team = input;
		}
		
		
	}
	
	// Getters //
	
	public boolean isConnected() {
		return this.isConnected;
	}
	
	public String getTeam() {
		return this.team;
	}
}
