package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;

import Environment.DestructibleObject;
import Particles.Explosion;
import Particles.TrailParticle;
import Stage.StagePanel;


public class Rocket extends Projectile {
	private Polygon poly;
	public float rotationDelay = 4;

	
	public Rocket(int x, int y, int w, int h, Color c, float angle, Shape targetShape, DestructibleObject targetDestructibleObject) {
		super(x, y, w, h, c, angle, 0, 0.3f, targetShape, targetDestructibleObject);
		poly = new Polygon();
		poly.addPoint((int)(w/2),(int)(-h/2));
		poly.addPoint((int)(w/2), (int)(h/4)); 
		poly.addPoint((int)0,(int)(h*(3/4.0)));
		poly.addPoint((int)(- w/2),(int)(h/4));
		poly.addPoint((int)(- w/2),(int)(-h/2));
	}
	
	public void addTrailParticle() { 
		int randomSize = (int)(Math.random() * 2) + 4;
		StagePanel.particles.add(new TrailParticle(x, y, randomSize, 0, c, 0, 3, 0));
	}
	
	// draws the rocket
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(poly);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
	}
	
	public void tryExplodeTarget() {
		checkHitAnyTarget();
		if(hasHitTarget) {
			explode();
		}
	}
	
	// creates an explosion on impact
	public void explode() {
		StagePanel.particles.add(new Explosion((float)x, (float)y,1, (float)(Math.random()*360)));
		isDestroyed = true;
	}
}
