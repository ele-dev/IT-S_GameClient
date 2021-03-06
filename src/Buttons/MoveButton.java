package Buttons;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import GamePieces.GamePiece;
import Stage.Commons;

class MoveButton extends InterfaceButton{
	private GamePiece parentGamepiece;
	
	public MoveButton(int startx, int starty, int w, GamePiece parentGamepiece) {
		super(startx, starty, w, Commons.cMove, "Move");
		this.parentGamepiece = parentGamepiece;
	}
	// draws the button but differently if it is hover or blocked
	@Override
	public void drawButton(Graphics2D g2d) {
		if(isHover && !parentGamepiece.getHasExecutedMove()) {
			g2d.setColor(cHover);
		}else {
			g2d.setColor(c);
		}
		if(isActive) {
			g2d.setColor(c);
			g2d.fill(rect);
			g2d.setColor(new Color(cHover.getRed(),cHover.getGreen(),cHover.getBlue(),100));
		}
		g2d.fill(rect);
		
		
		if(isHover && !parentGamepiece.getHasExecutedMove()) {
			g2d.setColor(c);
		}else {
			g2d.setColor(cHover); 
		}
		if(isActive) {
			g2d.setColor(c);
		}
		if(parentGamepiece.getHasExecutedMove()) {
			g2d.setColor(new Color(10,10,10));
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
			if(!parentGamepiece.getHasExecutedMove() && !parentGamepiece.getHasExecutedAttack()) {
				isActive = true;
			}else {
				isActive = false;
			}	
			return true;
		}
		isActive = false;
		return false;
	}
	
	
	
}
