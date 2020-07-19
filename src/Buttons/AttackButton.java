package Buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Stage.Commons;

class AttackButton {
	private int startx,starty;
	private Color c = new Color(30,30,30);
	private Color cIsHover = Commons.cAttack;
	private Color cInactive = new Color(60,60,60);
	private Color cActive = Commons.cAttackActive;
	protected Rectangle rect;
	private String name = "Attack";
	private boolean isHover = false;
	public boolean isActive = false;
	private GamePiece parentGamepiece;
	
	public AttackButton(int startx, int starty, GamePiece parentGamepiece) {
		this.startx = startx;
		this.starty = starty;	 
		this.rect = new Rectangle(startx,starty,150,80);
		this.parentGamepiece = parentGamepiece;
	}
	 
	public boolean getIsHover() {
		return isHover; 
	}

	// draws the button but differently if it is hover or inActive
	public void drawButton(Graphics2D g2d) {
		if(isHover && !parentGamepiece.getHasExecutedAttack()) {
			g2d.setColor(cIsHover);
		}else {
			g2d.setColor(c);
		}
		if(isActive) {
			g2d.setColor(cActive);
		}
		g2d.fill(rect);
		if(isHover && !parentGamepiece.getHasExecutedAttack()) {
			g2d.setColor(c);
		}else {
			g2d.setColor(cIsHover);
		}
		if(parentGamepiece.getHasExecutedAttack()) {
			g2d.setColor(cInactive);
		} 
		if(isActive) {
			g2d.setColor(c);
		}
		g2d.draw(rect);
		Font f = new Font("Arial",Font.BOLD,40);
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics(f);
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	
	public void press() {
		if(!parentGamepiece.getHasExecutedAttack()) {
			isActive = true;
			parentGamepiece.showPossibleAttacks();
		}
	}
	
	public void updateHover(Point mousePos) {
		if(rect.contains(mousePos)) {
			isHover = true;
		}else {
			isHover = false;
		}
	}
	
	public void updatePos(Point CameraPos) {
		this.rect.setBounds(startx-CameraPos.x,starty-CameraPos.y,150,80);
	}
}
