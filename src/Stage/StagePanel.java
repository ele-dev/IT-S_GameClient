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
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Abilities.RadialShield;
import Abilities.WallMine;
import Buttons.ButtonEndTurn;
import GamePieces.CommanderGamePiece;
import GamePieces.DetonatorPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GamePiece;
import GamePieces.GamePieceBase;
import GamePieces.GunnerPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.SniperPiece;
import LevelDesignTools.Levelinitializer;
import Lighting.LightingManager;
import Particles.EmptyShell;
import Particles.Particle;

public class StagePanel extends JPanel{
	int x,y;
	static int w;
	static int h;
	KL kl;
	
	public static final int amountOfRows= 16;
	public static final int amountOfColumns = 16;
	final int boardRectSize = Commons.boardRectSize;
	public static Rectangle mapRectangle;
	
	Timer tFrameRate;
	Timer tUpdateRate;
	private static int timeStopCounter = 0;

	public static ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	public static ArrayList<GamePiece> gamePieces = new ArrayList<GamePiece>();
	public static ArrayList<ValueLabel> valueLabels = new ArrayList<ValueLabel>();
	
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	
	// abilities
	public static ArrayList<RadialShield> radialShields = new ArrayList<RadialShield>();
	public static ArrayList<WallMine> wallMines = new ArrayList<WallMine>();
	
	ButtonEndTurn buttonEndTurn;
	static TurnInfo turnInfoPanel;
	
	DetonatorPiece detNotEnemy;
	DetonatorPiece detEnemy;
	
	static Camera camera;
	Point mousePos;
	Point mousePosUntranslated;
	
	public static BoardRectangle curHoverBR;
	public static GamePiece curSelectedGP,curActionPerformingGP;
	
	boolean createLevel = false;
	
	Rectangle rectLevelBorder;
	Color cBackGround;
	public static LightingManager lightingManager;
	Levelinitializer levelinitializer;
	
