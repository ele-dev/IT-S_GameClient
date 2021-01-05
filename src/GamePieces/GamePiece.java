package GamePieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Buttons.ActionSelectionPanel;
import Environment.DestructibleObject;
import Particles.Explosion;
import PathFinder.AStarPathFinder;
import PathFinder.PathCell;
import PlayerStructures.GoldMine;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;

public abstract class GamePiece {
	protected BoardRectangle boardRect;
	protected Color c;
	private String name;
	protected float angle,angleDesired; 
	private float dmg;
	protected boolean lineOfSightNeeded = false;
	
	private boolean isRed;
	protected boolean hasExecutedMove = true;  
	protected boolean hasExecutedAttack = true; 
	
	protected Timer attackDelayTimer,deathDelayTimer;
	protected GamePiece targetGamePiece;
	protected DestructibleObject targetDestructibleObject;
	
	public boolean isMoving;
	public ActionSelectionPanel actionSelectionPanel;
	private ArrayList<Line2D> linesOfSight = new ArrayList<Line2D>();
	protected Sprite spriteTurret;
	public GamePieceBase gamePieceBase;
	
	private static int rotationDelay = 4;
	
	private static AStarPathFinder pathFinder;
	
	public GamePiece(boolean isRed, String name, BoardRectangle boardRect, float dmg, int baseTypeIndex,boolean lineOfSightNeeded) {
		if(isRed) {
			this.c = Commons.cRed;
		}else {
			this.c = Commons.cBlue;
		}
		this.lineOfSightNeeded = lineOfSightNeeded;
		
		this.isRed = isRed;
		this.boardRect = boardRect;		
		gamePieceBase = new GamePieceBase(boardRect.getCenterX(), boardRect.getCenterY(), boardRect.getSize(), boardRect.getSize(),c,baseTypeIndex,this);

		this.name = name;
		this.dmg = dmg;
		this.actionSelectionPanel = new ActionSelectionPanel(this);
	}
	
	public String getName() {
		return name;
	}
	public boolean isRed() {
		return isRed;
	}
	public Rectangle getRectHitbox() {
		return gamePieceBase.getRectHitbox();
	}
	public BoardRectangle getBoardRect() {
		return boardRect;
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
	public void setHasExecutedMove(boolean hasExecutedMove) {
		this.hasExecutedMove = hasExecutedMove;
	}
	public int getCenterX() {
		return (int) getRectHitbox().getCenterX();
	}
	public int getCenterY() {
		return (int) getRectHitbox().getCenterY();
	}
	public Point getPos() {
		return new Point(getCenterX(),getCenterY());
	}
	public Color getColor() {
		return c;
	} 
	
	public boolean isDead() {
		return gamePieceBase.getHealth() <= 0;
	}
	
	public abstract boolean isAttacking();
	
	public boolean isPerformingAction() {
		return isAttacking() || isMoving;
	}
	
	// initializes the Pathfinding Grid (!!Does not start the Pathfinder!!)
	public static void initPathFinder(BoardRectangle startBR) {
		ArrayList<PathCell> pathCells = new ArrayList<PathCell>();
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
			BoardRectangle curBR  = StagePanel.boardRectangles.get(i);
			pathCells.add(new PathCell(curBR.getX(), curBR.getY(), StagePanel.boardRectSize, curBR.row, curBR.column,i));
				
			if(curBR.isWall || curBR.isDestructibleObject() || curBR.isGoldMine() || curBR.isGap) {
				pathCells.get(i).setIsWall(true);
			}else
			for(GamePiece curGP : StagePanel.gamePieces) {
				if(curGP.boardRect == StagePanel.boardRectangles.get(i) && StagePanel.boardRectangles.get(i) != startBR) {
					pathCells.get(i).setIsWall(true);
					break;
				}
			}
		} 
		pathFinder = new AStarPathFinder(pathCells);
	}
	
