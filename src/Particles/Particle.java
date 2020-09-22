package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.Timer;

public abstract class Particle {
	protected float x,y;
	protected float angle,angleDesired,rotation;
	protected Color c;
	protected float v;
	protected boolean isDestroyed = false;
	protected float fadeSpeed;
	private float alpha;
	protected Timer tFadeDelayTimer;
	
	protected Particle(float x, float y, float angle, float rotation, Color c, float v, float fadeSpeed) {
		this.x = x;
		this.y = y;
		this.angle = angle; 
		this.rotation = rotation;
		this.c = c;
		this.v = v;
		this.fadeSpeed = fadeSpeed;
		if(c != null) {
			alpha = c.getAlpha();
		}
	} 
	
	protected Particle(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	protected void fadeOut() {
		if(tFadeDelayTimer != null && tFadeDelayTimer.isRunning()) {
			return;
		}
		if(alpha > 10) {
			alpha-=fadeSpeed;
			c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha);
		}else {
			isDestroyed = true;
		}
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	
	public Point getPos() {
		return new Point((int)x,(int)y);
	}
	
	public abstract void drawParticle(Graphics2D g2d);
	
	public abstract void update();
	
	public boolean getIsDestroyed() {
		return isDestroyed;
	}
}
