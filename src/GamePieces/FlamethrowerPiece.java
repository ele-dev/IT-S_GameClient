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
	
	public FlamethrowerPiece(boolean isEnemy, BoardRectangle boardRect,
			CommanderGamePiece commanderGamePiece) {
		super(isEnemy, Commons.nameFlameThrower, boardRect, Commons.maxHealthFlameThrower, Commons.dmgFlameThrower, commanderGamePiece);
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
				curFTF.drawFlame(g2d);
		}
	}

	public boolean checkMoveRows(int selectedRow, int selectedColumn) {
		int row = this.boardRect.posRow;
		int column = this.boardRect.posColumn;
		
		if(row == selectedRow && column == selectedColumn) {
			return false;
		}
		if(row+1==selectedRow) {
			return true;
		}
		if(row-1==selectedRow) {
			return true;
		}
		if(row==selectedRow) {
			return true;
		}
		return false;
	}


	public boolean checkMoveColumns(int selectedRow, int selectedColumn) {
		int column = this.boardRect.posColumn;
		if(column+1==selectedColumn) {
			return true;
		}
		if(column-1==selectedColumn) {
			return true;
		}
		if(column==selectedColumn) {
			return true;
		}
		return false;
	}


	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(checkAttackRows(selectedRow,selectedColumn) && checkAttackColumns(selectedRow,selectedColumn)) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.posRow == selectedRow && curBR.posColumn == selectedColumn && !curBR.isGap && !curBR.isWall) {
					
					if(checkIfBoardRectangleInSight(curBR)) {
						return true;
					}
				}
			}
		}
		return false;
	}


	public boolean checkAttackRows(int selectedRow, int selectedColumn) {
		int row = this.boardRect.posRow;
		int column = this.boardRect.posColumn;
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
		int row = this.boardRect.posRow;
		int column = this.boardRect.posColumn;
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
		burstCounter++;
		if(burstCounter <burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		int randomSize = (int)(Math.random() * 5 +5);
		if(isWallAttack) {
			flames.add(new FlameThrowerFlame(getCenterX(), getCenterY(), randomSize, randomSize, Math.random()+3, getDmg(), angle + (Math.random()-0.5)*spreadAngle, null , currentTargetBoardRectangle));
		}else {
			flames.add(new FlameThrowerFlame(getCenterX(), getCenterY(), randomSize, randomSize, Math.random()+3, getDmg(), angle + (Math.random()-0.5)*spreadAngle, getCurrentTargetGamePiece() , null));
		}
			
		startedAttack = true;
	}
	// checks if the GamePiece is attacking and sets it (isAttacking = true)
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
		}
		if(burstTimer.isRunning()) {
			isAttacking = true;
		}
		for(FlameThrowerFlame curFTF : flames) {
			if(!curFTF.hasHitEnemy) {
				isAttacking = true;
			}
		}
	}
	// damages the Target only if all flames have hit it or are faded
	public void showWhenDmg() {
		boolean allHaveHit = true;
		for(FlameThrowerFlame curFTF : flames) {
			if(!curFTF.hasHitEnemy) {
				allHaveHit = false;
			}
		}
		if(allHaveHit && startedAttack && !isWallAttack) {
			getCurrentTargetGamePiece().getDamaged(getDmg(),getCommanderGamePiece());
			startedAttack = false;
		}
		if(allHaveHit && startedAttack && isWallAttack) {
			startedAttack = false;
			currentTargetBoardRectangle.isDestructibleWall = false;
		}	
	}
	
	@Override
	public void startAttackDestructibleWall(BoardRectangle targetBoardRectangle) {
		currentTargetBoardRectangle = targetBoardRectangle;
		isAttacking = true;
		updateAngle(true);
		attackDelayTimer.start();
		hasExecutedAttack = true;
		hasExecutedMove = true;
		isWallAttack = true;
		
	}
	// updates the attack (moves flames,checks if they hit something and so forth)
	public void updateAttack() {
		for(int i = 0;i<flames.size();i++) {
			FlameThrowerFlame curFTF = flames.get(i);
			curFTF.move();
			curFTF.checkHitEnemy();
			curFTF.updateFade();
			if(curFTF.c.getAlpha()<10) {
			flames.remove(i);
			}
		}
		updateIsAttacking();
		showWhenDmg();
		
	}
}
