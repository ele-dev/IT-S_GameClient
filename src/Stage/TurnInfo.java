package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TurnInfo {
	private Rectangle rect;
	private boolean isEnemyTurn = false;
	private Color c = new Color(20,20,20,240);
	
	private int turnCounter = 0;
	// is drawn after translation of graphics so it does not need to be moved with the camera
	public TurnInfo() {
		rect = new Rectangle(20, 20, 400, 175);
	}
	
	public boolean getIsEnemyTurn() {
		return isEnemyTurn;
	}
	// draws the TurnInfoPanel relative to who has Turn
	public void drawTurnInfo(Graphics2D g2d) {
		g2d.setColor(c); 
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(4));
		g2d.setColor(isEnemyTurn?Commons.enemyColor:Commons.notEnemyColor);
		g2d.draw(rect);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		g2d.drawString("Turn:", rect.x +30, rect.y +60);
		FontMetrics metrics = g2d.getFontMetrics();
		int textWidth = metrics.stringWidth("Turn:");
		
		if(isEnemyTurn) {
			g2d.setColor(Commons.enemyColor);
			g2d.drawString(" Enemy", rect.x +30 + textWidth, rect.y +60);
		}else {
			g2d.setColor(Commons.notEnemyColor);
			g2d.drawString(" NotEnemy", rect.x +30 + textWidth, rect.y +60);
		}
				
		g2d.setColor(Color.WHITE);
		g2d.drawString("Turns: " + turnCounter, rect.x+ 30, rect.y+60 + 60);
	}
	// toogles the isEnemyTurn variable and counts how often it was toggled
	public void toggleTurn() {
		isEnemyTurn = !isEnemyTurn;
		turnCounter++;
	}
	
}
