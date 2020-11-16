package PlayerStructures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Buttons.GenericButton;
import Stage.Commons;
import Stage.StagePanel;

public class PlayerFortressMenu {
	private Rectangle rect;
	private PlayerFortress playerFortress;
	private GenericButton exitButton;
	
	private RecruitGPSectorButton[] recruitGPSectorButtons = new RecruitGPSectorButton[3];
	private RecruitGPSectorButton activeRecruitGPSectorButton;
	RecruitGPInfoPanel lastPressedGamePieceInfoPanel;
	
	public PlayerFortressMenu(PlayerFortress playerFortress) {
		this.playerFortress = playerFortress;
		rect = new Rectangle(0,0,StagePanel.w,StagePanel.h);
		int border = StagePanel.w/100;
		exitButton = new GenericButton(rect.x+rect.width-StagePanel.w/6-border, border, StagePanel.w/6, StagePanel.w/16, "Exit", new Color(20,20,20), new Color(255,0,50), StagePanel.w/24);
		
		String[] strs = {"Normal","Fire","Electro"};
		Color[] colors = {new Color(170,170,170),new Color(255,50,0),new Color(58, 160, 155)};
		for(int i = 0;i<recruitGPSectorButtons.length;i++) {
			recruitGPSectorButtons[i] = new RecruitGPSectorButton(StagePanel.w/40+StagePanel.w/2/3*i, StagePanel.w/40 * 3, StagePanel.w/2/3, StagePanel.w/20, strs[i], new Color(20,20,20), colors[i], StagePanel.w/48,playerFortress);
		}
		activeRecruitGPSectorButton = recruitGPSectorButtons[0];
		activeRecruitGPSectorButton.setActive(true);;
	}
	
	public void drawPlayerFortressMenu(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10));
		g2d.fill(rect);
		exitButton.drawButton(g2d);
		for(RecruitGPInfoPanel curRGPIP : activeRecruitGPSectorButton.getRecruitGamePieceInfoPanels()) {
			curRGPIP.drawGamePieceInfo(g2d);
		}
		for(RecruitGPSectorButton curRGPSB : recruitGPSectorButtons) {
			if(activeRecruitGPSectorButton != curRGPSB) {
				curRGPSB.drawButton(g2d);
			}
		}
		activeRecruitGPSectorButton.drawButton(g2d);
		g2d.setColor(Commons.cCurrency);
		g2d.setFont(new Font("Arial", Font.BOLD, StagePanel.w/24));
		g2d.drawString("Coins: " + playerFortress.getCoinAmount(), rect.x+StagePanel.w/40, rect.y+StagePanel.w/40*2);
		
		drawHoverGPInfo(g2d);
	}
	
	public void drawHoverGPInfo(Graphics2D g2d) {
		for(RecruitGPInfoPanel curRGPIP : activeRecruitGPSectorButton.getRecruitGamePieceInfoPanels()) {
			if(curRGPIP.isHover() || curRGPIP.getRectruitButton().isHover()) {
				curRGPIP.drawHoverGPInfo(activeRecruitGPSectorButton.getRecruitGamePieceInfoPanels()[0],g2d);
			}
		}
	}
	
	public void update() {
		exitButton.updateHover(StagePanel.mousePosUntranslated);
		for(RecruitGPInfoPanel curRGPIP : activeRecruitGPSectorButton.getRecruitGamePieceInfoPanels()) {
			curRGPIP.update();
		}
		for(RecruitGPSectorButton curRGPSB : recruitGPSectorButtons) {
			curRGPSB.updateHover(StagePanel.mousePosUntranslated);
			curRGPSB.updateParticles();
		}
	}
	
	public void tryPresssButton() {
		if(exitButton.isHover()) {
			playerFortress.setSelected(false);
		}
		for(RecruitGPInfoPanel curRGPIP : activeRecruitGPSectorButton.getRecruitGamePieceInfoPanels()) {
			if(curRGPIP.tryPressButton()) {
				lastPressedGamePieceInfoPanel = curRGPIP;
				return;
			}
		}
		for(RecruitGPSectorButton curRGPSB : recruitGPSectorButtons) {
			if(curRGPSB.isHover()) {
				for(RecruitGPSectorButton curRGPSB1 : recruitGPSectorButtons) {
					curRGPSB1.setActive(false);
				}
				curRGPSB.setActive(true);
				activeRecruitGPSectorButton = curRGPSB;
			}
		}
	}
}
