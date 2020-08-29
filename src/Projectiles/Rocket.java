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
	private float x,y;
	private Color c;
	private Polygon poly;
	private Rectangle rectHitbox;
	private float angle;
	private float v;
	private float acc;
	private boolean isDestroyed = false;
	private GamePiece currentTarget;
	private BoardRectangle currentTargetBoardRectangle;
	private float rotationDelay = 4;
	
	private ArrayList<RocketTrailParticle> trailParticles = new ArrayList<RocketTrailParticle>();
	
	public Rocket(int x, int y, int w, int h, Color c,float angle,GamePiece currentTarget,BoardRectangle currentTargetBoardRectangle) {
		this.x = x;
		this.y = y;
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
		this.acc = 0.3f;
		this.currentTarget = currentTarget;
		this.currentTargetBoardRectangle = currentTargetBoardRectangle;
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	// moves the Rocket to the direction of the Angle and at the speed of v
	public void move() {
		if(this.v<8) {
			this.v += this.acc;
		}
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		x += vX;
		y += vY;	
		rectHitbox = new Rectangle((int)(x-rectHitbox.getWidth()/2),(int)(y-rectHitbox.getHeight()/2),(int)rectHitbox.getWidth(),(int)rectHitbox.getHeight());
	}
	
	public void addTrailParticle() {
		int randomSize = (int)(Math.random() * 2) +4;
		StagePanel.particles.add(new RocketTrailParticle(x, y, randomSize, randomSize, c, angle, 3));
	}
	
	public void updateAngle() {
		float ak = 0;
		float gk = 0;
		if(currentTarget != null && currentTargetBoardRectangle == null) {
			ak = currentTarget.boardRect.getCenterX() - x;
			gk = currentTarget.boardRect.getCenterY() - y;
		}else
		if(currentTarget == null && currentTargetBoardRectangle != null) {
			ak = currentTargetBoardRectangle.getCenterX() - x;
			gk = currentTargetBoardRectangle.getCenterY() - y;
		}
		
		
		float angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		if(angleDesired +180 == angle) {
			angleDesired+= 10;
		}
		angle = Stage.Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	// draws the rocket
	public void drawRocket(Graphics2D g2d) {
		int w = (int) rectHitbox.getWidth();
		int h = (int) rectHitbox.getHeight();
		this.poly.reset();
		this.poly = new Polygon();
		poly.addPoint(w/2,-h/2);
		poly.addPoint(w/2, h/4);
		poly.addPoint(0,(int)(h*(3/4.0)));
		poly.addPoint(- w/2,h/4);
		poly.addPoint(- w/2,-h/2);
		g2d.setColor(c);
		this.rectHitbox.setBounds((int)(x-w/2),(int)(y-h/2),w,h);
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(poly);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
	}
	// checks if the rocket has hit the target and damages the target if it has 
	// it also explodes if it has hit the target
	public void checkHitEnemy() {
		if(currentTarget != null && currentTargetBoardRectangle == null) {
			if(this.rectHitbox.intersects(currentTarget.getRectHitbox())) {
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
	
}
