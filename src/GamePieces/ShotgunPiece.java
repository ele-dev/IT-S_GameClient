package GamePieces;

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
import Stage.StagePanel;

public class ShotgunPiece extends GamePiece {
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private float spreadAngle = 30;
	private byte bulletAmount = 10;

	public ShotgunPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameShotgun, boardRect, Commons.dmgShotgun, Commons.baseTypeShotgun);
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
	public void drawAttack(Graphics2D g2d) {
		for(Bullet curB : bullets) {
			curB.drawProjectile(g2d);
		}
	}

	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		if(selectedRow < myRow+3 && selectedRow > myRow-3 && selectedColumn < myColumn+3 && selectedColumn > myColumn-3) {
			return true;
		}
		return false;
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2,boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize,StagePanel.boardRectSize,0,-angle-90,Arc2D.PIE);
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
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		if(bullets.size() >0) {
			isAttacking = true;
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
