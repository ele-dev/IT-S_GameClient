package GamePieces;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.TazerBolt;
import Particles.TrailParticle;
import Projectiles.Bullet;
import Sound.SoundEffect;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class TazerPiece extends GamePiece{
	
	private Bullet tazerBullet;

	
	public TazerPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameTazer, boardRect, Commons.dmgTazer, 2,Commons.neededLOSTazer); 
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
		return attackDelayTimer.isRunning();
	}

	@Override
	public void drawAttack(Graphics2D g2d) {}
	
	// checks if the parameter Pos is a valid attack position (also if it  is in line of sight)
	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect1 = new Rectangle(myColumn-1,myRow-1,3,3);
		Rectangle rect2 = new Rectangle(myColumn-3,myRow-3,7,7);
		return !rect1.contains(new Point(selectedColumn,selectedRow)) && rect2.contains(new Point(selectedColumn,selectedRow));
	}

	public void shootOnce() {	
		Arc2D aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
			
		tazerBullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/4, StagePanel.boardRectSize/4, isRed(),1,
				angle, shape,targetDestructibleObject);	
		StagePanel.applyScreenShake(5, 30);
		ArrayList<GamePiece> alreadyHitGPs = new ArrayList<GamePiece>();
		alreadyHitGPs.add(targetGamePiece);
		int amountOfTazerBolts = 2;
		
		ArrayList<ArrayList<Point>> pointsArray = new ArrayList<ArrayList<Point>>();
		for(int k = 0;k<amountOfTazerBolts;k++) {
			pointsArray.add(new ArrayList<Point>());
			pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
		}
		int i = 0;
		while(true) {
			i++;
			if(i%2==0) {
				Color cTP =  new Color(58, 100+(int)(Math.random()*130), 140+(int)(Math.random()*30));
				int x = (int)(tazerBullet.getX() + (Math.random()-0.5)*10);
				int y = (int)(tazerBullet.getY() + (Math.random()-0.5)*10);
				StagePanel.particles.add(new TrailParticle(x, y, (int)(Math.random()*StagePanel.boardRectSize/16+StagePanel.boardRectSize/16), 
						(int)(Math.random()*360), cTP, (float)(Math.random()*0.1), 3,0.1f));
			} 
			int j = (int)(Math.random()*20)+40;
			if(i%j==0) {
				for(int k = 0;k<amountOfTazerBolts;k++) {
					pointsArray.get(k).add(new Point((int)(tazerBullet.getX() + (Math.random()-0.5)*StagePanel.boardRectSize/2), (int)(tazerBullet.getY() + (Math.random()-0.5)*StagePanel.boardRectSize/2)));
				}
			}
				
			tazerBullet.move();
			tazerBullet.checkHitAnyTarget();
				
			// checks if the bullet hit it's target
			if(tazerBullet.hasHitTarget()) {
				
				if(targetGamePiece == null) {
					for(int k = 0;k<amountOfTazerBolts;k++) {
						pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
						StagePanel.particles.add(new TazerBolt(pointsArray.get(k)));
					}
					break;
				}
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
						shape = targetGamePiece.getRectHitbox();
						tazerBullet = new Bullet((int)tazerBullet.getX(), (int)tazerBullet.getY(), StagePanel.boardRectSize/4, StagePanel.boardRectSize/4, isRed(),1,
								angleNew, shape,targetDestructibleObject);	
						break;
					}
				}
				// if it found no more targets than the ricochet ends and it breaks the loop
				if(nearNoTarget) {
					break;
				}
			}
			// failsave
			if(i > 500) {
				break;
			}
		}
		for(int k = 0;k<amountOfTazerBolts;k++) {
			pointsArray.get(k).add(new Point((int)tazerBullet.getX(), (int)tazerBullet.getY()));
			StagePanel.particles.add(new TazerBolt(pointsArray.get(k)));
		}
		
		if(targetGamePiece != null) {
			targetGamePiece = null;
			for(GamePiece curGP : alreadyHitGPs) {
				curGP.gamePieceBase.getDamaged(getDmg());
			}
		}else {
			targetDestructibleObject.getDamaged(getDmg(),tazerBullet.angle,isRed());
			targetDestructibleObject = null;
		}
		tazerBullet = null;
		SoundEffect.play("Electro.wav");
		SoundEffect.play("ShotDecay.wav");
	}

	@Override
	public void updateAttack() {}
}
