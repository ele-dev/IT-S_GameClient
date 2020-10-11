package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
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

import Abilities.RadialShield;
import Abilities.WallMine;
import Buttons.ButtonEndTurn;
import Environment.DestructibleObject;
import GamePieces.CommanderGamePiece;
import GamePieces.DetonatorPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GamePiece;
import GamePieces.GamePieceBase;
import GamePieces.GunnerPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.SniperPiece;
import GamePieces.TazerPiece;
import LevelDesignTools.LevelDesignTool;
import LevelDesignTools.Levelinitializer;
import Lighting.LightingManager;
import Particles.DestructionParticle;
import Particles.EmptyShell;
import Particles.Particle;
import PlayerStructures.PlayerFortress;

// This is the main Panel where the Game is Happening
@SuppressWarnings("serial")
public class StagePanel extends JPanel {
	public static int w;
	public static int h;
	KL kl = new KL();
	
	// FrameRate/UpdateRate
	Timer tFrameRate;
	Timer tUpdateRate;
	private static int timeStopCounter = 0;
	
	// winnerIndex = 0 (no winner)
	// winnerIndex = 1 (enemy is winner)
	// winnerIndex = 2 (notEnemy is winner)
	private static byte winnerIndex = 0;
	
	// gGmeMap
	public static ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	public static ArrayList<DestructibleObject> destructibleObjects = new ArrayList<DestructibleObject>();
	
	// GamePieces
	public static ArrayList<GamePiece> gamePieces = new ArrayList<GamePiece>();
	public static PlayerFortress enemyFortress,notEnemyFortress;
	public static ArrayList<ValueLabel> valueLabels = new ArrayList<ValueLabel>();
	
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	
	// abilities
	public static ArrayList<RadialShield> radialShields = new ArrayList<RadialShield>();
	public static ArrayList<WallMine> wallMines = new ArrayList<WallMine>();
	
	// game Info
	private ButtonEndTurn buttonEndTurn;
	private static TurnInfo turnInfoPanel;
	
	public static Camera camera;
	public static Point mousePos = new Point(0,0);
	private Point mousePosUntranslated;
	
	public static BoardRectangle curHoverBR;
	public static GamePiece curSelectedGP,curActionPerformingGP;
	
	private Color cBackGround;
	@SuppressWarnings("unused")
	private static LightingManager lightingManager;
	private Levelinitializer levelinitializer;
	public static GameMap gameMap;
	private LevelDesignTool levelDesignTool;
	
	
	public StagePanel(String mapName,ProjectFrame pf) {
		w = ProjectFrame.width; 
		h = ProjectFrame.height;
		setBounds(0, 0, w, h);

		setVisible(true);
		cBackGround = new Color(28,26,36);
		
		// create camera and listener(s)
		camera = new Camera();
		levelinitializer = new Levelinitializer();
		initGameMap(mapName,pf);
		
		if(levelDesignTool == null) {
			initFortresses();
			initGamePieces();
		}
		
		tFrameRate = new Timer(16, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				
			}
		});
		
		tUpdateRate = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateStage();
			}
		});
		tFrameRate.setRepeats(true);
		tUpdateRate.setRepeats(true);
		
		buttonEndTurn = new ButtonEndTurn(ProjectFrame.width-350, ProjectFrame.height -200);

		turnInfoPanel = new TurnInfo();
		
		// makes Cursor invisible
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
		
		// Add the listeners
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
		lightingManager = new LightingManager(w, h,camera);
		
		tFrameRate.start();
		tUpdateRate.start();
	}
	
	public static boolean isEnemyTurn() {
		return turnInfoPanel.getIsEnemyTurn();
	}
	
	// sets an impact-stop countdown (frame freezes)
	public static void impactStop() {
		timeStopCounter = 25;
	}
	
	// sets a screen shake so that the camera will shake for "screenShakeAmountOfFRames" of Frames
	public static void applyScreenShake(int screenShakeAmountOfFRames,int screenShakeMagnitude) {
		camera.applyScreenShake(screenShakeAmountOfFRames,screenShakeMagnitude);
	}
	// adds a dmgLabel (shows the dmg that was taken)
	public static void addDmgLabel(GamePiece targetGP,float dmg) {
		if(!targetGP.isDead) {
			StagePanel.valueLabels.add(new ValueLabel((float)(targetGP.getCenterX()+((Math.random()-0.5)*60)),(float)(targetGP.getCenterY()+((Math.random()-0.5)*60)),"-"+Math.round(dmg),2,0.3f,new Color(255,0,50)));
		}	
	}
	// adds a dmgLabel (shows the dmg that was taken)
	public static void addDmgLabel(int x, int y,float dmg) {
		StagePanel.valueLabels.add(new ValueLabel((float)(x+((Math.random()-0.5)*60)),(float)(y+((Math.random()-0.5)*60)),"-"+Math.round(dmg),2,0.3f,new Color(255,0,50)));
	}
	
