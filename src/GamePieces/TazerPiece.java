package GamePieces;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.TazerBolt;
import Particles.TazerTrailParticle;
import Projectiles.Bullet;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class TazerPiece extends GamePiece{
	
	Bullet tazerBullet;
	ArrayList<ArrayList<Point>> pointsArray = new ArrayList<ArrayList<Point>>();
	
	public TazerPiece(boolean isEnemy, BoardRectangle boardRect) {
		super(isEnemy, "T", boardRect, 2, 1);
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
		}else if(targetShield != null){
			updateAngle(targetShield.getPos());
		}else if(targetDestructibleObject != null){
			updateAngle(targetDestructibleObject.getPos());
		}
		if(isMoving) {
			updateMove();
		}
		updateAttack();
	}

	@Override
	public void drawAttack(Graphics2D g2d) {

	}
	
	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(selectedRow < boardRect.row+2 && selectedRow > boardRect.row-2 && selectedColumn < boardRect.column+2 && selectedColumn > boardRect.column-2) {
			return false;
		}
		if(selectedRow < boardRect.row+4 && selectedRow > boardRect.row-4 
				&& selectedColumn < boardRect.column+4 && selectedColumn > boardRect.column-4) {
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
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetShield != null?targetShield.getShieldCircle():
			targetDestructibleObject.getRectHitbox();
			
		tazerBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 6, 6, c,1,
				angle, shape,targetDestructibleObject);	
		StagePanel.applyScreenShake(5, 30);
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,-angle-90,Arc2D.PIE);
		ArrayList<GamePiece> alreadyHitGPs = new ArrayList<GamePiece>();
		alreadyHitGPs.add(targetGamePiece);
		int amountOfTazerBolts = 2;
		if(tazerBullet != null) { 
			for(int k = 0;k<amountOfTazerBolts;k++) {
				pointsArray.add(new ArrayList<Point>());
				pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
			}
			int i = 0;
			while(true) {
				i++;
				if(i%2==0) {
					StagePanel.particles.add(new TazerTrailParticle((int)(tazerBullet.getX() + (Math.random()-0.5)*15), (int)(tazerBullet.getY() + (Math.random()-0.5)*15)));
				}
				
				int j = (int)(Math.random()*20)+40;
				if(i%j==0) {
					for(int k = 0;k<amountOfTazerBolts;k++) {
						pointsArray.get(k).add(new Point((int)(tazerBullet.getX() + (Math.random()-0.5)*40), (int)(tazerBullet.getY() + (Math.random()-0.5)*40)));
					}
				}
				
				tazerBullet.move();
				tazerBullet.checkHitAnyTarget();
				if(tazerBullet.getHasHitTarget() && targetGamePiece == null){
					for(int k = 0;k<amountOfTazerBolts;k++) {
						pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
						StagePanel.particles.add(new TazerBolt(pointsArray.get(k)));
					}
					break;
				}
						
				// checks if the bullet hit it's target
				if(tazerBullet.getHasHitTarget() && targetGamePiece != null) {
					boolean nearNoTarget = true;
					// searches for a new target that is in a 1 BoardRectangle range around the target which it hit
					// 1. new target must be not the same team as the GamePiece that shot
					// 2. new target must not be already in the list of targets hit (alreadyHitGPs)
					for (GamePiece curGP : StagePanel.gamePieces) {
						if(targetGamePiece.boardRect.row <= curGP.boardRect.row+1 && targetGamePiece.boardRect.row >= curGP.boardRect.row-1 &&
								targetGamePiece.boardRect.column <= curGP.boardRect.column+1 && targetGamePiece.boardRect.column >= curGP.boardRect.column-1	
								&& targetGamePiece != curGP && checkIfEnemies(curGP) && !alreadyHitGPs.contains(curGP)) {
							nearNoTarget = false;
							// sets the found target that was in range from the old target to the new target
							targetGamePiece =  curGP;
							for(int k = 0;k<amountOfTazerBolts;k++) {
								pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
							}
							// adds the target to the list of targets it already hit (so it won't hit 1 target twice which would end in a loop)
							alreadyHitGPs.add(targetGamePiece);
							
							float ak = (float) (targetGamePiece.getCenterX() - tazerBullet.getX());
							float gk = (float) (targetGamePiece.getCenterY() - tazerBullet.getY());						
							float angleNew = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
							Shape shape = targetGamePiece.getRectHitbox();
							tazerBullet = new Bullet((int)tazerBullet.getX(), (int)tazerBullet.getY(), 6, 6, c,1,
									angleNew, shape, targetDestructibleObject);	
							break;
						}
					}
					// if it found no more targets than the ricochet ends and it breaks the loop
					if(nearNoTarget) {
						for(int k = 0;k<amountOfTazerBolts;k++) {
							pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
							StagePanel.particles.add(new TazerBolt(pointsArray.get(k)));
						}
						break;
					}
				}
			}
		
			
			isAttacking = false;
			pointsArray.clear();
			if(targetGamePiece != null) {
				targetGamePiece = null;
				
				for(GamePiece curGP : alreadyHitGPs) {
					curGP.gamePieceBase.getDamaged(getDmg());
				}
			}else if(targetShield != null){
				targetShield.getDamaged(getDmg());
				targetShield = null;
			}else {
				targetDestructibleObject.getDamaged(getDmg(),tazerBullet.angle);
				targetDestructibleObject = null;
			}
			tazerBullet = null;
			
		}
		
	}
}
