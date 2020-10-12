package Stage;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;

import clientPackage.Connection;
import menueGui.HomePanel;
import menueGui.LoginPanel;
import menueGui.RegisterPanel;

@SuppressWarnings("serial")
public
class ProjectFrame extends JFrame {
	
	// Network related
	public static Connection conn;

	// Windows related
	public static int width, height;
	
	// GUI panels of the application (JPanels)
	public static StagePanel stagePanel;
	public static LoginPanel loginPanel;
	public static HomePanel homePanel;
	public static RegisterPanel registerPanel;
	
	private ProjectFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		setSize(width, height);
		
		// Create and init the Window (JFrame)
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		setTitle(Commons.gameTitle);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		Container cp = getContentPane();
		stagePanel = new StagePanel("TestMap", this);
		
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
		ProjectFrame f = new ProjectFrame();
		
		System.out.println("Main Window is now visible");
		
		// Add a window listener to the frame
		f.addWindowListener(new WindowAdapter() {
			
			// Define window close event
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				f.setVisible(false);
				System.out.println("window was closed --> cleanup routine");
				
				conn.finalize();
				
				// Finally exit the application 
				System.out.println("Application close up");
				System.exit(0);
			}
		});
	}
}
