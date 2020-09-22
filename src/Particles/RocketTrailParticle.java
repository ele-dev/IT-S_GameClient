package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RocketTrailParticle extends Particle{
	private Rectangle rect;
	
	public RocketTrailParticle(float x, float y, int w, int h,Color c, float angle,float fadeSpeed) {
		super(x, y,0,(float)(Math.random()*360),c,0,fadeSpeed);
		rect = new Rectangle(-w/2,-h/2,w,h);;
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
	}	
}
