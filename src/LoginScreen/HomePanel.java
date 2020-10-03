package LoginScreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import Stage.Commons;

@SuppressWarnings("serial")
public class HomePanel extends JPanel {
	
	// Dimension and background color properties
	private int w, h;
	private Color bgColor;
	
	// Timers 
	private Timer tFrameRate;
	private Timer tUpdateRate;

	public HomePanel(int x, int y) {
		this.w = Commons.wf;
		this.h = Commons.hf;
		this.bgColor = new Color(28, 26, 36);
		this.setBounds(x, y, w, h);
		// add listeners
		// ...
		
		// Timer for painting/redrawing
		this.tFrameRate = new Timer(Commons.frametime, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		this.tFrameRate.setRepeats(true);
		this.tFrameRate.start();
		
		// Timer for updating
		this.tUpdateRate = new Timer(Commons.frametime, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// ...
			}
		});
		this.tUpdateRate.setRepeats(true);
		this.tUpdateRate.start();
	}
	
	// Drawing function 
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw the colored background
		g2d.setColor(this.bgColor);
		g2d.fillRect(0, 0, this.w, this.h);
		
		// Next draw some text
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 30));
		g2d.drawString("Home screen", 750, 300);
		
		// Draw the quick match join button
		// g2d.setColor(Color.WHITE);
		// ...
		
		// Draw additonal stuff
		// ...
	}
	
	
}
