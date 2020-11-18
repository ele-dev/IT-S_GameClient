package Stage;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import clientPackage.Connection;
import menueGui.GameState;
import menueGui.HomePanel;
import menueGui.LoginPanel;
import menueGui.RegisterPanel;

@SuppressWarnings("serial")
public class ProjectFrame extends JFrame {
	
	// Network related
	public static Connection conn;

	// Window related
	public static int width, height;
	private static GraphicsDevice device;
	@SuppressWarnings("unused")
	private static DisplayMode standardMode;
	
	// GUI panels of the application (JPanels)
	public static StagePanel stagePanel;
	public static LoginPanel loginPanel;
	public static HomePanel homePanel;
	public static RegisterPanel registerPanel;
	 
	private ProjectFrame() {
		
		// Get the screen dimensions of the monitor
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		setSize(width, height);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBoardRectangleSize();
		
		// Create and init the Window (JFrame)
		setLocationRelativeTo(null);
		setLayout(null);
		setUndecorated(false);
		setResizable(false);
		setTitle(Commons.gameTitle);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		Container cp = getContentPane();
		
		stagePanel = new StagePanel();
		
		// Create and init the GUI panels (JPanels)
		loginPanel = new LoginPanel();
		registerPanel = new RegisterPanel();
		homePanel = new HomePanel();
		stagePanel.setVisible(false);
		homePanel.setVisible(false);
		loginPanel.setVisible(true);			// Display the login screen first
		registerPanel.setVisible(false);		
		cp.add(loginPanel);
		cp.add(stagePanel);
		cp.add(homePanel);
		cp.add(registerPanel);
		addKeyListener(loginPanel);
		addKeyListener(registerPanel);
		addKeyListener(stagePanel.kl);
	}
	
	public boolean initFullscreenMode() {
		
		// Get the graphics device object
		GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = graphics.getDefaultScreenDevice();
		
		// Check for the fullscreen support
		if(!device.isFullScreenSupported()) {
			setVisible(true);
			return false;
		}
		
		// Try to go fullscreen
		device.setFullScreenWindow(this);
		
		// Change the display mode
		standardMode = device.getDisplayMode();
		DisplayMode fullscreenMode = new DisplayMode(width, height, 10, 60);
		device.setDisplayMode(fullscreenMode);
		
		return true;
	}
	
	public static void setBoardRectangleSize() {
		StagePanel.boardRectSize = width/24;
	}
	
	
	public static ProjectFrame f;
	
	// ------------------- MAIN Application Entry Point -------------------------- //
	
	public static void main(String[] args) {
		
		// First create a connection instance
		try {
			conn = new Connection();
		} catch (Exception e) {}
		
		// If the connection is established prompt the user to login
		if(conn.isConnected() == true) {
			// ...
		} else {
			// If theres no connection to the game server the exit
			System.out.println("\nApplication close up");
			System.exit(0);
		}
		
		// Second create the main window and start the actual game
		f = new ProjectFrame();
		/*
		boolean fullscreenSupport = f.initFullscreenMode();
		if(!fullscreenSupport) {
			JOptionPane.showMessageDialog(f, "Fullscreen is not supported");
			System.out.println("Fullscreen rendering is not supported!");
		}
		*/
		
		System.out.println("Main Window is now visible");
		
		// Add a window listener to the frame
		f.addWindowListener(new WindowAdapter() {
			
			// Define window close event
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				
				// Check if the player is currently ingame
				if(GameState.isIngame) {
					int option = JOptionPane.showConfirmDialog(null, "Are you sure that you want to surrender?",
													"Quit game?", JOptionPane.YES_NO_OPTION);
					// If player clicked No then abort the close up procedure
					if(option == JOptionPane.NO_OPTION) {
						return;
					}
				}
				
				// Hide the window and restore the original display moded
				f.setVisible(false);
				// device.setDisplayMode(standardMode);
				System.out.println("window was closed --> cleanup routine");
				
				conn.finalize();
				
				// Finally exit the application 
				System.out.println("Application close up");
				System.exit(0);
			}
		});
	}
}
