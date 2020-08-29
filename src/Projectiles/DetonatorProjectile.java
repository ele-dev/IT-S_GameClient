package Projectiles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import GamePieces.GamePiece;
import Particles.Explosion;



public class DetonatorProjectile {
	private float x,y;
	private float xRelTarget,yRelTarget;
	private Color c;
	private Rectangle rect;
	private Rectangle rectHitbox;
	private float dmg;
	private float angle;
	private float v;
	private boolean isStuckToTarget = false;
	private boolean isDestroyed = false;
	private GamePiece currentTarget;
	private GamePiece parentGP;
	
	public Timer detonationTimer;
	public Explosion detExplosion;
	private boolean isDetonated = false;
	public int turnsTillDetonation = 2;
	
	private Color cBlink;
	private int blinkCounter = 0;
	private int blinkeIntervall = 30;
	private boolean isColorBlink = false;
	
	public DetonatorProjectile(int x, int y, int w, int h, Color c,float dmg,float angle,GamePiece currentTarget,GamePiece parentGP) {
		this.x = x;
		this.y = y;
		this.c = c;
		this.cBlink = Color.BLACK;
		this.rect = new Rectangle(-w/2,-h/2,w,h);
		this.rectHitbox = new Rectangle(x-w/2,y-h/2,w,h);
		this.dmg = dmg;
		this.angle = angle;
		this.v = 16;
		this.currentTarget = currentTarget;
		this.parentGP = parentGP;
		this.detonationTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				detonate();
				
			}
		});
		detonationTimer.setRepeats(false);
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}
	public boolean isStuckToTarget() {
		return isStuckToTarget;
	}
	public boolean isDetonated() {
		return isDetonated;
	}
	
	public void setBlinkeIntervall(int blinkeIntervall) {
		this.blinkeIntervall = blinkeIntervall;
	}
	
	public void move() {
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;	
		rectHitbox = new Rectangle((int)(x-rectHitbox.getWidth()/2),(int)(y-rectHitbox.getHeight()/2),(int)rectHitbox.getWidth(),(int)rectHitbox.getHeight());
		
	}
	// draws the projectile
	public void drawDetonatorProjectile(Graphics2D g2d) {
		if(isStuckToTarget && !isColorBlink) {
			g2d.setColor(cBlink);
		}else {
			g2d.setColor(c);
		}
		rectHitbox.setBounds((int)(x-rectHitbox.getWidth()/2),(int)(y-rectHitbox.getHeight()/2),(int)rectHitbox.getWidth(),(int)rectHitbox.getHeight());
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(rect);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
		
		if(isStuckToTarget) {
			drawTTD(g2d);
		}
	}
	
	public void updateBlink() {
		blinkCounter++;
		if(blinkCounter>blinkeIntervall && !isColorBlink) {
			isColorBlink = true;
			blinkCounter = 0;
		}else if(blinkCounter>blinkeIntervall && isColorBlink){
			isColorBlink = false;
			blinkCounter = 0;
		}
	}
	// draws the turns it takes till the Bomb will Detonate
	public void drawTTD(Graphics2D g2d) {
		if(turnsTillDetonation > 0) {
			g2d.setColor(Color.WHITE);
		}
		g2d.setFont(new Font("Arial",Font.PLAIN,25));
		g2d.drawString(turnsTillDetonation+"", (int)x -5, (int)y -20);
	}
	// creates the explosion and damages the target
	public void detonate() {
		if(!isDetonated) {
			detExplosion = new Explosion((float)x, (float)y,1.5f,(float)angle);
			isDetonated = true;
			currentTarget.getDamaged(dmg,parentGP.getCommanderGamePiece());
		}
	}
	// checks if it has hit an Enemy and will set it to be Stuck (isStuckToTarget = true)
	public void checkHitEnemy() {
		if(this.rectHitbox.intersects(currentTarget.getRectHitbox())) {
			isStuckToTarget = true;
			
			xRelTarget = x - currentTarget.getCenterX();
			yRelTarget = y - currentTarget.getCenterY();
		}
	}
	
	public void stayStuck() {
		x = currentTarget.getCenterX() + xRelTarget;
		y = currentTarget.getCenterY() + yRelTarget;
	}
	// will set the projectile to be destroyed if its explosion has faded
	public void checkIfExplosionFaded() {
		if(detExplosion.checkIfExplosionFaded()) {
			this.isDestroyed = true;
		}
	}
	
}
