package main;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import clientPackage.Connection;

@SuppressWarnings("serial")
public class MainJFrame extends JFrame {
	
	private int w = 600;
	private int h = 600;
	
	// Network connection
	public static Connection conn = null;
	
	// Application Gui panels
	public static StagePanel stagePanel;
	
	// Constructor
	public MainJFrame() {
		// init the window frame (JFrame)
		setSize(w,h);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(null);
		setLocationRelativeTo(null);
		setTitle("TikTakToe Client");
		setVisible(true);
		setResizable(false);
		
		// init the game screen gui (JPanel)
		stagePanel = new StagePanel(w, h);
		Container cp = this.getContentPane();
		cp.add(stagePanel);
	}
	
	// Main method application entry point //
	public static void main(String[] args) {
		
		// Create connection instance
		conn = new Connection();
		
		// Print status of network connection
		if(conn.isConnected() == true) {
			JOptionPane.showMessageDialog(null, "Connect to server");
		} else {
			// close the application since playing offline is boring
			// System.exit(0);
		}
		
		// let the player choose his team (x or o)
		conn.chooseTeam();
		
		// Create the application window
		MainJFrame f = new MainJFrame();
		System.out.println("Window created");
		
		// Add a window listener to the frame
		f.addWindowListener(new WindowAdapter() {
			
			// Define window close event
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				f.setVisible(false);
				System.out.println("window was closed --> cleanup routine");
				
				// close the connection
				conn.finalize();
				
				// Finally exit the application 
				System.out.println("Application close up");
				System.exit(0);
			}
		});
	}
}
