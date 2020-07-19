package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class ExplosionCloud {
	private float x,y;
	private int size;
	Color c;
	private Ellipse2D oval;
	private float v;
	private float angle;
	private float airResistance = 0.2f;
	int fadeSpeed = 1;
	
	ExplosionCloud(float x, float y, int size, float v,float angle) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.c = new Color(255,20,20);
		this.oval = new Ellipse2D.Double(x-size/2,y-size/2,size,size);
		this.v = v;
		this.angle = angle;
	}
	// moves the clouds in the direction it points (they also float upwards because of buoyancy and also are stopped by air resistance)
	public void move() {
		if(v>0.5) {
			v -= airResistance;
		}
		
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY -0.2;	
	}
	// draws the ExplosionCloud
	public void drawExplosionCloud(Graphics2D g2d) {
		this.oval = new Ellipse2D.Double(x-size/2,y-size/2,size,size);
		g2d.setColor(c);
		g2d.fill(oval);
	}
	// lowers the opacity of the color of the Particle and also changes color to fit a explosion (from red to gray)
	public void updateFade() {
		int newRed = c.getRed();
		if(c.getRed()>20) {
			newRed = c.getRed() -4;
		}
		c = new Color(newRed,c.getGreen(),c.getBlue(),c.getAlpha() -fadeSpeed);
	}
}
