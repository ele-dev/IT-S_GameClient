package PlayerStructures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import Environment.DestructibleObject;
import GamePieces.GamePiece;
import Particles.GoldParticle;
import Particles.Particle;
import Particles.TrailParticle;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;
import Stage.ValueLabel;

public class GoldMine extends DestructibleObject {
	
	// nothing = 0
	// red = 1
	// blue = 2
	private byte captureState = 0;
	private float maxHealth;
	
	protected ArrayList<BoardRectangle> neighborBoardRectangles = new ArrayList<BoardRectangle>();
	protected ArrayList<Particle> particles = new ArrayList<Particle>();

	public GoldMine(BoardRectangle boardRectangle) {
		super(boardRectangle, 1, 1, 0, 0);
		maxHealth = Commons.goldMineHealth;
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(curBR != occupiedBRs[0] && Math.abs(curBR.row - occupiedBRs[0].row) <= 1 && Math.abs(curBR.column -occupiedBRs[0].column) <= 1) {
				neighborBoardRectangles.add(curBR);
			}
		}
		for(int i = 0; i < 100; i++) {
			int x =  (int) (neighborBoardRectangles.get(0).getX()+(Math.random())*StagePanel.boardRectSize*3);
			int y =  (int) (neighborBoardRectangles.get(0).getY()+(Math.random())*StagePanel.boardRectSize*3);
			int randomGreyScale = (int) (Math.random() * 30 + 15);
			particles.add(new TrailParticle(x, y, (int)(Math.random() * StagePanel.boardRectSize/3 + StagePanel.boardRectSize/8), 0, new Color(randomGreyScale,randomGreyScale,randomGreyScale), 0, 0, 0));
		}
		for(int i = 0; i < 20; i++) {
			int x =  (int) (neighborBoardRectangles.get(0).getX() + (Math.random()) * StagePanel.boardRectSize*3);
			int y =  (int) (neighborBoardRectangles.get(0).getY() + (Math.random()) * StagePanel.boardRectSize*3);
			particles.add(new TrailParticle(x, y, (int)(Math.random() * StagePanel.boardRectSize/5 + StagePanel.boardRectSize/12), 0, new Color(204 + (int)((Math.random()-0.5)*50),164+(int)((Math.random()-0.5)*50),61), 0, 0, 0));
		}
	}
	public byte getCaptureState() {
		return captureState;
	}
	public ArrayList<BoardRectangle> getNeighborBoardRectangles() {
		return neighborBoardRectangles;
	}
	public void drawDestructibleObject(Graphics2D g2d) {
		g2d.setColor(captureState == 0 ? new Color(20, 20, 20) : captureState == 1 ? Commons.cRed : Commons.cBlue);
		g2d.fill(rectHitbox);
		
		g2d.setStroke(new BasicStroke(StagePanel.boardRectSize/8));
		g2d.setColor(new Color(10, 10, 10));
		g2d.draw(rectHitbox);
		
		if(impactFlashCounter > -100) { 
			impactFlashCounter--;
		}
		if(impactFlashCounter > 0) {
			g2d.setColor(new Color(255, 255, 255, 200));
			g2d.translate(rectHitbox.getCenterX(), rectHitbox.getCenterY());
			g2d.rotate(Math.toRadians(rotation));
			int w = (int) (rectHitbox.getWidth() * 0.9); 
			int h = (int) (rectHitbox.getHeight() * 0.9);
			g2d.fill(new Rectangle(-w/2, -h/2, w, h)); 
			g2d.rotate(Math.toRadians(-rotation));
			g2d.translate(-rectHitbox.getCenterX(), -rectHitbox.getCenterY());
		}
		if(captureState != 0) {
			drawHealthValues(g2d, (int)rectHitbox.getCenterX(), (int)rectHitbox.getCenterY(), 25);
		}	
	}
	
	public void drawNeighborBRs(Graphics2D g2d) {
		g2d.setColor(new Color(20, 20, 20));
		for(BoardRectangle curtBR : neighborBoardRectangles) {
			if(!curtBR.isWall) {
				g2d.fill(curtBR.rect);
			}
		}
		g2d.setColor(new Color(10, 10, 10));
		g2d.setStroke(new BasicStroke(8));
		g2d.drawRect(neighborBoardRectangles.get(0).getX(), neighborBoardRectangles.get(0).getY(), StagePanel.boardRectSize*3, StagePanel.boardRectSize*3);
		for(Particle curP : particles) {
			curP.drawParticle(g2d);
		}
	}
	
	@Override
	public void getDamaged(float dmg, float attackAngle, boolean isRed) {
		health -= dmg;
		if(health <= 0) {
			captureState = 0;
			for(GamePiece curGP : StagePanel.gamePieces) {
				if(curGP.isRed() == isRed) {
					StagePanel.tryCaptureGoldMine(curGP);
				}
			}
		}
		StagePanel.addValueLabel((int)(rectHitbox.getCenterX() + (Math.random() - 0.5) * rectHitbox.getWidth()),
		(int)(rectHitbox.getCenterY() + (Math.random() - 0.5) * rectHitbox.getWidth()), dmg, Commons.cAttack);
	}
	
	public void capture(boolean isRed) {
		captureState = (byte) (isRed ? 1 : 2);
		health = maxHealth;
		StagePanel.valueLabels.add(new ValueLabel(occupiedBRs[0].getCenterX(), occupiedBRs[0].getCenterY(), "Captured", isRed ? Commons.cRed : Commons.cBlue));
	}
	
	public void tryGainGold() {
		if(captureState != 0) {
			for(int i = 0; i < Commons.goldDropGoldMine; i++) {
				StagePanel.particles.add(new GoldParticle((float)occupiedBRs[0].getCenterX(),(float)occupiedBRs[0].getCenterY(),
						(float)(Math.random() * 360), (float)(Math.random() * 360), (float)(Math.random() * 2.5f) + 2f, captureState == 1));
			}
		}
	}

}
