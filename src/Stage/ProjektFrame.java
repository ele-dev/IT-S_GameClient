package Stage;
import java.awt.Container;

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
		setDefaultCloseOperation(EXIT_ON_CLOSE);
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
	}

}
