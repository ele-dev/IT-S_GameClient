package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Stage.BoardRectangle;

public class Bullet {
	private float x,y;
	private Color c;
	private Rectangle rect;
	private Rectangle rectHitbox;
	private double angle;
	private double v;
	private boolean isDestroyed = false;
	private boolean hasHitEnemy = false;
	private GamePiece currentTarget;
	private BoardRectangle currentTargetBoardRectangle;

	public Bullet(int x, int y, int w, int h, Color c,float v,float angle,GamePiece currentTarget,BoardRectangle currentTargetBoardRectangle) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.rect = new Rectangle(-w/2,-h/2,w,h);
		this.rectHitbox = new Rectangle(x-w/2,y-h/2,w,h);
		this.angle = angle;
		this.v = v;
		this.currentTarget = currentTarget;
		this.currentTargetBoardRectangle = currentTargetBoardRectangle;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public boolean getIsDestroyed() {
		return isDestroyed;
	}
	
	public boolean getHasHitEnemy() {
		return hasHitEnemy;
	}
	
	// moves the bullet according to the velocity (v) and angle (angle)
	public void move() {
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;	
		this.rectHitbox = new Rectangle((int)(x-rectHitbox.width/2),(int)(y-rectHitbox.height/2),rectHitbox.width,rectHitbox.height);
	}
	// draws Bullet rotated (pointing tilted because of the angle)
	public void drawBullet(Graphics2D g2d) {
		g2d.setColor(c);
		this.rectHitbox.setBounds((int)(x-rectHitbox.width/2),(int)(y-rectHitbox.height/2),rectHitbox.width,rectHitbox.height);
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(rect);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
	}
	// check if it hit its target (currentTarget) and damages it
	public void checkHitEnemy() {
		if(currentTarget != null && currentTargetBoardRectangle == null) {
			if(this.rectHitbox.intersects(currentTarget.getRectHitbox())) {
				currentTarget.resetDmgFlashCountDown();
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