//같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같	
//----------------------------------------- Initializer Methods --------------------------------------
//______________________________________________________________________________________________________	
	
	// initializes a map depending on the name (mapName can be null in that case it will load empty map to edit)
	private void initGameMap(String mapName, ProjectFrame pf) {
		if(mapName == null) {
			gameMap = new GameMap(15,30);
			levelDesignTool = new LevelDesignTool();
			addMouseWheelListener(levelDesignTool.mwl);
		}else {
			levelinitializer.readFile(mapName, pf);
			gameMap = new GameMap(levelinitializer);
		}
		
		boardRectangles = gameMap.getBoardRectangles();	
	}
		
	// initializes/creates all GamePieces
	private void initGamePieces() {
		gamePieces.add(new SniperPiece(false, boardRectangles.get(82)));
		gamePieces.add(new SniperPiece(true, boardRectangles.get(105)));
		gamePieces.add(new GunnerPiece(false, boardRectangles.get(100)));
		gamePieces.add(new GunnerPiece(true, boardRectangles.get(103)));
		gamePieces.add(new RocketLauncherPiece(false, boardRectangles.get(137)));
		gamePieces.add(new RocketLauncherPiece(true, boardRectangles.get(120)));
		gamePieces.add(new FlamethrowerPiece(false, boardRectangles.get(145)));
		gamePieces.add(new FlamethrowerPiece(true, boardRectangles.get(171)));
		gamePieces.add(new TazerPiece(false, boardRectangles.get(133)));
		gamePieces.add(new TazerPiece(true, boardRectangles.get(135)));
		gamePieces.add(new DetonatorPiece(false, boardRectangles.get(91)));
		gamePieces.add(new DetonatorPiece(true, boardRectangles.get(69)));
		
		for(GamePiece curGP : gamePieces) {
			curGP.initPathFinder();
			curGP.restoreMovesAndAttacks();
		}
	}
	private void initFortresses() {
		enemyFortress = new PlayerFortress(boardRectangles.get(76),true);
		notEnemyFortress = new PlayerFortress(boardRectangles.get(97),false);
	}
	
//같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같	
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
		g2d.fillRect(0, 0, StagePanel.w, StagePanel.h);
		
		g2d.translate(camera.getPos().x, camera.getPos().y);
		
		drawEveryBoardRectangle(g2d);
