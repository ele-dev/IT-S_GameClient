package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class EmptyShell extends Particle {
	private static final float friction = 0.1f;
	private static final float vRotation = (float) ((Math.random()-0.5)*10);
	private int w,h;
	private float vY,capVY;
	
	public EmptyShell(float x, float y, int w, int h, float angle, Color c, float v) {
		super(x, y, angle+(float)((Math.random()-0.5f)*30), (float)(Math.random()*360), c, v,0);
		this.w = w;
		this.h = h;
		this.rectHitbox = new Rectangle((int)x+w/2,(int)y+h/2,w,h);
		vY = -v/2;
		capVY= -vY;
	}
	
	@Override
	public void drawParticle(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation)); 
		Rectangle rectHidden = new Rectangle(-w/2,-h/2,w,h);
		g2d.fill(rectHidden);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
	}

	@Override
	public void update() {
		if(v > 0) {
			move();
			tryGetWallBlocked();
		}
	}
	
	public void move() {
		x += Math.cos(Math.toRadians(angle+90)) * v;
		y += Math.sin(Math.toRadians(angle+90)) * v;
		if(vY < capVY) {
			vY+=friction;
			y+=vY;
		}
		v = v-friction > 0?v-friction:0;
		rotation += vRotation;
		rectHitbox.setBounds((int)x+w/2,(int)y+h/2,w,h);
	}
	
}
