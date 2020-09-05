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
		if(checkAttackRows(selectedRow,selectedColumn) || checkAttackColumns(selectedRow,selectedColumn)) {
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
		int column = this.boardRect.column;
		int row = this.boardRect.row;
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
		detProjectiles.add(new DetonatorProjectile(getCenterX(), getCenterY(), 10, 20, c, 
				getDmg(), (float)(angle + (Math.random()-0.5)*10), getCurrentTargetGamePiece(),currenTargetShield));
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
			currentTargetGamePiece = null;
		}
	}

	public void updateAttack() {
		for(int i = 0;i<detProjectiles.size();i++) {
			DetonatorProjectile curDP = detProjectiles.get(i);
			if(!curDP.getHasHitTarget()) {
				curDP.move();
				curDP.checkHitEnemy();
				curDP.checkHitTargetShield();
			}else {
				curDP.stayStuck();
				curDP.updateBlink();
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
