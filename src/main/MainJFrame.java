package main;

import java.awt.Container;

import javax.swing.JFrame;

public class MainJFrame extends JFrame {
	
	private int w = 600;
	private int h = 600;
	
	StagePanel stagePanel;
	
	public MainJFrame() {
		setSize(w,h);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setLocationRelativeTo(null);
		setTitle("TikTakToe Client");
		setVisible(true);
		setResizable(false);
		
		stagePanel = new StagePanel(w, h);
		
		Container cp = getContentPane();
		cp.add(stagePanel);
	}
	
	
	public static void main(String[] args) {
		MainJFrame f = new MainJFrame();
	}
}