//		drawEveryBoardRectangleIndex(g2d);
		
		drawAllDestructionParticles(g2d);
		drawAllEmptyShells(g2d);
		drawEveryWall(g2d);
		drawEveryDestructibleObject(g2d);
		
		drawAllGamePiecePointers(g2d);
		
		// Draw the game pieces/actors, particles, 
		drawAllGamePieces(g2d);
		if(curHoverBR != null && (levelDesignTool != null || !enemyFortress.containsBR(curHoverBR) && !notEnemyFortress.containsBR(curHoverBR))) {
			curHoverBR.tryDrawHover(g2d);
		}
		drawFortresses(g2d);
		drawAllGamePieceHealth(g2d);
		drawAllGamePieceAttacksAbilities(g2d);
		drawParticles(g2d);
		
		g2d.setStroke(new BasicStroke(80));
		g2d.setColor(cBackGround);
		g2d.draw(GameMap.mapRectangle);
		drawValueLabels(g2d);
		
		// lightingManager.drawLight(g2d);
		
		drawMovesPanel(g2d);
		
		if(levelDesignTool == null) {
			if(enemyFortress.isSelected()) enemyFortress.drawFortressMenu(g2d);
			if(notEnemyFortress.isSelected()) notEnemyFortress.drawFortressMenu(g2d);
		}
		
		
		if(levelDesignTool != null) {
			levelDesignTool.drawEquippedBuildObject(g2d);
		}else {
			buttonEndTurn.drawButton(g2d);
			g2d.translate(-camera.getPos().x, -camera.getPos().y);
			turnInfoPanel.drawTurnInfo(g2d);
			if(winnerIndex != 0) {
				drawWinnigScreen(g2d);
			}
			g2d.translate(camera.getPos().x, camera.getPos().y);
		}
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.WHITE);
		g2d.draw(GameMap.mapRectangle);
		g2d.fillOval((int)camera.getCenterOfScreen().x-5, (int)camera.getCenterOfScreen().y-5, 10, 10);
		camera.drawRectOfView(g2d);
		drawCursor(g2d);
		g2d.translate(-camera.getPos().x, -camera.getPos().y);
		
		g2d.dispose();
	}
	
	private void drawWinnigScreen(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10,230));
		Rectangle rectWinScreen = new Rectangle(100,100,w-200,h-200);
		g2d.fill(rectWinScreen);
		g2d.setStroke(new BasicStroke(20));
		g2d.setColor(new Color(5,5,5));
		g2d.draw(rectWinScreen);
		
		g2d.setFont(new Font("Arial",Font.BOLD,100));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		g2d.setColor(Color.WHITE);
		g2d.drawString("The winner", w/2-fontMetrics.stringWidth("The winner")/2, h/2+textHeight/3-textHeight);
		g2d.drawString("is", w/2-fontMetrics.stringWidth("is")/2, h/2+textHeight/3);
		
		if(winnerIndex == 1) {
			textWidth = fontMetrics.stringWidth("Enemy");
			g2d.setColor(Commons.enemyColor);
			g2d.drawString("Enemy", w/2-textWidth/2, h/2+textHeight/3+textHeight);
		}else {
			textWidth = fontMetrics.stringWidth("NotEnemy");
			g2d.setColor(Commons.notEnemyColor);
			g2d.drawString("NotEnemy", w/2-textWidth/2, h/2+textHeight/3+textHeight);
		}
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
		if(levelDesignTool == null) {
			enemyFortress.drawDestructibleObject(g2d);
			notEnemyFortress.drawDestructibleObject(g2d);
		}
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
				if(curGP.getIsEnemy() && turnInfoPanel.getIsEnemyTurn()) {
					curGP.drawPointer(g2d);
				}else 
				if(!curGP.getIsEnemy() && !turnInfoPanel.getIsEnemyTurn()) {
					curGP.drawPointer(g2d);
				}
			}
		}
	}
	
	private void drawCursor(Graphics2D g2d) {
		if(StagePanel.mousePos != null) {
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
		drawAbilities(g2d);
	}
	
	private void drawAbilities(Graphics2D g2d) {
		for(WallMine curWM : wallMines) {
			if(Math.abs(curWM.angle) > Math.abs(curWM.lockedRotation)-2 && Math.abs(curWM.angle) < Math.abs(curWM.lockedRotation)+2 && curWM.getHasHitTarget()) {
				curWM.drawLaser(g2d);
			}
			curWM.drawProjectile(g2d);
		}
		for(RadialShield curRS : radialShields) {
			curRS.drawDestructibleObject(g2d);
			if(curRS.isDestroyed()) {
				radialShields.remove(curRS);
				return;
			}
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
				curBR.drawBoardRectangle(g2d,boardRectangles);
			} 	
		}
	}
		
	private void drawEveryDestructibleObject(Graphics2D g2d) {
		for(DestructibleObject curDO : destructibleObjects) {
			curDO.drawDestructibleObject(g2d);
		}
	}
	
	// draws all the Walls including destructible walls
	private void drawEveryWall(Graphics2D g2d){
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
		for(BoardRectangle curBR : boardRectangles) {
			curBR.drawIndex(g2d);
		}
	}
	
