package Abilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Particles.Explosion;
import Projectiles.Projectile;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class WallMine extends Projectile{
	boolean isEnemy;
	public BoardRectangle targetBR;
	BoardRectangle[] affectedBoardRectangles = new BoardRectangle[2];
	public Point targetPoint;
	public float lockedRotation;
	float dmg;
	GamePiece parentGP;
	
	public WallMine(int x, int y, int w, int h,GamePiece parentGP, float angle,BoardRectangle targetBR,Point targetPoint,float lockedRotation, byte rotationIndex) {
		super(x, y, w, h, null, angle, 16, 0, null,null);
		this.parentGP = parentGP;
		this.isEnemy = parentGP.getIsEnemy();
		if(isEnemy) {
			this.c = Commons.enemyColor;
		}else {
			this.c = Commons.notEnemyColor;
		}
		this.dmg = 3;
		shapeShow = new Rectangle(-w/2,-h/2,w,h);
		this.targetBR = targetBR;
		this.targetPoint = targetPoint;
		this.lockedRotation = lockedRotation;
		switch (rotationIndex) {
		// north
		case 0:
			affectedBoardRectangles[0] = targetBR.southBR;
			affectedBoardRectangles[1] = affectedBoardRectangles[0].southBR;
			break;
		// south
		case 1:
			affectedBoardRectangles[0] = targetBR.northBR;
			affectedBoardRectangles[1] = affectedBoardRectangles[0].northBR;
			break;
		// east
		case 2:
			affectedBoardRectangles[0] = targetBR.westBR;
			affectedBoardRectangles[1] = affectedBoardRectangles[0].westBR;
			break;
		//west
		case 3:
			affectedBoardRectangles[0] = targetBR.eastBR;
			affectedBoardRectangles[1] = affectedBoardRectangles[0].eastBR;
			break;
		default:
			break;
		}
	}
	
	

	@Override
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
	}
	
	public void drawLaser(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(4));
		for(int i = 0;i<affectedBoardRectangles.length;i++) {
			g2d.setColor(Commons.cAbility);
			g2d.draw(affectedBoardRectangles[i].rect);
		}
		g2d.setColor(new Color(255,0,50,200));
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));
		g2d.drawLine(0, 0, 0, -Commons.boardRectSize*2);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
	}
	
	public void update() {
		if(!getHasHitTarget()) {
			move();
			checkHitTargetWall();
			parentGP.isPerformingAbility = true;
		}else {
			if(!(Math.abs(angle) > Math.abs(lockedRotation)-1 && Math.abs(angle) < Math.abs(lockedRotation)+1)) {
				rotateInPlace();
				parentGP.isPerformingAbility = true;
			}else {
				for (GamePiece curGP : StagePanel.gamePieces) {
					tryDmgEnemy(curGP);
				}
			}
		}
	}
	
	public void tryDmgEnemy(GamePiece curMovingGP) {
		for(int i = 0;i<affectedBoardRectangles.length;i++) {
			if(curMovingGP.boardRect == affectedBoardRectangles[i]) {
				if(curMovingGP.getIsEnemy() && !isEnemy) {
					curMovingGP.gamePieceBase.getDamaged(dmg);
					StagePanel.particles.add(new Explosion(x, y, 1.5f, angle));
					isDestroyed = true;
				}else if(!curMovingGP.getIsEnemy() && isEnemy){
					curMovingGP.gamePieceBase.getDamaged(dmg);
					StagePanel.particles.add(new Explosion(x, y, 1.5f, angle));
					isDestroyed = true;
				}
				break;
			}
		}
	}
	
	public void checkHitTargetWall() {
		hasHitTarget = targetBR.rect.contains(new Point((int)x,(int)y));
	}
	
	public void rotateInPlace() {
		angle = Stage.Commons.calculateAngleAfterRotation(angle, lockedRotation, 2);
	}
	
}
