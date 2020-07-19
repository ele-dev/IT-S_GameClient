package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import GamePieces.GamePiece;
import Particles.Explosion;
import Stage.BoardRectangle;
import Stage.StagePanel;


public class Rocket {
	double x,y;
	int w,h;
	Color c;
	Polygon poly;
	Rectangle rectHitbox;
	double angle;
	double v;
	double acc;
	public boolean isDestroyed = false;
	GamePiece currentTarget;
	BoardRectangle currentTargetBoardRectangle;
	double rotationDelay = 15;
	
	ArrayList<RocketTrailParticle> trailParticles = new ArrayList<RocketTrailParticle>();
	
	public boolean trailFaded = false;
	
	public Rocket(int x, int y, int w, int h, Color c,double angle,GamePiece currentTarget,BoardRectangle currentTargetBoardRectangle) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.c = c;
		this.poly = new Polygon();
		poly.addPoint((int)(w/2),(int)(-h/2));
		poly.addPoint((int)(w/2), (int)(h/4));
		poly.addPoint((int)0,(int)(h*(3/4.0)));
		poly.addPoint((int)(- w/2),(int)(h/4));
		poly.addPoint((int)(- w/2),(int)(-h/2));
		this.rectHitbox = new Rectangle(x-w/2,y-h/2,w,h);
		this.angle = angle;
		this.v = 0;
		this.acc = 0.3;
		this.currentTarget = currentTarget;
		this.currentTargetBoardRectangle = currentTargetBoardRectangle;
	}
	
	public void move() {
		if(this.v<8) {
			this.v += this.acc;
		}
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;	
		this.rectHitbox = new Rectangle((int)(x-w/2),(int)(y-h/2),w,h);
	}
	
	public void addTrailParticle() {
		int randomSize = (int)(Math.random() * 2) +4;
		trailParticles.add(new RocketTrailParticle(x, y, randomSize, randomSize, c, angle, 3));
	}
	
	public void drawTrail(Graphics2D g2d) {
		for(int i = 0;i<trailParticles.size();i++) {
			RocketTrailParticle curRTP = trailParticles.get(i);
			
			curRTP.drawRocketTrailParticle(g2d);
			
		}
		if(trailParticles.size()==0) {
			trailFaded = true;
		}
	}
	
	public void updateAngle() {
		double ak = 0;
		double gk = 0;
		if(currentTarget != null && currentTargetBoardRectangle == null) {
			ak = currentTarget.boardRect.centeredX - x;
			gk = currentTarget.boardRect.centeredY - y;
		}
		if(currentTarget == null && currentTargetBoardRectangle != null) {
			ak = currentTargetBoardRectangle.centeredX - x;
			gk = currentTargetBoardRectangle.centeredY - y;
		}
		double angleDesired = Math.toDegrees(Math.atan2(ak, gk)) *-1;
		
		if(angleDesired<-90 && angle >90) {
			angle = -179;
		}
		
		if(angle<-90 && angleDesired >90) {
			angle = 179;
		}
		// makes angle turn slowly
		angle = (angleDesired + this.angle*rotationDelay)/(rotationDelay+1);
	}
	// draws the rocket
	public void drawRocket(Graphics2D g2d) {
		
		this.poly.reset();
		this.poly = new Polygon();
		poly.addPoint((int)(w/2),(int)(-h/2));
		poly.addPoint((int)(w/2), (int)(h/4));
		poly.addPoint((int)0,(int)(h*(3/4.0)));
		poly.addPoint((int)(- w/2),(int)(h/4));
		poly.addPoint((int)(- w/2),(int)(-h/2));
		g2d.setColor(c);
		this.rectHitbox.setBounds((int)(x-w/2),(int)(y-h/2),w,h);
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(poly);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
		
//		g2d.setStroke(new BasicStroke(2));
//		g2d.setColor(Color.BLACK);
//		g2d.draw(rectHitbox);
		
	}
	// checks if the rocket has hit the target and damages the target if it has 
	// it also explodes if it has hit the target
	public void checkHitEnemy() {
		if(currentTarget != null && currentTargetBoardRectangle == null) {
			if(this.rectHitbox.intersects(currentTarget.getRectHitbox())) {
				currentTarget.resetDmgFlashCountDown();
				explode();
			}
		}
		if(currentTarget == null && currentTargetBoardRectangle != null) {
			if(this.rectHitbox.intersects(currentTargetBoardRectangle.rect)) {
				explode();
			}
		}
		
		
	}
	// creates an explosion on impact
	public void explode() {
		StagePanel.particles.add(new Explosion((float)x, (float)y,1, (float)(Math.random()*360)));
		isDestroyed = true;
	}
	// updates the trail (fades it)
	public void updateTrail() {
		for(int i = 0;i<trailParticles.size();i++) {
			RocketTrailParticle curRTP = trailParticles.get(i);
			if(curRTP.c.getAlpha() > 10) {
				curRTP.updateFade();
			}else {
				trailParticles.remove(i);
			}
		}
	}
	
}
