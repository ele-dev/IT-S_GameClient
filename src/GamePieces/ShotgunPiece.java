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
import Stage.StagePanel;

public class ShotgunPiece extends GamePiece {
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private float spreadAngle = 30;
	private byte bulletAmount = 10;

	public ShotgunPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameShotgun, boardRect, Commons.dmgShotgun, Commons.baseTypeShotgun,Commons.neededLOSShotgun);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootOnce();
			}
		});
		attackDelayTimer.setRepeats(false);
	}
	@Override
	public boolean isAttacking() {
		return attackDelayTimer.isRunning() || bullets.size()>0;
	}

	@Override
	public void drawAttack(Graphics2D g2d) {
		for(Bullet curB : bullets) {
			curB.drawProjectile(g2d);
		}
	}

	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect1 = new Rectangle(myColumn-2,myRow-1,5,3);
		Rectangle rect2 = new Rectangle(myColumn-1,myRow-2,3,5);
		return rect1.contains(new Point(selectedColumn,selectedRow)) || rect2.contains(new Point(selectedColumn,selectedRow));
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		for(int i = 0;i<bullets.size();i++) {
			Bullet curB = bullets.get(i);
			curB.move();
			curB.checkHitAnyTarget(); 
			
			if(curB.hasHitTarget()) {
				bullets.remove(i);
			}
		} 
		updateIsAttacking();
	}
	
	public void shootOnce() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		startedAttack = true;
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
		for(int i = 0;i<bulletAmount;i++) {
			bullets.add(new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/14, StagePanel.boardRectSize/4, isRed(),16, 
					(float) (angle + (Math.random()-0.5)*spreadAngle), shape,targetDestructibleObject));	
		}
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(),StagePanel.boardRectSize/14,StagePanel.boardRectSize/4, (float)angle -90, c,(float)(Math.random()*3+2)));
		StagePanel.applyScreenShake(5, 10);
	}
	
	// updates the isAttacking state
	public void updateIsAttacking() {
		if(isAttacking()) {
			return;
		}
		
		if(startedAttack) {
			if(targetGamePiece != null) {
				targetGamePiece.gamePieceBase.getDamaged(getDmg());
				targetGamePiece = null;
			}else { 
				targetDestructibleObject.getDamaged(getDmg(),angle,isRed());
				targetDestructibleObject = null;
			}
			startedAttack = false;
		}
		
	}

}
