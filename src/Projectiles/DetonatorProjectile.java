package Projectiles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Environment.DestructibleObject;
import GamePieces.GamePiece;
import Particles.Explosion;

public class DetonatorProjectile extends Projectile{
	private float xRelTarget,yRelTarget;
	private float dmg;
	
	public Timer detonationTimer;
	public Explosion detExplosion;
	private boolean isDetonated = false;
	public int turnsTillDetonation = 2;
	
	private Color cBlink;
	private int blinkCounter = 0;
	private int blinkeIntervall = 30;
	private boolean isColorBlink = false;
	
	private GamePiece targetGamePiece;
	private DestructibleObject targetDestructibleObject;
	
	
	public DetonatorProjectile(int x, int y, int w, int h, Color c,float dmg,float angle,Shape targetShape
			,GamePiece targetGamePiece,DestructibleObject targetDestructibleObject) {
		super(x, y, w, h, c, angle, 16, 0, targetShape, targetDestructibleObject);
		shapeShow = new Rectangle(-w/2,-h/2,w,h);
		cBlink = Color.BLACK;
		this.dmg = dmg;
		detonationTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				detonate();
				
			}
		});
		detonationTimer.setRepeats(false);
		this.targetGamePiece = targetGamePiece;
		this.targetDestructibleObject = targetDestructibleObject;
	}
	
	public GamePiece getTargetGamePiece() {
		return targetGamePiece;
	}
	
	public boolean isDetonated() {
		return isDetonated;
	}
	public void setBlinkeIntervall(int blinkeIntervall) {
		this.blinkeIntervall = blinkeIntervall;
	}
	// draws the projectile
	public void drawProjectile(Graphics2D g2d) {
		if(hasHitTarget && !isColorBlink) {
			g2d.setColor(cBlink);
		}else {
			g2d.setColor(c);
		}
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
		
		if(hasHitTarget) {
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
			if(targetGamePiece != null) {
				targetGamePiece.gamePieceBase.getDamaged(dmg);
			}else if(targetDestructibleObject != null) {
				targetDestructibleObject.getDamaged(dmg,angle);
			}
			
		}
	}
	// checks if it has hit an Enemy and will set it to be Stuck (isStuckToTarget = true)
	public void checkHitEnemy() {
		if(targetGamePiece != null && rectHitbox.intersects(targetGamePiece.getRectHitbox())) {
			hasHitTarget = true;
			
			xRelTarget = x - targetGamePiece.getCenterX();
			yRelTarget = y - targetGamePiece.getCenterY();
		}
	}
	
	
	public void checkHitTargetShieldOrDestructibleObject() {
		if((targetDestructibleObject != null) && hasHitTarget) {
			detonate();
		}
	}
	public void stayStuck() {
		x = targetGamePiece.getCenterX() + xRelTarget; 
		y = targetGamePiece.getCenterY() + yRelTarget;
	}
	// will set the projectile to be destroyed if its explosion has faded
	public void checkIfExplosionFaded() {
		if(detExplosion.checkIfExplosionFaded()) {
			isDestroyed = true;
		}
	}
	
}
