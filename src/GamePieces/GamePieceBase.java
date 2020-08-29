package GamePieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import Particles.UltChargeOrb;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;
import Stage.ValueLabel;

// Lower half of the GamePiece (The half that does all the movement animating)
public class GamePieceBase {
	private float x,y;
	private Rectangle rectHitbox;
	private float angle,angleDesired,v = 3f;
	private float rotationDelay = 2.5f;
	private Sprite spriteBase;
	private float health,maxHealth,shield,maxShield;
	private int movementRange;
	
	ArrayList<BoardRectangle> pathBoardRectangles = new ArrayList<BoardRectangle>();
	int curTargetPathCellIndex = 0;
	GamePiece parentGP;
	
	int baseTypeIndex;
	Point targetPoint;
	
	
	public GamePieceBase(float x, float y, int w, int h, Color c,int baseTypeIndex, GamePiece parentGP) {
		this.x = x;
		this.y = y;
		this.rectHitbox = new Rectangle((int)x-w/2,(int)y-h/2,w,h);
		this.parentGP = parentGP;

		initBaseType(baseTypeIndex);
		initSprites();
	}
	
	private void initBaseType(int baseTypeIndex) {
		this.baseTypeIndex = baseTypeIndex;
		
		switch (baseTypeIndex) {
		case 0:
			this.maxHealth = Commons.maxHealthType0;
			this.movementRange = Commons.MovementRangeType0;
			this.maxShield = Commons.maxShieldType0;
			break;
		case 1:
			this.maxHealth = Commons.maxHealthType1;
			this.movementRange = Commons.MovementRangeType1;
			this.maxShield = Commons.maxShieldType1;
			break;
		default:
			break;
		}
		
		this.health = this.maxHealth;
		this.shield = this.maxShield;
	}
	
	private void initSprites() {
		ArrayList<String> spriteLinks = new ArrayList<String>();
		if(parentGP.getIsEnemy()) {
			spriteLinks.add(Commons.pathToSpriteSource+"GamePieces/GamePieceBaseE0.png");
			spriteLinks.add(Commons.pathToSpriteSource+"GamePieces/GamePieceBaseE1.png");
		}else {
			spriteLinks.add(Commons.pathToSpriteSource+"GamePieces/GamePieceBaseNE0.png");
			spriteLinks.add(Commons.pathToSpriteSource+"GamePieces/GamePieceBaseNE1.png");
		}
		spriteBase = new Sprite(spriteLinks, Commons.boardRectSize,Commons.boardRectSize, 10);
	}
	
	// getters
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public Rectangle getRectHitbox() {
		return rectHitbox;
	}
	public float getHealth() {
		return health;
	}
	public float getMaxHealth() {
		return maxHealth;
	}
	public int getMovementRange() {
		return movementRange;
	}
	
	// rendering method
	public void drawGamePieceBase(Graphics2D g2d) {
		spriteBase.drawSprite(g2d, (int)x, (int)y, angle+90, 1);
		if(parentGP.isMoving) {
			spriteBase.animate();
		}
		if(targetPoint != null) {
			g2d.setColor(Commons.cMove);
			g2d.setStroke(new BasicStroke(4));
			g2d.translate(targetPoint.x, targetPoint.y);
			g2d.drawLine(-10, 0, 10, 0);
			g2d.drawLine(0, -10, 0, 10);
			g2d.translate(-targetPoint.x, -targetPoint.y);
		}
		
	}
	
