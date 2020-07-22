package clientPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Connection {
	
	// class members //
	private Socket clientSocket;
	private String username;
	private String password;
	
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
		
		if(success) 
			JOptionPane.showMessageDialog(null, "Connected to the game server");
		else
			JOptionPane.showMessageDialog(null,  "Failed to connect to the game server!");
		
		ObjectOutputStream objOut = new ObjectOutputStream(this.clientSocket.getOutputStream());
		objOut.flush();
		ObjectInputStream objIn = new ObjectInputStream(this.clientSocket.getInputStream());
		
		
	}
}
