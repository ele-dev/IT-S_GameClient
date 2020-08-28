package Particles;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class Particle {
	protected float x,y;
	protected float angle,angleDesired,rotation;
	protected Color c;
	protected float v;
	protected boolean isDestroyed = false;
	
	protected Particle(float x, float y, float angle, float rotation, Color c, float v) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.angleDesired = 0;
		this.rotation = rotation;
		this.c = c;
		this.v = v;
	}
	protected Particle(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public abstract void drawParticle(Graphics2D g2d);
	
	public abstract void update();
	
	public boolean getIsDestroyed() {
		return isDestroyed;
	}
}
