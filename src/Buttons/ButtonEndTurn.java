package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class ButtonEndTurn {
	private int startx,starty;
	private Color c;
	private Color cHover;
	private Color cIsNotPressable;
	private Rectangle rect;
	private boolean isHover = false;
	private String name = " End Turn";
	private Font f;
	private boolean isPressable = true;
	 
	public ButtonEndTurn(int startx, int starty) {
		this.startx = startx;
		this.starty = starty;
		this.c = new Color(20,20,20);
		this.cHover = new Color(60,60,60);
		this.cIsNotPressable = new Color(10,10,10);
		this.rect = new Rectangle(startx,starty,250,100);
		f = new Font("Arial",Font.BOLD,30);
	}
	
	public boolean getIsHover() {
		return isHover; 
	}
	
	public void drawButton(Graphics2D g2d) {
		g2d.setColor(c);
		if(isHover) {
			g2d.setColor(cHover);
		}
		if(!isPressable) {
			g2d.setColor(cIsNotPressable);
		}
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(3));
		
		g2d.setColor(new Color(80,80,80));
		if(isHover) {
			g2d.setColor(c);
		}
		if(!isPressable) {
			g2d.setColor(new Color(20,20,20));
		}
		g2d.draw(rect);
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics(f);
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	
	public void updatePressable(boolean isOngoingAttack) {
		if(isOngoingAttack) {
			isPressable = false;
		}else {
			isPressable = true;
		}
	}
	
	public void updateHover(Point mousePos) {
		if(mousePos != null) {
			if(rect.contains(mousePos)) {
				isHover = true;
			}else {
				isHover = false;
			}
		}
		
	}
	
	public void updatePos(Point CameraPos) {
		this.rect.setBounds(startx-CameraPos.x,starty-CameraPos.y,250,100);
	}
	
	
}