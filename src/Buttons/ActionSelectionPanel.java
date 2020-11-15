package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Stage.Commons;
import Stage.StagePanel;

// Panel from GamePiece where Players can select an action for the GamePiece from (attack or move)
public class ActionSelectionPanel {
	private int w,h;
	private Rectangle rect;
	private GamePiece parentGamepiece;
	private AttackButton attackButton;
	private MoveButton moveButton;
	int border;
	private Font font;
	 
	public ActionSelectionPanel(GamePiece parentGamepiece) {
		w = StagePanel.w/5;
		h = StagePanel.h*2/3;
		int x = StagePanel.w/100;
		int y = StagePanel.h -h -x*2;
		border = w/10;
		rect = new Rectangle(x,y,w,h);
		this.parentGamepiece = parentGamepiece;
		int gapSize = h/20;
		int buttonW = w-border*2;
		attackButton = new AttackButton(x + border,(int)(y + h/2.5) +gapSize,buttonW ,parentGamepiece);
		moveButton = new MoveButton(x + border,  attackButton.rect.y+attackButton.rect.height+gapSize, buttonW, parentGamepiece);
		
		font = new Font("Arial",Font.BOLD,w/10);
		
	}
	
	public boolean getAttackButtonIsActive() {
		return attackButton.isActive;
	}
	public boolean getMoveButtonIsActive() {
		return moveButton.isActive;
	}
	public void setAttackButtonActive(boolean isActive) {
		attackButton.isActive = isActive;
	}
	public void setMoveButtonActive(boolean isActive) {
		moveButton.isActive = isActive;
	}
	 
	// draws the ActionSelectionPanel including it's buttons and the GamePieces info
	public void drawActionSelectionPanel(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20,230));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		g2d.setColor(new Color(10,10,10));
		g2d.draw(rect);
		int x = rect.x;	
		int y = rect.y;
		parentGamepiece.gamePieceBase.drawHealth(g2d, x+border, y+border*2, w-border*2, attackButton.rect.height/2,w/8);
		x +=border;
		y +=h/5;
		
		float dmg = parentGamepiece.getDmg();
		String[] strs = {"Name: ","Dmg: ","Moves: "};
		String[] strValues = {parentGamepiece.getName(),dmg == Math.round(dmg)?Math.round(dmg)+"":dmg+"",parentGamepiece.gamePieceBase.getMovementRange()+""};
		Color[] colors = {parentGamepiece.getColor(),Commons.cAttack,Commons.cMove};
		
		g2d.setFont(font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		
		
		for(int i = 0;i<strs.length;i++) {
			int textWidth = fontMetrics.stringWidth(strs[i]);
			g2d.setColor(colors[i]);
			g2d.drawString(strs[i], x, y+textHeight*(i+1));
			g2d.setColor(Color.WHITE);
			g2d.drawString(strValues[i], x+textWidth, y+textHeight*(i+1));
		}

		
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		attackButton.drawButton(g2d);
		moveButton.drawButton(g2d);
	}
	public void updateActionSelectionPanel() {
		if(attackButton.isActive) {
			parentGamepiece.showPossibleAttacks();
		}
	}
	
	// tries to press any button and returns true if succeeded at doing so
	public boolean tryPressButton() {
		if(moveButton.tryPress()) {
			attackButton.isActive = false;
			return true;
		}
		return attackButton.tryPress();
	} 
	
	public boolean containsMousePos(Point mousePos) {
		return rect.contains(mousePos);
	}
	
	public void updateHover(Point mousePos) {
		attackButton.updateHover(mousePos);
		moveButton.updateHover(mousePos);
	}
	
}
