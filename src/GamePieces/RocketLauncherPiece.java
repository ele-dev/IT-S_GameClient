package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.Explosion;
import Projectiles.Rocket;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;


public class RocketLauncherPiece extends GamePiece{
	ArrayList<Rocket> rockets = new ArrayList<Rocket>();
	int burstCounter;
	Timer burstTimer;
	int burstRocketAmount = 8;
	
	double spreadAngle = 120;
	boolean startedAttack = false;
	
	public RocketLauncherPiece(boolean isEnemy, BoardRectangle boardRect,CommanderGamePiece commanderGamePiece) {
		super(isEnemy, Commons.nameRocketLauncher, boardRect, Commons.maxHealthRocketLauncher, Commons.dmgRocketLauncher, commanderGamePiece);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootBurst();
			}
		});
		attackDelayTimer.setRepeats(false);
		
		burstTimer = new Timer(150, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shootOnce();
			}
		});
		burstTimer.setRepeats(false);
		
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Turrets/RocketLauncher.png");
		spriteTurret = new Sprite(spriteLinks, Commons.boardRectSize,Commons.boardRectSize, 0);
		
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/4,boardRect.getCenterY()-Commons.boardRectSize/4,
				Commons.boardRectSize/2,Commons.boardRectSize/2,0,0,Arc2D.PIE);
	}

	public void drawAttack(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20,200));
		g2d.fill(aimArc);
		for(int i = 0;i<rockets.size();i++) {		
			Rocket curR = rockets.get(i);
			curR.drawRocket(g2d);
			curR.drawTrail(g2d);
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
			return true;
		}
		return false;
	}


	public boolean checkAttackRows(int selectedRow, int selectedColumn) {
		int row = this.boardRect.posRow;
		int column = this.boardRect.posColumn;
		
		if(row == selectedRow && column == selectedColumn) {
			return false;
		}
		
		if(row+3==selectedRow) {
			for(int i = -3;i<4;i++) {
				if(column + i == selectedColumn) {
					return true;
				}
			}
		}
		if(row-3==selectedRow) {
			for(int i = -3;i<4;i++) {
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
		if(column+3==selectedColumn) {
			for(int i = -3;i<4;i++) {
				if(row + i == selectedRow) {
					return true;
				}
			}
		}
		if(column-3==selectedColumn) {
			for(int i = -3;i<4;i++) {
				if(row + i == selectedRow) {
					return true;
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
		addRocketInArcFlight();
		burstCounter++;
		if(burstCounter <burstRocketAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		
	}

	// adds rocket to fly in an arc adds specific angle if its target is in a specific area from it 
	public void addRocketInArcFlight() {
		double arcAngleOffset = 0;
		if(angle<=0 && angle>=-180) {
			arcAngleOffset = -90;
		}
		if(angle>=0 && angle<=180) {
			arcAngleOffset = 90;
		}
		if(angle>=135 && angle<=180) {
			arcAngleOffset = 0;
		}
		if(angle<=-135 && angle>= -180) {
			arcAngleOffset = 0;
		}
		if(angle>=-45 && angle<=45) {
			arcAngleOffset = 0;
		}
		if(isWallAttack) {
			rockets.add(new Rocket((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 10, 20, c, angle + (Math.random()-0.5)*spreadAngle + arcAngleOffset, null, currentTargetBoardRectangle));
		}else {
			rockets.add(new Rocket((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 10, 20, c, angle + (Math.random()-0.5)*spreadAngle + arcAngleOffset, getCurrentTargetGamePiece(), null));
		}
		
	}
	// checks if the piece is attacking and sets its boolean isAttacking to true if it is currently attacking and to false if it isn't
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
		}
		if(burstTimer.isRunning()) {
			isAttacking = true;
		}
		if(rockets.size() > 0 && startedAttack) {
			isAttacking = true;
		}
		if(!isAttacking && isWallAttack) {
			isWallAttack = false;
			currentTargetBoardRectangle.isDestructibleWall = false;
		}
		if(!isAttacking && startedAttack) {
			startedAttack = false;
			getCurrentTargetGamePiece().getDamaged(getDmg(),getCommanderGamePiece());
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

	// updates all things that are animated with the attack (for example moves the rockets and updates the explosions)
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/4,boardRect.getCenterY()-Commons.boardRectSize/4,
				Commons.boardRectSize/2,Commons.boardRectSize/2,0,-angle,Arc2D.PIE);
		
		for(int i = 0;i<rockets.size();i++) {
			Rocket curR = rockets.get(i);
			curR.updateTrail();
			curR.addTrailParticle();
			curR.checkHitEnemy();
			curR.updateAngle();
			curR.move();
			if(curR.isDestroyed) {
				rockets.remove(i);
			}
		}
		updateIsAttacking();
		
	}

}
