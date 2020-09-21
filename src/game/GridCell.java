package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class GridCell {
	public static int size = 200;
	public Rectangle rect;
	private static Color c = new Color(255,0,50);
	
	public byte type = 0;
	
	public GridCell(int row, int column) {
		this.rect = new Rectangle(column*size,row*size,size,size);
	}
	 
	public void drawGridCell(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(10));
		g2d.setColor(c);
		g2d.draw(rect);
		
		
		int border = size/10;
		g2d.setColor(Color.WHITE);
		if(type == 1) {
			g2d.drawLine(rect.x+border, rect.y+border, rect.x+rect.width-border*2, rect.y+rect.height-border*2);
			g2d.drawLine(rect.x+rect.width-border*2, rect.y+border, rect.x+border, rect.y+rect.height-border*2);
		}else if(type == 2){
			g2d.drawOval(rect.x+border, rect.y+border, size-border*2, size-border*2);
		}
	}
	
	
}
