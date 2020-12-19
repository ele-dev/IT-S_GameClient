package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import Buttons.ButtonEndTurn;
import Buttons.GenericButton;
import Buttons.WinScreen;
import Environment.DestructibleObject;
import GamePieces.DetonatorPiece;
import GamePieces.EMPPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GamePiece;
import GamePieces.GamePieceBase;
import GamePieces.GunnerPiece;
import GamePieces.RapidElectroPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.ShotgunPiece;
import GamePieces.SniperPiece;
import GamePieces.TazerPiece;
import LevelDesignTools.LevelDesignTool;
import LevelDesignTools.LevelInitializer;
import Particles.DestructionParticle;
import Particles.EmptyShell;
import Particles.GoldParticle;
import Particles.Particle;
import PlayerStructures.GoldMine;
import PlayerStructures.PlayerFortress;
import menueGui.GameState;
import networking.GenericMessage;
import networking.MsgAccountStats;
import networking.MsgAttack;
import networking.MsgMakeMove;
import networking.SignalMessage;

// This is the main Panel where the Game is Happening
@SuppressWarnings("serial")
public class StagePanel extends JPanel {
	public static int w;
	public static int h;
	
	public static int boardRectSize = 60;
	KL kl = new KL();
	
	// gameMap
	public static ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	public static Rectangle mapRectangle;
	public static int mapRows;
	public static int mapColumns;
	// all DestructibleObjects (does NOT include GoldMines or PlayerFortresses!!!)
	public static ArrayList<DestructibleObject> destructibleObjects = new ArrayList<DestructibleObject>();
	
	public static ArrayList<GoldMine> goldMines = new ArrayList<GoldMine>();
	public static PlayerFortress blueBase, redBase;
	// GamePieces
	public static ArrayList<GamePiece> gamePieces = new ArrayList<GamePiece>();
	
	public static ArrayList<ValueLabel> valueLabels = new ArrayList<ValueLabel>();
	
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	
	// game Info
	private static GenericButton surrenderButton;
	private static ButtonEndTurn endTurnButton;
	private static TurnInfo turnInfoPanel;
	
	public static Camera camera;
	public static Point mousePos = new Point(0,0);
	public static Point mousePosUntranslated = mousePos;
	
	public static BoardRectangle curHoverBR;
	public static GamePiece curSelectedGP,curActionPerformingGP;
	
	private static LevelInitializer levelInitializer;
	public static GameMap gameMap;
	private static LevelDesignTool levelDesignTool;
	
	private static WinScreen winScreen;
	
	public static int amountOfActionsLeft = 0;
	
	private boolean ctrDown = false;
	
	private static BufferedImage bufferedImage;
	
	
	public StagePanel() {
		// Init the dimensions
		w = ProjectFrame.width; 
		h = ProjectFrame.height-20;
		setBounds(0, 0, w, h);
		
		// create camera and timers
		camera = new Camera();
		levelInitializer = new LevelInitializer();
		
		// create and init the buttons 
		endTurnButton = new ButtonEndTurn();
		int border = StagePanel.w/100;
		surrenderButton = new GenericButton(StagePanel.w-(border+StagePanel.w/6),border,StagePanel.w/6,StagePanel.w/16,
				"Surrender", new Color(20,20,20), new Color(255,0,50), StagePanel.w/16/3);
		
		// create and init the TurnInfo display
		turnInfoPanel = new TurnInfo();
		
		// makes Cursor invisible 
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
		
		// Add the listeners and start the timers
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
	}
	
