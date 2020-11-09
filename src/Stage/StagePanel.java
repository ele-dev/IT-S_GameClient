package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.Timer;

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
import networking.MsgAttack;
import networking.MsgMakeMove;
import networking.SignalMessage;

// This is the main Panel where the Game is Happening
@SuppressWarnings("serial")
public class StagePanel extends JPanel {
	public static int w;
	public static int h;
	KL kl = new KL();
	
	// FrameRate/UpdateRate
	static Timer tFrameRate;
	static Timer tUpdateRate;
	private static int timeStopCounter = 0;
	
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
	private GenericButton surrenderButton;
	private ButtonEndTurn endTurnButton;
	private static TurnInfo turnInfoPanel;
	
	public static Camera camera;
	public static Point mousePos = new Point(0,0);
	private Point mousePosUntranslated;
	
	public static BoardRectangle curHoverBR;
	public static GamePiece curSelectedGP,curActionPerformingGP;
	
	private Color cBackGround;
	private static LevelInitializer levelInitializer;
	public static GameMap gameMap;
	private static LevelDesignTool levelDesignTool;
	
	private static WinScreen winScreen;
	
	
	public StagePanel() {
		// Init the dimensions
		w = ProjectFrame.width; 
		h = ProjectFrame.height;
		setBounds(0, 0, w, h);
		// setVisible(true);
		cBackGround = new Color(28, 26, 36);
		
		// create camera and timers
		camera = new Camera();
		levelInitializer = new LevelInitializer();
		
		if(levelDesignTool != null) {
			addMouseWheelListener(levelDesignTool.mwl);
		}
		tFrameRate = new Timer(16, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		tFrameRate.setRepeats(true);
		tUpdateRate = new Timer(Commons.frametime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateStage();
			}
		});
		tUpdateRate.setRepeats(true);
		
		// create and init the buttons 
		endTurnButton = new ButtonEndTurn(w,h);
		surrenderButton = new GenericButton(w-200, 50, 150, 75, "Surrender", new Color(20,20,20), new Color(255,0,50), 20);
		
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
	
	public static void resetMatch(String mapName) {
		initGameMap(mapName);
		tFrameRate.start();
		tUpdateRate.start();
	}
	
	// initializes a map depending on the name (mapName can be null in that case it will load empty map to edit)
	private static void initGameMap(String mapName) {
		boardRectangles.clear();
		gamePieces.clear();
		destructibleObjects.clear();
		goldMines.clear();
		particles.clear();
		valueLabels.clear();
		if(mapName == null) {
			gameMap = new GameMap(25,25);
			levelDesignTool = new LevelDesignTool();
		} else {
			levelInitializer.readMapFromImage(mapName);
			mapRows = levelInitializer.getMapRows();
			mapColumns = levelInitializer.getMapColumns();
			mapRectangle = new Rectangle(mapColumns*Commons.boardRectSize,mapRows*Commons.boardRectSize);
		}
		initFortresses();
		initGamePieces();
		
		System.out.println(GameState.myTeamIsRed);
	}
	
	// sets an impact-stop countdown (frame freezes)
	public static void impactStop() {
		timeStopCounter = 25;
	}
	
	// sets a screen shake so that the camera will shake for "screenShakeAmountOfFRames" of Frames
	public static void applyScreenShake(int screenShakeAmountOfFRames, int screenShakeMagnitude) {
		camera.applyScreenShake(screenShakeAmountOfFRames, screenShakeMagnitude);
	}
	// adds a dmgLabel (shows the dmg that was taken)
	public static void addValueLabel(GamePiece targetGP,float value, Color c) {
		if(!targetGP.isDead) {
			StagePanel.valueLabels.add(new ValueLabel((float)(targetGP.getCenterX()+((Math.random()-0.5)*60)),(float)(targetGP.getCenterY()+((Math.random()-0.5)*60)),"-"+Math.round(value), c));
		}	
	}
	// adds a dmgLabel (shows the dmg that was taken)
	public static void addValueLabel(int x, int y,float value,Color c) {
		StagePanel.valueLabels.add(new ValueLabel((float)(x+((Math.random()-0.5)*60)),(float)(y+((Math.random()-0.5)*60)),"-"+Math.round(value),c));
	}
	
