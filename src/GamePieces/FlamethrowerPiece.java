package GamePieces;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.FlameThrowerFlame;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class FlamethrowerPiece extends GamePiece {
	
	ArrayList<FlameThrowerFlame> flames = new ArrayList<FlameThrowerFlame>();
	int burstCounter;
	Timer burstTimer;
	int burstBulletAmount = 200;
	
	double spreadAngle = 20;
	
	public FlamethrowerPiece(boolean isEnemy, BoardRectangle boardRect) {
		super(isEnemy, Commons.nameFlameThrower, boardRect, Commons.dmgFlameThrower,Commons.baseTypeFlameThrower);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootBurst();
			} 
		});
		attackDelayTimer.setRepeats(false);
		
		burstTimer = new Timer(10, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shootOnce();
			}
		}); 
		burstTimer.setRepeats(false);
	}
	

	public void drawAttack(Graphics2D g2d) {
		for(FlameThrowerFlame curFTF : flames) {
				curFTF.drawProjectile(g2d);
		}
	}

	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.row == selectedRow && curBR.column == selectedColumn && !curBR.isWall) {
					int dist = BoardRectangle.getDistanceBetweenBRs(boardRect, curBR);
					if(dist <= 2 && !curBR.isWall) {
						if(checkIfBoardRectangleInSight(curBR)) {
							return true;
						}
					}
				}
			}
		return false;
	}

	
	public void shootBurst() {
		burstTimer.start();
	}
	
	public void shootOnce() {
		startedAttack = true;
		burstCounter++;
		if(burstCounter <burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		int randomSize = (int)(Math.random() * 5 +5);
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
			
		flames.add(new FlameThrowerFlame(getCenterX(), getCenterY(), randomSize, randomSize, 
				(float)Math.random()+2, (float)(angle + (Math.random()-0.5)*spreadAngle), shape,targetDestructibleObject));
		
			
		
	}
	// checks if the GamePiece is attacking and sets it (isAttacking = true)
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		if(burstTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		for(FlameThrowerFlame curFTF : flames) {
			if(!curFTF.hasHitEnemy) {
				isAttacking = true;
				return;
			}
		} 
	}
	// damages the Target only if all flames have hit it or are faded
	public void showWhenDmg() {
		if(startedAttack) {
			boolean allHaveHit = true;
			for(FlameThrowerFlame curFTF : flames) {
				if(!curFTF.getHasHitTarget()) {
					allHaveHit = false;
				}
			}
			if(allHaveHit) {
				if(targetGamePiece != null) {
					targetGamePiece.gamePieceBase.getDamaged(getDmg());
					targetGamePiece = null;
				}else {
					targetDestructibleObject.getDamaged(getDmg(),angle,getIsEnemy());
					targetDestructibleObject = null;
				}
				startedAttack = false;
			}
		}
		
			
	}
	
	// updates the attack (moves flames,checks if they hit something and so forth)
	public void updateAttack() {
		for(int i = 0;i<flames.size();i++) {
			FlameThrowerFlame curFTF = flames.get(i);
			curFTF.move();
			curFTF.checkHitAnyTarget();
			curFTF.updateFade();
			if(curFTF.getColor().getAlpha()<10) {
			flames.remove(i);
			}
		}
		updateIsAttacking();
		showWhenDmg();
	}
}
