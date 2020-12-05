package GamePieces;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Projectiles.FlameThrowerFlame;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class FlamethrowerPiece extends GamePiece {
	
	private ArrayList<FlameThrowerFlame> flames = new ArrayList<FlameThrowerFlame>();
	private int burstCounter;
	private Timer burstTimer;
	private int burstBulletAmount = 200;
	
	public FlamethrowerPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameFlameThrower, boardRect, Commons.dmgFlameThrower,1,Commons.neededLOSFlameThrower);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootBurst();
			} 
		});
		attackDelayTimer.setRepeats(false);
		
		burstTimer = new Timer(10, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shootOnce();
			}
		}); 
		burstTimer.setRepeats(false);
	}
	
	@Override
	public boolean isAttacking() {
		return attackDelayTimer.isRunning() || flames.size()>0 || burstTimer.isRunning();
	}
	

	public void drawAttack(Graphics2D g2d) {
		for(FlameThrowerFlame curFTF : flames) {
				curFTF.drawProjectile(g2d);
		}
	}
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		int dist = BoardRectangle.getDistanceBetweenBRs(myRow,myColumn, selectedRow,selectedColumn);
		return dist <= 2;
	}
	
	public void shootBurst() {
		burstTimer.start();
	}
	
	public void shootOnce() {
		Arc2D aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		burstCounter++;
		if(burstCounter<burstBulletAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
		int randomSize = (int)(Math.random()*StagePanel.boardRectSize/16+StagePanel.boardRectSize/16);
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
		Rectangle targetRect = (Rectangle) shape;
		float angleDesiredProjectile = calculateAngle((int)(targetRect.getCenterX()+(Math.random()-0.5)*targetRect.width/2), (int)(targetRect.getCenterY()+(Math.random()-0.5)*targetRect.height/2));		
		flames.add(new FlameThrowerFlame((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), randomSize, randomSize, 
				(float)Math.random()+2, angleDesiredProjectile, shape,targetDestructibleObject));
		
			
		
	}
	
	// updates the attack (moves flames,checks if they hit something and so forth)
	public void updateAttack() {
		for(int i = 0;i<flames.size();i++) {
			FlameThrowerFlame curFTF = flames.get(i);
			curFTF.move();
			curFTF.checkHitAnyTarget();
			curFTF.updateFade();
			if(curFTF.getColor().getAlpha()<10) {
				flames.remove(i);
				if(flames.size() == 0) {
					if(targetGamePiece != null) {
						targetGamePiece.gamePieceBase.getDamaged(getDmg());
						targetGamePiece = null;
					}else {
						targetDestructibleObject.getDamaged(getDmg(),angle,isRed());
						targetDestructibleObject = null;
					}
				}
			}
			
				
		}
	}
}
