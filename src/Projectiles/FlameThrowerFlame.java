package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Stage.BoardRectangle;


public class FlameThrowerFlame {
	double x,y;
	double w,h;
	public Color c;
	Rectangle rect;
	Rectangle rectHitbox;
	double dmg;
	double angle;
	double v;
	boolean isDestroyed = false;
	public boolean hasHitEnemy = false;
	GamePiece currentTarget;
	BoardRectangle currentTargetBoardRectangle;
	
	public FlameThrowerFlame(int x, int y, int w, int h,double v,double dmg,double angle,GamePiece currentTarget,BoardRectangle currentTargetBoardRectangle) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.c = new Color(255,250,10);
		this.rect = new Rectangle(-w/2,-h/2,w,h);
		this.rectHitbox = new Rectangle(x-w/2,y-h/2,w,h);
		this.dmg = dmg;
		this.angle = angle;
		
		this.v = v;
		this.currentTarget = currentTarget;
		this.currentTargetBoardRectangle = currentTargetBoardRectangle;
	}
	
	public void move() {
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		if(hasHitEnemy) {
			vX = Math.cos(Math.toRadians(this.angle + 90)) * v/3;
			vY = Math.sin(Math.toRadians(this.angle + 90)) * v/3;
		}
		x += vX;
		y += vY;	
		
		w += 0.2;
		h += 0.2;
		this.rect = new Rectangle((int)(-w/2),(int)(-h/2),(int)(w),(int)(h));
		this.rectHitbox = new Rectangle((int)(x-w/2),(int)(y-h/2),(int)(w),(int)(h));
	}
	
	public void drawFlame(Graphics2D g2d) {
		
		g2d.setColor(c);
		this.rectHitbox.setBounds((int)(x-w/2),(int)(y-h/2),(int)(w),(int)(h));
		g2d.translate(this.x, this.y);
		g2d.fill(rect);
		g2d.translate(-this.x, -this.y);
	}
	
	public void updateFade() {
		if(c.getGreen()>10) {
			c = new Color(c.getRed() -1,c.getGreen() - 4,c.getBlue(),c.getAlpha() -2);
		}else {
			c = new Color(c.getRed() -2,c.getGreen(),c.getBlue(),c.getAlpha() -2);
		}
	}
	
	public void checkHitEnemy() {
		if(currentTarget != null && currentTargetBoardRectangle == null) {
			if(this.rectHitbox.intersects(currentTarget.getRectHitbox())) {
				hasHitEnemy = true;
			}
		}
		if(currentTarget == null && currentTargetBoardRectangle != null) {
			if(this.rectHitbox.intersects(currentTargetBoardRectangle.rect)) {
				hasHitEnemy = true;
			}
		}
		
	}
}
