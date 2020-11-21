package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import Environment.DestructibleObject;
import GamePieces.GamePiece;
import Particles.TrailParticle;
import Stage.Commons;
import Stage.StagePanel;
import Stage.TurnCountDown;



public class EMPProjectile extends Projectile{
	private float xRelTarget,yRelTarget;
	private GamePiece targetGamePiece;
	private DestructibleObject targetDestructibleObject;
	private TurnCountDown destroyCountDown;
	
	private int particleSpawnIntervall = 50, particleSpawnCounter = 0;
	
	public static float rotationDelay = 3;
	private int autoStickCounter = 500;
	public EMPProjectile(int x, int y, int w, int h, Color c, float dmg, float angle, Shape targetShape, 
			GamePiece targetGamePiece, DestructibleObject targetDestructibleObject) {
		super(x, y, w, h, c, angle, 0, 0.3f, targetShape, targetDestructibleObject);
		shapeShow = new Rectangle(-w/2, -h/2, w, h);
		this.targetGamePiece = targetGamePiece;
		this.targetDestructibleObject = targetDestructibleObject;
		destroyCountDown = new TurnCountDown(2, c);
	}
	public GamePiece getTargetGamePiece() {
		return targetGamePiece;
	}
	public TurnCountDown getDestroyCountDown() {
		return destroyCountDown;
	}
	// draws the projectile
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(c); 
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
		if(hasHitTarget) destroyCountDown.drawCountDown(g2d, (int)x, (int)y-30);
	}
	 
	public void update() {
		if(targetGamePiece != null) {
			targetGamePiece.setHasExecutedMove(true);
			targetGamePiece.actionSelectionPanel.setMoveButtonActive(false);
		}	
		particleSpawnCounter--;
		if(particleSpawnCounter <= 0) {
			particleSpawnCounter = particleSpawnIntervall;
			for(int i = 0; i < 10; i++) {
				Color cTP =  new Color(58, 100+(int)(Math.random()*130), 140+(int)(Math.random()*30));
				StagePanel.particles.add(new TrailParticle((int)x, (int)y,(int) (Math.random()*5+5), (float) ((Math.random()-0.5)*60)+ angle + 90, cTP,
						(float) (Math.random()*0.5)+0.5f, (float) (Math.random()*2+4), 0));
			}
		}
	}
	
	public void homeInOnTarget() {
		float ak = 0;
		float gk = 0;
		Point targetPoint = null;
		if(targetGamePiece != null) {
			targetPoint = targetGamePiece.getPos();
		}else if(targetDestructibleObject != null){
			targetPoint = targetDestructibleObject.getPos();
		}
		ak = targetPoint.x - x;
		gk = targetPoint.y - y;
		
		float angleDesired = (float) Math.toDegrees(Math.atan2(ak * -1, gk));
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
		
		autoStickCounter--;
		if(autoStickCounter <= 0){
			x = targetPoint.x;
			y = targetPoint.y;
			rectHitbox.setBounds((int)(x-rectHitbox.width/2), (int)(y-rectHitbox.height/2), rectHitbox.width, rectHitbox.height);
			hasHitTarget = true;
		}
	}
	
	public void destroy() {
		isDestroyed = true;
	}
	// checks if it has hit an Enemy and will set it to be Stuck (isStuckToTarget = true)
	public void checkHitEnemy() {
		if(targetGamePiece != null && rectHitbox.intersects(targetGamePiece.getRectHitbox())) {
			hasHitTarget = true;
			xRelTarget = x - targetGamePiece.getCenterX();
			yRelTarget = y - targetGamePiece.getCenterY();
		}
	}

	public void checkHitDestructibleObject() {
		if((targetDestructibleObject != null) && targetShape.intersects(rectHitbox)) {
			isDestroyed = true;
			targetDestructibleObject.getDamaged(2, angle, true);
		}
	}
	public void stayStuck() {
		x = targetGamePiece.getCenterX() + xRelTarget; 
		y = targetGamePiece.getCenterY() + yRelTarget;
	}
	
}
