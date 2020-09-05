package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class SniperTrailParticle extends Particle{
	private int size;
	private Rectangle rectShow;
	private float fadeSpeed;
	private float alpha = 100;
	private int greyTone;
	
	public SniperTrailParticle(int x, int y) {
		super(x, y);
		
		size = (int)(Math.random()*3+5);
		fadeSpeed = (float) (Math.random()*0.2+0.6);
		rectShow = new Rectangle(-size/2,-size/2,size,size);
		greyTone = (int)(Math.random()*90+10);
		c = new Color(greyTone,greyTone,greyTone,(int)alpha);
		rotation = (float) (Math.random()*360);
	}
	
	private void updateFade() {
		alpha -= fadeSpeed;
		this.c = new Color(greyTone,greyTone,greyTone,(int)alpha);
		if(alpha < 10) {
			isDestroyed = true;
		}
	}
 
	@Override
	public void drawParticle(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		g2d.fill(rectShow);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
	}

	@Override
	public void update() {
		updateFade();
	}
	

	
	
	
}
