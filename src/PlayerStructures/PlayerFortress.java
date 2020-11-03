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
import Stage.ValueLabel;
import menueGui.GameState;

public class PlayerFortress extends DestructibleObject {
	private boolean isEnemy;
	private Color teamColor;
	private boolean isHover,isSelected,isRecruitingMode;
	private PlayerFortressMenu fortressMenu;
	
	private ArrayList<BoardRectangle> recruitableBoardRectangles = new ArrayList<BoardRectangle>();
	private int coinAmount = Commons.startCoinAmount;
	
	
	private ValueLabel goldCollectLabel;
	private int lastCollectedGoldAmount = 0;
	
	public PlayerFortress(BoardRectangle boardRectangle, Color teamColor) {
		super(boardRectangle, 3, 3, Commons.PlayerFortressHealth, 0);
		this.isEnemy = false;
		this.teamColor = teamColor;
		refreshRecruitableBoardRectangles();
		fortressMenu = new PlayerFortressMenu(this);
	}
	
	public Color getTeamColor() {
		return this.teamColor;
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
	
	public void increaseCoinAmount(int incAmount, int x, int y) {
		coinAmount += incAmount;
		lastCollectedGoldAmount+=incAmount;
		goldCollectLabel = new ValueLabel(x, y, "+"+lastCollectedGoldAmount, Commons.cCurrency);
		
	}
	public int getCoinAmount() {
		return coinAmount;
	}
	
	public void tryGetBackToFortressMenu(){
		if(isRecruitingMode()) {
			setRecruitingMode(false);
			setSelected(true);
		}
	}
	
	public void tryPlaceRecruitedGP(BoardRectangle boardRectangle) {
		if(isRecruitingMode() && getRecruitableBoardRectangles().contains(boardRectangle)) {
			fortressMenu.lastPressedGamePieceInfoPanel.placeGamePiece(teamColor, boardRectangle);
			coinAmount-=fortressMenu.lastPressedGamePieceInfoPanel.getGamePieceCost();
			String str = "-"+fortressMenu.lastPressedGamePieceInfoPanel.getGamePieceCost();
			StagePanel.valueLabels.add(new ValueLabel((int)rectHitbox.getCenterX(),(int)rectHitbox.getCenterY(), str, Commons.cCurrency));
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
			if(occupiedBRs[i] != null)g2d.draw(occupiedBRs[i].rect);
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
		
		drawHealthValues(g2d, (int)rectHitbox.getCenterX(), (int)rectHitbox.getCenterY(), 25);
		
		if(goldCollectLabel != null)goldCollectLabel.drawValueLabel(g2d);
	}
	
	public void tryDrawRecruitableBoardRectangles(Graphics2D g2d) {
		if(isRecruitingMode) {
			for(BoardRectangle curBR : recruitableBoardRectangles) {
				curBR.drawPossibleRecruitPlace(g2d);
			}
		}
	}
	
	public void update() {
		fortressMenu.update();
		updateHover();
		if(goldCollectLabel != null) {
			if(goldCollectLabel.getColor().getAlpha()>10) {
				goldCollectLabel.updateFade();
			} else {
				lastCollectedGoldAmount = 0;
				goldCollectLabel = null;
			}
		}	
	}
	
	public void tryPresssButton() {
		fortressMenu.tryPresssButton();
	}
	@Override
	public void getDamaged(float dmg, float attackAngle, boolean isEnemyAttack) {
		health-=dmg;
		if(health<=0) {
			isDestroyed = true;
			StagePanel.checkIfSomeOneWon();
		}
		StagePanel.addValueLabel((int)(rectHitbox.getCenterX()+(Math.random()-0.5)*rectHitbox.getWidth()),
		(int)(rectHitbox.getCenterY()+(Math.random()-0.5)*rectHitbox.getWidth()), dmg,Commons.cAttack);
	}
	
	private void updateHover() {
		isHover = containsBR(StagePanel.curHoverBR);
	}
	
	// public method to assign fortresses to their side after team colors have been assigned
	public static void assignFortressesToSides() {
		
		StagePanel.enemyFortress.teamColor = GameState.enemyTeamColor;
		StagePanel.enemyFortress.isEnemy = true;
		StagePanel.notEnemyFortress.teamColor = GameState.myTeamColor;
		StagePanel.notEnemyFortress.isEnemy = false;
	}
}
