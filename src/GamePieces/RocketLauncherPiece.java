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

import Projectiles.Rocket;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;


public class RocketLauncherPiece extends GamePiece{
	private ArrayList<Rocket> rockets = new ArrayList<Rocket>();
	private int burstCounter;
	private Timer burstTimer;
	private int burstRocketAmount = 8;
	 
	private static float spreadAngle = 120;
	
	public RocketLauncherPiece(boolean isRed, BoardRectangle boardRect) {
		super(isRed, Commons.nameRocketLauncher, boardRect, Commons.dmgRocketLauncher,2,Commons.neededLOSRocketLauncher);
		attackDelayTimer = new Timer(1500,new ActionListener() {
			 
			@Override
			public void actionPerformed(ActionEvent e) { 
				angle = angleDesired;
				shootBurst();
			} 
		});
		attackDelayTimer.setRepeats(false);
		
		burstTimer = new Timer(150, new ActionListener() {
			 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shootOnce();
			} 
		});
		burstTimer.setRepeats(false);
		
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Turrets/RocketLauncher.png");
		spriteTurret = new Sprite(spriteLinks, StagePanel.boardRectSize,StagePanel.boardRectSize, 0);
	}
	
	@Override
	public boolean isAttacking() {
		return attackDelayTimer.isRunning() || burstTimer.isRunning() || rockets.size() > 0;
	}

	public void drawAttack(Graphics2D g2d) {
		for(Rocket curR : rockets)curR.drawProjectile(g2d);		
	}

	@Override
	public boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn) {
		Rectangle rect1 = new Rectangle(myColumn-2,myRow-2,5,5);
		Rectangle rect2 = new Rectangle(myColumn-3,myRow-3,7,7);
		return !rect1.contains(new Point(selectedColumn,selectedRow)) && rect2.contains(new Point(selectedColumn,selectedRow));
	}

	
	public void shootBurst() {
		burstTimer.start();
	}
	
	public void shootOnce() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		addRocketInArcFlight();
		burstCounter++;
		if(burstCounter <burstRocketAmount) {
			burstTimer.start();
		}else {
			burstCounter = 0;
		}
	}

	
	public void addRocketInArcFlight() {
		Shape shape = targetGamePiece != null?targetGamePiece.getRectHitbox():
			targetDestructibleObject.getRectHitbox();
			
			
		rockets.add(new Rocket((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), StagePanel.boardRectSize/8, StagePanel.boardRectSize/4, c,
				(float) (angle + (Math.random()-0.5)*spreadAngle), shape,targetDestructibleObject));
	}

	// updates all things that are animated with the attack (for example moves the rockets and updates the explosions)
	public void updateAttack() {
		aimArc = new Arc2D.Double(getCenterX()-StagePanel.boardRectSize/2, getCenterY()-StagePanel.boardRectSize/2,
				StagePanel.boardRectSize, StagePanel.boardRectSize, 0, -angle-90, Arc2D.PIE);
		
		for(int i = 0;i<rockets.size();i++) {
			rockets.get(i).addTrailParticle();
			rockets.get(i).tryExplodeTarget();
			if(targetGamePiece != null) {
				rockets.get(i).homeInOnTarget(targetGamePiece.getPos(), Rocket.rotationDelay);
			}else if(targetDestructibleObject != null){
				rockets.get(i).homeInOnTarget(targetDestructibleObject.getPos(), Rocket.rotationDelay);
			}
			
			rockets.get(i).move();
			rockets.get(i).decAutoExplodeCounter();
			if(rockets.get(i).isDestroyed()) {
				rockets.remove(i);
				if(rockets.size() == 0 && burstCounter == 0) {
					if(targetGamePiece != null) {
						targetGamePiece.gamePieceBase.getDamaged(getDmg());
						targetGamePiece = null;
					} else if(targetDestructibleObject != null) {
						targetDestructibleObject.getDamaged(getDmg(), angle,isRed());
						targetDestructibleObject = null;
					}
				}
			}
		}
	}
}
