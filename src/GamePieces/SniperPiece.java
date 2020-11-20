package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
		super(isRed, Commons.nameSniper, boardRect, Commons.dmgSniper, Commons.baseTypeSniper,Commons.neededLOSSniper);
		attackDelayTimer = new Timer(1500,new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootOnce();
			}
		});
		attackDelayTimer.setRepeats(false);
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
	public boolean isAttacking() {
		return attackDelayTimer.isRunning();
	}

	@Override
	public void drawAttack(Graphics2D g2d) {
		
	}
	
	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect1 = new Rectangle(myColumn-3,myRow-1,7,3);
		Rectangle rect2 = new Rectangle(myColumn-1,myRow-3,3,7);
		return rect1.contains(new Point(selectedColumn,selectedRow)) || rect2.contains(new Point(selectedColumn,selectedRow));
	}

	public void shootOnce() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
			
		sniperBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/10, StagePanel.boardRectSize/4, isRed(),1,
				angle, shape, targetDestructibleObject);	
		StagePanel.particles.add(new EmptyShell((float)getCenterX(), (float)getCenterY(),StagePanel.boardRectSize/8, StagePanel.boardRectSize/3, (float)angle -90, c,(float)(Math.random()*3+2)));
		
		int i = 0;
		while(true) {
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
			i++;
			// failsave
			if(i > 10000) {
				break;
			}
		}	
		if(targetGamePiece != null) {
			targetGamePiece.gamePieceBase.getDamaged(getDmg());
			targetGamePiece = null;
		}else {
			targetDestructibleObject.getDamaged(getDmg(),sniperBullet.angle,isRed());
			targetDestructibleObject = null;
		}
		sniperBullet = null;
		StagePanel.applyScreenShake(5, 30);
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
	}
}
