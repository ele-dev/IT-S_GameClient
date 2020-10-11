package Environment;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import Particles.DestructionParticle;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.Sprite;
import Stage.StagePanel;

public class DestructibleObject {
	protected float rotation;
	protected float health;
	protected boolean isDestroyed;
	protected Rectangle rectHitbox;
	protected Sprite sprite;
	protected int impactFlashCounter;
	protected BoardRectangle[] occupiedBRs;
	
	
	public DestructibleObject(BoardRectangle boardRectangle,int occupiedRows,int occupiedColumns,float maxHealth,float rotation) {
		this.health = maxHealth;
		rectHitbox = new Rectangle(boardRectangle.getX(),boardRectangle.getY(),Commons.boardRectSize*occupiedColumns,Commons.boardRectSize*occupiedRows);
		this.rotation = rotation;
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Environment/crate.png");
		sprite = new Sprite(spriteLinks, rectHitbox.width,rectHitbox.height, 0);
		occupiedBRs = new BoardRectangle[occupiedRows*occupiedColumns];
		if(occupiedRows*occupiedColumns > 1) {
			int i = 0;
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				if(boardRectangle.row <= curBR.row && boardRectangle.row+occupiedRows > curBR.row 
						&& boardRectangle.column <= curBR.column && boardRectangle.column+occupiedColumns > curBR.column) {
					occupiedBRs[i] = curBR;
					i++;
				}
			}
		}else {
			occupiedBRs[0] = boardRectangle;
		}
	}
	
	public Rectangle getRectHitbox() {
		return rectHitbox;
	}
	public Point getPos() {
		return new Point((int)rectHitbox.getCenterX(),(int)rectHitbox.getCenterY());
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}
	
	public boolean containsBR(BoardRectangle targetBR) {
		for(BoardRectangle curBR : occupiedBRs) {
			if(curBR == targetBR) {
				return true;
			}
		}
		return false;
	}
	
	public void drawDestructibleObject(Graphics2D g2d) {
		sprite.drawSprite(g2d, (int)rectHitbox.getCenterX(), (int)rectHitbox.getCenterY(), rotation, 1);
		if(impactFlashCounter > -100) { 
			impactFlashCounter--;
		}
		if(impactFlashCounter > 0) {
			g2d.setColor(new Color(255,255,255,200));
			g2d.translate(rectHitbox.getCenterX(), rectHitbox.getCenterY());
			g2d.rotate(Math.toRadians(rotation));
			int w = (int) (rectHitbox.getWidth()*0.9); 
			int h = (int) (rectHitbox.getHeight()*0.9);
			g2d.fill(new Rectangle(-w/2,-h/2,w,h)); 
			g2d.rotate(Math.toRadians(-rotation));
			g2d.translate(-rectHitbox.getCenterX(), -rectHitbox.getCenterY());
		}
		drawHealthValues(g2d, (int)rectHitbox.getCenterX(), (int)rectHitbox.getCenterY(), 25);
	}
	
	protected void drawHealthValues(Graphics2D g2d,int x, int y, int fontSize) {
		g2d.setFont(new Font("Arial",Font.BOLD,fontSize));
		FontMetrics metrics = g2d.getFontMetrics();
		int textHeight = metrics.getHeight();
		int textWidth = 0;
		
		String str = Math.round(health)+"";
		textWidth = metrics.stringWidth(str);
		int size0 = (int) (textWidth+textHeight/2);
		
		Rectangle r = new Rectangle((int)x-size0/2, (int)y-textHeight*2, size0, textHeight);
		g2d.setColor(new Color(20,20,20));
		g2d.fill(r);	
		g2d.setColor(new Color(5,5,5));
		g2d.setStroke(new BasicStroke(5));
		g2d.draw(r);
		g2d.setColor(Commons.cHealth);
		g2d.drawString(str, (int)r.getCenterX()-textWidth/2, (int)r.getCenterY()+textHeight/3);
	}
	
	public boolean checkIntersects(Rectangle rect) {
		if(rectHitbox.intersects(rect)) {
			if(impactFlashCounter <-3) {
				impactFlashCounter=5;
				float x = (float)(rectHitbox.getCenterX()+(Math.random()-0.5)*rectHitbox.width);
				float y = (float)(rectHitbox.getCenterY()+(Math.random()-0.5)*rectHitbox.height);
				float angle = (float)(Math.random()*360);
				StagePanel.particles.add(new DestructionParticle(x,y,
						(int)(Math.random()*Commons.boardRectSize/3)+Commons.boardRectSize/6,
						(int)(Math.random()*Commons.boardRectSize/10)+Commons.boardRectSize/8, 
						sprite.getRandomPixelColor(),angle, 3));
			}
			return true;
		}
		return false;
	}
	
	public void getDamaged(float dmg, float attackAngle) {
		health-=dmg;
		if(health<=0) {
			isDestroyed = true;
			for(int i = 0;i<8*occupiedBRs.length;i++) {
				float x = (float)(rectHitbox.getCenterX()+(Math.random()-0.5)*rectHitbox.width);
				float y = (float)(rectHitbox.getCenterY()+(Math.random()-0.5)*rectHitbox.height);
				float angle = attackAngle + (float)((Math.random()-0.5)*50);
				StagePanel.particles.add(new DestructionParticle(x,y,
					(int)(Math.random()*Commons.boardRectSize/2)+Commons.boardRectSize/3,
					(int)(Math.random()*Commons.boardRectSize/8)+Commons.boardRectSize/8, 
					sprite.getRandomPixelColor(),angle, (float)(Math.random()*4+dmg)));
			}
		}
		StagePanel.addDmgLabel((int)(rectHitbox.getCenterX()+(Math.random()-0.5)*rectHitbox.getWidth()),
		(int)(rectHitbox.getCenterY()+(Math.random()-0.5)*rectHitbox.getWidth()), dmg);
	}
	
	
}