package menueGui;

/*
 * written by Elias Geiger
 * 
 * This class is the abstract super class of all GUI element classes.
 * It holds the Rectangle body and the enabled status attributes that all
 * GUI elements possess in the same way.
 * Additionally it has the abstract method draw() to automatically force every 
 * children classes to implement this function in order to be drawable
 * 
 */

import java.awt.Graphics2D;
import java.awt.Rectangle;

import Stage.ProjectFrame;

public abstract class GuiElement {
	
	// class members
	protected boolean isEnabled;
	protected Rectangle rect;
	
	// basic constructor
	public GuiElement() {
		this.isEnabled = false;
		this.rect = new Rectangle();
	}
	
	// advanced constructor
	public GuiElement(int width, int height) {
		this();
		this.rect = new Rectangle(0, 0, width, height);
		
		// Calculate absolute pixel size of gui element 
		// important: must be done before setRelativePosition()
		this.setRelativeSize(width, height);
	}
	
	// Drawing method
	public abstract void draw(Graphics2D g2d);
	
	// Function for determining the size absolute size (pixels) for a relative size (per mill)
	// using the screen resolution of the device
	public final void setRelativeSize(int relWidth, int relHeight) {
		
		// calculate the the absolute width and height
		float wFactor = 0.001f * relWidth;
		float hFactor = 0.001f * relHeight;
		int width = Math.round(wFactor * ProjectFrame.width);
		int height = Math.round(hFactor * ProjectFrame.height);
		
		// Apply the calculated absolute size to the GUIs rectangle
		this.rect.width = width;
		this.rect.height = height;
	}
	
	// Function for positioning a gui element relatively to the window frame
	public final void setRelativePosition(int x, int y) {
		
		// calculate the absolute coordinates in the frame
		float xFactor = 0.01f * x;
		float yFactor = 0.01f * y;
		int posX = Math.round(xFactor * ProjectFrame.width);
		int posY = Math.round(yFactor * ProjectFrame.height);
		
		// Center the GUI element using the own dimensions (width & height in pixels)
		posX -= this.rect.width / 2;
		posY -= this.rect.height / 2;
		
		// Don't exit the frame 
		if(posX < 0)
			posX = 0;
		if(posY < 0)
			posY = 0;
		if(posX > ProjectFrame.width)
			posX = ProjectFrame.width;
		if(posY > ProjectFrame.height) 
			posY = ProjectFrame.height;
		
		// Apply the coordinates to this GUI element
		this.rect.x = posX;
		this.rect.y = posY;
		
	}
	
	// Getter & setter
	public final void setEnabled(boolean status) {
		this.isEnabled = status;
	}
	
	public final boolean isEnabled() {
		return this.isEnabled;
	}
}
