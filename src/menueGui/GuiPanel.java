package menueGui;

/*
 * written by Elias Geiger
 * 
 * This abstract class extends JPanel abend implements multiple listener interfaces
 * to serve as superclass for all panel classes that make up the game menue.
 * This class holds timers for processing & drawing, listeners for event based input handling 
 * and a few basic graphical attributes that all panels posses: width, height, background color
 * 
 * This class intends to simplify the implementation of Panel classes by hiding most of the mechanics
 * and aspects of panels that don't have to be customizable during panel design and would only overload
 * the child classes with redundant bloat code.
 * 
 * Panel classes can be implemented by extending this abstract class, defining the drawPanelContent()
 * method and calling the super constructor at the very beginning 
 * 
 * All the Event methods of the listener and the update() method can be optionally overwritten in the
 * child classes to add functionality as desired
 * 
 */

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

import com.sun.jdi.event.Event;

import Stage.Commons;
import Stage.ProjectFrame;

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
		this.width = ProjectFrame.width;
		this.height = ProjectFrame.height;
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
	
	// Advanced Constructor for non-fullscreen panels (relative positioning)
	public GuiPanel(int x, int y, int width, int height) {
		
		// Set the gui configs
		this.width = width;
		this.height = height;
	
		setRelativePosition(x, y);
	}
	
	// Private method for relative positioning of non-fullscreen panels 
	private void setRelativePosition(int x, int y) {
		
		// calculate the abosolute coordinates in the frame
		float xFactor = 0.01f * x;
		float yFactor = 0.01f * y;
		int posX = Math.round(xFactor * ProjectFrame.width);
		int posY = Math.round(yFactor * ProjectFrame.height);
		
		// Center the panel using the own dimensions
		posX -= this.width / 2;
		posY -= this.height / 2;
		
		// Dont exit the frame 
		if(posX < 0)
			posX = 0;
		if(posY < 0)
			posY = 0;
		if(posX > ProjectFrame.width)
			posX = ProjectFrame.width;
		if(posY > ProjectFrame.height) 
			posY = ProjectFrame.height;
		
		// Apply the coordinates to this Panel
		this.setBounds(posX, posY, this.width, this.height);
		
	}
	
	// Main drawing method
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw the colored background
		g2d.setColor(this.bgColor);
		g2d.fillRect(this.getX(), this.getY(), this.width, this.height);
		
		// Draw the rest of the GUI
		drawPanelContent(g2d);
	}
	
	// Main update/processing method (does nothing by default, can be overwritten)
	protected void update() {
		// ...
	}
	
	// Method that is called to close a panel that is currently visible
	public void closePanel() {
		
		// Hide the Panel 
		this.setVisible(false);
		
		// call the reset method to do optional cleanup tasks before leaving
		onClose();
	}
	
	// Event method that is called on Panel close up (can be overwritten)
	protected void onClose() {
		// ...
	}
	
	public void handleInputEvent(Event e) {
		
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
	
	protected abstract void initGuiElements();
	
	// setters
	
	protected void setBackgoundColor(Color color) {
		this.bgColor = color;
	}
}
