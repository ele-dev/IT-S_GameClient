package GamePieces;

import java.awt.Graphics2D;
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
	
	public DetonatorPiece(boolean isEnemy,BoardRectangle boardRect, CommanderGamePiece commanderGamePiece) {
		super(isEnemy, Commons.nameDetonator, boardRect, Commons.maxHealthDetonator, Commons.dmgDetonator, commanderGamePiece);
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
		for(int i = 0;i<detProjectiles.size();i++) {
			DetonatorProjectile curDP = detProjectiles.get(i);
			
			if(curDP.isDetonated) {
				curDP.detExplosion.drawParticle(g2d);
			}
			if(!curDP.isDetonated) {
				curDP.drawDetonatorProjectile(g2d);	
			}
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
		if(checkAttackRows(selectedRow,selectedColumn) || checkAttackColumns(selectedRow,selectedColumn)) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.posRow == selectedRow && curBR.posColumn == selectedColumn && !curBR.isGap && !curBR.isWall) {
					if(curBR.isDestructibleWall) {
						return false;
					}
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
		
		if(row+2==selectedRow) {
			for(int i = -2;i<3;i++) {
				if(column + i == selectedColumn) {
					return true;
				}
			}
		}
		if(row-2==selectedRow) {
			for(int i = -2;i<3;i++) {
				if(column + i == selectedColumn) {
					return true;
				}
			}
		}
		return false;
	}


	public boolean checkAttackColumns(int selectedRow, int selectedColumn) {
		int column = this.boardRect.posColumn;
		int row = this.boardRect.posRow;
		if(column+2==selectedColumn) {
			for(int i = -2;i<3;i++) {
				if(row + i == selectedRow) {
					return true;
				}
			}
		}
		if(column-2==selectedColumn) {
			for(int i = -2;i<3;i++) {
				if(row + i == selectedRow) {
					return true;
				}
			}
		}
		return false;
	}
	// creates/shoots the DetonatorProjectile
	public void shootDetonator() {
		detProjectiles.add(new DetonatorProjectile(getCenterX(), getCenterY(), 10, 20, c, getDmg(), angle + (Math.random()-0.5)*10, getCurrentTargetGamePiece(),this));
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public void decDetonaterTimers() {
		for(DetonatorProjectile curDP : detProjectiles) {
			if(curDP.turnsTillDetonation>0) {
				curDP.turnsTillDetonation--;
			}
			
			if(curDP.turnsTillDetonation<=0) {
				curDP.detonationTimer.start();
				curDP.blinkeIntervall = 5;
			}
		}
		
	}
	
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
		}
		for(DetonatorProjectile curDP : detProjectiles) {
			if(curDP.detonationTimer.isRunning()) {
				isAttacking = true;
			}
		}
		for(DetonatorProjectile curDP : detProjectiles) {
			if(!curDP.isStuckToTarget) {
				isAttacking = true;
			}
		}
	}

	@Override
	public void startAttackDestructibleWall(BoardRectangle targetBoardRectangle) {
		
	}

	public void updateAttack() {
		for(int i = 0;i<detProjectiles.size();i++) {
			DetonatorProjectile curDP = detProjectiles.get(i);
			if(!curDP.isStuckToTarget) {
				curDP.move();
				curDP.checkHitEnemy();
			}else {
				curDP.stayStuck();
				curDP.updateBlink();
			}
			if(curDP.isDetonated) {
				curDP.detExplosion.updateExplosion();
				curDP.detExplosion.moveAllFrags();
				curDP.checkIfExplosionFaded();
			}
			
			if(curDP.isDestroyed) {
				detProjectiles.remove(i);
			}
		}
		updateIsAttacking();
		
	}
}
