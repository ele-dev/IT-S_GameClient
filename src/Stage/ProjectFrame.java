package Stage;

import java.awt.Container;
import java.awt.Dimension;
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

	// Windows related
	public static int width, height;
	
	// GUI panels of the application (JPanels)
	public static StagePanel stagePanel;
	public static LoginPanel loginPanel;
	public static HomePanel homePanel;
	public static RegisterPanel registerPanel;
	
	private static Timer tFrameRate,tUpdateRate;
	 
	private ProjectFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		width = (int) 1600;
		height = (int) width*9/16;
		setSize(width, height);
//		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setBoardRectangleSize();
		
		// Create and init the Window (JFrame)
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		setTitle(Commons.gameTitle);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		tFrameRate = new Timer(16, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		tFrameRate.setRepeats(true);
		tUpdateRate = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAllPanels();
			}
		});
		tUpdateRate.setRepeats(true);

		stagePanel = new StagePanel();
		if(Commons.editMap) {
			stagePanel.resetMatch(Commons.mapName);
		}
		
		// Create and init the GUI panels (JPanels)
		loginPanel = new LoginPanel();
		registerPanel = new RegisterPanel();
		homePanel = new HomePanel();
		stagePanel.setVisible(Commons.editMap);
		homePanel.setVisible(false);
		loginPanel.setVisible(!Commons.editMap);			// Display the login screen first
		registerPanel.setVisible(false);
		Container cp = getContentPane();		
		cp.add(loginPanel);
		cp.add(stagePanel);
		cp.add(homePanel);
		cp.add(registerPanel);
		addKeyListener(loginPanel);
		addKeyListener(registerPanel);
		addKeyListener(stagePanel.kl);
		
		tUpdateRate.start();
		tFrameRate.start();
	} 
	
	public static void setBoardRectangleSize(){
		StagePanel.boardRectSize = width/24;
	}
	
	private static void updateAllPanels() {
		if(loginPanel.isVisible())loginPanel.update();
		if(homePanel.isVisible())homePanel.update();
		if(registerPanel.isVisible())registerPanel.update();
		if(stagePanel.isVisible())stagePanel.update();
	}
	
	public static ProjectFrame f;
	
	// ------------------- MAIN Application Entry Point -------------------------- //
	
	public static void main(String[] args) {
		
		if(!Commons.editMap) {
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
		}
		
		
		// Second create the main window and start the actual game
		f = new ProjectFrame();
		
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
