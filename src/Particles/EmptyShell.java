package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;
import Stage.BoardRectangle;
import Stage.StagePanel;

public class EmptyShell extends Particle{
	int w,h;
	public double friction = 0.1;
	Rectangle rectHitbox;
	
	double vY1 = 1;
	double vRotation = Math.random()*5+3;
	
	public EmptyShell(float x, float y, int w, int h, float angle, Color c, float v) {
		super(x, y, angle+(float)((Math.random()-0.5f)*30), (float)(Math.random()*360), c, v);
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
		move();
		tryGetWallBlocked();
	}
	
	public void move() {
		if(v <= 0) {
			return;
		}
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;
		this.y -= this.vY1;
		if(vY1>-4) {
			vY1 -= friction*2;
		}else {
			v = 0;
		}
		rotation += vRotation;
		this.rectHitbox = new Rectangle((int)x+w/2,(int)y+h/2,w,h);
	}
	
	public void tryGetWallBlocked() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(curBR.isWall && this.rectHitbox.intersects(curBR.rect)){
				v = 0;
			}
		}
	}
	
}
