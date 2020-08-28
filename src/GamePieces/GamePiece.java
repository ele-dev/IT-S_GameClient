package GamePieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Buttons.GPMovesSelection;
import Particles.UltChargeOrb;
import PathFinder.AStarPathFinder;
import PathFinder.PathCell;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.DmgLabel;
import Stage.Sprite;
import Stage.StagePanel;


public abstract class GamePiece {
	public BoardRectangle boardRect;
	private Rectangle rectShowTurret;
	protected Color c,cTurret;
	private String name;
	protected float angle,angleDesired;
	private boolean isDead = false;
	private float health,maxHealth;
	private float dmg;
	private int movementRange;
	
	public boolean isSelected = false;
	private boolean isEnemy;
	protected boolean hasExecutedMove = false;
	protected boolean hasExecutedAttack = false;
	protected boolean isWallAttack = false;
	
	protected Timer attackDelayTimer;
	private GamePiece currentTargetGamePiece;
	protected BoardRectangle currentTargetBoardRectangle;
	protected Arc2D aimArc;
	
	protected boolean isAttacking = false;
	public boolean isMoving = false;
	public GPMovesSelection movesPanel;
	private ArrayList<Line2D> sightLines = new ArrayList<Line2D>();
	
	private int rotationDelay = 4;
	protected Sprite spriteTurret;
	private int spritePointerX,spritePointerY;
	private static double spritePointerElevation = 0;
	private static double speV = 0.3;
	private Sprite spritePointer,spritePointerDarkened;
	
	private float rectSizeInc = 0;
	private int dmgFlashCountDown = 0;
	private CommanderGamePiece commanderGamePiece;
	
	
	// pathfinding
	AStarPathFinder pathFinder;
	GamePieceBase gamePieceBase;
	
	public GamePiece(boolean isEnemy,String name,BoardRectangle boardRect,int maxHealth,float dmg,int movementRange,CommanderGamePiece commanderGamePiece) {
		this.isEnemy = isEnemy;
		this.boardRect = boardRect;
		rectShowTurret = new Rectangle((int)(-boardRect.getSize()*0.2),(int)(-boardRect.getSize()*0.2),(int)(boardRect.getSize()*0.4),(int)(boardRect.getSize()*0.4));
		if(isEnemy) {
			this.c = Commons.enemyColor;
			this.cTurret = Commons.enemyColorTurret;
		}else {
			this.c = Commons.notEnemyColor;
			this.cTurret = Commons.notEnemyColorTurret;
		} 
		gamePieceBase = new GamePieceBase(boardRect.getCenterX(), boardRect.getCenterY(), rectShowTurret.width+10, rectShowTurret.width+10, c);

		this.name = name;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.dmg = dmg;
		this.movementRange = movementRange;

		this.movesPanel = new GPMovesSelection(this);
		spritePointerX = boardRect.getCenterX();
		spritePointerY = boardRect.getCenterY()-60;
				
		if(commanderGamePiece != null) {
			this.commanderGamePiece = commanderGamePiece;
		}else {
			this.commanderGamePiece = (CommanderGamePiece) this;
		}
		if(isEnemy) {
			ArrayList<String> spriteLinks = new ArrayList<String>();
			spriteLinks.add(Commons.pathToSpriteSource+"GamePieces/EnemyPointer.png");
			spritePointer = new Sprite(spriteLinks, Commons.boardRectSize/2,Commons.boardRectSize/2, 0);
			ArrayList<String> spriteLinks1 = new ArrayList<String>();
			spriteLinks1.add(Commons.pathToSpriteSource+"GamePieces/EnemyPointerDarkened.png");
			spritePointerDarkened = new Sprite(spriteLinks1, Commons.boardRectSize/2,Commons.boardRectSize/2, 0);
		}else {
			ArrayList<String> spriteLinks = new ArrayList<String>();
			spriteLinks.add(Commons.pathToSpriteSource+"GamePieces/NotEnemyPointer.png");
			spritePointer = new Sprite(spriteLinks, Commons.boardRectSize/2,Commons.boardRectSize/2, 0);
			ArrayList<String> spriteLinks1 = new ArrayList<String>();
			spriteLinks1.add(Commons.pathToSpriteSource+"GamePieces/NotEnemyPointerDarkened.png");
			spritePointerDarkened = new Sprite(spriteLinks1, Commons.boardRectSize/2,Commons.boardRectSize/2, 0);
		}
	}	
	