	// draws a HealthBar and a String with The HealthAmount
	public void drawHealth(Graphics2D g2d) {	
		g2d.setColor(new Color(0,0,0,200));
		int w = parentGP.boardRect.getSize();
		int h = 15;
		int x = (int)getRectHitbox().getCenterX() - w/2;
		int y = (int)getRectHitbox().getCenterY() - parentGP.boardRect.getSize()/2;
		
		Rectangle maxHealthShieldRect = new Rectangle(x, y, w, h);
		g2d.fill(maxHealthShieldRect);
		float unitHealthSize = (w*(1.0f/(maxHealth+maxShield)));
		Rectangle maxHealthRect = new Rectangle(x,y, (int)(unitHealthSize * health), h);
		Rectangle maxShieldRect = new Rectangle(x+(int)(unitHealthSize * health),y, (int)(unitHealthSize * shield), h);
		g2d.setColor(Commons.cHealth);
		g2d.fill(maxHealthRect);
		g2d.setColor(Commons.cShield);
		g2d.fill(maxShieldRect);
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.BLACK);
		g2d.draw(maxHealthShieldRect);
		
		
//		g2d.setColor(Color.BLACK);
//		g2d.setStroke(new BasicStroke(1));
//		for(int i = 1;i<(maxHealth+maxShield+1);i++) {
//			g2d.drawLine((int)(x+i*unitHealthSize), y, (int)(x+i*unitHealthSize), y+h);
//		}	
		if(parentGP.boardRect == StagePanel.curHoverBoardRectangle || parentGP.isSelected) {
			drawHealthValues(g2d, x, y);
		}
	}
	
	private void drawHealthValues(Graphics2D g2d,int x, int y) {
		g2d.setFont(new Font("Arial",Font.BOLD,25));
		FontMetrics metrics = g2d.getFontMetrics();
		int textHeight = metrics.getHeight();
		int textWidth = 0;
		
		String str = Math.round(health)+"";
		textWidth = metrics.stringWidth(str);
		int size0 = (int) (textWidth+20);
		
		Rectangle r = new Rectangle((int)x, (int)y-textHeight, size0, textHeight);
		g2d.setColor(new Color(20,20,20));
		g2d.fill(r);	
		g2d.setColor(new Color(5,5,5));
		g2d.setStroke(new BasicStroke(5));
		g2d.draw(r);
		g2d.setColor(Commons.cHealth);
		g2d.drawString(str, (int)r.getCenterX()-textWidth/2, (int)r.getCenterY()+textHeight/3);
		
		str = Math.round(shield)+"";
		textWidth = metrics.stringWidth(str);
		int size1 = (int) (textWidth+20);
		
		if(maxShield > 0) {
			r = new Rectangle((int)x+size0, (int)y-textHeight, size1, textHeight);
			g2d.setColor(new Color(20,20,20));
			g2d.fill(r);	
			g2d.setColor(new Color(5,5,5));
			g2d.setStroke(new BasicStroke(5));
			g2d.draw(r);
			g2d.setColor(Commons.cShield);
			g2d.drawString(str, (int)r.getCenterX()-textWidth/2, (int)r.getCenterY()+textHeight/3);
		}
	}
	
	public void drawMoveRange(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		FontMetrics metrics = g2d.getFontMetrics();
		int textHeight = metrics.getHeight();
		int textWidth = 0;
		if(parentGP.movesPanel.getMoveButtonIsActive()) {
			g2d.setColor(Commons.cMove);
			for(int i = 1;i<pathBoardRectangles.size();i++) {
				textWidth = metrics.stringWidth(i+"");
				g2d.drawString(i+"", pathBoardRectangles.get(i).getCenterX()-textWidth/2, pathBoardRectangles.get(i).getCenterY()+textHeight/3);
			}
			
		}
		int usedMoves = pathBoardRectangles.size()-1;
		if(usedMoves < 0) {
			usedMoves = 0;
		}
		if(!parentGP.hasExecutedAttack && (parentGP.getIsEnemy() && StagePanel.getIsEnemyTurn()) || (!parentGP.getIsEnemy() && !StagePanel.getIsEnemyTurn())) {
			if(!parentGP.hasExecutedMove) {
				textWidth = metrics.stringWidth(movementRange-(usedMoves)+"");
				int size = (int) (textWidth*2);
				Rectangle r = new Rectangle((int)x-size/2, (int)y-size/2+Commons.boardRectSize/2, size, size);
				g2d.setColor(new Color(20,20,20));
				g2d.fill(r);	
				g2d.setColor(new Color(5,5,5));
				g2d.setStroke(new BasicStroke(5));
				g2d.draw(r);
				g2d.setColor(Commons.cMove);
				g2d.drawString(movementRange-(usedMoves)+"", (int)x-textWidth/2, (int)y+textHeight/3 + Commons.boardRectSize/2);
			}
		}
	}
	
	// damages the Piece (health--)
	public void getDamaged(float dmg,CommanderGamePiece otherCommander) {
		addDmgLabel(parentGP,dmg);
		int BRS= Commons.boardRectSize;
		if(shield - dmg >= 0) {
			shield-=dmg;
			dmg = 0;
		}else {
			dmg-=shield;
			shield = 0;
		}
		if(health-dmg > 0) {
			health-=dmg;
			for(int i = 0;i<(int)dmg;i++) {
				StagePanel.particles.add(new UltChargeOrb(x+(int)((Math.random()-0.5)*BRS), y+(int)((Math.random()-0.5)*BRS), otherCommander));
			}
		}else {
			for(int i = 0;i<(int)health;i++) {
				StagePanel.particles.add(new UltChargeOrb(x+(int)((Math.random()-0.5)*BRS), y+(int)((Math.random()-0.5)*BRS), otherCommander));
			}
			health = 0;
		}
	}
	
	public void regenShield() {
		if(shield + Commons.shieldRegen >= maxShield) {
			shield = maxShield;
		}else {
			shield += Commons.shieldRegen;
			StagePanel.valueLabels.add(new ValueLabel(x, y, "+" + Commons.shieldRegen+" Shield", 2,0.3f, Commons.cShield));
		}
	}
		
	private void addDmgLabel(GamePiece targetGP,float dmg) {
		if(!targetGP.isDead) {
			StagePanel.valueLabels.add(new ValueLabel((float)(x+((Math.random()-0.5)*60)),(float)(y+((Math.random()-0.5)*60)),"-"+Math.round(dmg),2,0.3f,new Color(255,0,50)));
		}	
	}
	
	// moves the GamePieceBase only if the angle is somewhat in the direction of the desiredangle, so it only moves if it points in the right direction
	public void move() {
		// tolerance of angle at which the Base starts moving
		int tolerance = 10;
		if(Math.abs(angle) < Math.abs(angleDesired)+tolerance && Math.abs(angleDesired)-tolerance < Math.abs(angle)) {
			double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
			double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
			
			this.x += vX;
			this.y += vY;	
			this.rectHitbox = new Rectangle((int)(x-rectHitbox.width/2),(int)(y-rectHitbox.height/2),rectHitbox.width,rectHitbox.height);
		}
		// curTargetPathBoardRectangle
		Rectangle curTPBR = pathBoardRectangles.get(curTargetPathCellIndex).rect;
		Rectangle rectSmallerBR = new Rectangle((int)curTPBR.getCenterX()-5,(int)curTPBR.getCenterY()-5,10,10);
		// updates the index when it crosses a pathCell so it counts down from pathCell to pathCell,
		// always having the next pathCell in the array as the target until the end is reached then it stops
		
		
		if(rectSmallerBR.contains(new Point((int)x,(int)y))) {
			if(curTargetPathCellIndex < pathBoardRectangles.size()-1) {
				curTargetPathCellIndex++;
				parentGP.boardRect = pathBoardRectangles.get(curTargetPathCellIndex-1);
			}else {
				parentGP.isMoving = false;
				parentGP.boardRect = pathBoardRectangles.get(curTargetPathCellIndex);
				pathBoardRectangles.clear();
			}
		}
	}
	
	// slowly moves the angle towards the desired angle (rotationDelay controls how fast this happens)
	public void updateAngle() {
		targetPoint = new Point(pathBoardRectangles.get(curTargetPathCellIndex).getCenterX(),pathBoardRectangles.get(curTargetPathCellIndex).getCenterY());
		float ak = 0;
		float gk = 0;
		ak = (float) (targetPoint.x - x);
		gk = (float) (targetPoint.y - y);
		angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		// if the angle and the angleDesired are opposites the Vector point into the opposite direction
		// this means the angle will be the angle of the longer vector which is always angle
		// so if that happens the angleDesired is offset so this won't happen
		
		if((angleDesired +175< angle && angleDesired +185> angle) || (angleDesired -175< angle && angleDesired -185> angle)) {
			angle = angleDesired;
			System.out.println(angle+"/"+angleDesired);
		}
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	
	
}
