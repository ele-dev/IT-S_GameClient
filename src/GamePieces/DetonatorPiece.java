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

import Projectiles.DetonatorProjectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class DetonatorPiece extends GamePiece {
	private static ArrayList<DetonatorProjectile> detProjectiles = new ArrayList<DetonatorProjectile>();
	public DetonatorPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameDetonator, boardRect, Commons.dmgDetonator,0, Commons.neededLOSDetonator);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				shootDetonator();
			} 
		}); 
		attackDelayTimer.setRepeats(false);
	}
	
	@Override
	public boolean isAttacking() {
		for(DetonatorProjectile curDP : detProjectiles) {
			if(curDP.detonationTimer.isRunning() || !curDP.hasHitTarget()) {
				return true;
			}
		}
		return attackDelayTimer.isRunning();
	}
	
	public void drawAttack(Graphics2D g2d) {
		for(int i = 0; i < detProjectiles.size(); i++) {
			DetonatorProjectile curDP = detProjectiles.get(i);
			curDP.drawProjectile(g2d);	
		}
	}
 
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect1 = new Rectangle(myColumn-1,myRow-1,3,3);
		Rectangle rect2 = new Rectangle(myColumn-2,myRow-2,5,5);
		return !rect1.contains(new Point(selectedColumn,selectedRow)) && rect2.contains(new Point(selectedColumn,selectedRow));
	}

	// creates/shoots the DetonatorProjectile
	public void shootDetonator() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		Shape shape = targetGamePiece != null ? targetGamePiece.getRectHitbox() : targetDestructibleObject.getRectHitbox();
		
		Rectangle targetRect = (Rectangle) shape;
		float angleDesiredProjectile = calculateAngle((int)(targetRect.getCenterX()+(Math.random()-0.5)*targetRect.width), (int)(targetRect.getCenterY()+(Math.random()-0.5)*targetRect.height));
		
		detProjectiles.add(new DetonatorProjectile((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, isRed(), 
				angleDesiredProjectile, shape, targetGamePiece, targetDestructibleObject,getDmg()));
		targetDestructibleObject = null;
		targetGamePiece = null;
	}
	// decreases the detonation counter and lets it explode if the timer <= 0
	public static void decDetonaterTimers() {
		for(DetonatorProjectile curDP : detProjectiles) {
			curDP.getDetonationCountDown().countDownOne();
			
			if(curDP.getDetonationCountDown().getCounter() <= 0) {
				curDP.detonationTimer.start();
				curDP.setBlinkeIntervall(5);
			}
		}
	}
	
	public static void drawDetonatorProjectiles(Graphics2D g2d) {
		for(DetonatorProjectile curDP : detProjectiles) {
			curDP.drawProjectile(g2d);
		}
	}

	public static void updateDetonatorProjectiles() {
		for(int i = 0; i < detProjectiles.size(); i++) { 
			DetonatorProjectile curDP = detProjectiles.get(i);
			if(!curDP.hasHitTarget()) {
				curDP.move();
				curDP.checkHitEnemy();
				curDP.checkHitDestructibleObject();
			} else {
				if(curDP.getTargetGamePiece() != null) {
					curDP.stayStuck();
					curDP.updateBlink();
				}
			}
			
			if(curDP.isDestroyed()) {
				detProjectiles.remove(i);
			}
		}
	}

	public void updateAttack() { 
		aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
	}
}
