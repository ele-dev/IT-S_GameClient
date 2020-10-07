package menueGui;

/*
 * written by Ben Brandes
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import Stage.Commons;

public class TextInputField extends GuiElement {
	
	// Class members 
	public String text;
	private boolean isSelected, isFlash;
	private short flashCounter;
	private final short flashIntervall = 10;
	private boolean hiddenText;
	
	// Constructor for text input field with default specs
	public TextInputField() {
		// call consructor from super class
		super(0, 0, 400, 100);
		
		// set default values
		this.isFlash = false;
		this.isSelected = false;
		this.hiddenText = false;
		this.text = "";
	}
	
	// Constructor, taking text, color and dimension
	public TextInputField(String text, int posX, int posY, int width, int height) {
		// Call default constructor
		this();
		
		// Set the parameters
		this.text = text;
		this.rect = new Rectangle(posX, posY, width, height);
		
		// enable the text field from the beginning
		this.isEnabled = true;
	}
	
	// Drawing method
	@Override
	public void draw(Graphics2D g2d) {
		
		// Only draw the button if it is enabled
		if(!this.isEnabled) {
			return;
		}
		
		// Draw box of the field
		drawBox(g2d);
		
		// Draw the contained text
		drawText(g2d);
	}
	
	private void drawBox(Graphics2D g2d) {
		g2d.setColor(isSelected? Commons.textFieldSelected : Color.darkGray);
		g2d.fill(rect);
	}

	private void drawText(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.PLAIN, 20));
		FontMetrics metrics = g2d.getFontMetrics();
		String str = "";
		if(isSelected) {
			flashCounter--;
			if(flashCounter <= 0) { 
				isFlash = !isFlash;
				flashCounter = flashIntervall;
			}
			str = isFlash? "_" : "";
		}
		
		// Check if the text should be drawn hidden or visible
		String drawingText = "";
		if(this.hiddenText) {
			for(int i = this.text.length(); i > 0; i--) 
			{
				drawingText += "*";
			}
		} else {
			drawingText = this.text;
		}
		
		// Estimate coordinates of the text inside the box of the text field
		int posX = this.rect.x + 5;
		int posY = this.rect.y + this.rect.height / 2 + metrics.getHeight() / 3;
		g2d.drawString(drawingText + str , posX, posY);
	}
	
	// method for selecting this field
	public void trySelectField(MouseEvent e) {
		isSelected = SwingUtilities.isLeftMouseButton(e) && rect.contains(e.getPoint());
	}
	
	// Setters
	public void hideText(boolean status) {
		this.hiddenText = status;
	}
	
	// Getters
	public boolean isSelected() {
		return isSelected;
	}
	
	public boolean isTextHidden() {
		return this.hiddenText;
	}
}
