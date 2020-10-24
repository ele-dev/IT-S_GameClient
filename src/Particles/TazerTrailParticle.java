package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TazerTrailParticle extends Particle{
	private int size;
	private Rectangle rectShow;
	private float fadeSpeed;
	private float alpha = 255;
	
	public TazerTrailParticle(int x, int y, float v, float fadeSpeed, float angle) {
		super(x, y);
		size = (int)(Math.random()*3+5);
		this.fadeSpeed = fadeSpeed;
		rectShow = new Rectangle(-size/2,-size/2,size,size);
		c = new Color(58, 100+(int)(Math.random()*130), 140+(int)(Math.random()*30),(int)alpha);
		rotation = (float) (Math.random()*360);
		
		this.angle = angle;
		this.v = v;
	} 
	
	private void updateFade() {
		alpha -= fadeSpeed;
		this.c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha);
		if(alpha < 20) {
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
		move();
	}
	
	public void move() {
		x += Math.cos(Math.toRadians(angle)) * v;
		y += Math.sin(Math.toRadians(angle)) * v;
	}
	
	
	
}
