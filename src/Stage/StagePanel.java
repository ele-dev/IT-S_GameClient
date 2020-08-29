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

import Buttons.ButtonEndTurn;
import GamePieces.CommanderGamePiece;
import GamePieces.DetonatorPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GamePiece;
import GamePieces.GamePieceBase;
import GamePieces.GunnerPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.SniperCommanderPiece;
import Particles.EmptyShell;
import Particles.Particle;

public class StagePanel extends JPanel{
	int x,y;
	int w,h;
	KL kl;
	
	int amountOfRows= 16;
	int amountOfColumns = 16;
	int boardRectSize = Commons.boardRectSize;
	
	Timer tFrameRate;
	Timer tUpdateRate;

	public static ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	CommanderGamePiece enemyCommanderPiece;
	CommanderGamePiece notEnemyCommanderPiece;
	public static ArrayList<GamePiece> gamePieces = new ArrayList<GamePiece>();
	public static ArrayList<ValueLabel> valueLabels = new ArrayList<ValueLabel>();
	
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	
	ButtonEndTurn buttonEndTurn;
	static TurnInfo turnInfoPanel;
	
	DetonatorPiece detNotEnemy;
	DetonatorPiece detEnemy;
	
	Camera camera;
	Point mousePos;
	Point mousePosUntranslated;
	
	public static BoardRectangle curHoverBoardRectangle;
	
	boolean createLevel = false;
	
