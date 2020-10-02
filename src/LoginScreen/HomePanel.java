package LoginScreen;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;

import javax.swing.JPanel;

import Stage.Commons;

@SuppressWarnings("serial")
public class HomePanel extends JPanel {
	
	// class members
	private int x, y;
	private int w, h;
	private Color bgColor;
	private Timer tFrameRate;
	private Timer tUpdateRate;

	public HomePanel(int x, int y) {
		this.x = x;
		this.y = y;
		this.w = Commons.wf;
		this.h = Commons.hf;
		this.bgColor = new Color(28, 26,36);
		this.setBounds(y, y, w, h);
	}
	
	// Drawing function 
	public void paintComponent(Graphics g) {
		
	}
}
