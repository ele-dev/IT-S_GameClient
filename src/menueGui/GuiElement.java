package menueGui;

/*
 * written by Elias Geiger
 * 
 * This class is the abstract super class of all GUI element classes.
 * It holds the Rectangle body and the enabled status attributes that all
 * GUI elements posses in the same way.
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
	public GuiElement(int posX, int posY, int width, int height) {
		this();
		this.rect = new Rectangle(posX, posY, width, height);
	}
	
	// Drawing method
	public abstract void draw(Graphics2D g2d);
	
	// Function for positioning a gui element relatively to the window frame
	public void setRelativePosition(int x, int y) {
		
		// calculate the abosolute coordinates in the frame
		float xFactor = 0.01f * x;
		float yFactor = 0.01f * y;
		int posX = Math.round(xFactor * ProjectFrame.width);
		int posY = Math.round(yFactor * ProjectFrame.height);
		
		// Center the gui element using the own dimensions
		posX -= this.rect.width / 2;
		posY -= this.rect.height / 2;
		
		// Dont exit the frame 
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
	public void setEnabled(boolean status) {
		this.isEnabled = status;
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
}
