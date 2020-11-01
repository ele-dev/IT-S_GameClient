package GamePieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.TazerBolt;
import Particles.TrailParticle;
import Projectiles.Bullet;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class RapidElectroPiece extends GamePiece {
	private Bullet bullet;
	private byte particleSpawnCounter,particleSpawnIntervall = 1;
	
	private byte burstCounter,burstIntervall = 15;
	private byte burstAmount;
	
	private ArrayList<GamePiece> targetGamePieces = new ArrayList<GamePiece>();

	public RapidElectroPiece(boolean isEnemy,BoardRectangle boardRect) {
		super(isEnemy, "RE", boardRect, 2, 1);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
				
				burstAmount = targetGamePieces.size() > 0?(byte) (targetGamePieces.size()):1;
				
			}
		});
		attackDelayTimer.setRepeats(false);
	}
	@Override
	protected void startAttackDelay() {
		isAttacking = true;
		attackDelayTimer.start();
		hasExecutedAttack = true;
		hasExecutedMove = true;
		targetGamePieces.clear();
		if(targetDestructibleObject == null) {
			
			ArrayList<BoardRectangle> possibleBoardRectangles = new ArrayList<BoardRectangle>();
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(checkAttacks(curBR.row,curBR.column)) {
					possibleBoardRectangles.add(curBR);
				}
			}
			for(GamePiece curGP : StagePanel.gamePieces) {
				if(checkIfEnemies(curGP) && possibleBoardRectangles.contains(curGP.boardRect)) {
					targetGamePieces.add(curGP);
				}
			}
		
			targetGamePiece = targetGamePieces.get(targetGamePieces.size()-1);
		}
		
	}

	@Override
	public void drawAttack(Graphics2D g2d) {
		if(bullet != null)bullet.drawProjectile(g2d);
	}

	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(selectedRow < boardRect.row+3 && selectedRow > boardRect.row-3 && selectedColumn < boardRect.column+3 && selectedColumn > boardRect.column-3) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.row == selectedRow && curBR.column == selectedColumn && !curBR.isWall && checkIfBoardRectangleInSight(curBR)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/2,boardRect.getCenterY()-Commons.boardRectSize/2,
				Commons.boardRectSize,Commons.boardRectSize,0,-angle-90,Arc2D.PIE);
		if(burstAmount > 0) {
			burstCounter--;
			if(burstCounter <= 0) {
				burstCounter = burstIntervall;
				burstAmount--;
				
				
				angle = angleDesired;
				if(targetDestructibleObject == null) {
					targetGamePiece = targetGamePieces.get(burstAmount);
				}
				
				shootOnce();
				if(targetDestructibleObject == null) {
					if(burstAmount > 0) {
						targetGamePiece = targetGamePieces.get(burstAmount-1);
					}else {
						targetGamePiece = null;
					}
				}
				
			}
		}
		updateIsAttacking();
	}
	
	public void shootOnce() {
		startedAttack = true;
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
		
		bullet = new Bullet(getCenterX(), getCenterY(), 10, 20, c, 2, angle, shape, targetDestructibleObject);
		
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point((int)bullet.getX(), (int)bullet.getY()));
		
		int i = 0;
		while (!bullet.hasHitTarget()) {
			particleSpawnCounter--;
			if(particleSpawnCounter <=0) {
				particleSpawnCounter = particleSpawnIntervall;
				Color cTP =  new Color(58, 100+(int)(Math.random()*130), 140+(int)(Math.random()*30));
				int x = (int)(bullet.getX() + (Math.random()-0.5)*10);
				int y = (int)(bullet.getY() + (Math.random()-0.5)*10);
				StagePanel.particles.add(new TrailParticle(x, y, (int)(Math.random()*5+6), 
						(int)(Math.random()*360), cTP, (float)(Math.random()*0.1), 3,0.1f));
				
				if(i % 10 == 0) {
					points.add(new Point((int)(bullet.getX() + (Math.random()-0.5)*40), (int)(bullet.getY() + (Math.random()-0.5)*40)));
				}
				
			
			}
			
			bullet.move();
			bullet.checkHitAnyTarget();
			i++;
		}
		points.add(new Point((int)bullet.getX(), (int)bullet.getY()));
		StagePanel.particles.add(new TazerBolt(points));
		
		bullet = null;
		if(targetDestructibleObject != null) {
			targetDestructibleObject.getDamaged(getDmg(),angle,getIsEnemy());
			targetDestructibleObject = null;
		}else { 
			targetGamePiece.gamePieceBase.getDamaged(getDmg());
		}
		
		StagePanel.applyScreenShake(5, 10);
	}
	
	// updates the isAttacking state
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning()) {
			isAttacking = true;
			return;
		}
	}

}
