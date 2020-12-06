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
import java.awt.RenderingHints;
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
	protected static Cursor crossCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	protected static Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
	protected static Cursor enterTextCursor = new Cursor(Cursor.TEXT_CURSOR);
	protected static Cursor loadingCursor = new Cursor(Cursor.WAIT_CURSOR);
	protected static Cursor defaultCursor = Cursor.getDefaultCursor();
	
	// Flag indicator for loading phase
	protected boolean isLoading = false;
	
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
	
	// Private method for relative positioning of non-fullscreen panels 
	@SuppressWarnings("unused")
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
	public void paintComponent(final Graphics g) {
		
		// Skip if this panel is not visible 
		if(!this.isVisible()) {
			return;
		}
		
		// Set the render target (front buffer in this case)
		Graphics2D renderTarget = (Graphics2D) g;
		
		// Set advanced rendering instructions (e.g. anti aliasing)
		renderTarget.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw the colored background
		renderTarget.setColor(this.bgColor);
		// renderTarget.fillRect(this.getX(), this.getY(), this.width, this.height);
		renderTarget.fillRect(0, 0, this.width, this.height);
		
		// Draw the rest of the GUI
		drawPanelContent(renderTarget);
	}
	
	// Main update/processing method (does nothing by default, can be overwritten)
	public void update() {
		
		// Update cursor depending on what's hovered (or not)
		Cursor updatedCursor = defaultCursor;
		for(GuiElement e: guiElements)
		{
			// Search a hovered gui element
			if(e instanceof Hoverable && ((Hoverable) e).isHovered()) {
				
				// Distinguish between a hovered button and a hovered text field
				if(e instanceof Button) {
					updatedCursor = handCursor;
				} else if(e instanceof TextInputField) {
					updatedCursor = enterTextCursor;
				}
				break;
			}
		}
		
		// If this panel is currently in a loading phase then overwrite with loading cursor
		if(this.isLoading) {
			updatedCursor = loadingCursor;
		}
		
		// Only update the cursor if the type has changed since the last check
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
		
		// Reset the cursor to the default one before closing panel
		this.isLoading = false;
		this.setCursor(defaultCursor);
	}
	
	// Event method that is called on Panel close up (can be overwritten)
	protected void onClose() {
		
		// remove remaining focus on gui elements
		for(GuiElement e: this.guiElements)
		{
			if(e instanceof Focusable) {
				((Focusable) e).focusNow(false);
			}
			// reset the hovered status of gui elements
			if(e instanceof Hoverable) {
				((Hoverable) e).resetHover();
			}
		}
	}
	
	// Listener event methods (supposed to be overwritten in child classes)
	public void mouseClicked(MouseEvent e) {}
	
	// Call the method that handles the mouse pressed events
	public void mousePressed(MouseEvent e) {
		
		// Skip the operations in case the panel is currently in loading state
		if(this.isLoading) {
			return;
		}
		
		tryChangeFocus(e);
	}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	// Mouse motion listener events for updating the hover status of GUI elements
	public void mouseDragged(MouseEvent e) {
		// Update all hoverable gui elements
		for(GuiElement element: guiElements)
		{
			if(element instanceof Hoverable) {
				((Hoverable) element).updateHover(e);
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
		// Update all hoverable gui elements
		for(GuiElement element: guiElements)
		{
			if(element instanceof Hoverable) {
				((Hoverable) element).updateHover(e);
			}
		}
	}
	
	// Call the method that handles the key pressed events
	public void keyPressed(KeyEvent e) {
		
		// Skip the operations in case the panel is currently in loading state
		if(this.isLoading) {
			return;
		}
		
		tryTypeIn(e);
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	// Abstract methods
	
	protected abstract void initGuiElements();
	protected abstract void tryChangeFocus(MouseEvent e);
	protected abstract void tryTypeIn(KeyEvent e);
	
	// setters
	
	protected void setBackgoundColor(Color color) {
		this.bgColor = color;
	}
}