	public void initPathFinder() {
		ArrayList<PathCell> pathCells = new ArrayList<PathCell>();
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
			BoardRectangle curBR  = StagePanel.boardRectangles.get(i);
			pathCells.add(new PathCell(curBR.getX(), curBR.getY(), Commons.boardRectSize, curBR.row, curBR.column,i));
				
			if(curBR.isGap || curBR.isDestructibleWall || curBR.isWall) {
				pathCells.get(i).setIsWall(true);
			}
			for(GamePiece curGP : StagePanel.gamePieces) {
				if(curGP.boardRect == StagePanel.boardRectangles.get(i) && !curGP.isDead && curGP != this) {
					pathCells.get(i).setIsWall(true);
					break;
				}
			}
		}
		pathFinder = new AStarPathFinder(pathCells);
	}
	
	public void resetPathFinder(BoardRectangle startBR, BoardRectangle endBR) {
		if(endBR.isWall || endBR.isDestructibleWall || startBR.isWall || startBR.isDestructibleWall){
			return;
		}
		initPathFinder();
		
		PathCell startPathCell = null, endPathCell =  null;
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
			if(startBR == StagePanel.boardRectangles.get(i)) {
				startPathCell = pathFinder.pathCells.get(i);
			}
			if(endBR == StagePanel.boardRectangles.get(i)){
				endPathCell = pathFinder.pathCells.get(i);
			}
		}
		if(startPathCell != endPathCell) {
			pathFinder.setPathEnds(startPathCell, endPathCell);
		}
		if(pathFinder.getPathPathCells().size() == 0) {
			return;
		}
		gamePieceBase.pathBoardRectangles.clear();
		for(int j = pathFinder.getPathPathCells().size()-1;j>=pathFinder.getPathPathCells().size()-(movementRange+1) && j>= 0;j--) {
			for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
				if(pathFinder.getPathPathCells().get(j).getIndex() == i) {
					gamePieceBase.pathBoardRectangles.add(StagePanel.boardRectangles.get(i));
					break;
				}
			}
		}
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
			StagePanel.boardRectangles.get(i).isPossibleMove = false;
		}
		for(BoardRectangle curBr : gamePieceBase.pathBoardRectangles) {
			curBr.isPossibleMove = true;
		}
		System.out.println("resetPathFinder");
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getIsDead() {
		return isDead;
	}
	
	public boolean getIsAttacking() {
		return isAttacking;
	}
	
	public boolean getIsEnemy() {
		return isEnemy;
	}
	
	public Rectangle getRectHitbox() {
		return gamePieceBase.getRectHitbox();
	}
	
	public CommanderGamePiece getCommanderGamePiece() {
		return commanderGamePiece;
	}
	
	public GamePiece getCurrentTargetGamePiece() {
		return currentTargetGamePiece;
	}
	public float getHealth() {
		return health;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getDmg() {
		return dmg;
	}
	
	public boolean getHasExecutedAttack() {
		return hasExecutedAttack;
	}

	public boolean getHasExecutedMove() {
		return hasExecutedMove;
	}

	public int getCenterX() {
		return (int) getRectHitbox().getCenterX();
	}
	public int getCenterY() {
		return (int) getRectHitbox().getCenterY();
	}
	
	// checks if the piece is dead and sets it as dead if it is ,also updates the Position of the Hitbox
	public void updatePosPointer(int x, int y) {
		int size = Commons.boardRectSize;
		if(isMoving) {
			spritePointerX = x;
			spritePointerY = y-60-(int)(size*1.5);
		}else {
			spritePointerX = boardRect.getCenterX();
			spritePointerY = boardRect.getCenterY()-60;
		}
		
	}
	
	// sets each BoardRectangle to being a possible attackPosition if it is(changes color accordingly)
	public void showPossibleAttacks() {
		if(isSelected) {
			sightLines.clear();
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(checkAttacks(curBR.row, curBR.column) && !curBR.isWall) {
					curBR.isPossibleAttack = true;
				}
			}
		}
	}
	
	// returns true if the BoardRectangle is in sight of the GamePiece and return false if it is for example behind a wall
	public boolean checkIfBoardRectangleInSight(BoardRectangle targetBoardRectangle) {
		Line2D lineOfSight = new Line2D.Double(boardRect.getCenterX(),boardRect.getCenterY(),targetBoardRectangle.getCenterX(),targetBoardRectangle.getCenterY());	
		sightLines.add(lineOfSight);
				
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			Rectangle rectWall = new Rectangle(curBR.rect.x +10 , curBR.rect.y +10,curBR.rect.width-20,curBR.rect.height-20);
			if(lineOfSight.intersects(rectWall) && (curBR.isWall || curBR.isDestructibleWall) && (!targetBoardRectangle.isWall && !targetBoardRectangle.isDestructibleWall)) {
				return false;
			}
		}
		return true;
	}
	// draws the attack differently for each GamePiece
	public abstract void drawAttack(Graphics2D g2d);
	// draws all LinesOfSight (only for devs)
	public void drawLinesOfSight(Graphics2D g2d) {
		if(isSelected) {
			for(Line2D line : sightLines) {
				g2d.setColor(new Color(0,0,255,200));
				g2d.draw(line);
			}
		}
	}
	
	// checks if the PositionParameter is a valid position to attack and returns true if it is
	// is abstract because every GamePiece has a different attack pattern
	public abstract boolean checkAttacks(int selectedRow, int selectedColumn);
		
	public abstract boolean checkAttackRows(int selectedRow,int selectedColumn);
	
	public abstract boolean checkAttackColumns(int selectedRow,int selectedColumn);
	
	// updates angle to face toward enemy and starts the attack (starts attackDelayTimer)
	public void startAttack(BoardRectangle targetBoardRectangle,ArrayList<GamePiece> gamepieces) {
		for(GamePiece curGP : gamepieces) {
			if(curGP.boardRect == targetBoardRectangle) {
				currentTargetGamePiece = curGP;
				if(!currentTargetGamePiece.isDead && checkIfEnemies(currentTargetGamePiece)) {
					isAttacking = true;
					attackDelayTimer.start();
					hasExecutedAttack = true;
					hasExecutedMove = true;
				}	
			}
		}
	}
	
	public abstract void startAttackDestructibleWall(BoardRectangle targetBoardRectangle);
	// updates all things that are animated with the attack (for example moves the rockets and updates the explosions)
	public abstract void updateAttack();
	
	// draws the GamePiece	
	public void drawGamePiece(Graphics2D g2d,BoardRectangle curHBR) {
		if(!isDead) {
			gamePieceBase.drawGamePieceBase(g2d);
			int cx = getCenterX();
			int cy = getCenterY();
			g2d.setColor(c);
			// draws different when it already attacked
			if(hasExecutedAttack) {
				g2d.setColor(new Color(c.getRed()/4,c.getGreen()/4,c.getBlue()/4));
			}
			if(dmgFlashCountDown > 0) {
				g2d.setColor(Color.WHITE);
			}
			if(isSelected) {
				drawSelect(g2d);
			}
			if(spriteTurret != null) {
				spriteTurret.drawSprite(g2d, cx, cy, angle+90, 1);
			}else {
				g2d.setColor(cTurret);
				g2d.translate(cx, cy);
				g2d.rotate(Math.toRadians(angle));
				g2d.setStroke(new BasicStroke(2));
				g2d.fill(rectShowTurret);
				g2d.setColor(Color.BLACK);
				g2d.draw(rectShowTurret);
				g2d.rotate(Math.toRadians(-angle));
				g2d.translate(-cx, -cy);
				
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("Arial",Font.BOLD,30));
				FontMetrics metrics = g2d.getFontMetrics();
				String text = name;
				int textHeight = metrics.getHeight();
				int textWidth = metrics.stringWidth(text);
				g2d.drawString(name, cx - textWidth/2, cy + textHeight/3);
			}
		}
	}
	
	public static void updateSpritePointerElevation() {
		if(spritePointerElevation > 20) {
			speV = -0.3;
		}
		if(spritePointerElevation <= 0) {
			speV = 0.3;
		}
		spritePointerElevation += speV;
	}
	
	public void drawPointer(Graphics2D g2d) {
		if(!hasExecutedAttack) {
			spritePointer.drawSprite(g2d, spritePointerX, spritePointerY+(int)spritePointerElevation, 0, 1);
		}else {
			spritePointerDarkened.drawSprite(g2d, spritePointerX, spritePointerY, 0, 1);
		}
	}
	// updates the GamePiece (does things like updating the attack or moves rockets)
	public void updateGamePiece() {
		updatePosPointer(0,0);
		
		if(currentTargetGamePiece != null) {
			updateAngle(false);
		}
		if(currentTargetBoardRectangle != null) {
			updateAngle(true);
		}
		if(rectSizeInc > 0) {
			rectSizeInc -= 1;
		}
		if(dmgFlashCountDown > 0) {
			dmgFlashCountDown--;
		}
	}
	// updates the MovesPanels position so it follows the camera (also updates the isHover boolean)
	public void updateMovesPanelPos(Point CameraPos,Point mousePos) {
		movesPanel.updatePos(CameraPos);
		if(mousePos != null && isSelected) {
			movesPanel.updateHover(mousePos);
		}
	}
	
	// updates the shot angle towards the TargetEnemy/TargetGamepice
	public void updateAngle(boolean targetIsWall) {
		int cx = getCenterX();
		int cy = getCenterY();
		float ak = 0;
		float gk = 0;
		if(targetIsWall) {
			ak = currentTargetBoardRectangle.getCenterX() - cx;
			gk = currentTargetBoardRectangle.getCenterY() - cy;
		}else {
			ak = currentTargetGamePiece.getCenterX() - cx;
			gk = currentTargetGamePiece.getCenterY() - cy;
		}
		
		
		angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		if(angleDesired +180 == angle) {
			angleDesired+= 10;
		}
		
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	// draws a HealthBar and a String with The HealthAmount
	public void drawHealth(Graphics2D g2d) {
		if(!isDead) {
			Font fHealthBar = new Font("Arial",Font.BOLD,18);
			FontMetrics metrics = g2d.getFontMetrics(fHealthBar);
			double healthRound = Math.round(this.health*100.0)/100.0;
			String s = healthRound +"";
			int textWidth = metrics.stringWidth(s);
	
			g2d.setColor(new Color(0,0,0,200));
			Rectangle maxHealthRect = new Rectangle((int)getRectHitbox().getCenterX() - (int)(getRectHitbox().width*0.75), (int)getRectHitbox().getCenterY() - boardRect.getSize(), boardRect.getSize(), 15);
			g2d.fill(maxHealthRect);
			g2d.setColor(Commons.cHealth);
			g2d.fillRect((int)getRectHitbox().getCenterX() - (int)(getRectHitbox().width*0.75),(int)getRectHitbox().getCenterY() - boardRect.getSize(), (int)(boardRect.getSize()*(health/maxHealth)), 15);
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.BLACK);
			g2d.draw(maxHealthRect);
			
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(1));
			for(int i = 0;i<maxHealth;i++) {
				g2d.drawLine((int)(maxHealthRect.x+maxHealthRect.width*(i/maxHealth)), (int)maxHealthRect.y, (int)(maxHealthRect.x+maxHealthRect.width*(i/maxHealth)), (int)(maxHealthRect.y+maxHealthRect.height));
			}
//			g2d.setFont(fHealthBar);
//			g2d.setColor(Color.WHITE);
//			g2d.drawString(health + "", boardRect.x + boardRect.size/2 -textWidth/2, maxHealthRect.y + maxHealthRect.height);
		}
	}
	// draws differently if it is Selected
	public void drawSelect(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(4));
		g2d.setColor(Color.GREEN);
		g2d.draw(getRectHitbox());
	}
	// damages the Piece (health--)
	public void getDamaged(double dmg,CommanderGamePiece otherCommander) {
		addDmgLabel(this,dmg);
		resetDmgFlashCountDown();
		if(health -dmg > 0) {
			health -= dmg;
			for(int i = 0;i<(int)dmg;i++) {
			StagePanel.particles.add(new UltChargeOrb(getCenterX()+(int)((Math.random()-0.5)*boardRect.getSize()), getCenterY()+(int)((Math.random()-0.5)*boardRect.getSize()), otherCommander));
			}
		}else {
			for(int i = 0;i<(int)health;i++) {
				StagePanel.particles.add(new UltChargeOrb(getCenterX()+(int)((Math.random()-0.5)*boardRect.getSize()), getCenterY()+(int)((Math.random()-0.5)*boardRect.getSize()), otherCommander));
			}
			health = 0;
		}
	}
	// resets the count down of the DmgFlash so if GamePieces are hit they Flash in white
	public void resetDmgFlashCountDown() {
		dmgFlashCountDown = Commons.dmgFlashCountDown;
	}
	
	public void addDmgLabel(GamePiece targetGP,double dmg) {
		if(!targetGP.isDead) {
			Point targetP = new Point(targetGP.boardRect.getX() + targetGP.boardRect.getSize()/2,targetGP.boardRect.getY() + targetGP.boardRect.getSize()/2);
			StagePanel.dmgLabels.add(new DmgLabel((float)(targetP.getX()+((Math.random()-0.5)*60)),(float)(targetP.getY()+((Math.random()-0.5)*60)),dmg,2,Color.WHITE));
		}	
	}
	
	public void restoreMovesAndAttacks() {
		hasExecutedAttack = false;
		hasExecutedMove = false;
	}
	// checks if two pieces are enemies and returns true if they are enemies
	public boolean checkIfEnemies(GamePiece gP) {
		if(gP.isEnemy && !this.isEnemy) {
			return true;
		}
		if(!gP.isEnemy && this.isEnemy) {
			return true;
		}
		return false;
	}
	// moves the GamePiece to the parameter BoardRectangle and exhausts its moving ability
	public void startMove() {
		movesPanel.setMoveButtonActive(false);
		hasExecutedMove = true;
		rectSizeInc = 5;
		currentTargetGamePiece = null;
		currentTargetBoardRectangle = null;
		isMoving = true;
		
		gamePieceBase.curTargetPathCellIndex = 0;	
	}
	
	public void updateMove() {
		gamePieceBase.updateAngle();
		gamePieceBase.move(this);
		updatePosPointer((int)gamePieceBase.getX(), (int)gamePieceBase.getY());
	}
}
