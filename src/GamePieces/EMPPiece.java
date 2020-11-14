package GamePieces;

import java.awt.Graphics2D;

import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.EMPProjectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;


public class EMPPiece extends GamePiece{
	ArrayList<EMPProjectile> empProjectiles = new ArrayList<EMPProjectile>();
	
	public EMPPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameEMP, boardRect, Commons.dmgEMP, Commons.baseTypeEMP);
		
		attackDelayTimer = new Timer(1500, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootEMP();
			} 
		}); 
		attackDelayTimer.setRepeats(false);
	}
	
	public void drawAttack(Graphics2D g2d) {
		for(EMPProjectile curEMPP : empProjectiles) {
			if(!curEMPP.isDestroyed()) {
				curEMPP.drawProjectile(g2d);	
			}
		}
	}
 
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		if(((selectedRow == myRow + 2 || selectedRow == myRow - 2) && selectedColumn < myColumn + 2 && selectedColumn > myColumn - 2) ||
				((selectedColumn == myColumn + 2 || selectedColumn == myColumn - 2) && selectedRow <= myRow + 2 && selectedRow >= myRow - 2)) {
			return true;
		}
		return false;
	}

	// creates/shoots the DetonatorProjectile
	public void shootEMP() {
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
			
		empProjectiles.add(new EMPProjectile(getCenterX(), getCenterY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, c, getDmg(), 
				(float)(angle + (Math.random()-0.5)*10), shape,targetGamePiece,targetDestructibleObject));
		targetDestructibleObject = null;
		targetGamePiece = null;
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public void decEMPTimers() {
		for(EMPProjectile curEMPP : empProjectiles) {
			curEMPP.getDestroyCountDown().countDownOne();
			if(curEMPP.getDestroyCountDown().getCounter() <= 0) {
				curEMPP.destroy();
			}
		}
	}
	
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		for(EMPProjectile curEMPP : empProjectiles) {
			if(!curEMPP.hasHitTarget()) {
				isAttacking = true;
				return;
			}
		}
	}

	public void updateAttack() { 
		for(int i = 0; i < empProjectiles.size(); i++) { 
			EMPProjectile curEMPP = empProjectiles.get(i);
			curEMPP.update();
			if(!curEMPP.hasHitTarget()) {
				curEMPP.move();
				curEMPP.checkHitEnemy();
				curEMPP.checkHitDestructibleObject();
				if(curEMPP.hasHitTarget()) {
					if(curEMPP.getTargetGamePiece() != null) {
						curEMPP.getTargetGamePiece().gamePieceBase.getDamaged(getDmg());
					} else {
						targetDestructibleObject.getDamaged(getDmg(), angle, isRed());
						targetDestructibleObject = null;
					}
				}
			} else {
				if(curEMPP.getTargetGamePiece() != null) {
					curEMPP.stayStuck();
				}
			}
			
			if(curEMPP.isDestroyed()) {
				empProjectiles.remove(i);
			}
		}
		updateIsAttacking();
	}
}
