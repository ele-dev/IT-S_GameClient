package Stage;

/*
 * This is the base class that contains the application the entry point
 * and the Window realted code
 * 
 */

import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

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
	
	private static Timer tFrameRate, tUpdateRate;
	 
	private ProjectFrame() {
    
    // Get the monitor screen resolution and take it as window dimension
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    width = (int) screenSize.getWidth();
    height = (int) screenSize.getHeight();

    /*
     * for windowed mode with fixed dimension and resolution
     * 
      width = (int) 1600;
      height = (int) width * 9 / 16;
     *
     */

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
		
		// Create the timers that make up the global realtime game loop
		tFrameRate = new Timer(Commons.frametime + 3, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		tFrameRate.setRepeats(true);
		tUpdateRate = new Timer(Commons.frametime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAllPanels();
			}
		});
		tUpdateRate.setRepeats(true);
		
		stagePanel = new StagePanel();
		
		// Create and init the GUI panels (JPanels)
		loginPanel = new LoginPanel();
		registerPanel = new RegisterPanel();
		homePanel = new HomePanel();
		stagePanel.setVisible(false);
		homePanel.setVisible(false);
		loginPanel.setVisible(true);			// Display the login screen first
		registerPanel.setVisible(false);
		Container cp = getContentPane();		
		cp.add(loginPanel);
		cp.add(stagePanel);
		cp.add(homePanel);
		cp.add(registerPanel);
		addKeyListener(loginPanel);
		addKeyListener(registerPanel);
		addKeyListener(stagePanel.kl);
		
		// Run the game loop by starting the timers (Update & Repaint)
		tUpdateRate.start();
		tFrameRate.start();
	} 
	
	// Method that defines board rectangle size based on window width
	public static void setBoardRectangleSize() {
		StagePanel.boardRectSize = width / 24;
	}
	
	// Method that updates the currently active panel(s)
	private static void updateAllPanels() {
		if(loginPanel.isVisible()) { loginPanel.update(); }
		if(homePanel.isVisible()) { homePanel.update(); }
		if(registerPanel.isVisible()) { registerPanel.update(); }
		if(stagePanel.isVisible()) { stagePanel.update(); }
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
	
	public static ProjectFrame f;
	
	// ------------------- MAIN Application Entry Point -------------------------- //
	
	public static void main(String[] args) {
		
		// First create a connection instance
		try {
			conn = new Connection();
		} catch (Exception e) {}
		
		// Check if the connection is established 
		if(conn.isConnected() == true) {
			// ...
		} else {
			// If there's no connection to the game server then exit
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
		
		// Add a window listener to the frame to trigger closeup routine on window close
		f.addWindowListener(new WindowAdapter() {
			
			// Define window close event
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				
				// Check if the player is currently ingame
				if(GameState.isIngame) {
					int option = JOptionPane.showConfirmDialog(f, "Are you sure that you want to surrender?",
													"Quit game?", JOptionPane.YES_NO_OPTION);
					// If player clicked No then abort the close up routine
					if(option == JOptionPane.NO_OPTION) {
						return;
					}
				}
				
				// Hide the window and restore the original display mode
				f.setVisible(false);
				// device.setDisplayMode(standardMode);
				System.out.println("window was closed --> cleanup routine");
				
				// close the network connection to the game server
				conn.finalize();
				
				// Finally exit the application 
				System.out.println("Application close up");
				System.exit(0);
			}
		});
	}
}
