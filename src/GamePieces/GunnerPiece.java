package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
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
	
	public GunnerPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameGunner, boardRect, Commons.dmgGunner, Commons.baseTypeGunner);
		attackDelayTimer = new Timer(1500, new ActionListener() {
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
		spriteTurret = new Sprite(spriteLinks, Commons.boardRectSize, Commons.boardRectSize, 0);
		
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2, boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize, Commons.boardRectSize, 0, 0, Arc2D.PIE);
	}
	
	//draws every bullet in the bullets Array
	public void drawAttack(Graphics2D g2d) {
		for(Bullet curB: bullets) {
			curB.drawProjectile(g2d);
		}
	}

	// updates the attack (moves bullets,checks if they hit something and so forth)
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2, boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize, Commons.boardRectSize, 0, -angle-90, Arc2D.PIE);
		for(int i = 0; i < bullets.size(); i++) {
			Bullet curB = bullets.get(i);
			curB.move();
			curB.checkHitAnyTarget(); 
			
			if(curB.hasHitTarget()) {
				bullets.remove(i);
			}
			
		} 
		
		updateIsAttacking();
	}

	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		
		if(selectedRow < boardRect.row+3 && selectedRow > boardRect.row-3 && selectedColumn < boardRect.column+3 && selectedColumn > boardRect.column-3) {
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
	
	// starts burstTimer
	public void shootBurst() { 
		burstTimer.start();
	}
	
	// shoots one shot every timer the burstTimer activates and starts the burstTimer again if it still has shots left to shoot
	// shots are counted by burstCounter (stops shooting if burstCounter >= burstBulletAmount)
	public void shootOnce() {
		StagePanel.applyScreenShake(2, 10);
		burstCounter++;
		if(burstCounter < burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		startedAttack = true;
		
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
			
		bullets.add(new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 20, getIsRed(), 16, 
				(float) (angle + (Math.random()-0.5)*spreadAngle), shape,targetDestructibleObject));	
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(), 8, 12, (float)angle -90, c, (float)(Math.random()*2+3)));
	}
	
	// updates the isAttacking state
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		if(burstCounter > 0 || bullets.size() > 0) {
			isAttacking = true;
			return;
		}
		if(startedAttack) {
			if(targetGamePiece != null) {
				targetGamePiece.gamePieceBase.getDamaged(getDmg());
				targetGamePiece = null;
			}else { 
				targetDestructibleObject.getDamaged(getDmg(), angle, getIsRed());
				targetDestructibleObject = null;
			}
			
			startedAttack = false;
			return;
		}
	}
	
}