	Rectangle rectLevelBorder;
	Color cBackGround;
	public StagePanel(int x, int y) {
		this.x = x;
		this.y = y;
		this.w = Commons.wf;
		this.h = Commons.hf;
		setBounds(x, y, w, h);
		setVisible(true);
		cBackGround = new Color(28,26,36);
		
		camera = new Camera();
		kl = new KL();
		
		initBoard();			
		initGamePieces();
		rectLevelBorder = new Rectangle(0,0,boardRectSize*amountOfColumns,boardRectSize*amountOfRows);
		
		tFrameRate = new Timer(10, new ActionListener() {
			
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
		
		buttonEndTurn = new ButtonEndTurn(Commons.wf-350, Commons.hf -200);
		turnInfoPanel = new TurnInfo();
		
		// makes Cursor invisible
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		setCursor(blankCursor);
		
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
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
						boardRectangles.add(new BoardRectangle(boardRectSize*j, boardRectSize*i, boardRectSize, i, j, false,index));
					}else {
						boardRectangles.add(new BoardRectangle(boardRectSize*j, boardRectSize*i, boardRectSize, i, j, true,index));
					}
				}else {
					if(j%2==0) {
						boardRectangles.add(new BoardRectangle(boardRectSize*j, boardRectSize*i, boardRectSize, i, j, true,index));
					}else {
						boardRectangles.add(new BoardRectangle(boardRectSize*j, boardRectSize*i, boardRectSize, i, j, false,index));
					}
				}
				index++;
			}
		}
		initLevelWalls(1);
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
	// generates preset Of walls in Level
	private void initLevelWalls(int levelIndex) {
		List<Integer> wallIndexes = Arrays.asList();
		List<Integer> destructibleWallIndexes = Arrays.asList();
		if(levelIndex == 1) {
			wallIndexes = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,31,32,47,48,49,50,51,52,56,57,58,59,62,63,64,68,72,79,80,88,95,96,
					111,112,123,124,125,126,127,128,143,144,149,159,160,165,166,167,168,169,170,175,176,179,186,187,191,192,195,207,208,211,223,224,
					239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255);
			
			destructibleWallIndexes = Arrays.asList(53,54,55);
		}
		 
		for(BoardRectangle curBR : boardRectangles) {
			for(int i = 0;i<wallIndexes.size();i++) {
				if(curBR.index == wallIndexes.get(i)) {
					curBR.isWall = true;
				}
			}
			for(int i = 0;i<destructibleWallIndexes.size();i++) {
				if(curBR.index == destructibleWallIndexes.get(i)) {
					curBR.isDestructibleWall = true;
					curBR.initDestructibleWallSprite();
				}
			}
		}
	}
		
	// initializes/creates all GamePieces
	private void initGamePieces() {
		enemyCommanderPiece = new SniperCommanderPiece(true, boardRectangles.get(101), null);
		notEnemyCommanderPiece = new SniperCommanderPiece(false, boardRectangles.get(105), null);
		
		gamePieces.add(enemyCommanderPiece);
		gamePieces.add(notEnemyCommanderPiece);
		
		gamePieces.add(new GunnerPiece(false, boardRectangles.get(100), notEnemyCommanderPiece));
		gamePieces.add(new GunnerPiece(true, boardRectangles.get(103), enemyCommanderPiece));
		gamePieces.add(new RocketLauncherPiece(false, boardRectangles.get(137), notEnemyCommanderPiece));
		gamePieces.add(new RocketLauncherPiece(true, boardRectangles.get(120), enemyCommanderPiece));
		gamePieces.add(new FlamethrowerPiece(false, boardRectangles.get(129), notEnemyCommanderPiece));
		gamePieces.add(new FlamethrowerPiece(true, boardRectangles.get(171), enemyCommanderPiece));
			
		detNotEnemy = new DetonatorPiece(false, boardRectangles.get(92), notEnemyCommanderPiece);
		detEnemy = new DetonatorPiece(true, boardRectangles.get(69), enemyCommanderPiece);
		gamePieces.add(detNotEnemy);
		gamePieces.add(detEnemy);
		
		for(GamePiece curGP : gamePieces) {
			curGP.initPathFinder();
		}
	}
	
	// graphics methode does all the drawing of objects (renders everything)
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(cBackGround);
		g2d.fillRect(0, 0, w, h);
		
		
		g2d.translate(camera.x, camera.y);
		drawEveryGap(g2d);
		drawEveryBoardRectangle(g2d);
		drawEveryBoardRectangleIndex(g2d);
		drawAllEmptyShells(g2d);
		drawEveryWall(g2d);
		
		drawAllGamePiecePointers(g2d);
		drawAllGamePieces(g2d);
		drawAllGamePieceHealth(g2d);
		drawSelectedGamePiece(g2d);
		drawSelectedGamePieceHealth(g2d);
		drawAllGamePieceAttacks(g2d);
		drawParticles(g2d);
		
		
		g2d.setStroke(new BasicStroke(80));
		g2d.setColor(cBackGround);
		g2d.draw(rectLevelBorder);
		drawValueLabels(g2d);
		
		buttonEndTurn.drawButton(g2d);
		drawMovesPanel(g2d);
		
		turnInfoPanel.drawTurnInfo(g2d);
		drawCursor(g2d);
		g2d.translate(-camera.x, -camera.y);
		g2d.dispose();
	}
	// updates the Stage (moves pieces, moves bullets, updates animations...)
	private void updateStage() {
		updateParticles();
		BoardRectangle pHBR = curHoverBoardRectangle;
		curHoverBoardRectangle = null;
		for(BoardRectangle curBR : boardRectangles) {
			if(mousePos != null) {
				curBR.updateHover(mousePos);
			}
			if(curBR.isHover) {
				curHoverBoardRectangle = curBR;
				if(pHBR != curHoverBoardRectangle) {
					changedHoverBR();
				}
			}
		}
		camera.move();
		if(mousePosUntranslated != null) {
			mousePos = new Point((int)(mousePosUntranslated.x-camera.x), (int)(mousePosUntranslated.y-camera.y));
		}
		buttonEndTurn.updatePos(camera.getPos());
		
		updateDmgLabels();
		boolean noOneAttacking = true;
		for(int i = 0;i<gamePieces.size();i++) {
			GamePiece curGP = gamePieces.get(i);
			if(curGP.isMoving) {
				curGP.updateMove();
			}
			
			curGP.updateGamePiece();
			
			
			
			curGP.updateMovesPanelPos(camera.getPos(), mousePos);
			if(curGP.getIsAttacking()) {
				noOneAttacking = false;
			}
			curGP.updateAttack();
			if(curGP.getIsDead()) {
				gamePieces.remove(i);
			}
		}
		buttonEndTurn.updatePressable(!noOneAttacking);
		buttonEndTurn.updateHover(mousePos);
	}
	// draws all GamePieces
	private void drawAllGamePieces(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			curGP.drawGamePiece(g2d,curHoverBoardRectangle);
			// for devs
//			curGP.drawLinesOfSight(g2d);
		}
		enemyCommanderPiece.drawUltCharge(g2d);
		notEnemyCommanderPiece.drawUltCharge(g2d);
	}
	// draws all Particles
	private void drawParticles(Graphics2D g2d) {
		for(Particle curP : particles) {
			if(!(curP instanceof EmptyShell)) {
				curP.drawParticle(g2d);
			}
		}
	}
	// Method must exist because if it is drawn with all other particles like explosions it is drawn on top of GamePieces
	// and does not make sense
	private void drawAllEmptyShells(Graphics2D g2d) {
		for(Particle curP : particles) {
			if(curP instanceof EmptyShell) {
				curP.drawParticle(g2d);
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
	private void drawSelectedGamePiece(Graphics2D g2d) {
		// draws the one GP that is in Moves-Selection on top of all others
		for(GamePiece curGP : gamePieces) {
			if(curGP.isSelected && curGP.movesPanel.getMoveButtonIsActive()) {
				curGP.drawGamePiece(g2d,curHoverBoardRectangle);
			}
		}
	}
	
	private void drawAllGamePiecePointers(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(curGP.getIsEnemy() && turnInfoPanel.getIsEnemyTurn()) {
				curGP.drawPointer(g2d);
			}else 
			if(!curGP.getIsEnemy() && !turnInfoPanel.getIsEnemyTurn()) {
				curGP.drawPointer(g2d);
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
			curGP.gamePieceBase.drawHealth(g2d);
		}
	}
	
	private void drawSelectedGamePieceHealth(Graphics2D g2d) {
		// draws the one GP that is in Moves-Selection on top of all others
		for(GamePiece curGP : gamePieces) {
			if(curGP.isSelected && curGP.movesPanel.getMoveButtonIsActive()) {
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
		
	}
	
	private void drawMovesPanel(Graphics2D g2d) {
		for(GamePiece curGP : gamePieces) {
			if(curGP.isSelected) {
				curGP.movesPanel.drawMovesPanel(g2d);
				return;
			}
		}
	}
	// updates/changes Turns
	private void updateTurn() {
		turnInfoPanel.toggleTurn();
		for(GamePiece curGP : gamePieces) {
			GamePieceBase curGPB = curGP.gamePieceBase;
			if(turnInfoPanel.getIsEnemyTurn()) {
				if(curGP.getIsEnemy()) {
					curGPB.regenShield();
				}
			}else {
				if(!curGP.getIsEnemy()) {
					curGPB.regenShield();
				}
			}
		}
		restoreMovesAndAttacks();
		
		detNotEnemy.decDetonaterTimers();
		detEnemy.decDetonaterTimers();
		
		for(GamePiece curGP : gamePieces) {
			curGP.isSelected = false;
			curGP.movesPanel.setAttackButtonActive(false);
			curGP.movesPanel.setMoveButtonActive(false);
		}
	}
	// draws every BoardRectangles rectangle that is not a gap and draws the Walls
	private void drawEveryBoardRectangle(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			if(!curBR.isGap) {
				curBR.drawBoardRectangle(g2d,boardRectangles);
			}
		}
		for(BoardRectangle curBR : boardRectangles) {
			curBR.tryDrawHover(g2d,gamePieces);
		}
	}
	// draws all the Walls including destructible walls
	private void drawEveryWall(Graphics2D g2d){
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.isWall) {
				curBR.drawWall(g2d,gamePieces);
			}
			if(curBR.isDestructibleWall) {
				curBR.drawDestructibleWall(g2d,gamePieces);
			}
		}
	}
	// draws all Gaps in the Board (all Rivers for now)
	private void drawEveryGap(Graphics2D g2d) {
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.isGap) {
				curBR.drawGapWall(g2d);		
			}
		}
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.isGap) {
				curBR.drawGapWater(g2d);
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
		for(BoardRectangle curBR : boardRectangles) {
			curBR.isPossibleAttack = false;
			curBR.isPossibleMove = false;
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
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.rect.contains(mousePos)) {
				for(GamePiece curGP : gamePieces) {
					if(!curGP.getIsDead()) {
						if(curGP.boardRect == curBR && checkIfHasTurn(curGP)) {
							curGP.isSelected = true;
						}else {
							curGP.isSelected = false;
							curGP.movesPanel.setAttackButtonActive(false);
							curGP.movesPanel.setMoveButtonActive(false);
						}
					}
				}
			}
		}
	}

	// presses button if it is clicked on a and not blocked and also a piece is selected
	private void tryPressButton(Point mousePos) {
		for(GamePiece curGP : gamePieces) {
			if(!curGP.getIsDead() && curGP.isSelected) {
				curGP.movesPanel.tryPressButton();
			}
		}
	}
	
	// moves selected GamePiece on the BoardRectangle pressed if it is a valid spot to move to (depends on the gamepieces checkMoves function)
	public void moveToPressedPositionIfPossible(Point mousePos) {
		for(GamePiece curGP : gamePieces) {
			if(!curGP.getIsDead() && curGP.isSelected) {
				for(BoardRectangle curBR : boardRectangles) {
					if(curBR.isPossibleMove && curBR == curHoverBoardRectangle) {
						curGP.startMove();
						curGP.isSelected = false;
						curGP.movesPanel.setAttackButtonActive(false);
						return;
					}
				}
			}
		}
	}
	//  selected GamePiece attacks GamePiece sitting (or attacks BoardRectangle) on the BoardRectangle pressed if it is a valid spot to attack(depends on the GamePieces checkAttacks function)
	private void attackPressedPositionIfPossible(Point mousePos) {
		for(GamePiece curGP : gamePieces) {
			if(!curGP.getIsDead() && curGP.isSelected) {
				for(BoardRectangle curBR : boardRectangles) {
					if(curBR.isPossibleAttack && curBR.rect.contains(mousePos)) {
						if(curBR.isDestructibleWall) {
							curGP.startAttackDestructibleWall(curBR);
							curGP.isSelected = false;
						}else {
							curGP.startAttack(curBR,gamePieces);
							curGP.isSelected = false;
							return;
						}
						
					}
				}
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
			if(curGp.movesPanel.getMoveButtonIsActive() && curGp.isSelected) {
				curGp.resetPathFinder(curGp.boardRect, curHoverBoardRectangle);
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
			boolean noOneAttacking = true;
			if(SwingUtilities.isLeftMouseButton(e)) {
				attackPressedPositionIfPossible(mousePos);
				moveToPressedPositionIfPossible(mousePos);
				for(GamePiece curGP : gamePieces) {
					if(curGP.getIsAttacking()) {
						noOneAttacking = false;
						break;
					}
				}
				resetShowPossibleMoves();
			
				if(noOneAttacking) {
					boolean canSelectGP = true;
					for(GamePiece curGP : gamePieces) {
						if(curGP.isSelected && curGP.movesPanel.containsMousePos(mousePos)) {
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