	// initializes a map depending on the name (mapName can be null in that case it will load empty map to edit)
	public static void initGameMap(String mapName) {
		boardRectangles.clear();
		gamePieces.clear();
		destructibleObjects.clear();
		goldMines.clear();
		particles.clear();
		valueLabels.clear();
		if(Commons.editMap) {
			if(mapName == null) {
				gameMap = new GameMap(21,21);
				levelDesignTool = new LevelDesignTool();
				ProjectFrame.stagePanel.addMouseWheelListener(levelDesignTool.mwl);
			} else {
				levelInitializer.readMapFromImage(mapName);
				mapRows = levelInitializer.getMapRows();
				mapColumns = levelInitializer.getMapColumns();
				mapRectangle = new Rectangle(mapColumns*boardRectSize,mapRows*boardRectSize);
				levelDesignTool = new LevelDesignTool();
				ProjectFrame.stagePanel.addMouseWheelListener(levelDesignTool.mwl);
			}
		}else {
			levelInitializer.readMapFromImage(mapName);
			mapRows = levelInitializer.getMapRows();
			mapColumns = levelInitializer.getMapColumns();
			mapRectangle = new Rectangle(mapColumns*boardRectSize,mapRows*boardRectSize);
		}
		
		initFortresses();
//		initGamePieces();
		BoardRectangle.initExtendedInfoStrings();
		if(redBase != null && blueBase != null) {
			if(GameState.myTeamIsRed) {
				camera.setCameraToBasePos(redBase);
			}else {
				camera.setCameraToBasePos(blueBase);
			}
		}
		
		updateAmountPossibleAttacks();
		if(GameState.myTurn) {
			endTurnButton.restartAutoEndTurnCountDown();
		}else {
			endTurnButton.tAutoEndTurn.stop();
		}
	}
	
	// sets a screen shake so that the camera will shake for "screenShakeAmountOfFRames" of Frames
	public static void applyScreenShake(int screenShakeAmountOfFRames, int screenShakeMagnitude) {
		camera.applyScreenShake(screenShakeAmountOfFRames, screenShakeMagnitude);
	}
	// adds a dmgLabel (shows the dmg that was taken)
	public static void addValueLabel(GamePiece targetGP,float value, Color c) {
		StagePanel.valueLabels.add(new ValueLabel((float)(targetGP.getCenterX()+((Math.random()-0.5)*60)),(float)(targetGP.getCenterY()+((Math.random()-0.5)*60)),"-"+Math.round(value), c));	
	}
	// adds a dmgLabel (shows the dmg that was taken)
	public static void addValueLabel(int x, int y,float value,Color c) {
		StagePanel.valueLabels.add(new ValueLabel((float)(x+((Math.random()-0.5)*60)),(float)(y+((Math.random()-0.5)*60)),"-"+Math.round(value),c));
	}
	
	public static void tryCaptureGoldMine(GamePiece gamePiece) {
		for(GoldMine curGM : StagePanel.goldMines) {
			if(curGM.getCaptureState() == 0 && curGM.getNeighborBoardRectangles().contains(gamePiece.getBoardRect())) {
				curGM.capture(gamePiece.isRed());
			}
		}
	}
	
	private void surrender() {
		// Destroy the own fortress to trigger the winnin screen
		if(GameState.myTeamIsRed) {
			redBase.getDamaged(redBase.getHealth(), 0, true);
		} else {
			blueBase.getDamaged(blueBase.getHealth(), 0, true);
		}
		
		// Send leave match message to server to surrender 
		SignalMessage surrenderMsg = new SignalMessage(GenericMessage.MSG_LEAVE_MATCH);
		ProjectFrame.conn.sendMessageAsync(surrenderMsg);
		System.out.println("Sent surrender/leave message to the server");
	}
	
	private void tryLeaveGame() {
		if (winScreen.getLeaveButton().isHover()) {
			
			// Reset game states for the next match
			GameState.isIngame = false;
			GameState.isSearching = false;
			
			// Navigate back to the homescreen panel 
			ProjectFrame.stagePanel.setVisible(false);
			ProjectFrame.homePanel.setVisible(true);
			
			// Terminate the winning screen since it's no longer needed
			StagePanel.winScreen = null;
			
			// Update the player's account stats (not for guests) according the match result
			if(!ProjectFrame.conn.isGuestPlayer()) {
				// increase the played matches counter
				GameState.playedMatches++;
				
				// Only winners earn money
				if(blueBase.isDestroyed() && GameState.enemyTeamColor.equals(Color.BLUE)
					|| redBase.isDestroyed() && GameState.enemyTeamColor.equals(Color.RED)) 
				{
					GameState.money += Commons.winnerMoney;

				} 
				
				// send an account stats message to the server
				MsgAccountStats accStats = new MsgAccountStats(GameState.playedMatches, GameState.money);
				ProjectFrame.conn.sendMessageAsync(accStats);
			}
			
			// At last reset the enemy state data 
			GameState.enemySurrender = false;
			GameState.enemyName = "";
		}
	}
	
//°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°	
//----------------------------------------- Initializer Methods --------------------------------------
//______________________________________________________________________________________________________	
  
