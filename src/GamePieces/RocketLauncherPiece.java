package GamePieces;

import java.awt.Graphics2D;
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


public class RocketLauncherPiece extends GamePiece{
	ArrayList<Rocket> rockets = new ArrayList<Rocket>();
	int burstCounter;
	Timer burstTimer;
	int burstRocketAmount = 8;
	
	double spreadAngle = 120;
	boolean startedAttack = false;
	
	public RocketLauncherPiece(boolean isEnemy, BoardRectangle boardRect) {
		super(isEnemy, Commons.nameRocketLauncher, boardRect, Commons.dmgRocketLauncher,Commons.baseTypeRocketLauncher);
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
		spriteTurret = new Sprite(spriteLinks, Commons.boardRectSize,Commons.boardRectSize, 0);
		
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/4,boardRect.getCenterY()-Commons.boardRectSize/4,
				Commons.boardRectSize/2,Commons.boardRectSize/2,0,0,Arc2D.PIE);
	}
	
	public void update() {
		if(targetGamePiece != null) {
			updateAngle(targetGamePiece.getPos());
		}else if(targetDestructibleObject != null){ 
			updateAngle(targetDestructibleObject.getPos());
		}
		if(isMoving) {
			updateMove();
		}
		updateAttack();
	}

	public void drawAttack(Graphics2D g2d) {
//		g2d.setColor(new Color(20,20,20,200));
//		g2d.fill(aimArc); 
		for(int i = 0;i<rockets.size();i++) {		
			Rocket curR = rockets.get(i);
			curR.drawProjectile(g2d);
		}
	}


	public boolean checkAttacks(int selectedRow, int selectedColumn) {
		if(((selectedRow == boardRect.row+3 || selectedRow == boardRect.row-3) && selectedColumn < boardRect.column+3 && selectedColumn > boardRect.column-3) ||
				((selectedColumn == boardRect.column+3 || selectedColumn == boardRect.column-3) && selectedRow <=boardRect.row+3 && selectedRow >= boardRect.row-3)) {
			return true;
		}
		return false;
	}

	
	public void shootBurst() {
		burstTimer.start();
	}
	
	public void shootOnce() {
		startedAttack = true;
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
			
			
		rockets.add(new Rocket((int)aimArc.getEndPoint().getX(), (int)aimArc.getEndPoint().getY(), 10, 20, c,
				(float) (angle + (Math.random()-0.5)*spreadAngle), shape,targetDestructibleObject));
	}
	// checks if the piece is attacking and sets its boolean isAttacking to true if it is currently attacking and to false if it isn't
	public void updateIsAttacking() {
		isAttacking = false;
		if(attackDelayTimer.isRunning() || burstTimer.isRunning()) {
			isAttacking = true;
			return;
		}
		if(rockets.size() > 0) {
			isAttacking = true;
			return;
		}
		if(!isAttacking && startedAttack) {
			if(targetGamePiece != null) {
				targetGamePiece.gamePieceBase.getDamaged(getDmg());
				targetGamePiece = null;
			}else if(targetDestructibleObject != null){
				targetDestructibleObject.getDamaged(getDmg(),angle);
				targetDestructibleObject = null;
			}
		}
	}

	// updates all things that are animated with the attack (for example moves the rockets and updates the explosions)
	public void updateAttack() {
		aimArc = new Arc2D.Double(boardRect.getCenterX()-Commons.boardRectSize/4,boardRect.getCenterY()-Commons.boardRectSize/4,
				Commons.boardRectSize/2,Commons.boardRectSize/2,0,-angle,Arc2D.PIE);
		
		for(int i = 0;i<rockets.size();i++) {
			Rocket curR = rockets.get(i);
			curR.addTrailParticle();
			curR.tryExplodeTarget();
			if(targetGamePiece != null) {
				curR.homeInOnTarget(targetGamePiece.getPos(), curR.rotationDelay);
			}else if(targetDestructibleObject != null){
				curR.homeInOnTarget(targetDestructibleObject.getPos(), curR.rotationDelay);
			}
			
			curR.move();
			if(curR.getIsDestroyed()) {
				rockets.remove(i);
			}
		}
		updateIsAttacking();
		
	}

}
