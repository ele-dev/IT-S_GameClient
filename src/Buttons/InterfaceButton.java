package Buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class InterfaceButton {
	protected int startx,starty;
	protected Color c;
	protected Color cIsHover;
	protected Color cInactive;
	protected Rectangle rect;
	protected String name;
	protected boolean isHover = false;
	protected boolean isActive = false;
	protected Font f = new Font("Arial",Font.BOLD,40);
	
	public InterfaceButton(int startx, int starty, int w, Color cIsHover, String name) {
		this.startx = startx;
		this.starty = starty;
		this.c = new Color(30,30,30);
		this.cIsHover = cIsHover;
		this.cInactive = new Color(10,10,10);
		this.rect = new Rectangle(startx,starty,w,75);
		this.name = name;
	}
	
	public void updateHover(Point mousePos) {
		isHover = rect.contains(mousePos);
	}
	
	public void updatePos(Point CameraPos) {
		rect.setBounds(startx-CameraPos.x,starty-CameraPos.y,rect.width,rect.height);
	}
	
	public abstract void drawButton(Graphics2D g2d);
	
	public abstract boolean tryPress();
	
	
}
