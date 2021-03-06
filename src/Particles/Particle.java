package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Stage.BoardRectangle;
import Stage.StagePanel;

public abstract class Particle {
	protected float x,y;
	protected float angle,angleDesired,rotation;
	protected Color c;
	protected float v;
	protected float fadeSpeed;
	private float alpha;
	protected Rectangle rectHitbox;
	
	protected boolean isDestroyed = false;
	Particle(float x, float y, float angle, float rotation, Color c, float v, float fadeSpeed) {
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
	public void setCNoAlpha(Color c) {
		this.c = new Color(c.getRed(),c.getGreen(),c.getBlue(),this.c.getAlpha());
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	protected Particle(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	protected void fadeOut() {
		if(alpha > fadeSpeed+1) {
			alpha-=fadeSpeed;
			c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha);
		}else {
			isDestroyed = true;
		}
	}
	
	// checks if hit a wall and sets velocity v to 0
	public void tryGetWallBlocked() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(curBR.isWall && rectHitbox.intersects(curBR.rect)){
				v = 0;
				break;
			}
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
	
	public abstract void move();
	
	
}
