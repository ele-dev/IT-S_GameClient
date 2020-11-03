package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import menueGui.GameState;

public class TurnInfo {
	private Rectangle rect;
	
	private int turnCounter = 0;
	// is drawn after translation of graphics so it does not need to be moved with the camera
	public TurnInfo() {
		rect = new Rectangle(20, 20, 400, 175);
	}
	
	// toggles the isEnemyTurn variable and counts how often it was toggled
	public void toggleTurn() {
		turnCounter++;
	}
	
	// draws the TurnInfoPanel relative to who has Turn
	public void drawTurnInfo(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20,240)); 
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(4));
		g2d.setColor(!GameState.myTurn ? GameState.enemyTeamColor : GameState.myTeamColor);
		g2d.draw(rect);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		g2d.drawString("Turn:", rect.x +30, rect.y +60);
		FontMetrics metrics = g2d.getFontMetrics();
		int textWidth = metrics.stringWidth("Turn:");
		
		if(!GameState.myTurn) {
			g2d.setColor(GameState.enemyTeamColor);
			g2d.drawString(" " + GameState.enemyName, rect.x +30 + textWidth, rect.y +60);
		} else {
			g2d.setColor(GameState.myTeamColor);
			g2d.drawString(" " + ProjectFrame.conn.getUsername(), rect.x +30 + textWidth, rect.y +60);
		}
				
		g2d.setColor(Color.WHITE);
		g2d.drawString("Turns: " + turnCounter, rect.x+ 30, rect.y+60 + 60);
	}
}
