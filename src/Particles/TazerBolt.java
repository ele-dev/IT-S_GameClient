package Particles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class TazerBolt extends Particle{

	private ArrayList<Point> points;
	private float alpha;

	public TazerBolt(ArrayList<Point> points) {
		super(0, 0, 0, 0, new Color(200, 200+(int)(Math.random()*50), 200+(int)(Math.random()*50),255), 0, 3);
		this.points = points;
		this.alpha = c.getAlpha();
	}

	@Override
	public void drawParticle(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(4));
		for(int i = 1;i<points.size();i++) {
			g2d.setColor(c);
			Point p1 = points.get(i-1);
			Point p2 = points.get(i);
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}

	@Override
	public void update() {
		updateFade();
	}
	
	private void updateFade() {
		alpha -= fadeSpeed;
		c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha);
		if(alpha < 10) {
			isDestroyed = true;
		} 
	}

	@Override
	public void move() {}

}
