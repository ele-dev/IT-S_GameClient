package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TrailParticle extends Particle{
	private Rectangle rect;
	private float size,shrinkSpeed;
	
	
	public TrailParticle(float x, float y, int size,float angle,Color c,float v, float fadeSpeed, float shrinkSpeed) {
		super(x, y,angle,(float)(Math.random()*360),c,v,fadeSpeed);
		rect = new Rectangle(-size/2,-size/2,size,size);
		this.size = size;
		this.shrinkSpeed = shrinkSpeed;
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
	
	// lowers the opacity of the color of the Particle if opacity is too low particle gets destroyed
	@Override
	public void update() {
		fadeOut();
		shrink();
		move();
	}


	@Override
	public void move() {
		x += Math.cos(Math.toRadians(angle)) * v;
		y += Math.sin(Math.toRadians(angle)) * v;
	}	
	
	public void shrink() {
		if(size-shrinkSpeed >= 1) {
			size-=shrinkSpeed;
		}else {
			isDestroyed = true;
		}
		rect = new Rectangle((int)(-size/2),(int)(-size/2),(int)(size),(int)(size));
		
	}
}
