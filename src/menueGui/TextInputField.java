package menueGui;

/*
 * written by Ben Brandes
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import Stage.Commons;
import Stage.ProjectFrame;

public class TextInputField extends GuiElement implements Focusable {
	
	// Class members 
	public String text;
	private String hint;
	private boolean isFocused, isFlash;
	private short flashCounter;
	private final short flashIntervall = 10;
	private boolean hiddenText;
	
	private static final int defaultTextSize = (int) Math.round(Math.sqrt((double)ProjectFrame.height) / 2.0);
	
	private String validChars = "abcdefghijklmnopqrstuvwxyz1234567890!?_";
	private short maxLength = Commons.maxInputLength;
	
	// Constructor for text input field with default specs
	public TextInputField(int width, int height) {
		// call consructor from super class
		super(width, height);
		
		// set default values
		this.isFlash = false;
		this.isFocused = false;
		this.hiddenText = false;
		this.text = "";
	}
	
	// Constructor, taking hint text, color and dimension
	public TextInputField(String hint, int width, int height) {
		// Call default constructor
		this(width, height);
		
		// Set the parameters
		this.hint = hint;
		// this.rect = new Rectangle(0, 0, width, height);
		
		// enable the text field from the beginning
		this.isEnabled = true;
	}
	
	// Method that adds typed in text to the field
	public void typeInText(KeyEvent e) {
		
		// Ignore if this field isnt selected at the moment
		if(!this.isFocused || !this.isEnabled) {
			return;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && this.text.length() > 0) 
		{
			// Remove the last character from the current text 
			this.text = this.text.substring(0, this.text.length() - 1);
		} 
		else if(this.isFocused() && this.text.length() < this.maxLength) 
		{
			if(validChars.contains((e.getKeyChar() + "").toLowerCase())) {
				this.text = this.text + e.getKeyChar();
			}
		} 
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
		
		// Hint info should only appear in empty fields unselected fields
		if(this.text.length() <= 0 && !this.isFocused) {
			// Draw the hint inside the empty field
			drawHint(g2d);
		} else {
			// Draw the contained text
			drawText(g2d);
		}
	}
	
	// Private method that draws the box of the text field
	private void drawBox(Graphics2D g2d) {
		g2d.setColor(isFocused ? Commons.textFieldFocused : Color.DARK_GRAY);
		g2d.fill(rect);
	}

	// Private method that draws the typed text inside the text field
	private void drawText(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.PLAIN, defaultTextSize));
		FontMetrics metrics = g2d.getFontMetrics();
		String str = "";
		if(isFocused) {
			flashCounter--;
			if(flashCounter <= 0) { 
				isFlash = !isFlash;
				flashCounter = flashIntervall;
			}
			str = isFlash? "_" : "";
		}
		
		// Decide if the text should be drawn hidden or visible
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
	
	// private method that draws the hint into the text field
	private void drawHint(Graphics2D g2d) {
		
		// Set the text color to dark grey and get the metrics of the hint text
		g2d.setColor(Color.GRAY);
		g2d.setFont(new Font("Arial", Font.PLAIN, defaultTextSize));
		FontMetrics metrics = g2d.getFontMetrics();
		
		// Estimate coordinates of the text inside the box of the text field
		int posX = this.rect.x + 5;
		int posY = this.rect.y + this.rect.height / 2 + metrics.getHeight() / 3;
		g2d.drawString(this.hint, posX, posY);
	}
	
	// method for selecting this field through mouse click
	public void trySelectField(MouseEvent e) {
		isFocused = SwingUtilities.isLeftMouseButton(e) && rect.contains(e.getPoint());
	}
	
	// method for selecting this field without any condition (e.g. TAB typed)
	public void focusNow(boolean status) {
		this.isFocused = status;
	}
	
	// Setters
	public void hideText(boolean status) {
		this.hiddenText = status;
	}
	
	public void clearField() {
		this.text = "";
	}
	
	public void changeMaxInputLength(short maxLength) {
		this.maxLength = maxLength;
	}
	
	public void addValidChars(char[] chars) {
		// Go through the elements of the char array and append them to the
		// string that lists all valid chars for this input field
		for(char validCharacter : chars)
		{
			this.validChars += validCharacter;
		}
	}
	
	// Getters
	public boolean isFocused() {
		return isFocused;
	}
	
	public boolean isTextHidden() {
		return this.hiddenText;
	}
}
