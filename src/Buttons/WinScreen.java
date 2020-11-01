package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Stage.ProjectFrame;
import Stage.StagePanel;
import menueGui.GameState;

public class WinScreen {
	// winnerIndex = 0 (no winner)
	// winnerIndex = 1 (enemy is winner)
	// winnerIndex = 2 (notEnemy is winner)
	private byte winnerIndex;
	private int w,h;
	
	private GenericButton leaveButton;
	
	public WinScreen(byte winnerIndex, int w, int h) {
		this.winnerIndex = winnerIndex;
		this.w = w;
		this.h = h;
		leaveButton = new GenericButton(w/2-160, h*3/4, 320, 100, "Leave Game", new Color(20,20,20), new Color(255,0,50), 50);
	}
	
	public GenericButton getLeaveButton() {
		return leaveButton;
	}
	
	public void drawWinScreen(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10,230));
		Rectangle rectWinScreen = new Rectangle(100,100,w-200,h-200);
		g2d.fill(rectWinScreen);
		g2d.setStroke(new BasicStroke(20));
		g2d.setColor(new Color(5,5,5));
		g2d.draw(rectWinScreen);
		
		g2d.setFont(new Font("Arial",Font.BOLD,100));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		g2d.setColor(Color.WHITE);
		g2d.drawString("The winner", w/2-fontMetrics.stringWidth("The winner")/2, h/2+textHeight/3-textHeight);
		g2d.drawString("is", w/2-fontMetrics.stringWidth("is")/2, h/2+textHeight/3);
		
		if(winnerIndex == 1) {
			textWidth = fontMetrics.stringWidth(GameState.enemyName);
			g2d.setColor(GameState.enemyTeamColor);
			g2d.drawString(GameState.enemyName, w/2-textWidth/2, h/2+textHeight/3+textHeight);
		} else {
			textWidth = fontMetrics.stringWidth(ProjectFrame.conn.getUsername());
			g2d.setColor(GameState.myTeamColor);
			g2d.drawString(ProjectFrame.conn.getUsername(), w/2-textWidth/2, h/2+textHeight/3+textHeight);
		}
		
		
	}
	
	public void drawButtons(Graphics2D g2d) {
		leaveButton.drawButton(g2d);
	}
	
	public void update() {
		leaveButton.updatePos(StagePanel.camera.getPos());
		leaveButton.updateHover(StagePanel.mousePos);
		
	}
}
