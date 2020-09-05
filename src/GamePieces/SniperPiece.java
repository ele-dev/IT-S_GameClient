package GamePieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Abilities.WallMine;
import Particles.EmptyShell;
import Particles.SniperTrailParticle;
import Projectiles.Bullet;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class SniperPiece extends CommanderGamePiece{
	
	Bullet sniperBullet;
	ArrayList<WallMine> wallMines = new ArrayList<WallMine>();
	BoardRectangle targetBoardRectangleNextWallMine;
	Point targetPointNextWallMine;
	float lockedRotationNextWallMine;
	byte rotationIndexNextWallMine;
	
	public SniperPiece(boolean isEnemy, BoardRectangle boardRect) {
		super(isEnemy, "SC", boardRect, 5, 3, 1);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootOnce();
			}
		});
		attackDelayTimer.setRepeats(false);
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,0,Arc2D.PIE);
		
		abilityDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				shootWallMine();
			} 
		});
		abilityDelayTimer.setRepeats(false);
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
		
		isPerformingAbility = false;
		if(abilityDelayTimer.isRunning()) {
			updateAngle(targetPointNextWallMine);
			isPerformingAbility = true;
		}
	}

	@Override
	public void drawAttack(Graphics2D g2d) {
		
	}
	
	@Override
	public void updatePossibleAbilities(BoardRectangle curHoverBoardRectangle) {
		int dist = 0;
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			dist = BoardRectangle.getDistanceBetweenBRs(boardRect, curBR);
			if(dist <= 3 && !curBR.isWall) {
				if(curBR.northBR != null && curBR.northBR.isWall && curBR.row <= boardRect.row) {
					curBR.isShowPossibleAbility = true;
				}else
				if(curBR.southBR != null && curBR.southBR.isWall && curBR.row >= boardRect.row){
					curBR.isShowPossibleAbility = true;
				}else
				if(curBR.eastBR != null && curBR.eastBR.isWall && curBR.column >= boardRect.column){
					curBR.isShowPossibleAbility = true;
				}else
				if(curBR.westBR != null && curBR.westBR.isWall && curBR.column <= boardRect.column){
					curBR.isShowPossibleAbility = true;
				}
			}
		}
		if(curHoverBoardRectangle == null) {
			return;
		}
		if(curHoverBoardRectangle.isShowPossibleAbility) {
			curHoverBoardRectangle.isPossibleAbility = true;
		}
	}
	
	@Override
	public void startAbility(BoardRectangle selectedBoardRectangle) {
		abilityCharge = 0;
		isPerformingAbility = true;
		abilityDelayTimer.start();
		if(selectedBoardRectangle.northBR != null && selectedBoardRectangle.northBR.isWall) {
			targetBoardRectangleNextWallMine = selectedBoardRectangle.northBR;
			targetPointNextWallMine = new Point(targetBoardRectangleNextWallMine.getCenterX(),targetBoardRectangleNextWallMine.getCenterY()+Commons.boardRectSize/2);
			lockedRotationNextWallMine = 180;
			rotationIndexNextWallMine = 0;
		}else 
		if(selectedBoardRectangle.southBR != null && selectedBoardRectangle.southBR.isWall){
			targetBoardRectangleNextWallMine = selectedBoardRectangle.southBR;
			targetPointNextWallMine = new Point(targetBoardRectangleNextWallMine.getCenterX(),targetBoardRectangleNextWallMine.getCenterY()-Commons.boardRectSize/2);
			lockedRotationNextWallMine = 0;
			rotationIndexNextWallMine = 1;
		}else 
		if(selectedBoardRectangle.eastBR != null && selectedBoardRectangle.eastBR.isWall){
			lockedRotationNextWallMine = -90;
			targetBoardRectangleNextWallMine = selectedBoardRectangle.eastBR;
			targetPointNextWallMine = new Point(targetBoardRectangleNextWallMine.getCenterX()-Commons.boardRectSize/2,targetBoardRectangleNextWallMine.getCenterY());
			rotationIndexNextWallMine = 2;
		}else 
		{
			lockedRotationNextWallMine = 90;
			targetBoardRectangleNextWallMine = selectedBoardRectangle.westBR;
			targetPointNextWallMine = new Point(targetBoardRectangleNextWallMine.getCenterX()+Commons.boardRectSize/2,targetBoardRectangleNextWallMine.getCenterY());
			rotationIndexNextWallMine = 3;
		}
	}
	
	public void shootWallMine() {
		angle = angleDesired;
		StagePanel.wallMines.add(new WallMine(getCenterX(), getCenterY(), 18, 25, this, angle,
				targetBoardRectangleNextWallMine,targetPointNextWallMine,lockedRotationNextWallMine,rotationIndexNextWallMine));
	}
	
	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
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
		for(int i = 0;i<4;i++) {
			if(row+i==selectedRow) {
				return true;
			}
			if(row-i==selectedRow) {
				return true;
			}
		}
		return false;
	}

	public boolean checkAttackColumns(int selectedRow, int selectedColumn) {
		int column = this.boardRect.column;
		for(int i = 0;i<4;i++) {
			if(column+i==selectedColumn) {
				return true;
			}
			if(column-i==selectedColumn) {
				return true;
			}
		}
		return false;
	}

	
	public void shootOnce() {	
		
		sniperBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 6, c,1, angle, getCurrentTargetGamePiece(),currenTargetShield);	
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(),8,20, (float)angle -90, c,(float)(Math.random()*1+1)));
		StagePanel.applyScreenShake(5, 30);
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,-angle-90,Arc2D.PIE);
		if(sniperBullet != null) { 
			for(int i = 0;i<1000;i++) {
				if(i%2==0) {
					StagePanel.particles.add(new SniperTrailParticle((int)(sniperBullet.getX() + (Math.random()-0.5)*10), (int)(sniperBullet.getY() + (Math.random()-0.5)*10)));
				}
				sniperBullet.move();
				sniperBullet.checkHitEnemy();
				sniperBullet.checkHitTargetShield();
				if(sniperBullet.getHasHitTarget()) {
					break;
				}
			}
		
			sniperBullet = null;
			isAttacking = false;
			if(currentTargetGamePiece != null) {
				getCurrentTargetGamePiece().gamePieceBase.getDamaged(getDmg());
				currentTargetGamePiece = null;
			}else if(currenTargetShield != null){
				currenTargetShield.getDamaged(getDmg());
				currenTargetShield = null;
			}
			
		}
		
	}
}
