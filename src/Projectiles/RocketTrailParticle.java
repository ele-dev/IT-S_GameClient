package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class RocketTrailParticle {
	double x,y;
	int w,h;
	Rectangle rect;
	public Color c;
	double angle;
	double rotationAngle;
	int fadeSpeed;
	
	public RocketTrailParticle(double x, double y, int w, int h,Color c, double angle,int fadeSpeed) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.rect = new Rectangle(-w/2,-h/2,w,h);;
		this.c = c;
		this.angle = angle;
		this.rotationAngle = Math.random()*360;
		this.fadeSpeed = fadeSpeed;
	}
	// draws the TrailParticle
	public void drawRocketTrailParticle(Graphics2D g2d) {
		
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotationAngle));
		g2d.fill(rect);
		g2d.rotate(Math.toRadians(-rotationAngle));
		g2d.translate(-x, -y);
	}
	// lowers the opacity of the color of the Particle
	public void updateFade() {
		c = new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()-fadeSpeed);
	}	
}
