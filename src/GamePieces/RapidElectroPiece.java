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

public class RapidElectroPiece extends GamePiece {
	private Bullet bullet;
	private byte particleSpawnCounter,particleSpawnIntervall = 1;
	
	private byte burstCounter,burstIntervall = 15;
	private byte burstAmount;
	
	private ArrayList<GamePiece> targetGamePieces = new ArrayList<GamePiece>();

	public RapidElectroPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameRapidElectro, boardRect, Commons.dmgRapidElectro, 1,Commons.neededLOSRapidElectro);
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
	public boolean isAttacking() {
		return attackDelayTimer.isRunning() || burstAmount > 0;
	}
	
	@Override
	protected void startAttackDelay() {
		attackDelayTimer.start();
		hasExecutedAttack = true;
		hasExecutedMove = true;
		targetGamePieces.clear();
		if(targetDestructibleObject == null) {
			ArrayList<BoardRectangle> possibleBoardRectangles = new ArrayList<BoardRectangle>();
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(checkAttacks(curBR.row,curBR.column,boardRect.row,boardRect.column)) {
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
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect = new Rectangle(myColumn-2,myRow-2,5,5);
		return rect.contains(new Point(selectedColumn,selectedRow)) && selectedRow != myRow && selectedColumn != myColumn;
	}

	@Override
	public void updateAttack() {
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
	}
	
	public void shootOnce() {
		Arc2D aimArc = new Arc2D.Double(boardRect.getCenterX()-StagePanel.boardRectSize/2, boardRect.getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
		
		bullet = new Bullet((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 10, 20, isRed(), 2, angle, shape, targetDestructibleObject);
		
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
				StagePanel.particles.add(new TrailParticle(x, y, (int)(Math.random()*StagePanel.boardRectSize/16+StagePanel.boardRectSize/16), 
						(int)(Math.random()*360), cTP, (float)(Math.random()*0.1), 3,0.1f));
				if(i % 10 == 0) {
					points.add(new Point((int)(bullet.getX() + (Math.random()-0.5)*StagePanel.boardRectSize/2), (int)(bullet.getY() + (Math.random()-0.5)*StagePanel.boardRectSize/2)));
				}
			}
			bullet.move();
			bullet.checkHitAnyTarget();
			i++;
			// failsave
			if(i > 500) {
				break;
			}
		}
		points.add(new Point((int)bullet.getX(), (int)bullet.getY()));
		
		
		bullet = null;
		if(targetDestructibleObject != null) {
			targetDestructibleObject.getDamaged(getDmg(),angle,isRed());
			targetDestructibleObject = null;
		}else { 
			targetGamePiece.gamePieceBase.getDamaged(getDmg());
		}
		StagePanel.applyScreenShake(5, 10);
		SoundEffect.play("Electro.wav");
		StagePanel.particles.add(new TazerBolt(points));
	}
}
