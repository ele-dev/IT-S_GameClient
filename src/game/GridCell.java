package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class GridCell {
	public static int size = 200;
	public Rectangle rect;
	private static Color c = new Color(255,0,50);
	
	private byte type = 0;
	
	// Constructor
	public GridCell(int row, int column) {
		this.rect = new Rectangle(column*size,row*size,size,size);
	}
	
	// Main drawing function 
	public void drawGridCell(Graphics2D g2d) {
		// Draw the cell itself
		g2d.setColor(new Color(20,20,20));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(10));
		g2d.setColor(c);
		g2d.draw(rect);
		
		// Draw the content of the cell (empty, cross or circle)
		int border = size/10;
		g2d.setColor(Color.WHITE);
		
		// Draw it depending on the type
		if(type == 1) {
			// Draw an X in to the rectangle
			g2d.drawLine(rect.x+border, rect.y+border, rect.x+rect.width-border*2, rect.y+rect.height-border*2);
			g2d.drawLine(rect.x+rect.width-border*2, rect.y+border, rect.x+border, rect.y+rect.height-border*2);
		}else if(type == 2){
			// Draw a circle in to the rectangle
			g2d.drawOval(rect.x+border, rect.y+border, size-border*2, size-border*2);
		}
	}
	
	// Setters
	public void setCellState(byte state) {
		this.type = state;
	}
	
	// Getters
	public byte getCellState() {
		return this.type;
	}
}
