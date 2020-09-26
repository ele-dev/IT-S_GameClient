package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import GamePieces.CommanderGamePiece;
import GamePieces.GamePiece;
import Stage.Commons;
import Stage.StagePanel;


public class ActionSelectionPanel {
	private int startx,starty;
	private int w,h;
	private Rectangle rect;
	private GamePiece parentGamepiece;
	private AttackButton attackButton;
	private MoveButton moveButton;
	private AbilityButton abilityButton;
	int border = 40;
	 
	public ActionSelectionPanel(GamePiece parentGamepiece) {
		w = 400;
		h = StagePanel.h-300;
		int x = 0;
		int y = StagePanel.h -h;
		startx = x;
		starty = y;
		
		rect = new Rectangle(x,y,w,h);
		this.parentGamepiece = parentGamepiece;
		int gapSize = 20;
		int buttonW = w-border*2;
		attackButton = new AttackButton(x + border,(int)(y + h/2.5) +gapSize,buttonW ,parentGamepiece);
		if(parentGamepiece instanceof CommanderGamePiece) {
			moveButton = new MoveButton(x + border, attackButton.rect.y+attackButton.rect.height+gapSize, buttonW, parentGamepiece);
			abilityButton = new AbilityButton(x + border,  moveButton.rect.y+moveButton.rect.height+gapSize, buttonW, parentGamepiece);
		}else {
			moveButton = new MoveButton(x + border,  attackButton.rect.y+attackButton.rect.height+gapSize, buttonW, parentGamepiece);
		}
		
	}
	
	public boolean getAttackButtonIsActive() {
		return attackButton.isActive;
	}
	public boolean getMoveButtonIsActive() {
		return moveButton.isActive;
	}
	public boolean getAbilityButtonIsActive() {
		return abilityButton.isActive; 
	}
	public void setAttackButtonActive(boolean isActive) {
		attackButton.isActive = isActive;
	}
	public void setMoveButtonActive(boolean isActive) {
		moveButton.isActive = isActive;
	}
	 
	// draws the MovesPanel including its buttons and the GamePieces info
	public void drawActionSelectionPanel(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20,230));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(10));
		g2d.setColor(new Color(10,10,10));
		g2d.draw(rect);
		int x = rect.x;	
		int y = rect.y;
		parentGamepiece.gamePieceBase.drawHealth(g2d, x+border, y+border+40, w-border*2, 40,40);
		x = x+border;
		y = y+h/5;
		
		g2d.setFont(new Font("Arial",Font.BOLD,40));
		FontMetrics metrics = g2d.getFontMetrics();
		int textHeight = metrics.getHeight();
		String str = "Name: ";
		int textWidth = metrics.stringWidth(str);
		g2d.setColor(parentGamepiece.getColor());
		g2d.drawString(str, x, y+textHeight);
		g2d.setColor(Color.WHITE);
		g2d.drawString(parentGamepiece.getName(), x+textWidth, y+textHeight);
		
		str = "Dmg: ";
		textWidth = metrics.stringWidth(str);
		g2d.setColor(Commons.cAttack);
		g2d.drawString(str, x, y+textHeight*2);
		g2d.setColor(Color.WHITE);
		float dmg = parentGamepiece.getDmg();
		
		g2d.drawString(dmg == Math.round(dmg)?Math.round(dmg)+"":dmg+"",x+textWidth,y+textHeight*2);
		
		str = "Moves: ";
		textWidth = metrics.stringWidth(str);
		g2d.setColor(Commons.cMove);
		g2d.drawString(str, x, y+textHeight*3);
		g2d.setColor(Color.WHITE);
		g2d.drawString(parentGamepiece.gamePieceBase.getMovementRange()+"", x+textWidth,y+textHeight*3);
		
		g2d.setStroke(new BasicStroke(8));
		attackButton.drawButton(g2d);
		moveButton.drawButton(g2d);
		if(abilityButton != null) {
			abilityButton.drawButton(g2d);
			drawAbilityChargeBar(g2d);
		}
	}
	
	public void drawAbilityChargeBar(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(8));
		CommanderGamePiece curPCGP = (CommanderGamePiece) parentGamepiece;
		int x = abilityButton.rect.x;
		int y = abilityButton.rect.y + abilityButton.rect.height+8;
		int w = abilityButton.rect.width;
		float unitLength = w  / curPCGP.getMaxAbilityCharge();
		g2d.setColor(new Color(10,10,10));
		g2d.fillRect(x, y, w, 30);
		g2d.setColor(Commons.cAbility);
		g2d.fillRect(x, y, (int)(unitLength*curPCGP.getAbilityCharge()), 30);
		
		g2d.setColor(new Color(30,30,30));
		for(int i = 1;i<curPCGP.getMaxAbilityCharge();i++) {
			g2d.drawRect(x, y, (int)unitLength*(i), 30);
		}
		g2d.drawRect(x, y, w, 30);
	}
	// updates the show functions/rectangles
	public void updateActionSelectionPanel() {
		if(attackButton.isActive) {
			parentGamepiece.showPossibleAttacks();
		}
	}
	
	public void tryPressButton() {
		if(moveButton.tryPress()) {
			attackButton.isActive = false;
			if(abilityButton != null) {
				abilityButton.isActive = false;
			}
			return;
		}
		if(abilityButton != null && abilityButton.tryPress()) {
			CommanderGamePiece curPCGP = (CommanderGamePiece) parentGamepiece;
			curPCGP.showPossibleAbilities(StagePanel.curHoverBR);
			attackButton.isActive = false;
			return;
		}
		attackButton.tryPress();
	} 
	
	public void updatePos(Point CameraPos) {
		rect.x = startx-CameraPos.x;
		rect.y = starty-CameraPos.y;
		attackButton.updatePos(CameraPos);
		moveButton.updatePos(CameraPos);
		if(abilityButton != null) {
			abilityButton.updatePos(CameraPos);
		}
		
	}
	
	public boolean containsMousePos(Point mousePos) {
		return rect.contains(mousePos);
	}
	
	public void updateHover(Point mousePos) {
		attackButton.updateHover(mousePos);
		moveButton.updateHover(mousePos);
		if(abilityButton != null) {
			abilityButton.updateHover(mousePos);
		}
		
	}
	
}
