package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Abilities.RadialShield;
import GamePieces.GamePiece;


public class FlameThrowerFlame extends Projectile{
	public boolean hasHitEnemy = false;
	float w,h;
	float rotation;
	
	public FlameThrowerFlame(int x, int y, int w, int h,float v,float angle,GamePiece currentTarget, RadialShield currenTargetShield) {
		super(x, y, w, h, new Color(255,250,10), angle, v, -0.01f, currentTarget,currenTargetShield);
		rotation = (float) (Math.random()*360);
		this.w = w;
		this.h = h;
		shapeShow = new Rectangle(-w/2,-h/2,w,h);
	}
	
	public Color getColor() {
		return c;
	}
	
	public void move() {
		if(acc+v > 0) {
			v+=acc;
		}else {
			v = 0;
		}
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
		shapeShow = new Rectangle((int)(-w/2),(int)(-h/2),(int)(w),(int)(h));
		rectHitbox = new Rectangle((int)(x-w/2),(int)(y-h/2),(int)(w),(int)(h));
	}
	@Override
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
	}
	
	@Override
	public void checkHitTargetShield() {
		if(currenTargetShield != null) {
			if(currenTargetShield.getShieldCircle().intersects(rectHitbox)) {
				v = 0.4f;
				hasHitTarget = true;
			}
		}
	}
	
	public void updateFade() {
		if(c.getGreen()>10) {
			c = new Color(c.getRed() -1,c.getGreen() - 4,c.getBlue(),c.getAlpha() -2);
		}else {
			c = new Color(c.getRed() -2,c.getGreen(),c.getBlue(),c.getAlpha() -2);
		}
	}
}
