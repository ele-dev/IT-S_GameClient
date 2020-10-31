package PlayerStructures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Buttons.GenericButton;
import GamePieces.DetonatorPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GunnerPiece;
import GamePieces.RocketLauncherPiece;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class RecruitGamePieceInfoPanel{
	private int startx,starty;;
	private GenericButton rectruitButton;
	private Rectangle rect;
	private PlayerFortress playerFortress;
	
	// GamePiece related
	private int gamePieceCost;
	
	private byte gamePieceType;
	private String gamePieceName;
	private float gamePieceDamage;
	
	// GamePieceBase related
	private byte gamePieceBaseType;
	private float gamePieceHealth,gamePieceShield;
	private int gamePieceMovementRange;
	
	public RecruitGamePieceInfoPanel(int startx, int starty, int w, int h, int gamePieceType,PlayerFortress playerFortress) {
		rect = new Rectangle(startx,starty,w,h);
		this.startx = startx;
		this.starty = starty;
		rectruitButton = new GenericButton(rect.x+rect.width-100, rect.y, 200, h, "recruit", new Color(20,20,20), Commons.cCurrency, 40);
		
		this.gamePieceType = (byte) gamePieceType;
		initGamePieceInfos();
		this.playerFortress = playerFortress;
	}
	
	public int getGamePieceCost() {
		return gamePieceCost;
	}
	
	private void initGamePieceInfos() {
		
		switch (gamePieceType) {
		case 0:
			gamePieceName = "Gunner";
			gamePieceDamage = Commons.dmgGunner;
			gamePieceBaseType = Commons.baseTypeGunner;
			break;
		case 1:
			gamePieceName = "FlameThrower";
			gamePieceDamage = Commons.dmgFlameThrower;
			gamePieceBaseType = Commons.baseTypeFlameThrower;
			break;
		case 2:
			gamePieceName = "Detonator";
			gamePieceDamage = Commons.dmgDetonator;
			gamePieceBaseType = Commons.baseTypeDetonator;
			break;
		case 3:
			gamePieceName = "RocketLauncher";
			gamePieceDamage = Commons.dmgRocketLauncher;
			gamePieceBaseType = Commons.baseTypeRocketLauncher;
			break;
		default:
			break;
		}
		
		
		switch (gamePieceBaseType) {
		case 0:
			gamePieceHealth = Commons.maxHealthType0;
			gamePieceShield = Commons.maxShieldType0;
			gamePieceMovementRange = Commons.MovementRangeType0;
			break;
		case 1:
			gamePieceHealth = Commons.maxHealthType1;
			gamePieceShield = Commons.maxShieldType1;
			gamePieceMovementRange = Commons.MovementRangeType1;
			break;
		}
		
		
		gamePieceCost = (int) (Math.random()*5+1)*10;
	}
	
	public void	placeGamePiece(boolean isEnemy, BoardRectangle boardRectangle) {
		switch (gamePieceType) {
		case 0:
			StagePanel.gamePieces.add(new GunnerPiece(isEnemy, boardRectangle));
			break;
		case 1:
			StagePanel.gamePieces.add(new FlamethrowerPiece(isEnemy, boardRectangle));
			break;
		case 2:
			StagePanel.gamePieces.add(new DetonatorPiece(isEnemy, boardRectangle));
			break;
		case 3:
			StagePanel.gamePieces.add(new RocketLauncherPiece(isEnemy, boardRectangle));
			break;
		default:
			break;
		}
	}
	
	public int getStarty() {
		return starty;
	}
	
	public boolean tryPressButton(){
		if(rectruitButton.isHover() && rectruitButton.isActive()) {
			playerFortress.refreshRecruitableBoardRectangles();
			playerFortress.setRecruitingMode(true);
			playerFortress.setSelected(false);
			return true;
		}
		return false;
	}
	
	public void update() {
		updatePos(StagePanel.camera.getPos());
		rectruitButton.updatePos(StagePanel.camera.getPos());
		if(playerFortress.getCoinAmount() >= gamePieceCost) {
			rectruitButton.updateHover(StagePanel.mousePos);
		}
		rectruitButton.setActive(playerFortress.getCoinAmount() >= gamePieceCost);
	}
	
	public void updatePos(Point cameraPos) {
		rect.x = startx-cameraPos.x;
		rect.y = starty-cameraPos.y;
	}
	
	public void drawGamePieceInfo(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(8));
		g2d.setColor(new Color(40,40,40));
		g2d.draw(rect);
		
		int x = rect.x;
		int y = rect.y;
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		
		String strs[] = {"Name: ","Dmg: ","Moves: "};
		String strValues[] = {(gamePieceName+""),(gamePieceDamage+""),(gamePieceMovementRange+"")};
		Color colors[] = {Color.WHITE,Commons.cAttack,Commons.cMove};
		for(byte i = 0;i<strs.length;i++) {
			String str = strs[i];
			textWidth = fontMetrics.stringWidth(str);
			g2d.setColor(colors[i]);
			g2d.drawString(str, x+25, y+10+textHeight*(i+1));
			g2d.setColor(Color.WHITE);
			g2d.drawString(strValues[i], x+25+textWidth, y+10+textHeight*(i+1));
		}
		
		String strs0[] = {"Health: ","Shield: "};
		String strValues0[] = {(gamePieceHealth+""),(gamePieceShield+"")};
		Color colors0[] = {Commons.cHealth,Commons.cShield};
		
		for(byte i = 0;i<strs0.length;i++) {
			String str = strs0[i];
			textWidth = fontMetrics.stringWidth(str);
			g2d.setColor(colors0[i]);
			g2d.drawString(str, x+25+350, y+10+textHeight*(i+1));
			g2d.setColor(Color.WHITE);
			g2d.drawString(strValues0[i], x+25+textWidth+350, y+10+textHeight*(i+1));
		}
		
		String str = "Cost: ";
		textWidth = fontMetrics.stringWidth(str);
		g2d.setColor(Commons.cCurrency);
		g2d.drawString(str, x+25+700, y+10+textHeight);
		g2d.setColor(Color.WHITE);
		g2d.drawString(gamePieceCost+"", x+25+textWidth+700, y+10+textHeight);
		
		rectruitButton.drawButton(g2d);
	}

}
