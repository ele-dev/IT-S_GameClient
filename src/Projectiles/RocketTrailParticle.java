package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Particles.Particle;

public class RocketTrailParticle extends Particle{
	private int fadeSpeed;
	private Rectangle rect;
	
	public RocketTrailParticle(float x, float y, int w, int h,Color c, float angle,int fadeSpeed) {
		super(x, y,angle,(float)(Math.random()*360),c,0);
		this.rect = new Rectangle(-w/2,-h/2,w,h);;
		this.fadeSpeed = fadeSpeed;
	}
	

	@Override
	public void drawParticle(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		g2d.fill(rect);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
	}
	// lowers the opacity of the color of the Particle
	@Override
	public void update() {
		if(c.getAlpha() > 10) {
			c = new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()-fadeSpeed);
		}else {
			isDestroyed = true;
		}
		
	}	
}