	// resets the Pathfinder and also finds a Path to the endBR
	public void resetPathFinder(BoardRectangle startBR, BoardRectangle endBR, boolean unlimitedMoveRange) {
		GamePieceBase.pathBoardRectangles.clear();
		for(int i = 0; i < StagePanel.boardRectangles.size(); i++) {
			StagePanel.boardRectangles.get(i).isPossibleMove = false;
			StagePanel.boardRectangles.get(i).isShowPossibleMove = false;
		}
		if(endBR.isWall || endBR.isGap){
			return;
		}
		for(GamePiece curGP : StagePanel.gamePieces) {
			if(curGP.boardRect == endBR && curGP != this) {
				return;
			}
		}
		// initializes Pathfinder again
		initPathFinder(startBR);
		// copies the end and startBR to the Pathfinders Grid
		PathCell startPathCell = null, endPathCell =  null;
		for(int i = 0; i < StagePanel.boardRectangles.size(); i++) {
			if(startBR == StagePanel.boardRectangles.get(i)) {
				startPathCell = pathFinder.pathCells.get(i);
			}
			if(endBR == StagePanel.boardRectangles.get(i)){
				endPathCell = pathFinder.pathCells.get(i);
			}
		}
		// sets the End and Start to pathFinder and also calculates Path
		if(startPathCell != endPathCell) {
			pathFinder.setPathEnds(startPathCell, endPathCell);
		}
		// stops if the Path Found has a length of 0 or it has not find a path to the end (maybe the end is blocked by walls)
		if(pathFinder.getPathPathCells().size() == 0 || pathFinder.noSolution) {
			return;
		}
		
		if(!unlimitedMoveRange) {
			int movementRange = gamePieceBase.getMovementRange();
			for(int j = pathFinder.getPathPathCells().size()-1;j >= pathFinder.getPathPathCells().size()-(movementRange+1) && j >= 0; j--) {
				for(int i = 0; i < StagePanel.boardRectangles.size(); i++) {
					if(pathFinder.getPathPathCells().get(j).getIndex() == i) {
						GamePieceBase.pathBoardRectangles.add(StagePanel.boardRectangles.get(i));
						if(StagePanel.boardRectangles.get(i).isHinderingTerrain) {
							movementRange--;
						}
						break;
					}
				}
			}
		} else {
			for(int j = pathFinder.getPathPathCells().size()-1; j >= 0; j--) {
				for(int i = 0; i < StagePanel.boardRectangles.size(); i++) {
					if(pathFinder.getPathPathCells().get(j).getIndex() == i) {
						GamePieceBase.pathBoardRectangles.add(StagePanel.boardRectangles.get(i));
					}
				}
			}
		}
		pathFinder = null;
	}
	
	public void showPathBRs() {
		for(BoardRectangle curBr : GamePieceBase.pathBoardRectangles) {
			if(curBr == StagePanel.curHoverBR) {
				curBr.isPossibleMove = true;
			}
			curBr.isShowPossibleMove = true;
		}
	}
	
	public void update() {
		if(targetGamePiece != null) {
			updateAngle(targetGamePiece.getPos());
		} else if(targetDestructibleObject != null){
			updateAngle(targetDestructibleObject.getPos());
		}
		updateAttack();
	}
	
	public void tryDie() {
		if(gamePieceBase.getHealth() <= 0) {
			StagePanel.particles.add(new Explosion(getCenterX(), getCenterY(), 1.2f));
			for(int i = 0; i < 3; i++) {
				StagePanel.particles.add(new Explosion(getCenterX()+(int)((Math.random()-0.5)*StagePanel.boardRectSize/2),
						getCenterY()+(int)((Math.random()-0.5)*StagePanel.boardRectSize/2), 1f));
			}
		}
	}
	
