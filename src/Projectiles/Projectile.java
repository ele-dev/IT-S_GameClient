package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import Abilities.RadialShield;
import GamePieces.GamePiece;
import Stage.StagePanel;

public abstract class Projectile {
	protected float x,y;
	protected Color c;
	protected Rectangle rectHitbox;
	protected Shape shapeShow;
	public float angle;
	protected float v;
	protected float acc;
	protected boolean hasHitTarget;
	protected boolean isDestroyed;
	protected GamePiece currentTarget;
	protected RadialShield currenTargetShield;
	
	public Projectile(int x, int y, int w, int h, Color c, float angle, float v, float acc, GamePiece currentTarget, RadialShield currenTargetShield) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.rectHitbox = new Rectangle(x-w/2,y-h/2,w,h);
		this.angle = angle;
		this.v = v;
		this.acc = acc;
		this.currentTarget = currentTarget;
		this.currenTargetShield = currenTargetShield;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public boolean getHasHitTarget() {
		return hasHitTarget;
	}
	
	public boolean getIsDestroyed() {
		return isDestroyed;
	}
	
	public GamePiece getCurrentTarget() {
		return currentTarget;
	}
	
	public RadialShield getCurrenTargetShield() {
		return currenTargetShield;
	}
	
	public void move() {
		if(acc+v > 0) {
			v+=acc;
		}else {
			v = 0;
		}
		double vX = Math.cos(Math.toRadians(angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;	
		this.rectHitbox = new Rectangle((int)(x-rectHitbox.width/2),(int)(y-rectHitbox.height/2),rectHitbox.width,rectHitbox.height);
	}
	
	public void homeInOnTarget(Point targetPoint, float rotationDelay) {
		float ak = 0;
		float gk = 0;
		
		ak = targetPoint.x - x;
		gk = targetPoint.y - y;
		
		
		float angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		angle = Stage.Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	
	public abstract void drawProjectile(Graphics2D g2d);
	
	// check if it hit its target (currentTarget)
	public void checkHitEnemy() {
		if(currentTarget != null) {
			if(this.rectHitbox.intersects(currentTarget.getRectHitbox())) {
				hasHitTarget = true;
			}
		}
	}
	
	public void checkHitTargetShield(){
		if(currenTargetShield != null) {
			if(currenTargetShield.getShieldCircle().intersects(rectHitbox)) {
				hasHitTarget = true;
			}
		}
	}
}
