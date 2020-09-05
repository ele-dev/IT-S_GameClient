package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import Abilities.RadialShield;
import GamePieces.GamePiece;
import Particles.Explosion;
import Particles.RocketTrailParticle;
import Stage.BoardRectangle;
import Stage.StagePanel;


public class Rocket extends Projectile{
	private Polygon poly;
	public float rotationDelay = 4;
	
	public Rocket(int x, int y, int w, int h, Color c,float angle,GamePiece currentTarget, RadialShield currenTargetShield) {
		super(x, y, w, h, c, angle, 0, 0.3f, currentTarget,currenTargetShield);
		poly = new Polygon();
		poly.addPoint((int)(w/2),(int)(-h/2));
		poly.addPoint((int)(w/2), (int)(h/4));
		poly.addPoint((int)0,(int)(h*(3/4.0)));
		poly.addPoint((int)(- w/2),(int)(h/4));
		poly.addPoint((int)(- w/2),(int)(-h/2));
	}
	
	public void addTrailParticle() { 
		int randomSize = (int)(Math.random() * 2) +4;
		StagePanel.particles.add(new RocketTrailParticle(x, y, randomSize, randomSize, c, angle, 3));
	}
	
	// draws the rocket
	public void drawProjectile(Graphics2D g2d) {
		int w = (int) rectHitbox.getWidth();
		int h = (int) rectHitbox.getHeight();
		this.poly.reset();
		this.poly = new Polygon();
		poly.addPoint(w/2,-h/2);
		poly.addPoint(w/2, h/4);
		poly.addPoint(0,(int)(h*(3/4.0)));
		poly.addPoint(- w/2,h/4);
		poly.addPoint(- w/2,-h/2);
		g2d.setColor(c);
		this.rectHitbox.setBounds((int)(x-w/2),(int)(y-h/2),w,h);
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(poly);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
	}
	// checks if the rocket has hit the target and damages the target if it has 
	// it also explodes if it has hit the target
	public void checkHitEnemy() {
		if(currentTarget != null) {
			if(rectHitbox.intersects(currentTarget.getRectHitbox())) {
				explode();
			}
		}
	}
	
	@Override
	public void checkHitTargetShield() {
		if(currenTargetShield != null) {
			if(currenTargetShield.getShieldCircle().intersects(rectHitbox)) {
				explode();
			}
		}
	}
	// creates an explosion on impact
	public void explode() {
		StagePanel.particles.add(new Explosion((float)x, (float)y,1, (float)(Math.random()*360)));
		isDestroyed = true;
	}
	
}
