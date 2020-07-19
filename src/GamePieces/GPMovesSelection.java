package GamePieces;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Buttons.AttackButton;
import Buttons.MoveButton;
import Buttons.*;

public class GPMovesSelection {
	int startx,starty;
	int x,y;
	int w,h;
	Color c;
	public Rectangle rect;
	GamePiece parentGamepiece;
	public AttackButton attackButton;
	public MoveButton moveButton;
	
	public GPMovesSelection(int x, int y, int w, int h, Color c,GamePiece parentGamepiece) {
		this.x = x;
		this.y = y;
		this.startx = x;
		this.starty = y;
		this.w = w;
		this.h = h;
		this.c = c;
		this.rect = new Rectangle(x,y,w,h);
		this.parentGamepiece = parentGamepiece;
		this.attackButton = new AttackButton((int)(x + w*0.05), (int)(y + h * 0.6), (int)(w*0.4), (int)(w*0.4)/2,parentGamepiece);
		this.moveButton = new MoveButton(attackButton.rect.x + attackButton.rect.width + (int)(x + w*0.05), (int)(y + h * 0.6), (int)(w*0.4), (int)(w*0.4)/2,parentGamepiece);
	}
	// draws the MovesPanel including its buttons and the GamePieces info
	public void drawMovesPanel(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.fill(rect);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial",Font.BOLD,40));
		g2d.drawString("name: " + parentGamepiece.name, rect.x +50, rect.y +80);
		g2d.drawString("hp: " + parentGamepiece.health + "/" +  parentGamepiece.maxHealth, rect.x +50, rect.y +150);
		g2d.drawString("dmg: " + parentGamepiece.dmg, rect.x +50, rect.y +200);
		
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
	
	public void updatePos(Point CameraPos) {
		this.x = startx-CameraPos.x;
		this.y = starty-CameraPos.y;
		this.rect.setBounds(x,y,w,h);
		attackButton.updatePos(CameraPos);
		moveButton.updatePos(CameraPos);
	}
	
	
}
