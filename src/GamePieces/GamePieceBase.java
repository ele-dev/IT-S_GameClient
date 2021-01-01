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
import java.util.ArrayList;

import javax.swing.Timer;

import Sound.SoundEffect;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;
import Stage.ValueLabel;

// Lower half of the GamePiece (The half that does all the movement animating)
public class GamePieceBase {
	private float x,y;
	private Rectangle rectHitbox;
	private float angle,angleDesired;
	private static float rotationDelay,v;
	private Sprite spriteBase;
	private float health,maxHealth,shield,maxShield;
	private int movementRange;
	
	static ArrayList<BoardRectangle> pathBoardRectangles = new ArrayList<BoardRectangle>();
	int curTargetPathCellIndex = 0;
	private GamePiece parentGP;
	
	private Timer tAutoDirectionCorrection;
	 
	public GamePieceBase(float x, float y, int w, int h, Color c,int baseTypeIndex, GamePiece parentGP) {
		this.x = x;
		this.y = y;
		this.rectHitbox = new Rectangle((int)x-w/2,(int)y-h/2,w,h);
		this.parentGP = parentGP;
		v = StagePanel.boardRectSize/40.0f;
		rotationDelay = StagePanel.boardRectSize/20.0f;

		initBaseType(baseTypeIndex);
		initSprites(); 
		
		tAutoDirectionCorrection = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				angle = angleDesired;
			}
		});
		tAutoDirectionCorrection.setRepeats(false);
	}
	
	private void initBaseType(int baseTypeIndex) {
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
		case 2:
			this.maxHealth = Commons.maxHealthType2;
			this.movementRange = Commons.MovementRangeType2;
			this.maxShield = Commons.maxShieldType2;
			break;
		default:
			break;
		}
		health = maxHealth;
		shield = maxShield;
	}
	
	private void initSprites() {
		ArrayList<String> spriteLinks = new ArrayList<String>();
		if(parentGP.isRed()) {
			spriteLinks.add(Commons.directoryToSprites+"GamePieces/GamePieceBaseE0.png");
			spriteLinks.add(Commons.directoryToSprites+"GamePieces/GamePieceBaseE1.png");
		}else {
			spriteLinks.add(Commons.directoryToSprites+"GamePieces/GamePieceBaseNE0.png");
			spriteLinks.add(Commons.directoryToSprites+"GamePieces/GamePieceBaseNE1.png");
		}
		spriteBase = new Sprite(spriteLinks, StagePanel.boardRectSize,StagePanel.boardRectSize, 10);
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
//		if(targetPoint != null) {
//			g2d.setColor(Commons.cMove);
//			g2d.setStroke(new BasicStroke(4));
//			g2d.translate(targetPoint.x, targetPoint.y);
//			g2d.drawLine(-10, 0, 10, 0);
//			g2d.drawLine(0, -10, 0, 10);
//			g2d.translate(-targetPoint.x, -targetPoint.y);
//		}
	}
	
	// draws a HealthBar and a String with The HealthAmount
	public void drawHealth(Graphics2D g2d) {	
		g2d.setColor(new Color(0,0,0,200));
		int w = StagePanel.boardRectSize;
		int h = StagePanel.boardRectSize/6;
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
		if(parentGP.boardRect == StagePanel.curHoverBR || parentGP == StagePanel.curSelectedGP) {
			drawHealthValues(g2d, x, y,StagePanel.boardRectSize/5);
		}
	}
	
	public void drawHealth(Graphics2D g2d, int x, int y, int w, int h, int fontSize) {	
		g2d.setColor(new Color(0,0,0,200));
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
		if(parentGP.boardRect == StagePanel.curHoverBR || parentGP == StagePanel.curSelectedGP) {
			drawHealthValues(g2d, x, y,fontSize);
		}
	}
	
	private void drawHealthValues(Graphics2D g2d,int x, int y, int fontSize) {
		g2d.setFont(new Font("Arial",Font.BOLD,fontSize));
		FontMetrics metrics = g2d.getFontMetrics();
		int textHeight = metrics.getHeight();
		int textWidth = 0;
		
		String str = Math.round(health)+"";
		textWidth = metrics.stringWidth(str);
		int size0 = (int) (textWidth+textHeight/2);
		
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
		int size1 = (int) (textWidth+textHeight/2);
		
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
	
	// damages the Piece (health--)
	public void getDamaged(float dmg) {
		StagePanel.addValueLabel(parentGP,dmg,Commons.cAttack);
		if(shield - dmg >= 0) {
			shield-=dmg;
			dmg = 0;
		}else {
			dmg-=shield;
			shield = 0;
		}
		if(health-dmg > 0) {
			health-=dmg;
		}else {
			health = 0;
		}
	}
	
	public void regenShield() {
		if(shield + Commons.shieldRegen >= maxShield) {
			if(shield < maxShield) {
				StagePanel.valueLabels.add(new ValueLabel(x+(int)((Math.random()-0.5)*StagePanel.boardRectSize), y+(int)((Math.random()-0.5)*StagePanel.boardRectSize), "+" + Commons.shieldRegen+"", Commons.cShield));
			}
			shield = maxShield;
		}else {
			shield += Commons.shieldRegen;
			StagePanel.valueLabels.add(new ValueLabel(x+(int)((Math.random()-0.5)*StagePanel.boardRectSize), y+(int)((Math.random()-0.5)*StagePanel.boardRectSize), "+" + Commons.shieldRegen+"", Commons.cShield));
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
				tAutoDirectionCorrection.restart();
			}else {
				parentGP.isMoving = false;
				parentGP.boardRect = pathBoardRectangles.get(curTargetPathCellIndex);
				pathBoardRectangles.clear();
				tAutoDirectionCorrection.stop();
				StagePanel.tryCaptureGoldMine(parentGP);
			}
		}
	}
	
	// slowly moves the angle towards the desired angle (rotationDelay controls how fast this happens)
	public void updateAngle() {
		float ak = 0;
		float gk = 0;
		ak = (float) (pathBoardRectangles.get(curTargetPathCellIndex).getCenterX() - x);
		gk = (float) (pathBoardRectangles.get(curTargetPathCellIndex).getCenterY() - y);
		angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		// if the angle and the angleDesired are opposites the Vector point into the opposite direction
		// this means the angle will be the angle of the longer vector which is always angle
		// so if that happens the angleDesired is offset so this won't happen
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	
	
}
