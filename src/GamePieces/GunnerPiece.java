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
import Sound.SoundEffect;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;


public class GunnerPiece extends GamePiece {
	
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private int burstCounter;
	private Timer burstTimer;
	private int burstBulletAmount = 16;
	
	public GunnerPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameGunner, boardRect, Commons.dmgGunner, 0,Commons.neededLOSGunner);
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
		
//		ArrayList<String> spriteLinks = new ArrayList<String>();
//		spriteLinks.add(Commons.directoryToSprites+"Turrets/Minigun.png");
//		spriteTurret = new Sprite(spriteLinks, StagePanel.boardRectSize, StagePanel.boardRectSize, 0);
	}
	
	@Override
	public boolean isAttacking() {
		return attackDelayTimer.isRunning() || burstTimer.isRunning() || bullets.size()>0;
	}
	
	//draws every bullet in the bullets Array
	public void drawAttack(Graphics2D g2d) {
		for(Bullet curB: bullets) {
			curB.drawProjectile(g2d);
		}
	}

	// updates the attack (moves bullets,checks if they hit something and so forth)
	public void updateAttack() {
		for(int i = 0; i < bullets.size(); i++) {
			Bullet curB = bullets.get(i);
			curB.move();
			curB.checkHitAnyTarget(); 
			
			if(curB.hasHitTarget()) {
				bullets.remove(i);
				if(bullets.size() == 0 && burstCounter == 0) {
					if(targetGamePiece != null) {
						targetGamePiece.gamePieceBase.getDamaged(getDmg());
						targetGamePiece = null;
					}else { 
						targetDestructibleObject.getDamaged(getDmg(), angle, isRed());
						targetDestructibleObject = null;
					}
				}
			}
			
		} 
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
		Arc2D aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		StagePanel.applyScreenShake(2, 10);
		burstCounter++;
		if(burstCounter < burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
		Rectangle targetRect = (Rectangle) shape;
		float angleDesiredProjectile = calculateAngle((int)(targetRect.getCenterX()+(Math.random()-0.5)*targetRect.width/2), (int)(targetRect.getCenterY()+(Math.random()-0.5)*targetRect.height/2));		
		bullets.add(new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/10, StagePanel.boardRectSize/5, isRed(), 12, 
				angleDesiredProjectile, shape,targetDestructibleObject));	
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, (float)angle -90, c, (float)(Math.random()*2+3)));
		SoundEffect.play("Shoot.wav");
	}
	
}
