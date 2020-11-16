package GamePieces;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
		super(isRed, Commons.nameGunner, boardRect, Commons.dmgGunner, Commons.baseTypeGunner,Commons.neededLOSGunner);
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
		spriteTurret = new Sprite(spriteLinks, StagePanel.boardRectSize, StagePanel.boardRectSize, 0);
	}
	
	@Override
	public boolean isAttacking() {
		return attackDelayTimer.isRunning() || bullets.size()>0;
	}
	
	//draws every bullet in the bullets Array
	public void drawAttack(Graphics2D g2d) {
		for(Bullet curB: bullets) {
			curB.drawProjectile(g2d);
		}
	}

	// updates the attack (moves bullets,checks if they hit something and so forth)
	public void updateAttack() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
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
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect = new Rectangle(myColumn-2,myRow-2,5,5);
		return rect.contains(new Point(selectedColumn,selectedRow));
	}
	
	// starts burstTimer
	public void shootBurst() { 
		burstTimer.start();
	}
	
	// shoots one shot every timer the burstTimer activates and starts the burstTimer again if it still has shots left to shoot
	// shots are counted by burstCounter (stops shooting if burstCounter >= burstBulletAmount)
	public void shootOnce() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		StagePanel.applyScreenShake(2, 10);
		burstCounter++;
		if(burstCounter < burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		startedAttack = true;
		
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
			
		bullets.add(new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/14, StagePanel.boardRectSize/6, isRed(), 16, 
				(float) (angle + (Math.random()-0.5)*spreadAngle), shape,targetDestructibleObject));	
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(), StagePanel.boardRectSize/12, StagePanel.boardRectSize/6, (float)angle -90, c, (float)(Math.random()*2+3)));
	}
	
	// updates the isAttacking state
	public void updateIsAttacking() {
		if (isAttacking()) {
			return;
		}
		if(startedAttack) {
			if(targetGamePiece != null) {
				targetGamePiece.gamePieceBase.getDamaged(getDmg());
				targetGamePiece = null;
			}else { 
				targetDestructibleObject.getDamaged(getDmg(), angle, isRed());
				targetDestructibleObject = null;
			}
			
			startedAttack = false;
			return;
		}
	}
	
}
