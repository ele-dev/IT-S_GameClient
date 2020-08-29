package GamePieces;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.EmptyShell;
import Projectiles.Bullet;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;


public class GunnerPiece extends GamePiece {
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	int burstCounter;
	Timer burstTimer;
	int burstBulletAmount = 16;
	
	double spreadAngle = 10;
	boolean startedAttack = false;
	
	public GunnerPiece(boolean isEnemy, BoardRectangle boardRect,CommanderGamePiece commanderGamePiece) {
		super(isEnemy, Commons.nameGunner, boardRect, Commons.dmgGunner,Commons.baseTypeGunner,commanderGamePiece);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootBurst();
			}
		});
		attackDelayTimer.setRepeats(false);
		
		burstTimer = new Timer(50, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shootOnce();
			}
		});
		burstTimer.setRepeats(false);
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Turrets/Minigun.png");
		spriteTurret = new Sprite(spriteLinks, Commons.boardRectSize,Commons.boardRectSize, 0);
		
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,0,Arc2D.PIE);
	}
	//draws every bullet in the bullets Array
	public void drawAttack(Graphics2D g2d) {
		for(Bullet curB:bullets) {
			curB.drawBullet(g2d);
		}
	}

	// updates the attack (moves bullets,checks if they hit something and so forth)
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,-angle-90,Arc2D.PIE);
		
		for(int i = 0;i<bullets.size();i++) {
			Bullet curB = bullets.get(i);
			
			curB.move();
			curB.checkHitEnemy();
			
			if(curB.getIsDestroyed()) {
				bullets.remove(i);
			}
		}
		updateIsAttacking();
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
		for(int i = 0;i<3;i++) {
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
		for(int i = 0;i<3;i++) {
			if(column+i==selectedColumn) {
				return true;
			}
			if(column-i==selectedColumn) {
				return true;
			}
		}
		return false;
	}
	
	// starts an attack against a destructible wall
	public void startAttackDestructibleWall(BoardRectangle targetBoardRectangle) {
		isAttacking = true;
		currentTargetBoardRectangle = targetBoardRectangle;
		attackDelayTimer.start();
		hasExecutedAttack = true;
		hasExecutedMove = true;
		isWallAttack = true;
	}
	// starts burstTimer
	public void shootBurst() {
		burstTimer.start();
	}
	// shoots one shot every timer the burstTimer activates and starts the burstTimer again if it still has shots left to shoot
	// shots are counted by burstCounter (stops shooting if burstCounter >= burstBulletAmount)
	public void shootOnce() {
		startedAttack = true;
		if(isWallAttack) {
			bullets.add(new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 20, c,16, (float) (angle + (Math.random()-0.5)*spreadAngle), null, currentTargetBoardRectangle));	
		}else {
			bullets.add(new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 20, c,16, (float) (angle + (Math.random()-0.5)*spreadAngle), getCurrentTargetGamePiece(),null));	
		}
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(),8,12, (float)angle -90, c,(float)(Math.random()*2+3)));
		
		burstCounter++;
		if(burstCounter < burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
	}
	
	
	// updates the isAttacking state
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
		}
		if(burstCounter > 0 || bullets.size() >0) {
			isAttacking = true;
		}
		if(!isAttacking && isWallAttack) {
			isWallAttack = false;
			currentTargetBoardRectangle.isDestructibleWall = false;
			return;
		}
		if(!isAttacking && startedAttack && getCurrentTargetGamePiece() != null) {
			getCurrentTargetGamePiece().getDamaged(getDmg(),getCommanderGamePiece());
			startedAttack = false;
		}
	}
	

}
