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
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import Stage.ProjectFrame;

@SuppressWarnings("serial")
public abstract class GuiPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	
	// static members
	public static Cursor crossCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	public static Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
	public static Cursor enterTextCursor = new Cursor(Cursor.TEXT_CURSOR);
	public static Cursor defaultCursor = Cursor.getDefaultCursor();
	public static Cursor currentCursor = defaultCursor;
	
	// Dimension and background color properties
	protected int width, height;
	protected Color bgColor;
	protected ArrayList<GuiElement> guiElements;
	
	// default constructor
	public GuiPanel() {
		
		// Set the gui configs
		this.width = ProjectFrame.width;
		this.height = ProjectFrame.height;
		setBounds(0, 0, width, height);
		this.bgColor = Color.GRAY; 			// Default panel color gray
		
		this.guiElements = new ArrayList<GuiElement>();
		
		// set the default cursor
		setCursor(defaultCursor);
		
		// Gain access to traversal key events on all panel (--> TAB, ENTER, etc)
		this.setFocusTraversalKeysEnabled(false);
		
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
	public void update() {
		
		// Update cursor depending on what's hovered (or not)
		Cursor updatedCursor = defaultCursor;
		for(GuiElement e: guiElements)
		{
			if(e instanceof Hoverable && ((Hoverable) e).isHovered()) {
				updatedCursor = handCursor;
				break;
			}
		}
		
		// Only update the cursor if the type has changed
		if(updatedCursor.getType() != this.getCursor().getType()) {
			this.setCursor(updatedCursor);
		}
	}
	
	protected void drawPanelContent(Graphics2D g2d) {
		
		// Just draw the gui elements in the list
		for(GuiElement element: guiElements) 
		{
			element.draw(g2d);
		}
	}
	
	// Method that is called to close a panel that is currently visible
	public void closePanel() {
		
		// Hide the Panel 
		this.setVisible(false);
		
		// call the reset method to do optional cleanup tasks before leaving
		this.onClose();
		
		// Reset the cursor to the default one
		setCursor(defaultCursor);
	}
	
	// Event method that is called on Panel close up (can be overwritten)
	protected void onClose() {
		
		// remove remaining focus on buttons
		for(GuiElement e: this.guiElements)
		{
			if(e instanceof Button) {
				Button currBtn = (Button) e;
				currBtn.focusNow(false);
				currBtn.resetHover();
			}
		}
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
	
	protected abstract void initGuiElements();
	
	// setters
	
	protected void setBackgoundColor(Color color) {
		this.bgColor = color;
	}
}
