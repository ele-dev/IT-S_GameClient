package menueGui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import Stage.Commons;

@SuppressWarnings("serial")
public abstract class GuiPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	
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
				update();
			}
		});
		tUpdateRate.setRepeats(true);
		tUpdateRate.start();
		
		// Add the listeners to the panel
		addMouseListener(this);
		addMouseMotionListener(this);
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
		drawPanelContent(g2d);
	}
	
	// Main update/processing method (does nothing by default)
	protected void update() {
		// ...
	}
	
	// Listener event methods (supposed to be overwritten in child classes)
	public void mouseClicked(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	// Abstract methods
	// This method must be implemented to draw draw gui elements
	protected abstract void drawPanelContent(Graphics2D g2d);
	
	// setters
	
	protected void setBackgoundColor(Color color) {
		this.bgColor = color;
	}
	
	// getters 
	
	// ...
}
