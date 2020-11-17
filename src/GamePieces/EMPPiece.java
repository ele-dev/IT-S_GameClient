package GamePieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.EMPProjectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;


public class EMPPiece extends GamePiece{
	ArrayList<EMPProjectile> empProjectiles = new ArrayList<EMPProjectile>();
	
	public EMPPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameEMP, boardRect, Commons.dmgEMP, Commons.baseTypeEMP,Commons.neededLOSEMP);
		
		attackDelayTimer = new Timer(1500, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootEMP();
			} 
		}); 
		attackDelayTimer.setRepeats(false);
	}
	
	@Override
	public boolean isAttacking() {
		for(EMPProjectile curEMPP : empProjectiles) {
			if(!curEMPP.hasHitTarget()) {
				return true;
			}
		}
		return attackDelayTimer.isRunning();
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
		Rectangle rect1 = new Rectangle(myColumn-1,myRow-1,3,3);
		Rectangle rect2 = new Rectangle(myColumn-2,myRow-2,5,5);
		return !rect1.contains(new Point(selectedColumn,selectedRow)) && rect2.contains(new Point(selectedColumn,selectedRow));
	}

	// creates/shoots the DetonatorProjectile
	public void shootEMP() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
		Rectangle targetRect = (Rectangle) shape;
		float angleDesiredProjectile = calculateAngle((int)(targetRect.getCenterX()+(Math.random()-0.5)*targetRect.width), (int)(targetRect.getCenterY()+(Math.random()-0.5)*targetRect.height));	
		empProjectiles.add(new EMPProjectile(getCenterX(), getCenterY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, c, getDmg(), 
				angleDesiredProjectile, shape,targetGamePiece,targetDestructibleObject));
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

	}

	public void updateAttack() { 
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
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
