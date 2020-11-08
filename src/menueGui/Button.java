package menueGui;

/*
 * written by Elias Geiger and Ben Brandes
 * 
 * This class defines a basic Button in the GUI panels
 * It's attributes are dimension (size and position), textlabel, and the hover status
 * This Buttons can't be strongly individualized (e.g color, text color, frame aren't changable) and are 
 * supposed for simple use cases as buttons for logout, login or match join
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import Stage.Commons;

public class Button extends GuiElement {

	// class members
	private boolean isHover;
	private String buttonLabel;
	private int textSize;
	
	// Constructor for Button with default specs
	public Button() {
		// call constructor from super class
		super(200, 100);
		
		// Set default values
		this.isHover = false;
		this.buttonLabel = "Button";
		this.textSize = 20;
	}
	
	// Constructor for creating button with desired dimensions and textlabel
	public Button(int width, int height, String label) {
		
		// call default constructor
		this();
		
		// Set the parameters
		this.rect = new Rectangle(0, 0, width, height);
		this.buttonLabel = label;
		
		// enable the button from the beginning
		this.isEnabled = true;
	}
	
	// Drawing Method 
	@Override
	public void draw(Graphics2D g2d) {
		
		// Only draw the button if it is enabled
		if(!this.isEnabled) {
			return;
		}
		
		// First draw the box of the button
		drawButtonBox(g2d);
		
		// Then draw the text label on the button
		drawButtonLabel(g2d);
	}
	
	private void drawButtonBox(Graphics2D g2d) {
		g2d.setColor(isHover? Commons.buttonHover : new Color(20,20,20));
		g2d.fill(this.rect);
	}
	
	// Method for drawing the buttons text label
	private void drawButtonLabel(Graphics2D g2d) {
		g2d.setFont(new Font("Arial", Font.PLAIN, this.textSize));
		FontMetrics metrics = g2d.getFontMetrics();
		int posX = this.rect.x + this.rect.width / 2 - metrics.stringWidth(this.buttonLabel) / 2;
		int posY = this.rect.y + this.rect.height / 2 + metrics.getHeight() / 3;
		g2d.setColor(Color.WHITE);
		g2d.drawString(this.buttonLabel, posX, posY);
	}
	
	// Update method for the hover status of a button through 
	public void updateHover(MouseEvent e) {
		// Only enabled buttons can be hovered
		this.isHover = this.rect.contains(e.getPoint()) && this.isEnabled;
	}
	
	// Setters
	public void setTextSize(int size) {
		this.textSize = size;
	}
	
	public void setPosition(int posX, int posY) {
		this.rect.x = posX;
		this.rect.y = posY;
	}
	
	// Getters
	public  boolean isHover() {
		// Only enabled buttons can be hovered
		return this.isHover && this.isEnabled;
	}
	
	public int getTextSize() {
		return this.textSize;
	}
	
	public Rectangle getDimension() {
		return this.rect;
	}
}