	public StagePanel(int x, int y) {
		this.x = x;
		this.y = y;
		this.w = ProjektFrame.width;
		this.h = ProjektFrame.height;
		setBounds(x, y, w, h);
		setVisible(true);
		cBackGround = new Color(28,26,36);
		mapRectangle = new Rectangle(amountOfColumns*Commons.boardRectSize,amountOfRows*Commons.boardRectSize);
		
		camera = new Camera();
		kl = new KL();
		levelinitializer = new Levelinitializer();
		initBoard();			
		initGamePieces();
		rectLevelBorder = new Rectangle(0,0,boardRectSize*amountOfColumns,boardRectSize*amountOfRows);
		
		tFrameRate = new Timer(16, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
				
			}
		});
		tFrameRate.setRepeats(true);
		tFrameRate.start();
		
		tUpdateRate = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateStage();
				
			}
		});
		tUpdateRate.setRepeats(true);
		tUpdateRate.start();
		
		buttonEndTurn = new ButtonEndTurn(ProjektFrame.width-350, ProjektFrame.height -200);
		turnInfoPanel = new TurnInfo();
		
		
		// makes Cursor invisible
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
		
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
		lightingManager = new LightingManager(w, h,camera);
	}
	
	public static boolean getIsEnemyTurn() {
		return turnInfoPanel.getIsEnemyTurn();
	}
	
	// creates every BoardRectangle and gives it an index
	// counts with an index and makes modulo magic to create a tile/chess pattern
	// it also creates the gaps defined at the end of the function
	private void initBoard() {
		int index = 0;
		for(int i = 0;i<amountOfRows;i++) {
			for(int j = 0;j<amountOfColumns;j++) {
				if(i%2==0) {
					if(j%2==0) {
						boardRectangles.add(new BoardRectangle(boardRectSize, i, j, false,index));
					}else {
						boardRectangles.add(new BoardRectangle(boardRectSize, i, j, true,index));
					}
				}else {
					if(j%2==0) {
						boardRectangles.add(new BoardRectangle(boardRectSize, i, j, true,index));
					}else {
						boardRectangles.add(new BoardRectangle(boardRectSize, i, j, false,index));
					}
				}
				index++;
			}
		}
		
		for(BoardRectangle curBR : boardRectangles) {
			curBR.initAdjecantBRs();
		}
		levelinitializer.readFile();
		boardRectangles = levelinitializer.getBoardRectangles();
		initWallSpriteConnections();
		
	}

		
	// connects all Walls together (purely Visual)
	private void initWallSpriteConnections() {
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.isWall) {
				curBR.initWallSprites(this);
			}
		}
	}
		
	// initializes/creates all GamePieces
	private void initGamePieces() {
		gamePieces.add(new SniperPiece(false, boardRectangles.get(98)));
		gamePieces.add(new SniperPiece(true, boardRectangles.get(105)));
		gamePieces.add(new GunnerPiece(false, boardRectangles.get(100)));
		gamePieces.add(new GunnerPiece(true, boardRectangles.get(103)));
		gamePieces.add(new RocketLauncherPiece(false, boardRectangles.get(137)));
		gamePieces.add(new RocketLauncherPiece(true, boardRectangles.get(120)));
		gamePieces.add(new FlamethrowerPiece(false, boardRectangles.get(129)));
		gamePieces.add(new FlamethrowerPiece(true, boardRectangles.get(171)));
			
		detNotEnemy = new DetonatorPiece(false, boardRectangles.get(92));
		detEnemy = new DetonatorPiece(true, boardRectangles.get(69));
		gamePieces.add(detNotEnemy);
		gamePieces.add(detEnemy);
		
		for(GamePiece curGP : gamePieces) {
			curGP.initPathFinder();
		}
	}
	
	public static void impactStop() {
		timeStopCounter = 5;
	}
	
	public static void addDmgLabel(GamePiece targetGP,float dmg) {
		if(!targetGP.isDead) {
			StagePanel.valueLabels.add(new ValueLabel((float)(targetGP.getCenterX()+((Math.random()-0.5)*60)),(float)(targetGP.getCenterY()+((Math.random()-0.5)*60)),"-"+Math.round(dmg),2,0.3f,new Color(255,0,50)));
		}	
	}
	
	public static void addDmgLabel(int x, int y,float dmg) {
		StagePanel.valueLabels.add(new ValueLabel((float)(x+((Math.random()-0.5)*60)),(float)(y+((Math.random()-0.5)*60)),"-"+Math.round(dmg),2,0.3f,new Color(255,0,50)));
	}
	
	// graphics methode does all the drawing of objects (renders everything)
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(cBackGround);
		g2d.fillRect(0, 0, w, h);
		
		
		g2d.translate(camera.getPos().x, camera.getPos().y);
		drawEveryBoardRectangle(g2d);
//		drawEveryBoardRectangleIndex(g2d);
		drawAllEmptyShells(g2d);
		drawEveryWall(g2d);
		
		drawAllGamePiecePointers(g2d);
		drawAllGamePieces(g2d);
		if(curHoverBR != null) {
			curHoverBR.tryDrawHover(g2d);
		}
		drawAllGamePieceHealth(g2d);
		drawAllGamePieceAttacksAbilities(g2d);
		drawParticles(g2d);
		
		
		
		
		g2d.setStroke(new BasicStroke(80));
		g2d.setColor(cBackGround);
		g2d.draw(rectLevelBorder);
		drawValueLabels(g2d);
		