	public static void tryCaptureGoldMine(GamePiece gamePiece) {
		for(GoldMine curGM : StagePanel.goldMines) {
			if(curGM.getCaptureState() == 0 && curGM.getNeighborBoardRectangles().contains(gamePiece.boardRect)) {
				curGM.capture(gamePiece.getIsRed());
			}
		}
	}
	
	private void surrender() {
		// Destroy the own fortress to trigger the winnin screen
		if(GameState.myTeamIsRed) {
			redBase.getDamaged(redBase.getHealth(), 0, true);
		}else {
			blueBase.getDamaged(blueBase.getHealth(), 0, true);
		}
		
		// Send leave match message to server to surrender 
		SignalMessage surrenderMsg = new SignalMessage(GenericMessage.MSG_LEAVE_MATCH);
		ProjectFrame.conn.sendMessageToServer(surrenderMsg);
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
			
			// At last reset the enemy state data
			GameState.enemySurrender = false;
			GameState.enemyName = "";
			
			tFrameRate.stop();
			tUpdateRate.stop();
		}
	}
	
//°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°	
//----------------------------------------- Initializer Methods --------------------------------------
//______________________________________________________________________________________________________	
  
	// initializes/creates all GamePieces
	private static void initGamePieces() {
		gamePieces.add(new SniperPiece(false, boardRectangles.get(58)));
		gamePieces.add(new SniperPiece(true, boardRectangles.get(105)));
		gamePieces.add(new GunnerPiece(false, boardRectangles.get(102)));
		gamePieces.add(new GunnerPiece(true, boardRectangles.get(103)));
		gamePieces.add(new RocketLauncherPiece(false, boardRectangles.get(137)));
		gamePieces.add(new RocketLauncherPiece(true, boardRectangles.get(120)));
		gamePieces.add(new FlamethrowerPiece(false, boardRectangles.get(145)));
		gamePieces.add(new FlamethrowerPiece(true, boardRectangles.get(172)));
		gamePieces.add(new TazerPiece(false, boardRectangles.get(133)));
		gamePieces.add(new TazerPiece(true, boardRectangles.get(135)));
		gamePieces.add(new DetonatorPiece(false, boardRectangles.get(91)));
		gamePieces.add(new DetonatorPiece(true, boardRectangles.get(69)));
		gamePieces.add(new EMPPiece(false, boardRectangles.get(139)));
		gamePieces.add(new ShotgunPiece(false, boardRectangles.get(101)));
		gamePieces.add(new RapidElectroPiece(true, boardRectangles.get(110)));
		gamePieces.add(new RapidElectroPiece(false, boardRectangles.get(59)));
		gamePieces.add(new RapidElectroPiece(false, boardRectangles.get(60)));
		gamePieces.add(new RapidElectroPiece(false, boardRectangles.get(61)));
		
		for(GamePiece curGP : gamePieces) {
			curGP.initPathFinder();
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
		if(timeStopCounter > 0) {
			timeStopCounter--;
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		
		// Draw the background
		g2d.setColor(this.cBackGround);
		g2d.fillRect(0, 0, w, h);
		
		g2d.translate(camera.getPos().x, camera.getPos().y);
		
		drawEveryBoardRectangle(g2d);
		drawEveryBoardRectangleIndex(g2d);
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
			curHoverBR.tryDrawHover(g2d);
		}
		
		drawAllGamePieceHealth(g2d);
		drawAllGamePieceAttacksAbilities(g2d);
		drawParticles(g2d);
		
		g2d.setStroke(new BasicStroke(80));
		g2d.setColor(cBackGround);
		g2d.draw(mapRectangle);
		drawValueLabels(g2d);
		drawMovesPanel(g2d);
		
		if(levelDesignTool != null) {
			levelDesignTool.drawEquippedBuildObject(g2d);
		} else {
			endTurnButton.drawButton(g2d);
			surrenderButton.drawButton(g2d);
			
			g2d.translate(-camera.getPos().x, -camera.getPos().y);
			turnInfoPanel.drawTurnInfo(g2d);
			endTurnButton.drawParticles(g2d);
			if(winScreen != null) {
				winScreen.drawWinScreen(g2d);
			}
			g2d.translate(camera.getPos().x, camera.getPos().y);
		}
		if(levelDesignTool == null) {
			if(redBase.isSelected()) { redBase.drawFortressMenu(g2d); }
			if(blueBase.isSelected()) { blueBase.drawFortressMenu(g2d); }
		}
		// g2d.setStroke(new BasicStroke(3));
		// g2d.setColor(Color.WHITE);
		// g2d.draw(GameMap.mapRectangle);
		// g2d.fillOval((int)camera.getCenterOfScreen().x-5, (int)camera.getCenterOfScreen().y-5, 10, 10);
		// camera.drawRectOfView(g2d);
		
		if(winScreen != null) { winScreen.drawButtons(g2d); }
		drawCursor(g2d);
		
		g2d.translate(-camera.getPos().x, -camera.getPos().y);
		
		g2d.dispose();
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
				if(curGP.getIsRed() == GameState.myTeamIsRed && GameState.myTurn) {
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
	
	private void drawAllGamePieceAttacksAbilities(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			curGP.drawAttack(g2d);
		}
	}

	private void drawMovesPanel(Graphics2D g2d) {
		if(curSelectedGP != null) {
			curSelectedGP.actionSelectionPanel.drawActionSelectionPanel(g2d);
		}
	}	
	
	// draws every BoardRectangles rectangle that is not a gap and draws the Walls
	private void drawEveryBoardRectangle(Graphics2D g2d) {
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
	
	private void drawEveryBoardRectangleIndex(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			curBR.drawIndex(g2d);
		}
	}
	
//°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°°	
//----------------------------------------- Main Update Method -----------------------------------------
//______________________________________________________________________________________________________
	
	// updates the Stage (moves pieces, moves bullets, updates animations...)
	private void updateStage() {
		if(timeStopCounter > 0) {
			timeStopCounter--;
			return;
		}
		
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
		
		endTurnButton.updateHover(mousePos);
		endTurnButton.updatePos(camera.getPos());
		endTurnButton.setActive(curActionPerformingGP == null && levelDesignTool == null && GameState.myTurn
				&& noFortressSelected() && !goldUncollected());
		
		surrenderButton.updateHover(mousePos);
		surrenderButton.updatePos(camera.getPos());
		surrenderButton.setActive(curActionPerformingGP == null && levelDesignTool == null && noFortressSelected() && !goldUncollected());
		
		if(levelDesignTool == null) { updateFortresses(); }
		if(winScreen != null) { winScreen.update(); }
	}
	
	public static void checkIfSomeOneWon() {
		
		if(blueBase.isDestroyed()) {
			winScreen = new WinScreen((byte)1, w, h);
		}
		else if(redBase.isDestroyed()) {
			winScreen = new WinScreen((byte)2, w, h);
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
		if(curHoverBR != null) {
			curHoverBR.tryAnimate();
		}
	}
	
	private void updateFortresses() {
		if(StagePanel.redBase != null && StagePanel.blueBase != null) {
			redBase.update();
			blueBase.update();
		}
	}
	
	// updates all the GamePieces (checks if they are performing an action,are dead
	// also moves the ActionSelectionPanel relative to the camera position
	private void updateGamePieces() {
		StagePanel.curActionPerformingGP = null;
		for(int i = 0; i < gamePieces.size(); i++) {
			GamePiece curGP = gamePieces.get(i);
			if(curGP.isPerformingAction()) {
				curActionPerformingGP = curGP;
			}
			curGP.updateMove();
			curGP.update();
			curGP.tryDie();
			curGP.updateActionSelectionPanelPos(camera.getPos(), mousePos);
			if(curGP.getIsDead()) {
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
	public void updateTurn() {
		
		turnInfoPanel.toggleTurn();
		curSelectedGP = null;
		for(GoldMine curGM : goldMines) {
			curGM.tryGainGold();
		}
		for(GamePiece curGP : gamePieces) {
			curGP.actionSelectionPanel.setAttackButtonActive(false);
			curGP.actionSelectionPanel.setMoveButtonActive(false);
			GamePieceBase curGPB = curGP.gamePieceBase;
			curGPB.regenShield();
			curGP.restoreMovesAndAttacks();
			
			if(curGP instanceof DetonatorPiece) {
				((DetonatorPiece)(curGP)).decDetonaterTimers();
			}
			if(curGP instanceof EMPPiece) {
				((EMPPiece)(curGP)).decEMPTimers();
			}
		}
	}
	
	// sets all BoardRectangles to the default color so they don't represent a possible move/attack
	private void resetShowPossibleActivities() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			curBR.isPossibleAbility = false;
			curBR.isShowPossibleAbility = false;
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
				if(curGP.boardRect.equals(curHoverBR) && curGP.getIsRed() == GameState.myTeamIsRed) {
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
		return curSelectedGP != null && (curSelectedGP.actionSelectionPanel.tryPressButton() || curSelectedGP.actionSelectionPanel.containsMousePos(StagePanel.mousePos));
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
		if(curSelectedGP != null && !curSelectedGP.getIsDead() && curHoverBR != null && GameState.myTurn) {
			
			// check additional preconditions for performing any action
			if(curHoverBR.isPossibleMove || curHoverBR.isPossibleAttack || curHoverBR.isPossibleAbility) {
				
				// Make a move
				if(curHoverBR.isPossibleMove) {
						
					// Get 2D coordinates of the game piece's position and the destination field 
					Point destination = new Point(curHoverBR.row, curHoverBR.column);
					Point piecePos = new Point(curSelectedGP.boardRect.row, curSelectedGP.boardRect.column);
					
					// Send a make move message to the server passing player position and movement vector 
					MsgMakeMove moveMessage = new MsgMakeMove(piecePos, destination);
					ProjectFrame.conn.sendMessageToServer(moveMessage);
					System.out.println("Sent move message to server: pos=" + piecePos + " dest=" + destination);
					
					// Trigger the graphical animation for the move
					curSelectedGP.startMove(curHoverBR);
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
					Point attackerPos = new Point(curSelectedGP.boardRect.row, curSelectedGP.boardRect.column);
					Point victimPos = new Point(curHoverBR.row, curHoverBR.column);
					
					// Send an attack message to the server passing the attacker's and victims positions
					MsgAttack attackMessage = new MsgAttack(attackerPos, victimPos);
					ProjectFrame.conn.sendMessageToServer(attackMessage);
					System.out.println("Sent attack message to the server: attacker=" + attackerPos + " victim=" + victimPos);
					
					// Trigger the graphical animation for the attack
					curSelectedGP.startAttack(curHoverBR);
				} 

				
				curSelectedGP = null;
				resetShowPossibleActivities();
			}
		}
	}
	
	private void changedHoverBR() { 
		if(curSelectedGP != null) {
			for(BoardRectangle curBR : boardRectangles) {
				curBR.isPossibleAttack = false;
			}
			if(curSelectedGP.actionSelectionPanel.getMoveButtonIsActive()) {
				curSelectedGP.resetPathFinder(curSelectedGP.boardRect, curHoverBR, false);
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
							ProjectFrame.conn.sendMessageToServer(endTurn);
							
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
			if(winScreen == null) {
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
		}

		@Override
		public void keyReleased(KeyEvent e) {
			camera.updateMovementReleasedKey(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {}
	}
}
