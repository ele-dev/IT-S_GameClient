package PlayerStructures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Buttons.GenericButton;
import Stage.Commons;
import Stage.StagePanel;

public class PlayerFortressMenu {
	private int startx,starty;
	private Rectangle rect;
	private PlayerFortress playerFortress;
	private GenericButton exitButton;
	
	private RecruitGamePieceInfoPanel[] recruitGamePieceInfoPanels = new RecruitGamePieceInfoPanel[4];
	RecruitGamePieceInfoPanel lastPressedGamePieceInfoPanel;
	
	public PlayerFortressMenu(PlayerFortress playerFortress) {
		this.startx = 50;
		this.starty = 50;
		this.playerFortress = playerFortress;
		rect = new Rectangle(startx,starty,StagePanel.w-startx*2,StagePanel.h-starty*2);
		exitButton = new GenericButton(rect.x+rect.width-200, rect.y, 200, 100, "Exit", new Color(20,20,20), new Color(255,0,50), 50);
		
		int gap = 20;
		for(byte i = 0; i < recruitGamePieceInfoPanels.length; i++) {
			if(i == 0) {
				recruitGamePieceInfoPanels[i] = new RecruitGamePieceInfoPanel(startx+100, starty+200, 1000, 150, i, playerFortress);
			} else {
				recruitGamePieceInfoPanels[i] = new RecruitGamePieceInfoPanel(startx+100, recruitGamePieceInfoPanels[i-1].getStarty()+150+gap, 1000, 150, i, playerFortress);
			}
		}
	}
	
	public void drawPlayerFortressMenu(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10,250));
		g2d.fill(rect);
		exitButton.drawButton(g2d);
		for(RecruitGamePieceInfoPanel curRGPIP : recruitGamePieceInfoPanels) {
			curRGPIP.drawGamePieceInfo(g2d);
		}
		
		g2d.translate(rect.x, rect.y);
		g2d.setColor(Commons.cCurrency);
		g2d.setFont(new Font("Arial", Font.BOLD, 60));
		g2d.drawString("Coins: " + playerFortress.getCoinAmount(), 100, 150);
		g2d.translate(-rect.x, -rect.y);
	}
	
	public void update() {
		updatePos(StagePanel.camera.getPos());
		exitButton.updateHover(StagePanel.mousePos);
		for(RecruitGamePieceInfoPanel curRGPIP : recruitGamePieceInfoPanels) {
			curRGPIP.update();
		}
	}
	
	private void updatePos(Point cameraPos) {
		rect.x = startx-cameraPos.x;
		rect.y = starty-cameraPos.y;
		
		exitButton.updatePos(cameraPos);
	}
	
	public void tryPresssButton() {
		if(exitButton.isHover()) {
			playerFortress.setSelected(false);
		}
		for(RecruitGamePieceInfoPanel curRGPIP : recruitGamePieceInfoPanels) {
			if(curRGPIP.tryPressButton()) {
				lastPressedGamePieceInfoPanel = curRGPIP;
				return;
			}
		}
	}
	
	
}
