package Buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import GamePieces.GamePiece;
import Stage.Commons;

class AttackButton extends InterfaceButton{
	private GamePiece parentGamepiece;
	 
	public AttackButton(int startx, int starty, int w, GamePiece parentGamepiece) {
		super(startx, starty, w, Commons.cAttack, "Attack");
		this.parentGamepiece = parentGamepiece;
	}

	// draws the button but differently if it is hover or inActive
	@Override
	public void drawButton(Graphics2D g2d) {
		if(isHover && !parentGamepiece.getHasExecutedAttack()) {
			g2d.setColor(cIsHover);
		}else {
			g2d.setColor(c);
		}
		if(isActive) {
			g2d.setColor(c);
			g2d.fill(rect);
			g2d.setColor(new Color(cIsHover.getRed(),cIsHover.getGreen(),cIsHover.getBlue(),100));
		}
		g2d.fill(rect);
		
		
		if(isHover && !parentGamepiece.getHasExecutedAttack()) {
			g2d.setColor(c);
		}else {
			g2d.setColor(cIsHover);
		}
		if(isActive) {
			g2d.setColor(c);
		}
		if(parentGamepiece.getHasExecutedAttack()) {
			g2d.setColor(cInactive);
		} 
		g2d.draw(rect);
		
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics(f);
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	@Override
	public boolean tryPress() {
		if(isHover) {
			if(!parentGamepiece.getHasExecutedAttack()) {
				isActive = true;
				parentGamepiece.showPossibleAttacks();
			}
			return true;
		}
		isActive = false;
		return false;
		
	}
}
