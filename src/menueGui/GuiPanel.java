package menueGui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import Stage.Commons;

@SuppressWarnings("serial")
public abstract class GuiPanel extends JPanel {
	
	// Dimension and background color properties
	protected int width, height;
	protected Color bgColor;
	
	// Timers 
	protected Timer tFrameRate;
	protected Timer tUpdateRate;
	
	// default constructor
	public GuiPanel() {
		
		// Set the gui configs
		this.width = Commons.wf;
		this.height = Commons.hf;
		setBounds(0, 0, width, height);
		this.bgColor = Color.GRAY; 			// Default panel color gray
		
		// Timer for repainting/redrawing
		tFrameRate = new Timer(Commons.frametime, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		tFrameRate.setRepeats(true);
		tFrameRate.start();
		
		// Timer for updating 
		tUpdateRate = new Timer(Commons.frametime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ...
			}
		});
		tUpdateRate.setRepeats(true);
		tUpdateRate.start();
	}
	
	// Advanced Constructor for non-fullscreen panels
	public GuiPanel(int posX, int posY, int width, int height) {
		
		// Set the gui configs
		this.width = width;
		this.height = height;
		setBounds(posX, posY, width, height);
	}
	
	// Main drawing method
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw the colored background
		g2d.setColor(this.bgColor);
		g2d.fillRect(0, 0, this.width, this.height);
		
		// Draw the rest of the GUI
		drawPanel(g2d);
	}
	
	// Abstract methods
	protected abstract void drawPanel(Graphics2D g2d);
	
	// setters
	
	protected void setBackgoundColor(Color color) {
		this.bgColor = color;
	}
	
	// getters 
	
	// ...
}
