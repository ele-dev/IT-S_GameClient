package Stage;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

import Stage.StagePanel.KL;
import clientPackage.Connection;

public class ProjektFrame extends JFrame {
	
	// Network related
	static Connection conn;
	
	// Windows related
	int width = Commons.wf;
	int height = Commons.hf;
	
	StagePanel stagePanel;
	
	public ProjektFrame() {
		setSize(width,height);
		setLocationRelativeTo(null);
		setLayout(null);
		setResizable(false);
		setTitle("IT PROJECT");
		// setDefaultCloseOperation(EXIT_ON_CLOSE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		Container cp = getContentPane();
		stagePanel = new StagePanel(0, 0);
		cp.add(stagePanel);
		addKeyListener(stagePanel.kl);
		// test
	}
	
	// ------------------- MAIN Application Entry Point -------------------------- //
	
	public static void main(String[] args) {
		
		// First create a connection instance
		try {
			conn = new Connection();
		} catch (Exception e) {}
		
		// Second create the main window and start the actual game
		ProjektFrame f = new ProjektFrame();
		System.out.println("Main Window is now visible");
		
		// Add a window listener to the frame
		f.addWindowListener(new WindowAdapter() {
			
			// Define window close event
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				System.out.println("window was closed --> cleanup routine");
				
				// close the network connection to the game server
				conn.closeConnection();
				
				// Finally exit the application 
				System.out.println("Application close up");
				System.exit(0);
			}
		});
	}

}
