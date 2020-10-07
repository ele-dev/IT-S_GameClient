package menueGui;

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
