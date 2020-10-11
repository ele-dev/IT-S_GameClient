package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class GenericButton {
	protected String name;
	protected int startx,starty;
	protected Rectangle rect;
	protected boolean isHover = false;
	protected boolean isActive = false;
	
	protected Color c;
	protected Color cHover;
	protected Color cInactive;
	
	protected Font f;
	
	public GenericButton(int startx, int starty, int w, int h, String name, Color c, Color cHover, Color cInactive, int fontSize) {
		this.startx = startx;
		this.starty = starty;
		rect = new Rectangle(startx,starty,w,h);
		this.name = name;
		this.c = c;
		this.cHover = cHover;
		this.cInactive = cInactive;
		this.f = new Font("Arial",Font.BOLD,fontSize);
		
	}
	
	public boolean isHover() {
		return isHover;
	}
	
	public void drawButton(Graphics2D g2d) {
		g2d.setColor(isHover?cHover:c);
		g2d.fill(rect);
		g2d.setColor(isHover?c:cHover);
		
		g2d.setStroke(new BasicStroke(8));
		g2d.draw(rect);
		
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics();
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	
	public void updateHover(Point mousePos) {
		isHover = rect.contains(mousePos);
	}
	
	public void updatePos(Point cameraPos) {
		rect.x = startx-cameraPos.x;
		rect.y = starty-cameraPos.y;
	}

	

	

}
