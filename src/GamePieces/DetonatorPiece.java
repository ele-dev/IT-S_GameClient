package GamePieces;

import java.awt.Graphics2D;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.DetonatorProjectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class DetonatorPiece extends GamePiece {
	
	ArrayList<DetonatorProjectile> detProjectiles = new ArrayList<DetonatorProjectile>();
	
	public DetonatorPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameDetonator, boardRect, Commons.dmgDetonator, Commons.baseTypeDetonator);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootDetonator();
			} 
		}); 
		attackDelayTimer.setRepeats(false);
	}
	
	public void drawAttack(Graphics2D g2d) {
		for(int i = 0; i < detProjectiles.size(); i++) {
			DetonatorProjectile curDP = detProjectiles.get(i);
			
			if(curDP.isDetonated()) {
				curDP.detExplosion.drawParticle(g2d);
			} 
			if(!curDP.isDetonated()) {
				curDP.drawProjectile(g2d);	
			}
		}
	}
 
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		if(((selectedRow == myRow+2 || selectedRow == myRow-2) && selectedColumn < myColumn+2 && selectedColumn > myColumn-2) ||
				((selectedColumn == myColumn+2 || selectedColumn == myColumn-2) && selectedRow <=myRow+2 && selectedRow >= myRow-2)) {
			return true;
		}
		return false;
	}

	// creates/shoots the DetonatorProjectile
	public void shootDetonator() {
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
			
		detProjectiles.add(new DetonatorProjectile(getCenterX(), getCenterY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, isRed(), 
				getDmg(), (float)(angle + (Math.random()-0.5)*10), shape, targetGamePiece, targetDestructibleObject));
		targetDestructibleObject = null;
		targetGamePiece = null;
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public void decDetonaterTimers() {
		for(DetonatorProjectile curDP : detProjectiles) {
			curDP.getDetonationCountDown().countDownOne();
			
			if(curDP.getDetonationCountDown().getCounter() <= 0) {
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
			if(curDP.detonationTimer.isRunning() || !curDP.hasHitTarget()) {
				isAttacking = true;
				return;
			}
		}
	}

	public void updateAttack() { 
		for(int i = 0; i < detProjectiles.size(); i++) { 
			DetonatorProjectile curDP = detProjectiles.get(i);
			if(!curDP.hasHitTarget()) {
				curDP.move();
				curDP.checkHitEnemy();
				curDP.checkHitDestructibleObject();
			} else {
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
			
			if(curDP.isDestroyed()) {
				detProjectiles.remove(i);
			}
		}
		updateIsAttacking();
	}
}
