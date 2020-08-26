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
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.DmgLabel;
import Stage.Sprite;
import Stage.StagePanel;


public abstract class GamePiece {
	public BoardRectangle boardRect;
	protected Rectangle rectHitbox;
	private Rectangle rectShowTurret;
	protected Color c;
	private Color cTurret;
	private Color cExecutedAttack;
	private String name;
	protected float angle,angleDesired;
	private boolean isDead = false;
	private float health,maxHealth;
	private float dmg;
	private Font fPieces;
	
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
	
	public GamePiece(boolean isEnemy,String name,BoardRectangle boardRect,int maxHealth,float dmg,CommanderGamePiece commanderGamePiece) {
		this.isEnemy = isEnemy;
		this.boardRect = boardRect;
		rectHitbox = new Rectangle((int)(boardRect.getCenterX()-boardRect.getSize()*0.3),(int)(boardRect.getCenterY()-boardRect.getSize()*0.3),
				(int)(boardRect.getSize()*0.6),(int)(boardRect.getSize()*0.6));
		rectShowTurret = new Rectangle((int)(-boardRect.getSize()*0.2),(int)(-boardRect.getSize()*0.2),(int)(boardRect.getSize()*0.4),(int)(boardRect.getSize()*0.4));
		if(isEnemy) {
			this.c = Commons.enemyColor;
			this.cTurret = Commons.enemyColorTurret;
		}else {
			this.c = Commons.notEnemyColor;
			this.cTurret = Commons.notEnemyColorTurret;
		} 
		this.name = name;
		this.maxHealth = maxHealth;
		this.health = maxHealth;
		this.dmg = dmg;
		this.cExecutedAttack =new Color(c.getRed()/4,c.getGreen()/4,c.getBlue()/4);
		this.fPieces = new Font("Arial",Font.BOLD,30);

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
		return rectHitbox;
	}
	
	public CommanderGamePiece getCommanderGamePiece() {
		return commanderGamePiece;
	}
	
