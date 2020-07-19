package GamePieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import Stage.BoardRectangle;
import Stage.Commons;

public abstract class CommanderGamePiece extends GamePiece {
	
	float maxUltCharge = 10;
	float ultCharge;

	public CommanderGamePiece(boolean isEnemy, String name, BoardRectangle boardRect,
			 int maxHealth, double dmg, CommanderGamePiece commanderGamePiece) {
		super(isEnemy, name, boardRect, maxHealth, dmg, commanderGamePiece);
	}
	
	public void updateUltCharge(float value) {
		if(ultCharge+value < maxUltCharge) {
			ultCharge += value;
		}else {
			ultCharge = maxUltCharge;
		}
	}
	
	public void drawUltCharge(Graphics2D g2d) {
		
		Rectangle rectUltChargeBar = new Rectangle((int)rectHitbox.getCenterX() - (int)(rectHitbox.width*0.75),
				(int)rectHitbox.getCenterY() + boardRect.size/4, boardRect.size, 15);
		g2d.setColor(new Color(0,0,0,200));
		g2d.fill(rectUltChargeBar);
		g2d.setColor(Commons.cUltCharge);
		g2d.fillRect(rectUltChargeBar.x,rectUltChargeBar.y,(int)(rectUltChargeBar.width*(ultCharge/maxUltCharge)),rectUltChargeBar.height);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		g2d.draw(rectUltChargeBar);
	}
	
	
	

	

}
