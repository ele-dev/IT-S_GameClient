package Buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import GamePieces.GamePiece;
import Stage.Commons;


public class GPMovesSelection {
	private int startx,starty;
	private int x,y;
	private int w,h;
	private Color c;
	private Rectangle rect;
	private GamePiece parentGamepiece;
	private AttackButton attackButton;
	private MoveButton moveButton;
	 
	public GPMovesSelection(GamePiece parentGamepiece) {
		this.x = 20;
		this.y = Commons.hf -40 -500;
		this.startx = x;
		this.starty = y;
		this.w = 400;
		this.h = 500;
		this.c = Commons.cGPMovesPanel;
		this.rect = new Rectangle(x,y,w,h);
		this.parentGamepiece = parentGamepiece;
		this.attackButton = new AttackButton((int)(x + w*0.05), (int)(y + h * 0.6),parentGamepiece);
		this.moveButton = new MoveButton(attackButton.rect.x + attackButton.rect.width+50, (int)(y + h * 0.6),parentGamepiece);
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
	
	// draws the MovesPanel including its buttons and the GamePieces info
	public void drawMovesPanel(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.fill(rect);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial",Font.BOLD,40));
		g2d.drawString("name: " + parentGamepiece.getName(), rect.x +50, rect.y +80);
		g2d.drawString("hp: " + parentGamepiece.getHealth() + "/" +  parentGamepiece.getMaxHealth(), rect.x +50, rect.y +150);
		g2d.drawString("dmg: " + parentGamepiece.getDmg(), rect.x +50, rect.y +200);
		
		attackButton.drawButton(g2d);
		moveButton.drawButton(g2d);
	}
	// updates the show functions/rectangles
	public void updateMovesPanel() {
		if(attackButton.isActive) {
			parentGamepiece.showPossibleAttacks();
		}
		if(moveButton.isActive) {
			parentGamepiece.showPossibleMoves();
		}
	}
	
	public void tryPressButton() {
		if(moveButton.getIsHover()) {
			moveButton.press();
			return;
		}else {
			moveButton.isActive = false;
		}
		if(attackButton.getIsHover()) {
			attackButton.press();
		}else {
			attackButton.isActive = false;
		}
	}
	
	public void updatePos(Point CameraPos) {
		this.x = startx-CameraPos.x;
		this.y = starty-CameraPos.y;
		this.rect.setBounds(x,y,w,h);
		attackButton.updatePos(CameraPos);
		moveButton.updatePos(CameraPos);
	}
	
	public boolean containsMousePos(Point mousePos) {
		return rect.contains(mousePos);
	}
	
	public void updateHover(Point mousPos) {
		attackButton.updateHover(mousPos);
		moveButton.updateHover(mousPos);
	}
	
	
}
