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
	// winnerIndex = 1 (red is winner)
	// winnerIndex = 2 (blue is winner)
	private byte winnerIndex;
	private int w,h;
	
	// Gui elements in the Winscreen
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
		String winnerName = "";
		// If the red has won
		if(winnerIndex == 1) {
			winnerName = GameState.myTeamIsRed?ProjectFrame.conn.getUsername():GameState.enemyName;
			textWidth = fontMetrics.stringWidth(winnerName);
			g2d.setColor(Color.RED);
			g2d.drawString(winnerName, w/2-textWidth/2, h/2+textHeight/3+textHeight);
		}
		// If blue has won
		else if(this.winnerIndex == 2) {
			winnerName = !GameState.myTeamIsRed?ProjectFrame.conn.getUsername():GameState.enemyName;
			textWidth = fontMetrics.stringWidth(winnerName);
			g2d.setColor(Color.BLUE);
			g2d.drawString(winnerName, w/2-textWidth/2, h/2+textHeight/3+textHeight);
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
		leaveButton.updatePos(StagePanel.camera.getPos());
		leaveButton.updateHover(StagePanel.mousePos);
	}
}
