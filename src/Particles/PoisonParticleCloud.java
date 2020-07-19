package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class PoisonParticleCloud {
	double x,y;
	int size;
	public Color c;
	Ellipse2D oval;
	double v;
	double angle;
	double airResistance = 0.003;
	int fadeSpeed = 1;
	
	int fadeCounter = 0;
	
	public PoisonParticleCloud(double x, double y, int size, double v,double angle) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.c = new Color(99,255,56,200);
		this.oval = new Ellipse2D.Double(x-size/2,y-size/2,size,size);
		this.v = v;
		this.angle = angle;
	}
	// moves the clouds in the direction it points (they also float upwards because of buoyancy and also are stopped by air resistance)
	public void move() {
		if(v>0.05) {
			v -= airResistance;
		}
		
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY -0.05;	
	}
	// draws the PoisonCloud
	public void drawPoisonParticleCloud(Graphics2D g2d) {
		this.oval = new Ellipse2D.Double(x-size/2,y-size/2,size,size);
		g2d.setColor(c);
		g2d.fill(oval);
	}
	// lowers the opacity of the color of the Particle
	public void updateFade() {
		fadeCounter++;
		if(fadeCounter >= 3) {
			fadeCounter = 0;
			c = new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha() -fadeSpeed);
		}
		
		
	}
}