	// initializes/creates all GamePieces
	@SuppressWarnings("unused")
	private static void initGamePieces() {
		gamePieces.add(new SniperPiece(false, boardRectangles.get(58)));
		gamePieces.add(new SniperPiece(true, boardRectangles.get(556)));
		gamePieces.add(new SniperPiece(true, boardRectangles.get(188)));
		gamePieces.add(new GunnerPiece(false, boardRectangles.get(102)));
		gamePieces.add(new GunnerPiece(true, boardRectangles.get(559)));
		gamePieces.add(new RocketLauncherPiece(false, boardRectangles.get(137)));
		gamePieces.add(new RocketLauncherPiece(true, boardRectangles.get(432)));
		gamePieces.add(new FlamethrowerPiece(false, boardRectangles.get(145)));
		gamePieces.add(new FlamethrowerPiece(true, boardRectangles.get(508)));
		gamePieces.add(new TazerPiece(false, boardRectangles.get(133)));
		gamePieces.add(new TazerPiece(true, boardRectangles.get(516)));
		gamePieces.add(new DetonatorPiece(false, boardRectangles.get(91)));
		gamePieces.add(new DetonatorPiece(true, boardRectangles.get(472)));
		gamePieces.add(new EMPPiece(false, boardRectangles.get(139)));
		gamePieces.add(new ShotgunPiece(true, boardRectangles.get(514)));
		gamePieces.add(new RapidElectroPiece(true, boardRectangles.get(470)));
		gamePieces.add(new RapidElectroPiece(false, boardRectangles.get(59)));
		gamePieces.add(new RapidElectroPiece(true, boardRectangles.get(490)));
		gamePieces.add(new RapidElectroPiece(false, boardRectangles.get(61)));
		
		
		for(GamePiece curGP : gamePieces) {
			curGP.restoreMovesAndAttacks();
		}
	}
	
	private static void initFortresses() {
		if(levelInitializer.getRedBaseIndex() > -1) {
			redBase = new PlayerFortress(boardRectangles.get(levelInitializer.getRedBaseIndex()), Commons.cRed);
		}
		if(levelInitializer.getBlueBaseIndex() > -1) {
			blueBase = new PlayerFortress(boardRectangles.get(levelInitializer.getBlueBaseIndex()), Commons.cBlue);
		}
	}
	
