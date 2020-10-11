package PlayerStructures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import Environment.DestructibleObject;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class PlayerFortress extends DestructibleObject {
	private boolean isEnemy;
	private boolean isHover,isSelected,isRecruitingMode;
	private PlayerFortressMenu fortressMenu;
	
	private ArrayList<BoardRectangle> recruitableBoardRectangles = new ArrayList<BoardRectangle>();
		
	public PlayerFortress(BoardRectangle boardRectangle, boolean isEnemy) {
		super(boardRectangle, 3, 3, 3, 0);
		this.isEnemy = isEnemy;
		refreshRecruitableBoardRectangles();
		fortressMenu = new PlayerFortressMenu(this);
	}
	
	public boolean isHover() {
		return isHover;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public void setRecruitingMode(boolean isRecruitingMode) {
		this.isRecruitingMode = isRecruitingMode;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public boolean isRecruitingMode() {
		return isRecruitingMode;
	}
	
	public ArrayList<BoardRectangle> getRecruitableBoardRectangles() {
		return recruitableBoardRectangles;
	}
	
	public void tryGetBackToFortressMenu(){
		if(isRecruitingMode()) {
			setRecruitingMode(false);
			setSelected(true);
		}
	}
	
	public void tryPlaceRecruitedGP(BoardRectangle boardRectangle) {
		if(isRecruitingMode() && getRecruitableBoardRectangles().contains(boardRectangle)) {
			fortressMenu.lastPressedGamePieceInfoPanel.placeGamePiece(isEnemy, boardRectangle);
			setRecruitingMode(false);
		}
	}
	
	public void refreshRecruitableBoardRectangles() {
		recruitableBoardRectangles.clear();
		BoardRectangle centerBR =  occupiedBRs[4];
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(!curBR.isWall && !curBR.isGap &&  !curBR.isDestructibleObject() && !curBR.hasGamePieceOnIt()) {
				if(((curBR.row == centerBR.row+2 || curBR.row == centerBR.row-2) && curBR.column < centerBR.column+2 && curBR.column > centerBR.column-2) ||
				((curBR.column == centerBR.column+2 || curBR.column == centerBR.column-2) && curBR.row <=centerBR.row+2 && curBR.row >= centerBR.row-2)) {
					recruitableBoardRectangles.add(curBR);
				}
			}
		}
	}
	
	public void drawFortressMenu(Graphics2D g2d) {
		fortressMenu.drawPlayerFortressMenu(g2d);
	}
	@Override
	public void drawDestructibleObject(Graphics2D g2d) {
		g2d.setColor(isEnemy?Commons.enemyColor:Commons.notEnemyColor);
		g2d.fill(rectHitbox);
		g2d.setStroke(new BasicStroke(8));
		for(int i = 0;i<occupiedBRs.length;i++) {
			g2d.setColor(Color.WHITE);
			g2d.draw(occupiedBRs[i].rect);
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
		
		// if the Object is Hover it draws Outline
		if(isHover) {
			g2d.setStroke(new BasicStroke(8));
			g2d.setColor(Commons.cMove);
			int x = (int) rectHitbox.getX();
			int y = (int) rectHitbox.getY();
			int s = (int) rectHitbox.getWidth();
			int soI = (int)4;
			g2d.translate(x, y);
			g2d.drawLine(-soI/2, -soI/2, s/4-soI/2, -soI/2);
			g2d.drawLine(s+soI/2, -soI/2, s*3/4+soI/2, -soI/2);
					
			g2d.drawLine(-soI/2, s+soI/2, s/4-soI/2, s+soI/2);
			g2d.drawLine(s+soI/2, s+soI/2, s*3/4+soI/2, s+soI/2);
					
			g2d.drawLine(-soI/2, -soI/2, -soI/2, s/4-soI/2);
			g2d.drawLine(-soI/2, s+soI/2, -soI/2, s*3/4+soI/2);
					
			g2d.drawLine(s+soI/2, -soI/2, s+soI/2, s/4-soI/2);
			g2d.drawLine(s+soI/2, s+soI/2, s+soI/2, s*3/4+soI/2);
			g2d.translate(-x, -y);
		}
		
		if(isRecruitingMode) {
			for(BoardRectangle curBR : recruitableBoardRectangles) {
				curBR.drawPossibleRecruitPlace(g2d);
			}
		}
		
		drawHealthValues(g2d, (int)rectHitbox.getCenterX(), (int)rectHitbox.getCenterY(), 25);
	}
	
	public void update() {
		fortressMenu.update();
		updateHover();
	}
	
	public void tryPresssButton() {
		fortressMenu.tryPresssButton();
	}
	@Override
	public void getDamaged(float dmg, float attackAngle) {
		health-=dmg;
		if(health<=0) {
			isDestroyed = true;
			StagePanel.checkIfSomeOneWon();
		}
		StagePanel.addDmgLabel((int)(rectHitbox.getCenterX()+(Math.random()-0.5)*rectHitbox.getWidth()),
		(int)(rectHitbox.getCenterY()+(Math.random()-0.5)*rectHitbox.getWidth()), dmg);
	}
	
	private void updateHover() {
		isHover = containsBR(StagePanel.curHoverBR);
	}

}