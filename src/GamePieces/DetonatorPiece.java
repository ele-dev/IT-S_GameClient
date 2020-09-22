package GamePieces;

import java.awt.Graphics2D;
<<<<<<< Updated upstream
=======
import java.awt.Point;
import java.awt.Shape;
>>>>>>> Stashed changes
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.DetonatorProjectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;


public class DetonatorPiece extends GamePiece{
	ArrayList<DetonatorProjectile> detProjectiles = new ArrayList<DetonatorProjectile>();
	
	public DetonatorPiece(boolean isEnemy,BoardRectangle boardRect) {
		super(isEnemy, Commons.nameDetonator, boardRect, Commons.dmgDetonator,Commons.baseTypeDetonator);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootDetonator();
			}
		}); 
		attackDelayTimer.setRepeats(false);
	}
	
	public void update() {
		if(targetGamePiece != null) {
			updateAngle(targetGamePiece.getPos());
		}else if(targetShield != null){
			updateAngle(targetShield.getPos());
		}else if(targetDestructibleObject != null){
			updateAngle(targetDestructibleObject.getPos());
		}
		if(isMoving) {
			updateMove();
		}
		updateAttack();
	}
	
	public void drawAttack(Graphics2D g2d) {
		for(int i = 0;i<detProjectiles.size();i++) {
			DetonatorProjectile curDP = detProjectiles.get(i);
			
			if(curDP.isDetonated()) {
				curDP.detExplosion.drawParticle(g2d);
			}
			if(!curDP.isDetonated()) {
				curDP.drawProjectile(g2d);	
			}
		}
	}
 

	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(((selectedRow == boardRect.row+2 || selectedRow == boardRect.row-2) && selectedColumn < boardRect.column+2 && selectedColumn > boardRect.column-2) ||
				((selectedColumn == boardRect.column+2 || selectedColumn == boardRect.column-2) && selectedRow <=boardRect.row+2 && selectedRow >= boardRect.row-2)) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.row == selectedRow && curBR.column == selectedColumn && !curBR.isWall) {
					if(checkIfBoardRectangleInSight(curBR)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// creates/shoots the DetonatorProjectile
	public void shootDetonator() {
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetShield != null?targetShield.getShieldCircle():
			targetDestructibleObject.getRectHitbox();
			
		detProjectiles.add(new DetonatorProjectile(getCenterX(), getCenterY(), 10, 20, c, 
				getDmg(), (float)(angle + (Math.random()-0.5)*10), shape,targetGamePiece,targetShield,targetDestructibleObject));
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public void decDetonaterTimers() {
		for(DetonatorProjectile curDP : detProjectiles) {
			if(curDP.turnsTillDetonation>0) {
				curDP.turnsTillDetonation--;
			}
			
			if(curDP.turnsTillDetonation<=0) {
				curDP.detonationTimer.start();
				curDP.setBlinkeIntervall(5);
			}
		}
	}
	
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		for(DetonatorProjectile curDP : detProjectiles) {
			if(curDP.detonationTimer.isRunning() || !curDP.getHasHitTarget()) {
				isAttacking = true;
				return;
			}
		}
		if(!isAttacking) {
			targetGamePiece = null;
		}
	}

	public void updateAttack() { 
		for(int i = 0;i<detProjectiles.size();i++) { 
			DetonatorProjectile curDP = detProjectiles.get(i);
			if(!curDP.getHasHitTarget()) {
				curDP.move();
				curDP.checkHitAnyTarget();
				curDP.checkHitTargetShieldOrDestructibleObject();
			}else {
				if(curDP.getTargetGamePiece() != null) {
					curDP.stayStuck();
					curDP.updateBlink();
				}
			}
			if(curDP.isDetonated()) {
				curDP.detExplosion.updateExplosion();
				curDP.detExplosion.moveAllFrags();
				curDP.checkIfExplosionFaded();
			}
			
			if(curDP.getIsDestroyed()) {
				detProjectiles.remove(i);
			}
		}
		updateIsAttacking();
		
	}
}
