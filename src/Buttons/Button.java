package Buttons;

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

public class Button {

	// class members
	private Rectangle rect;
	private boolean isHover;
	private String buttonLabel;
	private int textSize;
	
	// Constructor for Button with default specs
	public Button() {
		// Set default values
		this.rect = new Rectangle(0, 0, 200, 100);
		this.isHover = false;
		this.buttonLabel = "Button";
		this.textSize = 20;
	}
	
	// Constructor for creating button with desired dimensions and textlabel
	public Button(int x, int y, int w, int h, String label) {
		this();
		this.rect.x = x;
		this.rect.y = y;
		this.rect.width = w;
		this.rect.height = h;
		this.buttonLabel = label;
	}
	
	// Drawing method
	public void drawButton(Graphics2D g2d) {
		// First draw the box of the button
		g2d.setColor(isHover? Commons.buttonHover : new Color(20,20,20));
		g2d.fill(this.rect);
		
		// Then draw the text label on the button
		drawButtonLabel(g2d);
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
		this.isHover = this.rect.contains(e.getPoint());
	}
	
	// Setter
	public void setTextSize(int size) {
		this.textSize = size;
	}
	
	// Getter
	public  boolean isHover() {
		return this.isHover;
	}
	
	public int getTextSize() {
		return this.textSize;
	}
}
