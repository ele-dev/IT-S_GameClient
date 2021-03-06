package menueGui;

/*
 * written by Elias Geiger and Ben Brandes
 * 
 * This class defines a basic Button in the GUI panels
 * It's attributes are dimension (size and position), text label, and the hover status
 * This Buttons can't be strongly individualized (e.g color, text color, frame aren't changeable) and are 
 * supposed for simple use cases as buttons for logout, login or match join
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import Sound.SoundEffect;
import Stage.Commons;
import Stage.ProjectFrame;

public class Button extends GuiElement implements Focusable, Hoverable {

	// class members
	private boolean isHovered, isFocused;
	private String buttonLabel;
	private int textSize;
	
	private static int defaultTextSize = (int) Math.round(Math.sqrt((double)ProjectFrame.height) / 2.0);
	
	// Constructor for Button with default specifications
	public Button(int width, int height) {
		// call constructor from super class
		super(width, height);
		
		// Set default values
		this.isHovered = false;
		this.isFocused = false;
		this.buttonLabel = "Button";
		this.textSize = defaultTextSize;
	}
	
	// Constructor for creating button with desired dimensions and text-label
	public Button(int width, int height, String label) {
		
		// call default constructor
		this(width, height);
		
		// Set the parameters
		// this.rect = new Rectangle(0, 0, width, height);
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
		g2d.setColor(this.isFocused ? Commons.buttonFocused : Commons.buttonDefault);
		if(this.isHovered) { g2d.setColor(Commons.buttonHover); }
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
	@Override
	public void updateHover(MouseEvent e) {
		// Only enabled buttons can be hovered
		boolean prevHover = isHovered;
		this.isHovered = this.rect.contains(e.getPoint()) && this.isEnabled;
		if(prevHover == false && isHovered == true) {
			playHoverSound();
		}
	}
	
	@Override
	public void resetHover() {
		this.isHovered = false;
	}
	
	// Setters
	public void setTextSize(int size) {
		this.textSize = size;
	}
	
	public void setPosition(int posX, int posY) {
		this.rect.x = posX;
		this.rect.y = posY;
	}
	
	@Override
	public void focusNow(boolean status) {
		this.isFocused = status;
	}
	
	// Getters
	@Override
	public  boolean isHovered() {
		// Only enabled buttons can be hovered
		return this.isHovered && this.isEnabled;
	}
	
	public boolean tryPress() {
		if((this.isHovered || this.isFocused) && this.isEnabled) {
			playSelectSound();
		}
		return (this.isHovered || this.isFocused) && this.isEnabled;
	}
	
	@Override
	public boolean isFocused() {
		// Only enabled buttons can be selected/focused
		return this.isFocused && this.isEnabled;
	}
	
	public int getTextSize() {
		return this.textSize;
	}
	
	public Rectangle getDimension() {
		return this.rect;
	}
	
	static void playSelectSound() {
		SoundEffect.play("Select.wav");
	}
	
	static void playHoverSound() {
		SoundEffect.play("Hover.wav");
	}
}
