package GamePieces;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.Rocket;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;


public class RocketLauncherPiece extends GamePiece{
	ArrayList<Rocket> rockets = new ArrayList<Rocket>();
	int burstCounter;
	Timer burstTimer;
	int burstRocketAmount = 8;
	
	double spreadAngle = 120;
	boolean startedAttack = false;
	
	public RocketLauncherPiece(boolean isEnemy, BoardRectangle boardRect) {
		super(isEnemy, Commons.nameRocketLauncher, boardRect, Commons.dmgRocketLauncher,Commons.baseTypeRocketLauncher);
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
//		g2d.setColor(new Color(20,20,20,200));
//		g2d.fill(aimArc);
		for(int i = 0;i<rockets.size();i++) {		
			Rocket curR = rockets.get(i);
			curR.drawProjectile(g2d);
		}
	}


	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(checkAttackRows(selectedRow,selectedColumn) || checkAttackColumns(selectedRow,selectedColumn)) {
			return true;
		}
		return false;
	}


	public boolean checkAttackRows(int selectedRow, int selectedColumn) {
		int row = this.boardRect.row;
		int column = this.boardRect.column;
		
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
		int column = this.boardRect.column;
		int row = this.boardRect.row;
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

	
	public void addRocketInArcFlight() {
		rockets.add(new Rocket((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 10, 20, c,
				(float) (angle + (Math.random()-0.5)*spreadAngle), currentTargetGamePiece,currenTargetShield));
	}
	// checks if the piece is attacking and sets its boolean isAttacking to true if it is currently attacking and to false if it isn't
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning() || burstTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		if(rockets.size() > 0) {
			isAttacking = true;
			return;
		}
		if(!isAttacking && startedAttack) {
			if(currentTargetGamePiece != null) {
				getCurrentTargetGamePiece().gamePieceBase.getDamaged(getDmg());
				currentTargetGamePiece = null;
				startedAttack = false;
			}else if(currenTargetShield != null){
				currenTargetShield.getDamaged(getDmg());
				currenTargetShield = null;
				startedAttack = false;
			}
		}
	}

	// updates all things that are animated with the attack (for example moves the rockets and updates the explosions)
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/4,boardRect.getCenterY()-Commons.boardRectSize/4,
				Commons.boardRectSize/2,Commons.boardRectSize/2,0,-angle,Arc2D.PIE);
		
		for(int i = 0;i<rockets.size();i++) {
			Rocket curR = rockets.get(i);
			curR.addTrailParticle();
			curR.checkHitEnemy();
			curR.checkHitTargetShield();
			if(curR.getCurrentTarget() != null) {
				curR.homeInOnTarget(curR.getCurrentTarget().getPos(), curR.rotationDelay);
			}else if(curR.getCurrenTargetShield() != null){
				curR.homeInOnTarget(curR.getCurrenTargetShield().getPos(), curR.rotationDelay);
			}
			
			curR.move();
			if(curR.getIsDestroyed()) {
				rockets.remove(i);
			}
		}
		updateIsAttacking();
		
	}

}
