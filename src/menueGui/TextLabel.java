package menueGui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import Stage.ProjectFrame;

public class TextLabel extends GuiElement {

	// class members 
	private String labelText;
	private Color textColor;
	private int textSize;
	
	private static final int defaultTextSize = 16;
	private static final int defaultRenderingTextSize = (int) Math.round(Math.sqrt((double)ProjectFrame.height) / 2.0) - 1;
	
	// Constructor for Label with default specifications
	public TextLabel() {
		// call constructor from super class
		super(30, 15);
		
		// Set the default values
		this.labelText = "Just a label";
		this.textColor = Color.WHITE;
		this.textSize = defaultTextSize;
	}
	
	// Constructor for creating a label with initial text
	public TextLabel(String text, int textSize) {
		// call the default constructor
		this();
		
		// Set the text size and the text
		this.labelText = text;
		this.textSize = textSize;
		
		// enable the label from the beginning
		this.isEnabled = true;
	}
	
	@Override
	public void draw(Graphics2D g2d) {
		
		// Only draw text label if it is enabled
		if(!this.isEnabled) {
			return;
		}
		
		// Draw the text of the label
		g2d.setFont(new Font("Arial", Font.PLAIN, this.getRenderingTextSize(this.textSize)));
		FontMetrics metrics = g2d.getFontMetrics();
		int posX = this.rect.x + this.rect.width / 2 - metrics.stringWidth(labelText) / 2;
		int posY = this.rect.y + this.rect.height / 2 - metrics.getHeight() / 3;
		g2d.setColor(this.textColor);
		g2d.drawString(this.labelText, posX, posY);
	}
	
	// Setters
	public void setText(String text) {
		this.labelText = text;
	}
	
	public void setTextColor(Color c) {
		this.textColor = c;
	}
	
	public void setTextSize(int size) {
		this.textSize = size;
	}
	
	// Getters
	public String getText() {
		return this.labelText;
	}
	
	public Color getTextColor() {
		return this.textColor;
	}
	
	public int getTextSize() {
		return this.textSize;
	}
	
	// Helper function to calculate actual text size for rendering from relative size
	private int  getRenderingTextSize(int relativeTextSize) {
		
		int size = defaultRenderingTextSize + (relativeTextSize - defaultTextSize);
		
		return size;
	}
}