//		if(curActionPerformingGP != null) {
//			g2d.setColor(Color.GREEN);
//			g2d.fill(curActionPerformingGP.boardRect.rect);
//		}
		
		
		lightingManager.drawLight(g2d);
		buttonEndTurn.drawButton(g2d);
		drawMovesPanel(g2d);
		g2d.setColor(Color.WHITE);
		g2d.draw(mapRectangle);
		g2d.fillOval((int)camera.getCenterOfScreen().x-5, (int)camera.getCenterOfScreen().y-5, 10, 10);
		g2d.translate(-camera.getPos().x, -camera.getPos().y);
		turnInfoPanel.drawTurnInfo(g2d);
		g2d.translate(camera.getPos().x, camera.getPos().y);
		camera.drawRectOfView(g2d);
		drawCursor(g2d);
		g2d.translate(-camera.getPos().x, -camera.getPos().y);
		
		g2d.dispose();
	}
	// updates the Stage (moves pieces, moves bullets, updates animations...)
	private void updateStage() {
		if(timeStopCounter > 0) {
			timeStopCounter--;
			return;
		}
		updateParticles();
		BoardRectangle pHBR = curHoverBR;
		curHoverBR = null;
		for(BoardRectangle curBR : boardRectangles) {
			if(mousePos != null) {
				curBR.updateHover(mousePos);
			}
			if(curBR.isHover) {
				curHoverBR = curBR;
				if(pHBR != curHoverBR) {
					changedHoverBR();
				} 
			}
		}
		if(curHoverBR != null) {
			curHoverBR.tryAnimate();
		}
		camera.move(mapRectangle);
		if(mousePosUntranslated != null) {
			mousePos = new Point((int)(mousePosUntranslated.x-camera.getPos().x), (int)(mousePosUntranslated.y-camera.getPos().y));
		}
		buttonEndTurn.updatePos(camera.getPos());
		
		updateDmgLabels();
		StagePanel.curActionPerformingGP = null;
		for(int i = 0;i<gamePieces.size();i++) {
			GamePiece curGP = gamePieces.get(i);
			if(curGP.isPerformingAction()) {
				curActionPerformingGP = curGP;
			}
			curGP.update();
			curGP.tryDie();
			curGP.updateActionSelectionPanelPos(camera.getPos(), mousePos);
		}
		
		updateAbilities();
		buttonEndTurn.updatePressable(curActionPerformingGP != null);
		buttonEndTurn.updateHover(mousePos);
	}
	
	public static void applyScreenShake(int screenShakeAmountOfFRames,int screenShakeMagnitude) {
		camera.applyScreenShake(screenShakeAmountOfFRames,screenShakeMagnitude);
	}
	
	// draws all GamePieces
	private void drawAllGamePieces(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(camera.isInView(curGP.getPos())) {
				curGP.drawGamePiece(g2d,curHoverBR);
			}
			// for devs
//			curGP.drawLinesOfSight(g2d);
		}
	}
	// draws all Particles
	private void drawParticles(Graphics2D g2d) {
		for(Particle curP : particles) {
			if(!(curP instanceof EmptyShell)) {
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
	// updates all Particles
	private void updateParticles() {
		int amountOfEmptyShells = 0;
		for(int i = 0;i<particles.size();i++) {
			Particle curP = particles.get(i);
			curP.update();
			if(curP instanceof EmptyShell) {
				amountOfEmptyShells++;
			}
			if(curP.getIsDestroyed()) {
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
	
	// updates all the ValueLabels (fades them out)
	private void updateDmgLabels() {
		for(int i = 0;i<valueLabels.size();i++) {
			ValueLabel curVL = valueLabels.get(i);
			if(curVL.getColor().getAlpha()>10) {
				curVL.updateFade();
			}else {
				valueLabels.remove(i);
			}
		}
	}
	
	private void drawAllGamePiecePointers(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(!curGP.getIsDead() && camera.isInView(curGP.getPos())) {
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
			if(!curGP.getIsDead() && camera.isInView(curGP.getPos())) {
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
			curRS.drawRadialShield(g2d);
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
	
	private void drawMovesPanel(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(curGP.isSelected) {
				curGP.actionSelectionPanel.drawActionSelectionPanel(g2d);
				return;
			}
		}
	}
	// updates/changes Turns
	private void updateTurn() {
		turnInfoPanel.toggleTurn();
		for(GamePiece curGP : gamePieces) {
			GamePieceBase curGPB = curGP.gamePieceBase;
			if((turnInfoPanel.getIsEnemyTurn() && curGP.getIsEnemy()) || (!turnInfoPanel.getIsEnemyTurn() && !curGP.getIsEnemy())) {
				curGPB.regenShield();
				if(curGP instanceof CommanderGamePiece) {
					CommanderGamePiece curCGP = (CommanderGamePiece) curGP;
					curCGP.regenAbilityCharge();
				}
			}
		}
		restoreMovesAndAttacks();
		
		detNotEnemy.decDetonaterTimers();
		detEnemy.decDetonaterTimers();
		curSelectedGP =null;
		for(GamePiece curGP : gamePieces) {
			curGP.isSelected = false;
			curGP.actionSelectionPanel.setAttackButtonActive(false);
			curGP.actionSelectionPanel.setMoveButtonActive(false);
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
	
	public void drawEveryBoardRectangleIndex(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			curBR.drawIndex(g2d);
		}
	}
	
	// sets all BoardRectangles to the default color so they don't represent a possible move/attack
	private void resetShowPossibleMoves() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			curBR.isPossibleAbility = false;
			curBR.isShowPossibleAbility = false;
			curBR.isPossibleAttack = false;
			curBR.isShowPossibleAttack = false;
			curBR.isPossibleMove = false;
			curBR.isShowPossibleMove = false;
		}
	}
	
	// restores the Attack and Move ability of each GamePiece
	private void restoreMovesAndAttacks() {
		for(GamePiece curGP : gamePieces) {
			curGP.restoreMovesAndAttacks();
		}
	}
	
	// selects a piece if it is clicked on and not dead
	private void selectPieceIfPossible(Point mousePos) {
		curSelectedGP = null;
		for(BoardRectangle curBR : boardRectangles) {
			if(mousePos != null && curBR.rect.contains(mousePos)) {
				for(GamePiece curGP : gamePieces) {
					if(!curGP.getIsDead()) {
						if(curGP.boardRect == curBR && checkIfHasTurn(curGP)) {
							curGP.isSelected = true;
							curSelectedGP = curGP;
						}else {
							curGP.isSelected = false;
							curGP.actionSelectionPanel.setAttackButtonActive(false);
							curGP.actionSelectionPanel.setMoveButtonActive(false);
						}
					}
				}
			}
		}
	}

	// presses button if it is clicked on a and not blocked and also a piece is selected
	private void tryPressButton(Point mousePos) {
		resetShowPossibleMoves();
		for(GamePiece curGP : gamePieces) {
			if(!curGP.getIsDead() && curGP.isSelected) {
				curGP.actionSelectionPanel.tryPressButton();
			}
		}
	}
	
	// moves selected GamePiece on the BoardRectangle pressed if it is a valid spot to move to (depends on the gamepieces checkMoves function)
	public void moveToPressedPositionIfPossible() {
		if(curSelectedGP != null && !curSelectedGP.getIsDead() && curHoverBR != null) {	
			if(curHoverBR.isPossibleMove) {
				resetShowPossibleMoves();
				curSelectedGP.startMove();
				curSelectedGP.isSelected = false;
				curSelectedGP = null;
				return;
			}
		}
	}
	//  selected GamePiece attacks GamePiece sitting (or attacks BoardRectangle) on the BoardRectangle pressed if it is a valid spot to attack(depends on the GamePieces checkAttacks function)
	private void attackPressedPositionIfPossible() {
		if(curSelectedGP != null && !curSelectedGP.getIsDead() && curHoverBR != null) {
			if(curHoverBR.isPossibleAttack) {
				resetShowPossibleMoves();
				curSelectedGP.startAttack(curHoverBR);
				curSelectedGP.isSelected = false;
				curSelectedGP = null;
				return;	
			}
		}
	}
	
	private void abilityPressedPositionIfPossible() {
		if(curSelectedGP != null && !curSelectedGP.getIsDead() && curHoverBR != null) {
			
			if(curHoverBR.isPossibleAbility) {
				resetShowPossibleMoves();
				
				CommanderGamePiece curCGP = (CommanderGamePiece) curSelectedGP;
				curCGP.startAbility(curHoverBR);
				curSelectedGP.isSelected = false;
				curSelectedGP = null;
				return;	
			}
			
		}
	}
	
	// only dev
	private void giveWallsAsList() {
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.isWall) {
				System.out.print(curBR.index+",");
			}
		}
	}
	
	private boolean checkIfHasTurn(GamePiece gamePiece) {
		if(gamePiece.getIsEnemy() && turnInfoPanel.getIsEnemyTurn()) {
			return true;
		}
		if(!gamePiece.getIsEnemy() && !turnInfoPanel.getIsEnemyTurn()) {
			return true;
		}
		return false;
	}
	
	private void changedHoverBR() { 
		for(GamePiece curGp : gamePieces) { 
			if(curGp.isSelected) {
				for(BoardRectangle curBR : boardRectangles) {
					curBR.isPossibleAttack = false;
				}
				if(curGp.actionSelectionPanel.getMoveButtonIsActive()) {
					curGp.resetPathFinder(curGp.boardRect, curHoverBR);
				}else if(curGp instanceof CommanderGamePiece && curGp.actionSelectionPanel.getAbilityButtonIsActive()){
					CommanderGamePiece curCGP = (CommanderGamePiece) curGp;
					curCGP.showPossibleAbilities(curHoverBR);
				}else if(curGp.actionSelectionPanel.getAttackButtonIsActive()){
					if(curHoverBR.isShowPossibleAttack) {
						curHoverBR.isPossibleAttack = true;
					}
				}
			}
		}
	}
	
	class ML implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(createLevel) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					for(BoardRectangle curBR : boardRectangles) {
						if(curBR.rect.contains(mousePos)) {
							curBR.isWall = true;
						}
					}
				}
				if(SwingUtilities.isRightMouseButton(e)) {
					for(BoardRectangle curBR : boardRectangles) {
						if(curBR.rect.contains(mousePos)) {
							curBR.isWall = false;
						}
					}
				}
				initWallSpriteConnections();
			}
			
			
			
			mousePosUntranslated = e.getPoint();
			boolean noOnePerformingAction = true;
			if(SwingUtilities.isLeftMouseButton(e)) {
				attackPressedPositionIfPossible();
				moveToPressedPositionIfPossible();
				abilityPressedPositionIfPossible();
				for(GamePiece curGP : gamePieces) {
					if(curGP.isPerformingAction()) {
						noOnePerformingAction = false;
						break;
					}
				}
			
				if(noOnePerformingAction) {
					boolean canSelectGP = true;
					for(GamePiece curGP : gamePieces) {
						if(curGP.isSelected && curGP.actionSelectionPanel.containsMousePos(mousePos)) {
							canSelectGP = false;
						}
					}
					if(canSelectGP) {
						selectPieceIfPossible(mousePos);
					}
					tryPressButton(mousePos);
					if(buttonEndTurn.getIsHover()) {
						updateTurn();
					}
				}
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
	
	class MML implements MouseMotionListener{

		@Override		
		public void mouseDragged(MouseEvent e) {
			mousePosUntranslated = e.getPoint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mousePosUntranslated = e.getPoint();
		}
	}
	
	class KL implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			camera.updateMovementPressedKey(e);
			
			if(e.getKeyCode() == KeyEvent.VK_G && createLevel) {
				giveWallsAsList();
			}
			if(e.getKeyCode() == KeyEvent.VK_K) {
				levelinitializer.writeFile(boardRectangles);
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			camera.updateMovementReleasedKey(e);
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}
