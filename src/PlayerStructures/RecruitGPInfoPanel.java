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
import GamePieces.EMPPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GunnerPiece;
import GamePieces.RapidElectroPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.ShotgunPiece;
import GamePieces.SniperPiece;
import GamePieces.TazerPiece;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.ProjectFrame;
import Stage.StagePanel;
import networking.MsgSpawnGamepiece;

public class RecruitGPInfoPanel {
	
	@SuppressWarnings("unused")
	private int startx, starty;
	private GenericButton rectruitButton;
	private Rectangle rect;
	private PlayerFortress playerFortress;
	private boolean isHover = false;
	private static GamePieceAttackRangePreviewPanel gamePieceAttackRangePreviewPanel;
	
	// GamePiece related
	private int gamePieceCost;
	
	private String gamePieceName;
	private float gamePieceDamage;
	private boolean gamePieceNeededLOS;
	// GamePieceBase related
	private byte gamePieceBaseType;
	private float gamePieceHealth,gamePieceShield;
	private int gamePieceMovementRange;
	
	private Font font;
	
	public RecruitGPInfoPanel(int startx, int starty, int w, int h, String gamePieceName, PlayerFortress playerFortress, int index) {
		rect = new Rectangle(startx, starty, w, h);
		this.startx = startx;
		this.starty = starty;
		rectruitButton = new GenericButton(rect.x+w, rect.y, w/4, h, "recruit", new Color(20,20,20), Commons.cCurrency, w/16);
		this.gamePieceCost = index*10+10;
		this.gamePieceBaseType = (byte) index;
		this.gamePieceName = gamePieceName;
		initGamePieceInfos();
		this.playerFortress = playerFortress;
		
		font = new Font("Arial",Font.BOLD,StagePanel.w/50);
		int size = StagePanel.w-StagePanel.w/40-w-w/4-StagePanel.w/32*2;
		gamePieceAttackRangePreviewPanel = new GamePieceAttackRangePreviewPanel(7, 7, size/2);
	}
	
	public int getX() {
		return rect.x;
	}
	public int getY() {
		return rect.y;
	}
	public int getWidth() {
		return rect.width;
	}
	public int getTotalWidth() {
		return rect.width + rect.width/4;
	}
	public int getHeight() {
		return rect.height;
	}
	
	public boolean isHover() {
		return isHover;
	}
	
	public GenericButton getRectruitButton() {
		return rectruitButton;
	}
	
	public int getGamePieceCost() {
		return gamePieceCost;
	}
	
