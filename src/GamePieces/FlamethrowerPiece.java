package GamePieces;

import java.awt.Graphics2D;
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
	boolean startedAttack = false;
	
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
	
	public void update() {
		if(currentTargetGamePiece != null) {
			updateAngle(currentTargetGamePiece.getPos());
		}else if(currenTargetShield != null){
			updateAngle(currenTargetShield.getPos());
		}
		if(isMoving) {
			updateMove();
		}
		updateAttack();
	}

	public void drawAttack(Graphics2D g2d) {
		for(FlameThrowerFlame curFTF : flames) {
				curFTF.drawProjectile(g2d);
		}
	}

	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(checkAttackRows(selectedRow,selectedColumn) && checkAttackColumns(selectedRow,selectedColumn)) {
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


	public boolean checkAttackRows(int selectedRow, int selectedColumn) {
		int row = this.boardRect.row;
		int column = this.boardRect.column;
		if(row == selectedRow && column == selectedColumn) {
			return false;
		}
		for(int i = 0;i<2;i++) {
			if(row+i==selectedRow) {
				return true;
			}
			if(row-i==selectedRow) {
				return true;
			}
		}
		if(row+2 == selectedRow && column == selectedColumn) {
			return true;
		}
		if(row-2 == selectedRow && column == selectedColumn) {
			return true;
		}
		return false;
	}


	public boolean checkAttackColumns(int selectedRow, int selectedColumn) {
		int row = this.boardRect.row;
		int column = this.boardRect.column;
		for(int i = 0;i<2;i++) {
			if(column+i==selectedColumn) {
				return true;
			}
			if(column-i==selectedColumn) {
				return true;
			}
		}
		if(column+2 == selectedColumn && row == selectedRow) {
			return true;
		}
		if(column-2 == selectedColumn && row == selectedRow) {
			return true;
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
		
		flames.add(new FlameThrowerFlame(getCenterX(), getCenterY(), randomSize, randomSize, 
				(float)Math.random()+2, (float)(angle + (Math.random()-0.5)*spreadAngle), currentTargetGamePiece,currenTargetShield));
		
			
		
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
				if(currentTargetGamePiece != null) {
					getCurrentTargetGamePiece().gamePieceBase.getDamaged(getDmg());
					currentTargetGamePiece = null;
				}else if(currenTargetShield != null){
					currenTargetShield.getDamaged(getDmg());
					currenTargetShield = null;
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
			curFTF.checkHitEnemy();
			curFTF.checkHitTargetShield();
			curFTF.updateFade();
			if(curFTF.getColor().getAlpha()<10) {
			flames.remove(i);
			}
		}
		updateIsAttacking();
		showWhenDmg();
		
	}
}
