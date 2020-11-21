package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Stage.Commons;
import Stage.ProjectFrame;
import Stage.StagePanel;
import menueGui.GameState;

public class WinScreen {
	// winnerIndex = 0 (no winner)
	// winnerIndex = 1 (red is winner)
	// winnerIndex = 2 (blue is winner)
	private byte winnerIndex;
	
	// Gui elements in the Winscreen
	private GenericButton leaveButton;
	private Rectangle rect;
	int centerX,centerY;
	public WinScreen(byte winnerIndex) {
		this.winnerIndex = winnerIndex;
		rect = new Rectangle(StagePanel.w/16,StagePanel.h/16,StagePanel.w-StagePanel.w/8,StagePanel.h-StagePanel.h/8);
		centerX = (int)rect.getCenterX();
		centerY = (int)rect.getCenterY();
		leaveButton = new GenericButton(centerX-StagePanel.w/8, centerY-StagePanel.w/16+rect.width/4, StagePanel.w/4, StagePanel.w/12, "Leave Game", new Color(20,20,20), new Color(255,0,50), StagePanel.w/24);
	}
	
	public GenericButton getLeaveButton() {
		return leaveButton;
	}
	
	public void drawWinScreen(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10,230));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(20));
		g2d.setColor(new Color(5,5,5));
		g2d.draw(rect);
		
		g2d.setFont(new Font("Arial",Font.BOLD,StagePanel.w/15));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		g2d.setColor(Color.WHITE);
		g2d.drawString("The winner", centerX-fontMetrics.stringWidth("The winner")/2, centerY+textHeight/3-textHeight);
		g2d.drawString("is", centerX-fontMetrics.stringWidth("is")/2, centerY+textHeight/3);
		String winnerName = "";
		// If the red has won
		if(winnerIndex == 1) {
			winnerName = GameState.myTeamIsRed?ProjectFrame.conn.getUsername():GameState.enemyName;
			textWidth = fontMetrics.stringWidth(winnerName);
			g2d.setColor(Commons.cRed);
			g2d.drawString(winnerName, centerX-textWidth/2, centerY+textHeight/3+textHeight);
		}
		// If blue has won
		else if(this.winnerIndex == 2) {
			winnerName = !GameState.myTeamIsRed?ProjectFrame.conn.getUsername():GameState.enemyName;
			textWidth = fontMetrics.stringWidth(winnerName);
			g2d.setColor(Commons.cBlue);
			g2d.drawString(winnerName, centerX-textWidth/2, centerY+textHeight/3+textHeight);
		} 
		// If nobody has won
		else {
			// ...
		}
	}
	
	public void drawButtons(Graphics2D g2d) {
		leaveButton.drawButton(g2d);
	}
	
	public void update() {
		leaveButton.updateHover(StagePanel.mousePosUntranslated);
	}
}