	// sets each BoardRectangle to being a possible attackPosition if it is(changes color accordingly)
	public void showPossibleAttacks() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(checkAttacks(curBR.row, curBR.column,boardRect.row,boardRect.column) && checkInLineOfSightIfNecessary(curBR.row, curBR.column) && !curBR.isWall) {
				boolean success = true;
				for(GamePiece curGP : StagePanel.gamePieces) {
					if(curGP.boardRect == curBR && !checkIfEnemies(curGP)) {
						success = false;
						break;
					}
				}
				if(success) {
					curBR.isShowPossibleAttack = true;
				}
			}
		}
	} 
	// draws the attack differently for each GamePiece
	public abstract void drawAttack(Graphics2D g2d);
	
	// checks if the PositionParameter is a valid position to attack and returns true if it is
	// is abstract because every GamePiece has a different attack pattern
	public abstract boolean checkAttacks(int selectedRow, int selectedColumn, int myRow, int myColumn);
	
	public boolean checkInLineOfSightIfNecessary(int selectedRow, int selectedColumn) {
		if(lineOfSightNeeded) {
			return checkIfInSight(selectedRow, selectedColumn);
		}else {
			return true;
		}
	}
	
	// updates angle to face toward enemy and starts the attack (starts attackDelayTimer)
	public void startAttack(BoardRectangle targetBoardRectangle) {
		
		targetGamePiece = null;
		targetDestructibleObject = null;
		
		if(StagePanel.redBase.containsBR(targetBoardRectangle) && !isRed) {
			targetDestructibleObject = StagePanel.redBase;
			startAttackDelay();
			return;
		} else if(StagePanel.blueBase.containsBR(targetBoardRectangle) && isRed) {
			targetDestructibleObject = StagePanel.blueBase;
			startAttackDelay();
			return;
		}
		for(GoldMine curGM : StagePanel.goldMines) {
			if(curGM.containsBR(targetBoardRectangle) && checkIfEnemies(curGM)) {
				targetDestructibleObject = curGM;
				startAttackDelay();
				return;
			}
		}
		for(DestructibleObject curDO : StagePanel.destructibleObjects) {
			if(curDO.containsBR(targetBoardRectangle)) {
				targetDestructibleObject = curDO;
				startAttackDelay();
				return;
			}
		}
		for(GamePiece curGP : StagePanel.gamePieces) {
			if(curGP.boardRect == targetBoardRectangle) {
				if(checkIfEnemies(curGP)) {
					targetGamePiece = curGP;
					startAttackDelay();
					return;
				}	
			}
		}
	}
	
	protected void startAttackDelay() {
		attackDelayTimer.start();
		hasExecutedAttack = true;
		hasExecutedMove = true;
	}
	
	// updates all things that are animated with the attack (for example moves the rockets and updates the explosions)
	public abstract void updateAttack();
	
	// draws the GamePiece	
	public void drawGamePiece(Graphics2D g2d) {
		gamePieceBase.drawGamePieceBase(g2d);
		int cx = getCenterX();
		int cy = getCenterY();
		g2d.setColor(c);
		// draws different when it already attacked
		if(hasExecutedAttack) {
			g2d.setColor(new Color(c.getRed()/4,c.getGreen()/4,c.getBlue()/4));
		}
		if(spriteTurret != null) {
			spriteTurret.drawSprite(g2d, cx, cy, angle+90, 1);
		} else {
			g2d.setColor(c);
			g2d.translate(cx, cy);
			g2d.rotate(Math.toRadians(angle));
			g2d.setStroke(new BasicStroke(2));
			Rectangle rectShowTurret = new Rectangle((int)(-boardRect.getSize()*0.2),(int)(-boardRect.getSize()*0.2),(int)(boardRect.getSize()*0.4),(int)(boardRect.getSize()*0.4));
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
			g2d.drawString(text, cx - textWidth/2, cy + textHeight/3);
		}
		
		
		// only Dev
//		g2d.setColor(new Color(0,0,255,200));
//		if(aimArc != null) {
//			g2d.fill(aimArc);
//		}	
//		if(StagePanel.curSelectedGP == this) {
//			drawLinesOfSight(g2d);
//		}
	}
	
	// draws all LinesOfSight (only for devs)
	public void drawLinesOfSight(Graphics2D g2d) {
		g2d.setColor(new Color(0,0,255,200));
		for(Line2D line : linesOfSight) {
			g2d.draw(line);
		}
	}
	
	public void updateLinesOfSight() {
		linesOfSight.clear();
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(checkAttacks(curBR.row, curBR.column, boardRect.row, boardRect.column) && !curBR.isWall) {
				Line2D sightLine = new Line2D.Double(getCenterX(),getCenterY(),curBR.getCenterX(),curBR.getCenterY());
				boolean noIntersection = true;
				for(BoardRectangle curBR1 : StagePanel.boardRectangles) {
					if(curBR1.isWall) {
						Rectangle rectWall = new Rectangle(curBR1.rect.x +10 , curBR1.rect.y +10,curBR1.rect.width-20,curBR1.rect.height-20);
						if(sightLine.intersects(rectWall)) {
							noIntersection = false;
						}
					}
				} 
				if(noIntersection) {
					linesOfSight.add(sightLine);
				}
			}
		}
	}
	
	// returns true if the BoardRectangle is in sight of the GamePiece and return false if it is, for example behind a wall
	public boolean checkIfInSight(int targetRow, int targetColumn) {
		BoardRectangle targetBoardRectangle = null;
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(curBR.row == targetRow && curBR.column == targetColumn) {
				targetBoardRectangle = curBR;
			}
		}
		for(Line2D curLOS : linesOfSight) {
			if(targetBoardRectangle.rect.contains(curLOS.getP2())) {
				return true;
			}
		} 
		return false;
	}
	
	public void drawPointer(Graphics2D g2d) {
		if(!hasExecutedAttack) {
			boardRect.drawBROutline(g2d, Commons.cAttack, 45,StagePanel.boardRectSize*7/8);
			if(!hasExecutedMove) {
				boardRect.drawBROutline(g2d, Commons.cMove, 0,StagePanel.boardRectSize*7/8);
			}
		}
	}
	// updates the MovesPanels position so it follows the camera (also updates the isHover boolean)
	public void updateActionSelectionPanelHover() {
		actionSelectionPanel.updateHover(StagePanel.mousePosUntranslated);
	}
	
	// updates the shot angle towards the TargetEnemy/TargetGamepice
	public void updateAngle(Point targetPoint) {
		angleDesired = calculateAngle(targetPoint.x,targetPoint.y);
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	
	public float calculateAngle(int x , int y) {
		float ak = (float) (x - getCenterX());
		float gk = (float) (y - getCenterY());
		return (float) Math.toDegrees(Math.atan2(ak*-1, gk));
	}
	
	public void restoreMovesAndAttacks() {
		hasExecutedAttack = false;
		hasExecutedMove = false;
	}
	// checks if two pieces are enemies and returns true if they are enemies
	public boolean checkIfEnemies(GamePiece gP) {
		return gP.isRed == !isRed;
	}
	public boolean checkIfEnemies(GoldMine goldMine) {
		return goldMine.getCaptureState() != 0 && (goldMine.getCaptureState()==1 && !isRed || goldMine.getCaptureState()==2 && isRed);
	}
	// moves the GamePiece to the parameter BoardRectangle and exhausts its moving ability
	public void startMove(BoardRectangle targetBoardRectangle) {
		actionSelectionPanel.setMoveButtonActive(false);
		hasExecutedMove = true;
		resetPathFinder(boardRect, targetBoardRectangle, true);
		gamePieceBase.curTargetPathCellIndex = 0;	
		isMoving = true;
		
	}
	
	public void updateMove() {
		if(isMoving) {
			for(int i = 0;i<2;i++) {
				if(GamePieceBase.pathBoardRectangles.size() > 0) {
					gamePieceBase.updateAngle();
					gamePieceBase.move();
				}
			}
		}
	}
	
	// Public helper function to get the GamePiece from it's coordinates on the game Field
	public static GamePiece getGamePieceFromCoords(Point pos) {
		
		GamePiece gp = null;
		
		// Go through the global list and find a match
		for(GamePiece currGP: StagePanel.gamePieces)
		{
			if(currGP.boardRect.row == pos.x && currGP.boardRect.column == pos.y) {
				gp = currGP;
				break;
			}
		}
		
		return gp;
	}
}
