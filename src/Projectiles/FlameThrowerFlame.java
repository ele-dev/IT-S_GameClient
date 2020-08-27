package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Stage.BoardRectangle;


public class FlameThrowerFlame {
	private float x,y;
	private Color c;
	private Rectangle rect;
	private Rectangle rectHitbox;
	private float angle;
	private float v;
	public boolean hasHitEnemy = false;
	private GamePiece currentTarget;
	private BoardRectangle currentTargetBoardRectangle;
	
	public FlameThrowerFlame(int x, int y, int w, int h,double v,double dmg,double angle,GamePiece currentTarget,BoardRectangle currentTargetBoardRectangle) {
		this.x = x;
		this.y = y;
		this.c = new Color(255,250,10);
		this.rect = new Rectangle(-w/2,-h/2,w,h);
		this.rectHitbox = new Rectangle(x-w/2,y-h/2,w,h);
		this.angle = (float) angle;
		
		this.v = (float) v;
		this.currentTarget = currentTarget;
		this.currentTargetBoardRectangle = currentTargetBoardRectangle;
	}
	
	public Color getColor() {
		return c;
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
		
		rect.width += 0.2;
		rect.height += 0.2;
		int w = rect.width;
		int h = rect.height;
		rect = new Rectangle((int)(-w/2),(int)(-h/2),(int)(w),(int)(h));
		rectHitbox = new Rectangle((int)(x-w/2),(int)(y-h/2),(int)(w),(int)(h));
	}
	
	public void drawFlame(Graphics2D g2d) {
		g2d.setColor(c);
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
