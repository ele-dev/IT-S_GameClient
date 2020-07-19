package Stage;
import java.awt.Container;

import javax.swing.JFrame;

import Stage.StagePanel.KL;

public class ProjektFrame extends JFrame {
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
	}
	
	public static void main(String[] args) {
		ProjektFrame f = new ProjektFrame();
	}

}
