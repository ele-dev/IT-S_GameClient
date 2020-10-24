package PlayerStructures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import Environment.DestructibleObject;
import GamePieces.GamePiece;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class GoldMine extends DestructibleObject {
	
	// nothing = 0
	// enemy = 1
	// notEnemy = 2
	private byte captureState = 1;
	private float maxHealth;
	
	protected ArrayList<BoardRectangle> neighborBoardRectangles = new ArrayList<BoardRectangle>();

	public GoldMine(BoardRectangle boardRectangle) {
		super(boardRectangle, 1, 1, 3, 0);
		maxHealth = 3;
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(curBR != occupiedBRs[0] && Math.abs(curBR.row -occupiedBRs[0].row) <=1 && Math.abs(curBR.column -occupiedBRs[0].column) <=1) {
				neighborBoardRectangles.add(curBR);
			}
		}
	}
	
	public byte getCaptureState() {
		return captureState;
	}
	
	public ArrayList<BoardRectangle> getNeighborBoardRectangles() {
		return neighborBoardRectangles;
	}
	
	public void drawDestructibleObject(Graphics2D g2d) {
		g2d.setColor(captureState==0?new Color(20,20,20):captureState==1?Commons.enemyColor:Commons.notEnemyColor);
		g2d.fill(rectHitbox);
		
		g2d.setStroke(new BasicStroke(8));
		g2d.setColor(new Color(10,10,10));
		g2d.draw(rectHitbox);
		
		for(BoardRectangle curtBR : neighborBoardRectangles) {
			g2d.setColor(Color.GREEN);
			g2d.draw(curtBR.rect);
		}
		
		if(impactFlashCounter > -100) { 
			impactFlashCounter--;
		}
		if(impactFlashCounter > 0) {
			g2d.setColor(new Color(255,255,255,200));
			g2d.translate(rectHitbox.getCenterX(), rectHitbox.getCenterY());
			g2d.rotate(Math.toRadians(rotation));
			int w = (int) (rectHitbox.getWidth()*0.9); 
			int h = (int) (rectHitbox.getHeight()*0.9);
			g2d.fill(new Rectangle(-w/2,-h/2,w,h)); 
			g2d.rotate(Math.toRadians(-rotation));
			g2d.translate(-rectHitbox.getCenterX(), -rectHitbox.getCenterY());
		}
		drawHealthValues(g2d, (int)rectHitbox.getCenterX(), (int)rectHitbox.getCenterY(), 25);
	}
	@Override
	public void getDamaged(float dmg, float attackAngle, boolean isEnemyAttack) {
		health-=dmg;
		if(health<=0) {
			captureState = 0;
			for(GamePiece curGP : StagePanel.gamePieces) {
				if(curGP.getIsEnemy() == isEnemyAttack) {
					StagePanel.tryCaptureGoldMine(curGP);
				}
			}
		}
		StagePanel.addDmgLabel((int)(rectHitbox.getCenterX()+(Math.random()-0.5)*rectHitbox.getWidth()),
		(int)(rectHitbox.getCenterY()+(Math.random()-0.5)*rectHitbox.getWidth()), dmg);
	}
	
	public void capture(boolean isEnemy) {
		captureState = (byte) (isEnemy?1:2);
		health = maxHealth;
	}

}
