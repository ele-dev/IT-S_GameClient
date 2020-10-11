package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ExplosionCloud {
	private float x,y;
	private Color c;
	private Rectangle rectShow;
	private float v;
	private float angle,rotation;
	private float airResistance = 0.2f;
	private int fadeSpeed = 2;
	
	ExplosionCloud(float x, float y, int size, float v,float angle) {
		this.x = x;
		this.y = y;
		c = new Color(255,20,20);
		rectShow = new Rectangle(-size/2,-size/2,size,size);
		this.v = v;
		this.angle = angle;
		this.rotation = (float) (Math.random()*360);
	}
	
	public Color  getColor() {
		return c;
	}
	// moves the clouds in the direction it points (they also float upwards because of buoyancy and also are stopped by air resistance)
	public void move() {
		if(v>0.5) {
			v -= airResistance;
		}
		x += Math.cos(Math.toRadians(angle + 90)) * v;
		y += Math.cos(Math.toRadians(angle + 90)) * v -0.2;	
	}
	// draws the ExplosionCloud
	public void drawExplosionCloud(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		g2d.fill(rectShow);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
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
