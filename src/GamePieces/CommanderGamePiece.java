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
			 int maxHealth, float dmg,int movementRange, CommanderGamePiece commanderGamePiece) {
		super(isEnemy, name, boardRect, maxHealth, dmg, movementRange, commanderGamePiece);

	}
	
	public void updateUltCharge(float value) {
		if(ultCharge+value < maxUltCharge) {
			ultCharge += value;
		}else {
			ultCharge = maxUltCharge;
		}
	}
	
	public void drawUltCharge(Graphics2D g2d) {
		Rectangle rectUltChargeBar = new Rectangle((int)getRectHitbox().getCenterX() - (int)(getRectHitbox().width*0.75),
				(int)getRectHitbox().getCenterY() + boardRect.getSize()/4, boardRect.getSize(), 15);
		g2d.setColor(new Color(0,0,0,200));
		g2d.fill(rectUltChargeBar);
		g2d.setColor(Commons.cUltCharge);
		g2d.fillRect(rectUltChargeBar.x,rectUltChargeBar.y,(int)(rectUltChargeBar.width*(ultCharge/maxUltCharge)),rectUltChargeBar.height);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		g2d.draw(rectUltChargeBar);
	}
	
	
	

	

}
