package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Stage.BoardRectangle;
import Stage.StagePanel;

public class EmptyShell extends Particle{
	int w,h;
	public float friction = 0.1f;
	
	double vY1 = 1;
	double vRotation = Math.random()*5+3;
	
	public EmptyShell(float x, float y, int w, int h, float angle, Color c, float v) {
		super(x, y, angle+(float)((Math.random()-0.5f)*30), (float)(Math.random()*360), c, v,0);
		this.w = w;
		this.h = h;
		this.rectHitbox = new Rectangle((int)x+w/2,(int)y+h/2,w,h);
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
		y -= this.vY1;
		if(vY1>-4) {
			vY1 -= friction*2;
		}else {
			v = 0;
		}
		rotation += vRotation;
		rectHitbox.setBounds((int)x+w/2,(int)y+h/2,w,h);
	}
	
}
