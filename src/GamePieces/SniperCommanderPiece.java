package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

import javax.swing.Timer;

import Particles.EmptyShell;
import Particles.SniperTrailParticle;
import Projectiles.Bullet;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class SniperCommanderPiece extends CommanderGamePiece{
	
	Bullet sniperBullet;
	
	public SniperCommanderPiece(boolean isEnemy, BoardRectangle boardRect,CommanderGamePiece commanderGamePiece) {
		super(isEnemy, "SC", boardRect, 10, 5, 2, commanderGamePiece);
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
	}

	@Override
	public void drawAttack(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20,200));
		g2d.fill(aimArc);
	}
	

	public boolean checkMoveRows(int selectedRow, int selectedColumn) {
		int row = boardRect.row;
		int column = boardRect.column;
	
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
		int column = this.boardRect.column;
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
	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(checkAttackRows(selectedRow,selectedColumn) && checkAttackColumns(selectedRow,selectedColumn)) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.row == selectedRow && curBR.column == selectedColumn && !curBR.isGap && !curBR.isWall) {
					
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

	@Override
	public void startAttackDestructibleWall(BoardRectangle targetBoardRectangle) {
		isAttacking = true;
		currentTargetBoardRectangle = targetBoardRectangle;
		attackDelayTimer.start();
		hasExecutedAttack = true;
		hasExecutedMove = true;
		isWallAttack = true;
	}
	
	public void shootOnce() {	
		if(isWallAttack) {
			sniperBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 6, c,1, angle, null, currentTargetBoardRectangle);	
		}else {
			sniperBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 6, c,1, angle, getCurrentTargetGamePiece(),null);	
		}
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(),8,20, (float)angle -90, c,(float)(Math.random()*1+1)));
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,-angle-90,Arc2D.PIE);
		if(sniperBullet != null) {
			for(int i = 0;i<1000;i++) {
				StagePanel.particles.add(new SniperTrailParticle((int)(sniperBullet.getX() + (Math.random()-0.5)*10), (int)(sniperBullet.getY() + (Math.random()-0.5)*10)));
				sniperBullet.move();
				sniperBullet.checkHitEnemy();
				if(sniperBullet.getHasHitEnemy()) {
					sniperBullet = null;
					isAttacking = false;
					getCurrentTargetGamePiece().getDamaged(getDmg(),getCommanderGamePiece());
					break;
				}
			}
		}
	}
}