	//°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°	
	//----------------------------------------- Main Rendering Method --------------------------------------
	//______________________________________________________________________________________________________
	// graphics methode does all the drawing of objects (renders everything)
	public void paintComponent(Graphics g) {

		if(createBufferAsBufferedImage()) {
			Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
//		Graphics2D g2d = (Graphics2D)g;
		// Draw the background
		g2d.setColor(new Color(28, 26, 36));
		g2d.fillRect(0, 0, w, h);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);				
		g2d.translate(camera.getPos().x, camera.getPos().y);
						
		drawEveryBoardRectangle(g2d);
//		drawEveryBoardRectangleIndex(g2d);
		BoardRectangle.drawGravelParticles(g2d);
		drawGoldMines(g2d);
						
		if(redBase != null) { redBase.tryDrawRecruitableBoardRectangles(g2d); }
		if(blueBase != null) { blueBase.tryDrawRecruitableBoardRectangles(g2d); }
						
		drawAllDestructionParticles(g2d);
		drawAllEmptyShells(g2d);
		drawFortresses(g2d);
		drawEveryBoardRectangleState(g2d);
		drawEveryWall(g2d);
		drawEveryDestructibleObject(g2d);
		drawAllGamePiecePointers(g2d);
						
			// Draw the game pieces/actors, particles
		drawAllGamePieces(g2d);
		if(curHoverBR != null && (levelDesignTool != null || !redBase.containsBR(curHoverBR) && !blueBase.containsBR(curHoverBR))) {
			curHoverBR.drawHover(g2d);
		}
						
		drawAllGamePieceHealth(g2d);
		drawAllGamePieceAttacks(g2d);
		drawParticles(g2d);
						
						
						
		g2d.setStroke(new BasicStroke(80));
		g2d.setColor(new Color(28, 26, 36));
						
		g2d.draw(mapRectangle);
						
		if(curHoverBR != null && !curHoverBR.isPossibleAttack && !curHoverBR.isPossibleMove) {
			curHoverBR.drawLabel(g2d,ctrDown);
		}
		drawValueLabels(g2d);
						
		if(levelDesignTool != null) {
			levelDesignTool.drawEquippedBuildObject(g2d);
		} else {
							
							
		g2d.translate(-camera.getPos().x, -camera.getPos().y);
		turnInfoPanel.drawTurnInfo(g2d);
		endTurnButton.drawAmountOfAttacksLeft(g2d);
		endTurnButton.drawButton(g2d);
		endTurnButton.drawParticles(g2d);
		drawMovesPanel(g2d);
		surrenderButton.drawButton(g2d);
							
		if(levelDesignTool == null) {
			if(redBase.isSelected()) { redBase.drawFortressMenu(g2d); }
			if(blueBase.isSelected()) { blueBase.drawFortressMenu(g2d); }
		}
			if(winScreen != null) {
				winScreen.drawWinScreen(g2d);
			}
			if(winScreen != null) { winScreen.drawButtons(g2d); }
			g2d.translate(camera.getPos().x, camera.getPos().y);
		}
		// g2d.setStroke(new BasicStroke(3));
		// g2d.setColor(Color.WHITE);
		// g2d.draw(GameMap.mapRectangle);
		// g2d.fillOval((int)camera.getCenterOfScreen().x-5, (int)camera.getCenterOfScreen().y-5, 10, 10);
		// camera.drawRectOfView(g2d);
						
		drawCursor(g2d);
						
		g2d.translate(-camera.getPos().x, -camera.getPos().y);	
		g.drawImage(bufferedImage, 0, 0, this);
		g.dispose();
		g2d.dispose();
		}

	}
	
	@SuppressWarnings("unused")
	private boolean createBufferAsBufferedImage() {
		if(bufferedImage != null) {
			 return true;
		 }else {
		    bufferedImage = new BufferedImage(w, h,  Transparency.OPAQUE);
		 }
		 return true;
	}
	
	// draws all GamePieces
	private void drawAllGamePieces(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(camera.isInView(curGP.getPos())) {
				curGP.drawGamePiece(g2d);
			}
			// for devs
			// curGP.drawLinesOfSight(g2d);
		}
	}
	
	private void drawFortresses(Graphics2D g2d) {
		if(redBase != null)
			redBase.drawDestructibleObject(g2d);
		if(blueBase != null)
			blueBase.drawDestructibleObject(g2d);
	}
	private void drawGoldMines(Graphics2D g2d) {
		for(GoldMine curGM : goldMines) 
			curGM.drawNeighborBRs(g2d);
		
		for(GoldMine curGM : goldMines) 
			curGM.drawDestructibleObject(g2d);
	}
	
	// draws all Particles
	private void drawParticles(Graphics2D g2d) {
		for(Particle curP : particles) {
			if(!(curP instanceof EmptyShell) && !(curP instanceof DestructionParticle)) {
				if(camera.isInView(curP.getPos())) {
					curP.drawParticle(g2d);
				}
			}
		}
//		System.out.println(particles.size());
	}
		
	private void drawAllDestructionParticles(Graphics2D g2d) {
		for(Particle curP : particles) {
			if(curP instanceof DestructionParticle) {
				if(camera.isInView(curP.getPos())) {
					curP.drawParticle(g2d);
				}
			} 
		}
	}

	// Method must exist because if it is drawn with all other particles like explosions it is drawn on top of GamePieces
	// and does not make sense
	private void drawAllEmptyShells(Graphics2D g2d) {
		for(Particle curP : particles) {
			if(curP instanceof EmptyShell) {
				if(camera.isInView(curP.getPos())) {
					curP.drawParticle(g2d);
				}
			}
		}
	}

	private void drawAllGamePiecePointers(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(camera.isInView(curGP.getPos())) {
				if(curGP.isRed() == GameState.myTeamIsRed && GameState.myTurn) {
					curGP.drawPointer(g2d);
				}
			}
		}
	}
	
	private void drawCursor(Graphics2D g2d) {

		if(mousePos != null) {
			g2d.setColor(Color.WHITE);
			g2d.setStroke(new BasicStroke(5));
			int x = mousePos.x;
			int y = mousePos.y;
			g2d.drawLine(x-10, y, x+10, y);
			g2d.drawLine(x, y-10, x, y+10);
		}
	}
	
	private void drawAllGamePieceHealth(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(camera.isInView(curGP.getPos())) {
				curGP.gamePieceBase.drawHealth(g2d);
			}
		}
	}
	
	private void drawValueLabels(Graphics2D g2d) {
		for(ValueLabel curVL : valueLabels) {
			curVL.drawValueLabel(g2d);
		}
	}
	
	private void drawAllGamePieceAttacks(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			curGP.drawAttack(g2d);
		}
		EMPPiece.drawEMPProjectiles(g2d);
		DetonatorPiece.drawDetonatorProjectiles(g2d);
	}

	private void drawMovesPanel(Graphics2D g2d) {
		if(curSelectedGP != null) {
			curSelectedGP.actionSelectionPanel.drawActionSelectionPanel(g2d);
		}
	}	
	
	// draws every BoardRectangles rectangle that is not a gap and draws the Walls
	private void drawEveryBoardRectangle(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			if(camera.isInView(curBR.getPos()) && curBR.isGap) {
				curBR.drawGapBackGround(g2d);
			} 	
		}
		BoardRectangle.drawWaveParticles(g2d);
		for(BoardRectangle curBR : boardRectangles) {
			if(camera.isInView(curBR.getPos())) {
				curBR.drawBoardRectangle(g2d);
			} 	
		}
	}
	private void drawEveryBoardRectangleState(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			if(camera.isInView(curBR.getPos())) {
				curBR.drawState(g2d);
			} 	
		}
	}
		
	private void drawEveryDestructibleObject(Graphics2D g2d) {
		for(DestructibleObject curDO : destructibleObjects) {
			curDO.drawDestructibleObject(g2d);
		}
	}
	
	// draws all the Walls including destructible walls
	private void drawEveryWall(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.isWall) {
				if(camera.isInView(curBR.getPos())) {
					curBR.drawWall(g2d,gamePieces);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void drawEveryBoardRectangleIndex(Graphics2D g2d) {
		for(int i = 0;i<boardRectangles.size();i++) {
			boardRectangles.get(i).drawIndex(g2d,i);
		}
	}
	
//°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°	
//----------------------------------------- Main Update Method -----------------------------------------
//______________________________________________________________________________________________________
	
	// updates the Stage (moves pieces, moves bullets, updates animations...)
	void update() {
		BoardRectangle.incWaveCounter();
		if(levelDesignTool != null || noFortressSelected()) {
			updateBoardRectangles();
		}
		
		updateDestructibleObject();
		updateParticles();
		
		camera.move(mapRectangle);
		if(mousePosUntranslated != null) {
			mousePos = new Point((int)(mousePosUntranslated.x-camera.getPos().x), (int)(mousePosUntranslated.y-camera.getPos().y));
		}
		
		updateDmgLabels();
		updateGamePieces();
		
		turnInfoPanel.update();
		endTurnButton.updateHover(mousePosUntranslated);
		endTurnButton.updateParticles();
		endTurnButton.setActive(curActionPerformingGP == null && levelDesignTool == null && GameState.myTurn
				&& noFortressSelected() && !goldUncollected());
		
		surrenderButton.updateHover(mousePosUntranslated);
		surrenderButton.setActive(curActionPerformingGP == null && levelDesignTool == null && noFortressSelected() && !goldUncollected());
		
		if(levelDesignTool == null) { updateFortresses(); }
		if(winScreen != null) { winScreen.update(); }
	}
	
	public static void checkIfSomeOneWon() {
		if(blueBase.isDestroyed()) {
			winScreen = new WinScreen((byte)1);
		}
		else if(redBase.isDestroyed()) {
			winScreen = new WinScreen((byte)2);
		}
	}
	
	// updates all the BoardRectangles and the HoverBoardRectangle
	private void updateBoardRectangles() {
		BoardRectangle pHBR = curHoverBR;
		curHoverBR = null;
		for(BoardRectangle curBR : boardRectangles) {
			if(mousePos != null) {
				curBR.updateHover(mousePos);
			}
			if(curBR.isHover()) {
				curHoverBR = curBR;
				if(pHBR != curHoverBR) {
					changedHoverBR();
				} 
			}
		}
	}
	
	private void updateFortresses() {
		redBase.update();
		blueBase.update();
	}
	
	// updates all the GamePieces (checks if they are performing an action,are dead
	// also moves the ActionSelectionPanel relative to the camera position
	private void updateGamePieces() {
		StagePanel.curActionPerformingGP = null;
		EMPPiece.updateEMPProjectiles();
		DetonatorPiece.updateDetonatorProjectiles();
		for(int i = 0; i < gamePieces.size(); i++) {
			GamePiece curGP = gamePieces.get(i);
			if(curGP.isPerformingAction()) {
				curActionPerformingGP = curGP;
			}
			curGP.updateMove();
			curGP.update();
			curGP.updateLinesOfSight();
			curGP.tryDie();
			curGP.updateActionSelectionPanelHover();
			if(curGP.isDead()) {
				gamePieces.remove(i);
			}
		}
		
	}
	
	// updates all Particles
	private void updateParticles() {
		int amountOfEmptyShells = 0;
		for(int i = 0; i < particles.size(); i++) {
			Particle curP = particles.get(i);
			curP.update();
			if(curP instanceof EmptyShell) {
				amountOfEmptyShells++;
			}
			if(curP.isDestroyed()) {
				particles.remove(i);
			}
		}
		if(amountOfEmptyShells > 100) {
			for(int i = 0; i < particles.size(); i++) {
				Particle curP = particles.get(i);
				if(curP instanceof EmptyShell) {
					particles.remove(i);
					break;
				}
			}
		}
	}
	
	// updates destructible objects (removes them if they are flagged as destroyed)
	private void updateDestructibleObject() {
		for(int i = 0; i < destructibleObjects.size(); i++) {
			if(destructibleObjects.get(i).isDestroyed()) {
				destructibleObjects.remove(i);
			}
		}
	}
	
	// updates all the ValueLabels (fades them out)
	private void updateDmgLabels() {
		for(int i = 0; i < valueLabels.size(); i++) {
			ValueLabel curVL = valueLabels.get(i);
			if(curVL.getColor().getAlpha()>10) {
				curVL.updateFade();
			} else {
				valueLabels.remove(i);
			}
		}
	}
	
	// updates/changes Turns
	public static void updateTurn() {
		turnInfoPanel.toggleTurn();
		curSelectedGP = null;
		blueBase.gainGoldForSelf();
		redBase.gainGoldForSelf();
		for(GoldMine curGM : goldMines) {
			curGM.tryGainGold();
		}
		for(GamePiece curGP : gamePieces) {
			curGP.actionSelectionPanel.setAttackButtonActive(false);
			curGP.actionSelectionPanel.setMoveButtonActive(false);
			GamePieceBase curGPB = curGP.gamePieceBase;
			curGPB.regenShield();
			curGP.restoreMovesAndAttacks();
		}
		DetonatorPiece.decDetonaterTimers();
		EMPPiece.decEMPTimers();
		updateAmountPossibleAttacks();
		if(GameState.myTurn) {
			endTurnButton.restartAutoEndTurnCountDown();
		}else {
			endTurnButton.tAutoEndTurn.stop();
		}
		
		redBase.tryGetBackToFortressMenu();
		blueBase.tryGetBackToFortressMenu();
		redBase.setSelected(false);
		blueBase.setSelected(false);
	}
	
	// sets all BoardRectangles to the default color so they don't represent a possible move/attack
	private void resetShowPossibleActivities() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			curBR.isPossibleAttack = false;
			curBR.isShowPossibleAttack = false;
			curBR.isPossibleMove = false;
			curBR.isShowPossibleMove = false;
		}
	}
	
	// selects a piece if it is clicked on and not dead
	private boolean trySelectGamePiece() {
		curSelectedGP = null;
		if(curHoverBR != null && GameState.myTurn) {
			for(GamePiece curGP : gamePieces) {
				if(curGP.getBoardRect().equals(curHoverBR) && curGP.isRed() == GameState.myTeamIsRed) {
					curSelectedGP = curGP;
				} else {
					curGP.actionSelectionPanel.setAttackButtonActive(false);
					curGP.actionSelectionPanel.setMoveButtonActive(false);
				}
			}
		}
		return curSelectedGP != null;
	}

	// presses button if it is clicked on a and not blocked and also a piece is selected
	private boolean tryPressButton() {
		resetShowPossibleActivities();
		return curSelectedGP != null && (curSelectedGP.actionSelectionPanel.tryPressButton() || curSelectedGP.actionSelectionPanel.containsMousePos(StagePanel.mousePosUntranslated));
	}
	private boolean noFortressSelected() {
		boolean status = true;
		if(StagePanel.redBase != null && StagePanel.blueBase != null) {
			status = !redBase.isSelected() && !blueBase.isSelected();
		}
		return status;
	}
	
	private boolean noFortressRecruiting() {
		boolean status = true;
		
		if(StagePanel.redBase != null && StagePanel.blueBase != null) {
			status = !redBase.isRecruitingMode() && !blueBase.isRecruitingMode();
		}
		
		return status;
	}
	
	private boolean goldUncollected() {
		for(int i = 0; i < particles.size(); i++) {
			if(particles.get(i) instanceof GoldParticle) {
				return true;
			}
		}
		return false;
	}
	
	private void tryPerformActionOnPressedPos() {
		
		// First checked if the the move is possible and allowed at the moment
		if(curSelectedGP != null && curHoverBR != null && GameState.myTurn) {
			
			// check additional preconditions for performing any action
			if(curHoverBR.isPossibleMove || curHoverBR.isPossibleAttack) {
				
				// Make a move
				if(curHoverBR.isPossibleMove) {
						
					// Get 2D coordinates of the game piece's position and the destination field 
					Point destination = new Point(curHoverBR.row, curHoverBR.column);
					Point piecePos = new Point(curSelectedGP.getBoardRect().row, curSelectedGP.getBoardRect().column);
					
					// Send a make move message to the server passing player position and movement vector 
					MsgMakeMove moveMessage = new MsgMakeMove(piecePos, destination);
					ProjectFrame.conn.sendMessageAsync(moveMessage);
					System.out.println("Sent move message to server: pos=" + piecePos + " dest=" + destination);
					
					// Trigger the graphical animation for the move
					curSelectedGP.startMove(curHoverBR);
					if(curSelectedGP.getHasExecutedMove())endTurnButton.restartAutoEndTurnCountDown();
				} 
				// start an attack on an enemy player
				else if(curHoverBR.isPossibleAttack) {
					
					// Avoid attacks against the own fortress (they don't make sense and cause trouble additionally)
					if(GameState.myTeamIsRed && StagePanel.redBase.containsBR(curHoverBR) || !GameState.myTeamIsRed && StagePanel.blueBase.containsBR(curHoverBR)) {
						curSelectedGP = null;
						resetShowPossibleActivities();
						return;
					}
					
					// Get 2D coordinates of the attacker's game piece and the vicitim's game piece
					Point attackerPos = new Point(curSelectedGP.getBoardRect().row, curSelectedGP.getBoardRect().column);
					Point victimPos = new Point(curHoverBR.row, curHoverBR.column);
					
					// Send an attack message to the server passing the attacker's and victims positions
					MsgAttack attackMessage = new MsgAttack(attackerPos, victimPos);
					ProjectFrame.conn.sendMessageAsync(attackMessage);
					System.out.println("Sent attack message to the server: attacker=" + attackerPos + " victim=" + victimPos);
					
					// Trigger the graphical animation for the attack
					curSelectedGP.startAttack(curHoverBR);
					if(curSelectedGP.getHasExecutedAttack())endTurnButton.restartAutoEndTurnCountDown();
					
					updateAmountPossibleAttacks();
				} 

				
				curSelectedGP = null;
				resetShowPossibleActivities();
			}
		}
	}
	
	private static void updateAmountPossibleAttacks() {
		amountOfActionsLeft = 0;
		for(GamePiece curGP : gamePieces) {
			if(curGP.isRed() == GameState.myTeamIsRed && !curGP.getHasExecutedAttack()) {
				amountOfActionsLeft++;
			}
		}
	}
	
	private void changedHoverBR() { 
		if(curSelectedGP != null) {
			for(BoardRectangle curBR : boardRectangles) {
				curBR.isPossibleAttack = false;
			}
			if(curSelectedGP.actionSelectionPanel.getMoveButtonIsActive()) {
				curSelectedGP.resetPathFinder(curSelectedGP.getBoardRect(), curHoverBR, false);
				curSelectedGP.showPathBRs();
			}else if(curSelectedGP.actionSelectionPanel.getAttackButtonIsActive()) {
				if(curHoverBR.isShowPossibleAttack) {
					curHoverBR.isPossibleAttack = true;
				}
			}
		}
	}
	
	private class ML implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {
			if (levelDesignTool != null) {
				if(curHoverBR != null) {
					if(SwingUtilities.isLeftMouseButton(e)) {
						levelDesignTool.tryPlaceObject();
					} else if(SwingUtilities.isRightMouseButton(e)) {
						levelDesignTool.tryRemoveObject();
					}
				}
				return;
			}
			
			if(winScreen == null) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					
					if(levelDesignTool != null || (noFortressSelected() && noFortressRecruiting())) {
						
						tryPerformActionOnPressedPos();
						for(GamePiece curGP : gamePieces) {
							if(curGP.isPerformingAction()) {
								return;
							}
						}
						
						if(curSelectedGP == null) {
							if(trySelectGamePiece()) {
								return;
							}
						}
						if(tryPressButton()) {
							return;
						}
						
						// End Turn Button click event
						if(endTurnButton.isActive() && endTurnButton.isHover()) {
			
							// Update the global state variable
							GameState.myTurn = false;
							
							// Send the signal message to the server
							SignalMessage endTurn = new SignalMessage(GenericMessage.MSG_END_TURN);
							ProjectFrame.conn.sendMessageAsync(endTurn);
							
							// process the game events for turn switch
							updateTurn();
							
							return;
						}
						
						// Surrender button click event
						if(surrenderButton.isActive() && surrenderButton.isHover()) {
							surrender();
							return;
						}
						
					}
					redBase.tryPlaceRecruitedGP(curHoverBR);
					blueBase.tryPlaceRecruitedGP(curHoverBR);
				
					
					if(levelDesignTool == null && noFortressRecruiting() && GameState.myTurn) {
						if(GameState.myTeamIsRed) {
							if(!redBase.isSelected()) {
								redBase.setSelected(redBase.isHover());
								curSelectedGP = null;
							} else {
								redBase.tryPresssButton();
								return;
							}
						}else {
							if(!blueBase.isSelected()) {
								blueBase.setSelected(blueBase.isHover());	
								curSelectedGP = null;
							} else {
								blueBase.tryPresssButton();
								return;
							}
						}
					}
				}
			} else {
				if(SwingUtilities.isLeftMouseButton(e)) {
					tryLeaveGame();
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
	
	private class MML implements MouseMotionListener {

		@Override		
		public void mouseDragged(MouseEvent e) {
			mousePosUntranslated = e.getPoint();
			if (levelDesignTool != null) {
				if(curHoverBR != null) {
					if(SwingUtilities.isLeftMouseButton(e)) {
						levelDesignTool.tryPlaceObject();
					} else if(SwingUtilities.isRightMouseButton(e)){
						levelDesignTool.tryRemoveObject();
					}
				}
				return;
			}
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			mousePosUntranslated = e.getPoint();
		}
	}
	
	class KL implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if(winScreen == null && noFortressSelected()) {
				camera.updateMovementPressedKey(e);
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				redBase.tryGetBackToFortressMenu();
				blueBase.tryGetBackToFortressMenu();
			}
			if(e.getKeyCode() == KeyEvent.VK_K && levelDesignTool != null) {
				String mapName = JOptionPane.showInputDialog("Type in mapName");
				if(mapName != null && mapName.length() > 2) {
					levelInitializer.saveMapAsImage(mapName,boardRectangles);
				}
				return;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
				ctrDown = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			camera.updateMovementReleasedKey(e);
			if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
				ctrDown = false;
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {}
	}
}
