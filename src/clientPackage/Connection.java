package clientPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import networking.GenericMessage;
import networking.*;

public class Connection {
	
	// class members //
	private Socket clientSocket;
	private String username;
	private String password;
	
	ObjectOutputStream objOut = null;
	ObjectInputStream objIn = null;
	
	boolean playAsGuest;
	
	public Connection() throws IOException {
		
		this.playAsGuest = false;
		
		// Ask the user for login credentials
		this.username = JOptionPane.showInputDialog("Type in your username (leave empty for guest)", null);
		if(username.length() < 1) {
			this.password = "";
			this.playAsGuest = true;
		} else {
			this.password = JOptionPane.showInputDialog("Type in your password");
		}
		
		// Now attempt to connect to the game server
		boolean success = true;
		try {
			this.clientSocket = new Socket(NetworkConfig.serverAddress, NetworkConfig.serverPort);
		} catch (UnknownHostException e) {
			System.err.println("DNS lookup failed!");
			success = false;
		} catch (IOException e) {
			System.err.println("The server seems to be offline");
			success = false;
		}
		
		// Display connection status
		if(success)  {
			JOptionPane.showMessageDialog(null, "Connected to the game server");
			System.out.println("Socket is now connected to server");
		} else {
			JOptionPane.showMessageDialog(null,  "Failed to connect to the game server!");
			System.err.println("Failed establish connection to server!");
			return;
		}
			
		// Prepare I/O streams for Object (de)serialization
		objOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
		objOut.flush();
		objIn = new ObjectInputStream(this.clientSocket.getInputStream());
		
		// Test communication by sending login message
		MsgLogin loginMsg = new MsgLogin(this.username, this.password);
		this.sendMessageToServer(loginMsg);
		System.out.println("Sent login message to the server");
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