	private void initGamePieceInfos() {
		switch (gamePieceName) {
		case "Gunner":
			gamePieceDamage = Commons.dmgGunner;
			gamePieceNeededLOS = Commons.neededLOSGunner;
			break;
		case "Shotgun":
			gamePieceDamage = Commons.dmgShotgun;
			gamePieceNeededLOS = Commons.neededLOSShotgun;
			break;
		case "Sniper":
			gamePieceDamage = Commons.dmgSniper;
			gamePieceNeededLOS = Commons.neededLOSSniper;
			break;
		case "Detonator":
			gamePieceDamage = Commons.dmgDetonator;
			gamePieceNeededLOS = Commons.neededLOSDetonator;
			break;
		case "FlameThrower":
			gamePieceDamage = Commons.dmgFlameThrower;
			gamePieceNeededLOS = Commons.neededLOSFlameThrower;
			break;
		case "RocketLauncher":
			gamePieceDamage = Commons.dmgRocketLauncher;
			gamePieceNeededLOS = Commons.neededLOSRocketLauncher;
			break;
		case "EMP":
			gamePieceDamage = Commons.dmgEMP;
			gamePieceNeededLOS = Commons.neededLOSEMP;
			break;
		case "RapidElectro":
			gamePieceDamage = Commons.dmgRapidElectro;
			gamePieceNeededLOS = Commons.neededLOSRapidElectro;
			break;
		case "Tazer":
			gamePieceDamage = Commons.dmgTazer;
			gamePieceNeededLOS = Commons.neededLOSTazer;
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
		case 2:
			gamePieceHealth = Commons.maxHealthType2;
			gamePieceShield = Commons.maxShieldType2;
			gamePieceMovementRange = Commons.MovementRangeType2;
			break;	
			
		}
	}
	public void	placeGamePiece(boolean isRed, BoardRectangle boardRectangle) {
		// Place the game piece on the field and send message to the server
		Point coordinates = new Point(boardRectangle.row, boardRectangle.column);
		MsgSpawnGamepiece spawnGPmessage = new MsgSpawnGamepiece("undefined", coordinates, isRed?Color.RED:Color.BLUE);
		switch (gamePieceName) {
		case "Gunner":
			StagePanel.gamePieces.add(new GunnerPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("GunnerPiece");
			break;
		case "Shotgun":
			StagePanel.gamePieces.add(new ShotgunPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("ShotgunPiece");
			break;
		case "Sniper":
			StagePanel.gamePieces.add(new SniperPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("SniperPiece");
			break;
		case "Detonator":
			StagePanel.gamePieces.add(new DetonatorPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("DetonatorPiece");
			break;
		case "FlameThrower":
			StagePanel.gamePieces.add(new FlamethrowerPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("FlameThrowerPiece");
			break;
		case "RocketLauncher":
			StagePanel.gamePieces.add(new RocketLauncherPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("RocketLauncherPiece");
			break;
		case "EMP":
			StagePanel.gamePieces.add(new EMPPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("EMPPiece");
			break;
		case "RapidElectro":
			StagePanel.gamePieces.add(new RapidElectroPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("RapidElectroPiece");
			break;
		case "Tazer":
			StagePanel.gamePieces.add(new TazerPiece(isRed, boardRectangle));
			spawnGPmessage.setGamePieceClass("TazerPiece");
			break;
		default:
			break;
		}
		
		// send the message
		ProjectFrame.conn.sendMessageToServer(spawnGPmessage);
	}
	
	public int getStarty() {
		return starty;
	}
	
	public void updateHover(Point mousePos) {
		isHover = rect.contains(mousePos);
		if(isHover) {
			gamePieceAttackRangePreviewPanel.initShowPossiblAttack(this.gamePieceName);
		}
		
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
		updateHover(StagePanel.mousePosUntranslated);
		if(playerFortress.getGoldAmount() >= gamePieceCost) {
			rectruitButton.updateHover(StagePanel.mousePosUntranslated);
		}
		rectruitButton.setActive(playerFortress.getGoldAmount() >= gamePieceCost);
	}
	
	public void drawGamePieceInfo(Graphics2D g2d) {
		
		g2d.setColor(isHover?new Color(40,40,40):new Color(20,20,20));
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(8));
		g2d.setColor(isHover?new Color(20,20,20):new Color(40,40,40));
		g2d.draw(rect);
		
		int x = rect.x;
		int y = rect.y;
		g2d.setFont(font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		
		int border = StagePanel.w/100;
		int wUnit = rect.width/2;
		String strs[] = {"Name: ","Dmg: ","Moves: "};
		String strValues[] = {(gamePieceName+""),(gamePieceDamage+""),(gamePieceMovementRange+"")};
		Color colors[] = {Color.WHITE,Commons.cAttack,Commons.cMove};
		for(byte i = 0;i<strs.length;i++) {
			String str = strs[i];
			textWidth = fontMetrics.stringWidth(str);
			g2d.setColor(colors[i]);
			g2d.drawString(str, x+border, y+border+textHeight*(i+1));
			g2d.setColor(Color.WHITE);
			g2d.drawString(strValues[i], x+border+textWidth, y+border+textHeight*(i+1));
		}
		
		String strs0[] = {"Health: ","Shield: ",(gamePieceNeededLOS?"LOS needed":"no LOS needed")};
		String strValues0[] = {(gamePieceHealth+""),(gamePieceShield+""),""};
		Color colors0[] = {Commons.cHealth,Commons.cShield,Commons.cAttack};
		
		for(byte i = 0;i<strs0.length;i++) {
			String str = strs0[i];
			textWidth = fontMetrics.stringWidth(str);
			g2d.setColor(colors0[i]);
			g2d.drawString(str, x+border+wUnit, y+border+textHeight*(i+1));
			g2d.setColor(Color.WHITE);
			g2d.drawString(strValues0[i], x+border+textWidth+wUnit, y+border+textHeight*(i+1));
		}
		
		String str = "Cost: ";
		textWidth = fontMetrics.stringWidth(str);
		g2d.setColor(Commons.cCurrency);
		g2d.drawString(str, x+border+(int)(wUnit*1.5), y+border+textHeight);
		g2d.setColor(Color.WHITE);
		g2d.drawString(gamePieceCost+"", x+border+textWidth+(int)(wUnit*1.5), y+border+textHeight);
		
		rectruitButton.drawButton(g2d);
	}
	
	public void drawHoverGPInfo(RecruitGPInfoPanel firstRGPIP,Graphics2D g2d) {
		Rectangle rectangle = new Rectangle(firstRGPIP.getX()+firstRGPIP.getTotalWidth()+StagePanel.w/32,firstRGPIP.getY(),
				StagePanel.w-StagePanel.w/40-firstRGPIP.getTotalWidth()-StagePanel.w/32*2,StagePanel.h - StagePanel.w/40*6 - StagePanel.w/32);
		g2d.setColor(new Color(20,20,20));
		g2d.fill(rectangle);
		g2d.setStroke(new BasicStroke(8));
		g2d.setColor(new Color(40,40,40));
		g2d.draw(rectangle);
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = fontMetrics.stringWidth(gamePieceName);
		g2d.drawString(gamePieceName, (int)rectangle.getCenterX()-textWidth/2, rectangle.y+textHeight+textHeight/3);
		
		gamePieceAttackRangePreviewPanel.drawGamePieceAttackRangePreviewPanel(g2d, rectangle.x, rectangle.y+rectangle.height/2);
	}

}
