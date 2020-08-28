package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import GamePieces.CommanderGamePiece;
import Stage.Commons;

public class UltChargeOrb extends Particle{
	private Rectangle rectHitbox;
	private double rotationDelay = 3;
	private double acc = 0.1;
	private CommanderGamePiece commanderGamePiece;
	private boolean isCollected = false;
	
	private ArrayList<Rectangle> fadeRectangles = new ArrayList<Rectangle>();
	private ArrayList<Integer> fadeRectangleAlphas = new ArrayList<Integer>();

	public UltChargeOrb(float x, float y, CommanderGamePiece commanderGamePiece) {
		super(x, y, (float)(Math.random()*360), 0, Commons.cUltCharge, 0.5f);
		this.x = x;
		this.y = y;
		int size = (int)(Math.random()*3+6);
		this.rectHitbox = new Rectangle((int)x-size/2,(int)y-size/2,size,size);
		this.commanderGamePiece = commanderGamePiece;
	}
	
	@Override
	public void drawParticle(Graphics2D g2d) {
		if(!isCollected) {
			g2d.setColor(c);
			g2d.fill(rectHitbox);
		}
		for(int i = 0;i<fadeRectangles.size();i++) {
			g2d.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),fadeRectangleAlphas.get(i)));
			g2d.fill(fadeRectangles.get(i));
		}
	}
	@Override
	public void update() {
		if(!isCollected) {
			updateAngle();
			move();
			
			if(rectHitbox.intersects(commanderGamePiece.getRectHitbox())) {
				isCollected = true;
				commanderGamePiece.updateUltCharge(0.5f);
			}
		}
		for(int i = 0;i<fadeRectangleAlphas.size();i++) {
			if(fadeRectangleAlphas.get(i) > 10) {
				fadeRectangleAlphas.set(i,fadeRectangleAlphas.get(i)-8);
			}else {
				fadeRectangleAlphas.remove(i);
				fadeRectangles.remove(i);
			}	
		}
		if(isCollected && fadeRectangles.size() == 0) {
			isDestroyed = true;
		}
	}

	public void move() {
		if(v < 8) {
			this.v += acc;
		}
		
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;
		
		fadeRectangles.add(rectHitbox);
		fadeRectangleAlphas.add(255);
		rectHitbox = new Rectangle((int)(x-rectHitbox.getWidth()/2),(int)(y-rectHitbox.getHeight()/2),(int)rectHitbox.getWidth(),(int)rectHitbox.getHeight());
	}
	
	public void updateAngle() {
		double ak = commanderGamePiece.getCenterX() - x;
		double gk = commanderGamePiece.getCenterY() - y;
		
		angleDesired = (float)(Math.toDegrees(Math.atan2(ak*-1, gk)));
		if(angleDesired +180 == angle) {
			angleDesired+= 10;
		}
		
		double ak1 = Math.cos(Math.toRadians(angleDesired+90));
		double gk1 = Math.sin(Math.toRadians(angleDesired+90));
		
		double ak2 = Math.cos(Math.toRadians(angle+90)) * rotationDelay;
		double gk2 = Math.sin(Math.toRadians(angle+90)) * rotationDelay;

		double ak3 = (ak1+ak2*rotationDelay)/(rotationDelay+1);
		double gk3 = (gk1+gk2*rotationDelay)/(rotationDelay+1);
		
		angle = (float)(Math.toDegrees(Math.atan2(ak3*-1, gk3)));
	}

	
	
	
	
}
