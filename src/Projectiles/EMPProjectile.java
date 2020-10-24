package Projectiles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import Abilities.RadialShield;
import Environment.DestructibleObject;
import GamePieces.GamePiece;
import Particles.Explosion;
import Particles.TazerTrailParticle;
import Stage.StagePanel;



public class EMPProjectile extends Projectile{
	private float xRelTarget,yRelTarget;
	public int turnsTillDestruction = 2;
	private GamePiece targetGamePiece;
	private DestructibleObject targetDestructibleObject;
	
	private int particleSpawnIntervall = 50,particleSpawnCounter = 0;
	
	public EMPProjectile(int x, int y, int w, int h, Color c,float dmg,float angle,Shape targetShape
			,GamePiece targetGamePiece,DestructibleObject targetDestructibleObject) {
		super(x, y, w, h, c, angle, 16, 0, targetShape, targetDestructibleObject);
		shapeShow = new Rectangle(-w/2,-h/2,w,h);
		this.targetGamePiece = targetGamePiece;
		this.targetDestructibleObject = targetDestructibleObject;
	}
	
	public GamePiece getTargetGamePiece() {
		return targetGamePiece;
	}
	// draws the projectile
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(c); 
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
		if(hasHitTarget) {
			drawTTD(g2d);
		}
	}
	 
	public void update() {
		if(targetGamePiece != null) {
			targetGamePiece.setHasExecutedMove(true);
			targetGamePiece.actionSelectionPanel.setMoveButtonActive(false);
		}	
		particleSpawnCounter--;
		if(particleSpawnCounter <= 0) {
			particleSpawnCounter = particleSpawnIntervall;
			for(int i = 0;i<10;i++) {
				StagePanel.particles.add(new TazerTrailParticle((int)x, (int)y,(float) (Math.random()*0.5)+0.5f,
						(float) (Math.random()*2+4),(float) ((Math.random()-0.5)*60)+angle+90));
			}
		}
	}
	
	public void destroy() {
		isDestroyed = true;
	}
	// draws the turns it takes till the Bomb will Detonate
	public void drawTTD(Graphics2D g2d) {
		if(turnsTillDestruction > 0) {
			g2d.setColor(Color.WHITE);
		}
		g2d.setFont(new Font("Arial",Font.PLAIN,25));
		g2d.drawString(turnsTillDestruction+"", (int)x -5, (int)y -20);
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
			isDestroyed = true;
		}
	}
	public void stayStuck() {
		x = targetGamePiece.getCenterX() + xRelTarget; 
		y = targetGamePiece.getCenterY() + yRelTarget;
	}
	
}
