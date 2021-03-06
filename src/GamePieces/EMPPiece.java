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

import Environment.DestructibleObject;
import Projectiles.EMPProjectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;


public class EMPPiece extends GamePiece{
	private static ArrayList<EMPProjectile> empProjectiles = new ArrayList<EMPProjectile>();
	private static float spreadAngle = 120;
	
	private static DestructibleObject lastTargetDestructibleObject;
	
	public EMPPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameEMP, boardRect, Commons.dmgEMP, 0,Commons.neededLOSEMP);
		
		attackDelayTimer = new Timer(1500, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				lastTargetDestructibleObject = targetDestructibleObject;
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
	
	public void drawAttack(Graphics2D g2d) {}
 
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect1 = new Rectangle(myColumn-1,myRow-1,3,3);
		Rectangle rect2 = new Rectangle(myColumn-2,myRow-2,5,5);
		return !rect1.contains(new Point(selectedColumn,selectedRow)) && rect2.contains(new Point(selectedColumn,selectedRow));
	}

	// creates/shoots the DetonatorProjectile
	@SuppressWarnings("unused")
	public void shootEMP() {
		Arc2D aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
		empProjectiles.add(new EMPProjectile(getCenterX(), getCenterY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, c, 
				angle+(float)((Math.random()-0.5)* spreadAngle), shape,targetGamePiece,targetDestructibleObject,getDmg(),isRed()));
		targetDestructibleObject = null;
		targetGamePiece = null;
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public static void decEMPTimers() {
		for(EMPProjectile curEMPP : empProjectiles) {
			curEMPP.getDestroyCountDown().countDownOne();
			if(curEMPP.getDestroyCountDown().getCounter() <= 0) {
				curEMPP.destroy();
			}
		}
	}
	
	public static void drawEMPProjectiles(Graphics2D g2d) {
		for(EMPProjectile curEMPP : empProjectiles) {
			curEMPP.drawProjectile(g2d);	
		}
	}

	public static void updateEMPProjectiles() {
		for(int i = 0; i < empProjectiles.size(); i++) { 
			EMPProjectile curEMPP = empProjectiles.get(i);
			curEMPP.update();
			if(!curEMPP.hasHitTarget()) {
				curEMPP.homeInOnTarget();
				curEMPP.move();
				curEMPP.checkHitEnemy();
				curEMPP.checkHitDestructibleObject();
				if(curEMPP.hasHitTarget()) {
					if(curEMPP.getTargetGamePiece() != null) {
						curEMPP.getTargetGamePiece().gamePieceBase.getDamaged(curEMPP.getDmg());
					} else {
						lastTargetDestructibleObject.getDamaged(curEMPP.getDmg(), curEMPP.angle, curEMPP.isRed());
						lastTargetDestructibleObject = null;
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
	}	

	public void updateAttack() { }
}
