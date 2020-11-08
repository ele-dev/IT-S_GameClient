package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Environment.DestructibleObject;
import GamePieces.GamePiece;
import Particles.Explosion;
import Stage.TurnCountDown;

public class DetonatorProjectile extends Projectile {
	
	private float xRelTarget,yRelTarget;
	private float dmg;
	private boolean isRed;
	
	public Timer detonationTimer;
	public Explosion detExplosion;
	private boolean isDetonated = false;
	
	private Color cBlink;
	private int blinkCounter = 0;
	private int blinkeIntervall = 30;
	private boolean isColorBlink = false;
	
	private GamePiece targetGamePiece;
	private DestructibleObject targetDestructibleObject;
	
	private TurnCountDown detonationCountDown;
	public DetonatorProjectile(int x, int y, int w, int h, boolean isRed, float dmg, float angle, Shape targetShape
			, GamePiece targetGamePiece, DestructibleObject targetDestructibleObject) {
		super(x, y, w, h, isRed ? Color.RED : Color.BLUE, angle, 16, 0, targetShape, targetDestructibleObject);
		shapeShow = new Rectangle(-w/2,-h/2,w,h);
		cBlink = Color.BLACK;
		this.isRed = isRed;
		this.dmg = dmg;
		detonationTimer = new Timer(1500, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				detonate();
			}
		});
		detonationTimer.setRepeats(false);
		this.targetGamePiece = targetGamePiece;
		this.targetDestructibleObject = targetDestructibleObject;
		
		detonationCountDown = new TurnCountDown(2, c);
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
	
	public TurnCountDown getDetonationCountDown() {
		return detonationCountDown;
	}
	// draws the projectile
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(hasHitTarget && !isColorBlink ? cBlink : c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
		
		if(hasHitTarget) {
			detonationCountDown.drawCountDown(g2d, (int)x, (int)y-30);
		}
	}
	
	public void updateBlink() {
		blinkCounter++;
		if(blinkCounter>blinkeIntervall) {
			isColorBlink = !isColorBlink;
			blinkCounter = 0;
		}
	}
	// creates the explosion and damages the target
	public void detonate() {
		if(!isDetonated) {
			detExplosion = new Explosion((float)x, (float)y,1.5f,(float)angle);
			isDetonated = true;
			if(targetGamePiece != null) {
				targetGamePiece.gamePieceBase.getDamaged(dmg);
			}else if(targetDestructibleObject != null) {
				targetDestructibleObject.getDamaged(dmg,angle,isRed);
			}
		}
	}
	// checks if it has hit an Enemy and will set it to be Stuck
	public void checkHitEnemy() {
		if(targetGamePiece != null && rectHitbox.intersects(targetGamePiece.getRectHitbox())) {
			hasHitTarget = true;
			xRelTarget = x - targetGamePiece.getCenterX();
			yRelTarget = y - targetGamePiece.getCenterY();
		}
	}
	public void checkHitDestructibleObject() {
		if((targetDestructibleObject != null) && targetShape.intersects(rectHitbox)) detonate();
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
