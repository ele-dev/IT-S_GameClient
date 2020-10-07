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
	
	// Getter & setter
	public void setEnabled(boolean status) {
		this.isEnabled = status;
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
}