//같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같	
//----------------------------------------- Main Update Method -----------------------------------------
//______________________________________________________________________________________________________
	
	// updates the Stage (moves pieces, moves bullets, updates animations...)
	private void updateStage() {
		if(timeStopCounter > 0) {
			timeStopCounter--;
			return;
		}
		if(levelDesignTool != null || noFortressSelected()) updateBoardRectangles();
		
		updateDestructibleObject();
		updateParticles();
		
		camera.move(GameMap.mapRectangle);
		if(mousePosUntranslated != null) {
			mousePos = new Point((int)(mousePosUntranslated.x-camera.getPos().x), (int)(mousePosUntranslated.y-camera.getPos().y));
		}
		buttonEndTurn.updatePos(camera.getPos());
		
		updateDmgLabels();
		updateGamePieces();
		
		updateAbilities();
		buttonEndTurn.updatePressable(curActionPerformingGP != null || levelDesignTool != null || !noFortressSelected());
		buttonEndTurn.updateHover(mousePos);
		if(levelDesignTool == null)updateFortresses();
	}
	
	public static void checkIfSomeOneWon(){
		if(enemyFortress.isDestroyed()) {
			winnerIndex = 2;
		}else if(notEnemyFortress.isDestroyed()){
			winnerIndex = 1;
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
		enemyFortress.update();
		notEnemyFortress.update();
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
		for(int i = 0;i<particles.size();i++) {
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
			for(int i = 0; i<particles.size();i++) {
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
		for(int i = 0;i<destructibleObjects.size();i++) {
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
	
	private void updateAbilities() {
		for(WallMine curWM : wallMines) {
			curWM.update();
			if(curWM.getIsDestroyed()) {
				wallMines.remove(curWM);
				break;
			}
		}
	}
	

	// updates/changes Turns
	private void updateTurn() {
		turnInfoPanel.toggleTurn();
		curSelectedGP = null;
		for(GamePiece curGP : gamePieces) {
			GamePieceBase curGPB = curGP.gamePieceBase;
			if(turnInfoPanel.getIsEnemyTurn() == curGP.getIsEnemy()) {
				curGPB.regenShield();
				if(curGP instanceof CommanderGamePiece) {
					CommanderGamePiece curCGP = (CommanderGamePiece) curGP;
					curCGP.regenAbilityCharge();
				}
			}
			curGP.restoreMovesAndAttacks();
			
			if(curGP instanceof DetonatorPiece) {
				DetonatorPiece curDP = (DetonatorPiece)curGP;
				curDP.decDetonaterTimers();
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
		if(curHoverBR != null) {
			for(GamePiece curGP : gamePieces) {
				if(curGP.boardRect == curHoverBR && checkIfHasTurn(curGP)) {
					curSelectedGP = curGP;
				}else {
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
		return !enemyFortress.isSelected() && !notEnemyFortress.isSelected();
	}
	private boolean noFortressRecruiting() {
		return !enemyFortress.isRecruitingMode() && !notEnemyFortress.isRecruitingMode();
	}
	
	private void tryPerformActionOnPressedPos() {
		if(curSelectedGP != null && !curSelectedGP.getIsDead() && curHoverBR != null) {	
			if(curHoverBR.isPossibleMove || curHoverBR.isPossibleAttack || curHoverBR.isPossibleAbility) {
				
				if(curHoverBR.isPossibleMove) {
					curSelectedGP.startMove();
				}else if(curHoverBR.isPossibleAttack) {
					curSelectedGP.startAttack(curHoverBR);
				} else {
					CommanderGamePiece curCGP = (CommanderGamePiece) curSelectedGP;
					curCGP.startAbility(curHoverBR);
				}
				curSelectedGP = null;
				resetShowPossibleActivities();
			}
		}
	}
	
	private boolean checkIfHasTurn(GamePiece gamePiece) {
		return gamePiece.getIsEnemy() == turnInfoPanel.getIsEnemyTurn();
	}
	
	private void changedHoverBR() { 
		if(curSelectedGP != null) {
			for(BoardRectangle curBR : boardRectangles) {
				curBR.isPossibleAttack = false;
			}
			if(curSelectedGP.actionSelectionPanel.getMoveButtonIsActive()) {
				curSelectedGP.resetPathFinder(curSelectedGP.boardRect, curHoverBR);
			}else if(curSelectedGP instanceof CommanderGamePiece && curSelectedGP.actionSelectionPanel.getAbilityButtonIsActive()){
				CommanderGamePiece curCGP = (CommanderGamePiece) curSelectedGP;
				curCGP.showPossibleAbilities(curHoverBR);
			}else if(curSelectedGP.actionSelectionPanel.getAttackButtonIsActive()){
				if(curHoverBR.isShowPossibleAttack) {
					curHoverBR.isPossibleAttack = true;
				}
			}
		}
			
	}
	
	private class ML implements MouseListener{

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
					}else if(SwingUtilities.isRightMouseButton(e)){
						levelDesignTool.tryRemoveObject();
					}
				}
				return;
			}
			
			if(winnerIndex == 0) {
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
						if(buttonEndTurn.getIsHover()) {
							updateTurn();
							return;
						}
					}
					enemyFortress.tryPlaceRecruitedGP(curHoverBR);
					notEnemyFortress.tryPlaceRecruitedGP(curHoverBR);
				
					
					if(levelDesignTool == null && noFortressRecruiting()) {
						if(!enemyFortress.isSelected()) {
							enemyFortress.setSelected(enemyFortress.isHover() && isEnemyTurn());
							curSelectedGP = null;
						}else {
							enemyFortress.tryPresssButton();
							return;
						}
						if(!notEnemyFortress.isSelected()) {
							notEnemyFortress.setSelected(notEnemyFortress.isHover() && !isEnemyTurn());	
							curSelectedGP = null;
						}else {
							notEnemyFortress.tryPresssButton();
							return;
						}
					}
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
					}else if(SwingUtilities.isRightMouseButton(e)){
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
			camera.updateMovementPressedKey(e);
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				enemyFortress.tryGetBackToFortressMenu();
				notEnemyFortress.tryGetBackToFortressMenu();
			}
			if(e.getKeyCode() == KeyEvent.VK_K && levelDesignTool != null) {
				String mapName = JOptionPane.showInputDialog("Type in mapName");
				if(mapName != null && mapName.length() > 2) {
					levelinitializer.writeFile(mapName,boardRectangles);
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
