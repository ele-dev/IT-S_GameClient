package Particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

public class ExplosionFragment {
	private float x,y;
	private float size;
	Color c;
	private Ellipse2D oval;
	private float v;
	private float angle;
	private float airResistance = 0.02f;
	private int fadeSpeed = 3;
	private Point[] trail = new Point[10];
	private int countCoordinates = 0;
	
	ExplosionFragment(float x, float y, int size, Color c, float v,float angle) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.c = c;
		this.oval = new Ellipse2D.Double(x-size/2,y-size/2,size,size);
		this.v = v;
		this.angle = angle;
		for(int i = 0;i<trail.length;i++) {
			trail[i] = new Point((int)x,(int)y);
		}
	}
	// moves the explosionFragment in the direction of the angle and at the speed of v (velocity)
	public void move() {
		if(v>0.1) {
			v -= airResistance;
		}
		
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY + 0.2;
		updateTrail();
	}
	// updates the trail saves the latest 10(depending of the length of the array) positions and connects them with a line in drawExplosionFragments()
	public void updateTrail() {
		countCoordinates++;
		
		if(countCoordinates%4 == 0) {
			for(int i = trail.length-1;i>0;i--) {
				this.trail[i] = this.trail[i-1];
			}
			this.trail[0] = new Point((int)this.x,(int)this.y);
		}
	}
	// draws the fragment and the trail
	public void drawExplosionFragments(Graphics2D g2d) {
		this.oval = new Ellipse2D.Double(x-size/2,y-size/2,size,size);
		g2d.setColor(c);
		g2d.fill(oval);
		g2d.setStroke(new BasicStroke((int)(size/2)));
		for(int i = 0;i<trail.length-1;i++) {
			g2d.drawLine((int)trail[i].getX(), (int)trail[i].getY(), (int)trail[i+1].getX(), (int)trail[i+1].getY());
		}
	}
	// lowers the opacity of the color of the Particle and also changes color to fit a explosion (from red to white)
	public void updateFade() {
		c = new Color(c.getRed(),c.getGreen() + 3,c.getBlue() +2,c.getAlpha() -fadeSpeed);
	}
	
	
	
}
