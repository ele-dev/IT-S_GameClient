package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Stage.BoardRectangle;
import Stage.StagePanel;

public class DestructionParticle extends Particle{
	int w,h;
	private float friction = 0.08f;
	float vRotation = (float) (Math.random()*5+3);
	float fadeSpeed;
	
	public DestructionParticle(float x, float y, int w, int h, Color c,float angle, float v) {
		super(x, y, angle, (float)(Math.random()*360), c, v, 1f);
		this.w = w;
		this.h = h;
		this.rectHitbox = new Rectangle((int)x+w/2,(int)y+h/2,w,h);
		
		tFadeDelayTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		tFadeDelayTimer.setRepeats(false);
		tFadeDelayTimer.start();
	}

	@Override
	public void drawParticle(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		Rectangle rectShow = new Rectangle(-w/2,-h/2,w,h);
		g2d.fill(rectShow);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
		
	}

	@Override
	public void update() {
		if(v > 0) {
			move();
			tryGetWallBlocked();
		}
		fadeOut();
	}
	
	public void move() {
		x += Math.cos(Math.toRadians(angle+90)) * v;
		y += Math.sin(Math.toRadians(angle+90)) * v;
			
		v = v > 0?v-friction*2:0;
		
		rotation += vRotation;
		rectHitbox.setBounds((int)x+w/2,(int)y+h/2,w,h);
	}
}
