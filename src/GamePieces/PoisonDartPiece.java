package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.Explosion;
import Projectiles.Bullet;
import Projectiles.DetonatorProjectile;
import Projectiles.PoisonDart;
import Stage.BoardRectangle;
import Stage.Commons;


public class PoisonDartPiece extends GamePiece{
	ArrayList<PoisonDart> poisonDarts = new ArrayList<PoisonDart>();
	
	public PoisonDartPiece(boolean isEnemy, String name, BoardRectangle boardRect,ArrayList<BoardRectangle> boardRectangles, int playingBoardSize,CommanderGamePiece commanderGamePiece) {
		super(isEnemy, name, boardRect,boardRectangles, 10, Commons.dmgPoisonDart, playingBoardSize,commanderGamePiece);
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
		for(int i = 0;i<poisonDarts.size();i++) {
			PoisonDart curPD = poisonDarts.get(i);
		
			curPD.drawPoisonDart(g2d);
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
			for(BoardRectangle curBR : boardRectangles) {
				
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
		poisonDarts.add(new PoisonDart(cx, cy, 8, 24, c, dmg, angle + (Math.random()-0.5)*10, currentTarget,this));
		
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public void decPoisonTimers() {
		for(PoisonDart curPD : poisonDarts) {
			if(curPD.turnsTillFade>0) {
				curPD.turnsTillFade--;
				curPD.currentTarget.getDamaged(dmg,commanderGamePiece);
			}
			
			if(curPD.turnsTillFade<=0) {
				curPD.fadeAway();
			}
		}
		
	}
	
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
		}
		for(PoisonDart curPD : poisonDarts) {
			if(!curPD.isStuckToTarget) {
				isAttacking = true;
			}
		}
		
	}

	@Override
	public void startAttackDestructibleWall(BoardRectangle targetBoardRectangle) {
		
		
	}

	public void updateAttack() {
		for(int i = 0;i<poisonDarts.size();i++) {
			PoisonDart curPD = poisonDarts.get(i);
			if(!curPD.isStuckToTarget) {
				curPD.move();
				curPD.checkHitEnemy();
			}else {
				
				curPD.stayStuck();
				if(curPD.currentTarget.isDead) {
					curPD.isDestroyed = true;
				}
				curPD.poisonCloudCounter++;
				if(curPD.poisonCloudCounter >= curPD.poisonCloudSpeed) {
					curPD.addPoisonParticleCloud();
					curPD.poisonCloudCounter = 0;
				}
				
				curPD.updateAllPoisonParticleClouds();
			}
			
			if(curPD.isDestroyed) {
				poisonDarts.remove(i);
			}
		}
		updateIsAttacking();
		
	}
}