	public GamePiece getCurrentTargetGamePiece() {
		return currentTargetGamePiece;
	}
	public double getHealth() {
		return health;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public double getDmg() {
		return dmg;
	}
	
	public boolean getHasExecutedAttack() {
		return hasExecutedAttack;
	}

	public boolean getHasExecutedMove() {
		return hasExecutedMove;
	}

	public int getCenterX() {
		return (int) rectHitbox.getCenterX();
	}
	public int getCenterY() {
		return (int) rectHitbox.getCenterY();
	}
	
	// checks if the piece is dead and sets it as dead if it is ,also updates the Position of the Hitbox
	public void updatePos(BoardRectangle curHBR) {
		if(health<=0) {
			isDead = true;
		}
		int size = curHBR.getSize();
		if(isSelected && movesPanel.getMoveButtonIsActive() && curHBR.isPossibleMove) {
			rectShowTurret.setBounds((int)(-size*0.2),(int)(-size*0.2),(int)(size*0.4),(int)(size*0.4));
			rectHitbox = new Rectangle((int)(curHBR.getCenterX()-size*0.3-rectSizeInc/2),(int)(curHBR.getCenterY()-size*0.3-rectSizeInc/2)-(int)(size*1.5),
					(int)(size*0.6+rectSizeInc),(int)(size*0.6+rectSizeInc));
			spritePointerX = curHBR.getCenterX();
			spritePointerY = curHBR.getCenterY()-60-(int)(size*1.5);
		}else {
			rectShowTurret.setBounds((int)(-size*0.2),(int)(-size*0.2),(int)(size*0.4),(int)(size*0.4));
			rectHitbox = new Rectangle((int)(boardRect.getCenterX()-size*0.3-rectSizeInc/2),(int)(boardRect.getCenterY()-size*0.3-rectSizeInc/2),
				(int)(size*0.6+rectSizeInc),(int)(size*0.6+rectSizeInc));
			spritePointerX = boardRect.getCenterX();
			spritePointerY = boardRect.getCenterY()-60;
		}
		
	}
	// sets each BoardRectangle to being a possible movePosition if it is(changes color accordingly)
	public void showPossibleMoves() {
		if(isSelected) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				boolean nobodyThere = true;
				for(GamePiece curGP : StagePanel.gamePieces) {
					if(curGP.boardRect.posRow == curBR.posRow && curGP.boardRect.posColumn == curBR.posColumn && !curGP.isDead) {
						nobodyThere = false;
					}
				}
				if(this.checkMoves(curBR.posRow, curBR.posColumn) && nobodyThere) {
					curBR.isPossibleMove = true;
				}
			}
		}
	}
	// sets each BoardRectangle to being a possible attackPosition if it is(changes color accordingly)
	public void showPossibleAttacks() {
		if(isSelected) {
			sightLines.clear();
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(checkAttacks(curBR.posRow, curBR.posColumn) && !curBR.isWall) {
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
	
	// checks if the PositionParameter is a valid position to move to and returns true if it is
	// abstract because every GamePiece has different move pattern
	public boolean checkMoves(int selectedRow,int selectedColumn) {
		if(checkMoveRows(selectedRow,selectedColumn) && checkMoveColumns(selectedRow,selectedColumn)) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(curBR.posRow == selectedRow && curBR.posColumn == selectedColumn && (curBR.isGap || curBR.isWall || curBR.isDestructibleWall)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public abstract boolean checkMoveRows(int selectedRow, int selectedColumn);
	
	public abstract boolean checkMoveColumns(int selectedRow, int selectedColumn);
	
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
			int cx = getCenterX();
			int cy = getCenterY();
			g2d.setColor(c);
			// draws different when it already attacked
			if(hasExecutedAttack) {
				g2d.setColor(cExecutedAttack);
			}
			if(dmgFlashCountDown > 0) {
				g2d.setColor(Color.WHITE);
			}
			g2d.fill(rectHitbox);
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
				g2d.setFont(fPieces);
				FontMetrics metrics = g2d.getFontMetrics();
				String text = name;
				int textHeight = metrics.getHeight();
				int textWidth = metrics.stringWidth(text);
				g2d.drawString(name, cx - textWidth/2, cy + textHeight/3);
			}
			if(isSelected && movesPanel.getMoveButtonIsActive() && curHBR != null && curHBR.isPossibleMove) {
				g2d.setColor(new Color(20,20,20,150));
				g2d.fillRect((int)rectHitbox.getCenterX() - rectHitbox.width/2, (int)(rectHitbox.getCenterY()- rectHitbox.height/2 +boardRect.getSize()*1.5) , rectHitbox.width, rectHitbox.height);
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
	public void updateGamePiece(BoardRectangle curHoverBoardRectangle) {
		updatePos(curHoverBoardRectangle);
		
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
		
		double ak1 = Math.cos(Math.toRadians(angleDesired+90));
		double gk1 = Math.sin(Math.toRadians(angleDesired+90));
		
		double ak2 = Math.cos(Math.toRadians(angle+90)) * rotationDelay;
		double gk2 = Math.sin(Math.toRadians(angle+90)) * rotationDelay;

		double ak3 = (ak1+ak2*rotationDelay)/(rotationDelay+1);
		double gk3 = (gk1+gk2*rotationDelay)/(rotationDelay+1);
		
		angle = (float) Math.toDegrees(Math.atan2(ak3*-1, gk3));
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
			Rectangle maxHealthRect = new Rectangle((int)rectHitbox.getCenterX() - (int)(rectHitbox.width*0.75), (int)rectHitbox.getCenterY() - boardRect.getSize(), boardRect.getSize(), 15);
			g2d.fill(maxHealthRect);
			g2d.setColor(Commons.cHealth);
			g2d.fillRect((int)rectHitbox.getCenterX() - (int)(rectHitbox.width*0.75),(int)rectHitbox.getCenterY() - boardRect.getSize(), (int)(boardRect.getSize()*(health/maxHealth)), 15);
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
		g2d.draw(rectHitbox);
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
	public void move(BoardRectangle boardRectangle) {
		this.boardRect = boardRectangle;
		movesPanel.setMoveButtonActive(false);
		hasExecutedMove = true;
		rectSizeInc = 5;
		currentTargetGamePiece = null;
		currentTargetBoardRectangle = null;
	}
}
