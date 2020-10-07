package menueGui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import Stage.Commons;

public class TextInputField {
	
	// Class members 
	public String text;
	private Rectangle rect; 
	private boolean isSelected, isFlash;
	private short flashCounter, flashIntervall = 10;
	
	// Constructor, taking text, color and dimension
	public TextInputField(String text, int x, int y, int w, int h) {
		// init class members
		this.text = text;
		this.rect = new Rectangle(x, y, w, h);
	}
	
	// Getter 
	public boolean isSelected() {
		return isSelected;
	}
	
	// Drawing method
	public void drawTextInputField(Graphics2D g2d) {
		// Draw box of the field
		g2d.setColor(isSelected? Commons.textFieldSelected : Color.darkGray);
		g2d.fill(rect);
		// Draw the contained text
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.PLAIN, 20));
		FontMetrics m = g2d.getFontMetrics();
		String str = "";
		if(isSelected) {
			flashCounter--;
			if(flashCounter <= 0) { 
				isFlash = !isFlash;
				flashCounter = flashIntervall;
			}
			str = isFlash? "_" : "";
		}
		g2d.drawString(text + str , rect.x + 5, rect.y + rect.height / 2 + m.getHeight() / 3);
	}
	
	// method for selecting this field
	public void trySelectField(MouseEvent e) {
		isSelected = SwingUtilities.isLeftMouseButton(e) && rect.contains(e.getPoint());
	}
}
