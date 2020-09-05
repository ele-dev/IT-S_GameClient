package Stage;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import clientPackage.Connection;

@SuppressWarnings("serial")
public class ProjektFrame extends JFrame {
	
	// Network related
	static Connection conn;
	
	// Windows related
	public static int width,height;
	
	StagePanel stagePanel;
	
	public ProjektFrame() {
		// setUndecorated(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		setSize(width,height);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		setTitle("IT PROJECT");
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		Container cp = getContentPane();
		stagePanel = new StagePanel(0, 0);
		cp.add(stagePanel);
		addKeyListener(stagePanel.kl);
	}
	
	// ------------------- MAIN Application Entry Point -------------------------- //
	
	public static void main(String[] args) {
		
		/*
		// First create a connection instance
		try {
			conn = new Connection();
		} catch (Exception e) {}
		
		// If the connection is established prompt the user to login
		if(conn.isConnected() == true) {
			// Enter the login dialog
			loginDialog();
		}
		*/
		
		// Second create the main window and start the actual game
		ProjektFrame f = new ProjektFrame();
		
		System.out.println("Main Window is now visible");
		
		// Add a window listener to the frame
		f.addWindowListener(new WindowAdapter() {
			
			// Define window close event
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				System.out.println("window was closed --> cleanup routine");
				
				// conn.finalize();
				
				// Finally exit the application 
				System.out.println("Application close up");
				System.exit(0);
			}
		});
	}
	
	// this method is a temporary solution for the login dialog
	// Info: The functionality is supposed be integrated in the GUI later
	@SuppressWarnings("unused")
	private static void loginDialog() {
		
		// Ask the user for login credentials
		String inUser = JOptionPane.showInputDialog("Type in your username (leave empty for guest)");
		
		// Check if the the login dialog was canceled by the user 
		if(inUser == null) {
			// close connection and continue offline
			conn.finalize();
			return;
		}
		
		// For registered players we also need the password for authentification
		String inPassword = "";
		boolean guestLogin = false;
		if(inUser.length() < 1) {
			guestLogin = true;
		} else {
			inPassword = JOptionPane.showInputDialog("Type in your password");
			// Don't accept empty password or dialog abortion
			if(inPassword == null || inPassword.length() < 1) {
				// close connection and continue offline
				conn.finalize();
				return;
			}
		}
		
		// Before the player can enter the main menue he must identify himself
		boolean loginSuccess = false;
		if(guestLogin) {
			loginSuccess = conn.loginAsGuest();
		} else {
			loginSuccess = conn.loginWithAccount(inUser, inPassword);
		}
		
		// Show the login status to the user
		if(!loginSuccess) {
			System.err.println("Could not login to the game network!");
			JOptionPane.showMessageDialog(null, "Login Failed");
			return;
		} else {
			System.out.println("Logged in successfully");
			JOptionPane.showMessageDialog(null, "Logged in as " + conn.getUsername());
		}
	}

}
