package GamePieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;

import Abilities.RadialShield;
import Buttons.ActionSelectionPanel;
import Particles.Explosion;
import PathFinder.AStarPathFinder;
import PathFinder.PathCell;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;


public abstract class GamePiece {
	public BoardRectangle boardRect;
	private Rectangle rectShowTurret;
	protected Color c,cTurret;
	private String name;
	protected float angle,angleDesired;
	public boolean isDead = false;
	private float dmg;
	
	public boolean isSelected = false;
	private boolean isEnemy;
	protected boolean hasExecutedMove = false;
	protected boolean hasExecutedAttack = false;
	
	protected Timer attackDelayTimer,abilityDelayTimer,deathDelayTimer;
	protected GamePiece currentTargetGamePiece;
	protected RadialShield currenTargetShield;
	protected Arc2D aimArc;
	
	public boolean isMoving,isAttacking,isPerformingAbility;
	public ActionSelectionPanel actionSelectionPanel;
	private ArrayList<Line2D> sightLines = new ArrayList<Line2D>();
	
	private int rotationDelay = 4;
	protected Sprite spriteTurret;
	
	
	// pathfinding
	private AStarPathFinder pathFinder;
	public GamePieceBase gamePieceBase;
	
	public GamePiece(boolean isEnemy,String name,BoardRectangle boardRect,float dmg,int baseTypeIndex) {
		this.isEnemy = isEnemy;
		this.boardRect = boardRect;
		deathDelayTimer = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				StagePanel.particles.add(new Explosion(getCenterX(), getCenterY(), 1));
				for(int i = 0;i<3;i++) {
					StagePanel.particles.add(new Explosion(getCenterX()+(int)((Math.random()-0.5)*Commons.boardRectSize),
							getCenterY()+(int)((Math.random()-0.5)*Commons.boardRectSize), 1f));
				}
				
				isDead = true;
				
			}
		});
		deathDelayTimer.setRepeats(false);
    
		rectShowTurret = new Rectangle((int)(-boardRect.getSize()*0.2),(int)(-boardRect.getSize()*0.2),(int)(boardRect.getSize()*0.4),(int)(boardRect.getSize()*0.4));
		if(isEnemy) {
			this.c = Commons.enemyColor;
			this.cTurret = Commons.enemyColorTurret;
		}else {
			this.c = Commons.notEnemyColor;
			this.cTurret = Commons.notEnemyColorTurret;
		} 
		gamePieceBase = new GamePieceBase(boardRect.getCenterX(), boardRect.getCenterY(), rectShowTurret.width+10, rectShowTurret.width+10,c,baseTypeIndex,this);

		this.name = name;
		this.dmg = dmg;

		this.actionSelectionPanel = new ActionSelectionPanel(this);
	}
	
	public abstract void update();
	
	public Color getColor() {
		return c;
	} 
	
	public boolean isPerformingAction() {
		return isAttacking || isMoving || isPerformingAbility || deathDelayTimer.isRunning();
	}
	
	// initializes the Pathfinding Grid (!!Does not start the Pathfinder!!)
	public void initPathFinder() {
		ArrayList<PathCell> pathCells = new ArrayList<PathCell>();
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
			BoardRectangle curBR  = StagePanel.boardRectangles.get(i);
			pathCells.add(new PathCell(curBR.getX(), curBR.getY(), Commons.boardRectSize, curBR.row, curBR.column,i));
				
			if(curBR.isWall) {
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
	
	// resets the Pathfinder and also finds a Path to the endBR
	public void resetPathFinder(BoardRectangle startBR, BoardRectangle endBR) {
		gamePieceBase.pathBoardRectangles.clear();
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
			StagePanel.boardRectangles.get(i).isPossibleMove = false;
			StagePanel.boardRectangles.get(i).isShowPossibleMove = false;
		}
		if(endBR.isWall || startBR.isWall){
			return;
		}
		for(GamePiece curGP : StagePanel.gamePieces) {
			if(curGP.boardRect == endBR && !curGP.isDead && curGP != this) {
				return;
			}
		}
		// initializes Pathfinder again
		initPathFinder();
		// copies the end and startBR to the Pathfinders Grid
		PathCell startPathCell = null, endPathCell =  null;
		for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
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
		for(int j = pathFinder.getPathPathCells().size()-1;j>=pathFinder.getPathPathCells().size()-(gamePieceBase.getMovementRange()+1) && j>= 0;j--) {
			for(int i = 0;i<StagePanel.boardRectangles.size();i++) {
				if(pathFinder.getPathPathCells().get(j).getIndex() == i) {
					gamePieceBase.pathBoardRectangles.add(StagePanel.boardRectangles.get(i));
					break;
				}
			}
		}
		for(BoardRectangle curBr : gamePieceBase.pathBoardRectangles) {
			if(curBr == StagePanel.curHoverBR) {
				curBr.isPossibleMove = true;
			}
			curBr.isShowPossibleMove = true;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getIsDead() {
		return isDead;
	}
	
	public boolean getIsEnemy() {
		return isEnemy;
	}
	
	public Rectangle getRectHitbox() {
		return gamePieceBase.getRectHitbox();
	}
	
	public GamePiece getCurrentTargetGamePiece() {
		return currentTargetGamePiece;
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
	
	public Point getPos() {
		return new Point(getCenterX(),getCenterY());
	}
	
	public void tryDie() {
		if(gamePieceBase.getHealth() <= 0 && !deathDelayTimer.isRunning() && !getIsDead()) {
			deathDelayTimer.start();
		}
	}
	
	// sets each BoardRectangle to being a possible attackPosition if it is(changes color accordingly)
	public void showPossibleAttacks() {
		if(isSelected) {
			sightLines.clear(); 
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(checkAttacks(curBR.row, curBR.column) && !curBR.isWall) {
					boolean success = true;
					for(GamePiece curGP : StagePanel.gamePieces) {
						if(curGP.boardRect == curBR && !this.checkIfEnemies(curGP)) {
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
	} 

	
	// returns true if the BoardRectangle is in sight of the GamePiece and return false if it is for example behind a wall
	public boolean checkIfBoardRectangleInSight(BoardRectangle targetBoardRectangle) {
		Line2D lineOfSight = new Line2D.Double(boardRect.getCenterX(),boardRect.getCenterY(),targetBoardRectangle.getCenterX(),targetBoardRectangle.getCenterY());	
		sightLines.add(lineOfSight);
				
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			Rectangle rectWall = new Rectangle(curBR.rect.x +10 , curBR.rect.y +10,curBR.rect.width-20,curBR.rect.height-20);
			if(lineOfSight.intersects(rectWall) && (curBR.isWall) && (!targetBoardRectangle.isWall)) {
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
	public void startAttack(BoardRectangle targetBoardRectangle) {
		for(RadialShield curRS : StagePanel.radialShields) {
			if(curRS.contains(targetBoardRectangle) && checkIfEnemies(curRS)) {
				currenTargetShield = curRS;
				isAttacking = true;
				attackDelayTimer.start();
				hasExecutedAttack = true;
				hasExecutedMove = true;
				return;
			}
		}
		for(GamePiece curGP : StagePanel.gamePieces) {
			if(curGP.boardRect == targetBoardRectangle) {
				if(!curGP.isDead && checkIfEnemies(curGP)) {
					currentTargetGamePiece = curGP;
					isAttacking = true;
					attackDelayTimer.start();
					hasExecutedAttack = true;
					hasExecutedMove = true;
					return;
				}	
			}
		}
	}
	
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
			gamePieceBase.drawMoveRange(g2d);
		}
		
	}
	
	public void drawPointer(Graphics2D g2d) {
		if(!hasExecutedAttack) {
			g2d.setColor(c);
			
		}else {
			g2d.setColor(new Color(c.getRed()/2,c.getGreen()/2,c.getBlue()/2,200));
		}
		
		int x = boardRect.getX();
		int y = boardRect.getY();
		int s = boardRect.getSize();
		int soI = (int)boardRect.so;
		g2d.setStroke(new BasicStroke(6));
		g2d.drawLine(x-soI/2, y-soI/2, x+s/4-soI/2, y-soI/2);
		g2d.drawLine(x+s+soI/2, y-soI/2, x+s*3/4+soI/2, y-soI/2);
				
		g2d.drawLine(x-soI/2, y+s+soI/2, x+s/4-soI/2, y+s+soI/2);
		g2d.drawLine(x+s+soI/2, y+s+soI/2, x+s*3/4+soI/2, y+s+soI/2);
				
		g2d.drawLine(x-soI/2, y-soI/2, x-soI/2, y+s/4-soI/2);
		g2d.drawLine(x-soI/2, y+s+soI/2, x-soI/2, y+s*3/4+soI/2);
				
		g2d.drawLine(x+s+soI/2, y-soI/2, x+s+soI/2, y+s/4-soI/2);
		g2d.drawLine(x+s+soI/2, y+s+soI/2, x+s+soI/2, y+s*3/4+soI/2);	
	}
	// updates the MovesPanels position so it follows the camera (also updates the isHover boolean)
	public void updateActionSelectionPanelPos(Point CameraPos,Point mousePos) {
		actionSelectionPanel.updatePos(CameraPos);
		if(mousePos != null && isSelected) {
			actionSelectionPanel.updateHover(mousePos);
		}
	}
	
	// updates the shot angle towards the TargetEnemy/TargetGamepice
	public void updateAngle(Point targetPoint) {
		int cx = getCenterX();
		int cy = getCenterY();
		
		float ak = (float) (targetPoint.getX() - cx);
		float gk = (float) (targetPoint.getY() - cy);
		
		angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
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
	public boolean checkIfEnemies(RadialShield rs) {
		if(rs.isEnemy && !this.isEnemy) {
			return true;
		}
		if(!rs.isEnemy && this.isEnemy) {
			return true;
		}
		return false;
	}
	// moves the GamePiece to the parameter BoardRectangle and exhausts its moving ability
	public void startMove() {
		actionSelectionPanel.setMoveButtonActive(false);
		hasExecutedMove = true;
		isMoving = true;
		gamePieceBase.curTargetPathCellIndex = 0;	
	}
	
	public void updateMove() {
		gamePieceBase.updateAngle();
		gamePieceBase.move();
	}
}
