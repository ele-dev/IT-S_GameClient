package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;

import javax.swing.Timer;

import Particles.EmptyShell;
import Particles.TrailParticle;
import Projectiles.Bullet;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class SniperPiece extends GamePiece{
	Bullet sniperBullet;
	
	public SniperPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameSniper, boardRect, Commons.dmgSniper, Commons.baseTypeSniper);
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
	 
	public void update() {
		if(targetGamePiece != null) {
			updateAngle(targetGamePiece.getPos());
		}else if(targetDestructibleObject != null){
			updateAngle(targetDestructibleObject.getPos());
		}
		updateAttack();
	}

	@Override
	public void drawAttack(Graphics2D g2d) {
		
	}
	
	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(selectedRow < boardRect.row+4 && selectedRow > boardRect.row-4 && selectedColumn < boardRect.column+4 && selectedColumn > boardRect.column-4) {
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

	public void shootOnce() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,-angle-90,Arc2D.PIE);	
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
			
		sniperBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 6, getIsRed(),1,
				angle, shape, targetDestructibleObject);	
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(),8,20, (float)angle -90, c,(float)(Math.random()*1+1)));
		
		
		
		for(int i = 0;i<1000;i++) {
			if(i%2==0) {
				int greyTone = (int)(Math.random()*90+10);
				StagePanel.particles.add(new TrailParticle((int)(sniperBullet.getX() + (Math.random()-0.5)*10), (int)(sniperBullet.getY() + (Math.random()-0.5)*10),
						(int)(Math.random()*3+5),(float)(Math.random()*-180),new Color(greyTone,greyTone,greyTone),(float) (Math.random()*0.1),
						(float)(Math.random()*0.2+0.6),0));
			}
			sniperBullet.move();
			sniperBullet.checkHitAnyTarget();
			if(sniperBullet.hasHitTarget()) {
				break;
			}
		}
	
		sniperBullet = null;
		isAttacking = false;
				
		if(targetGamePiece != null) {
			targetGamePiece.gamePieceBase.getDamaged(getDmg());
			targetGamePiece = null;
		}else {
			targetDestructibleObject.getDamaged(getDmg(),angle,getIsRed());
			targetDestructibleObject = null;
		}
		StagePanel.applyScreenShake(5, 30);
	}

	@Override
	public void updateAttack() {

	}
}
