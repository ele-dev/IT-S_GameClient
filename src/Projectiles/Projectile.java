package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import Environment.DestructibleObject;
import Stage.Commons;

public abstract class Projectile {
	protected float x,y;
	protected Color c;
	protected Rectangle rectHitbox;
	protected Shape shapeShow;
	public float angle;
	protected float v;
	protected float acc;
	protected boolean hasHitTarget;
	protected boolean isDestroyed = false;
	
	protected Shape targetShape;
	protected DestructibleObject targetDestructibleObject;
	
	public Projectile(int x, int y, int w, int h, Color c, float angle, float v, float acc, Shape targetShape, DestructibleObject targetDestructibleObject) {
		this.x = x;
		this.y = y; 
		this.c = c;
		int rectHitboxSize = (w+h)/2;
		this.rectHitbox = new Rectangle(x-rectHitboxSize/2,y-rectHitboxSize/2,rectHitboxSize,rectHitboxSize);
		this.angle = angle;
		this.v = v;
		this.acc = acc;
		this.targetShape = targetShape;
		this.targetDestructibleObject = targetDestructibleObject;
	}
	
	public float getX() {
		return x;
	}
	public float getY() { 
		return y;
	}
	public boolean hasHitTarget() {
		return hasHitTarget;
	}
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public void move() {
		// adds on acceleration only if the result velocity is greater than 0 else v = 0
		v = acc + v > 0 ? v + acc : 0;
				
		x += Math.cos(Math.toRadians(angle + 90)) * v;
		y += Math.sin(Math.toRadians(angle + 90)) * v;	
		rectHitbox.setBounds((int)(x-rectHitbox.width/2), (int)(y-rectHitbox.height/2), rectHitbox.width, rectHitbox.height);
	}
	
	public void homeInOnTarget(Point targetPoint, float rotationDelay) {
		float ak = 0;
		float gk = 0;
		
		ak = targetPoint.x - x;
		gk = targetPoint.y - y;
		
		float angleDesired = (float) Math.toDegrees(Math.atan2(ak * -1, gk));
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	
	public abstract void drawProjectile(Graphics2D g2d);
	
	public void checkHitAnyTarget() {
		if(targetDestructibleObject != null && targetDestructibleObject.checkIntersects(rectHitbox)) {
			hasHitTarget = true;
			return;
		}
		if(targetShape.intersects(rectHitbox)) {
			hasHitTarget = true;
		}
	}
}
