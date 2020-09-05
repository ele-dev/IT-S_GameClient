package Abilities;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class RadialShield {
	private BoardRectangle anchorBR;
	public boolean isEnemy;
	private Ellipse2D shieldCircle;
	private float radius;
	private Float[] pulseRadius = new Float[3];
	private Color c;
	public BoardRectangle[] affectedBoardRectangles = new BoardRectangle[9];
	float maxHealth,health;
	
	public RadialShield(BoardRectangle anchorBR, boolean isEnemy) {
		this.anchorBR = anchorBR;
		this.isEnemy = isEnemy;
		this.radius = Commons.boardRectSize*2-Commons.boardRectSize/4;
		this.shieldCircle = new Ellipse2D.Float(anchorBR.getCenterX()-radius,anchorBR.getCenterY()-radius,radius*2,radius*2);
		if(isEnemy) {
			c = Commons.enemyColor;
		}else {
			c = Commons.notEnemyColor;
		}
		c = new Color(c.getRed(),c.getGreen(),c.getBlue(),60);
		int j = 0;
		for(int i = 0;i<radius;i+=radius/pulseRadius.length) {
			if(j <pulseRadius.length) {
				pulseRadius[j] = (float) i;
			}
			j++;
		}
		
		j = 0;
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if( Math.abs(curBR.row - anchorBR.row) <= 1 
					&& Math.abs(curBR.column - anchorBR.column) <= 1) {
				if(j<affectedBoardRectangles.length) {
					affectedBoardRectangles[j] = curBR;
					j++;
				}
				
			}
		}
		
		this.maxHealth = 6;
		this.health = maxHealth;
	}
	
	public Point getPos() {
		return anchorBR.getPos();
	}
	
	public Ellipse2D getShieldCircle() {
		return shieldCircle;
	}
	
	public void drawRadialShield(Graphics2D g2d) {
		for(int i = 0;i<affectedBoardRectangles.length;i++) {
			g2d.setColor(Commons.cAbility);
			g2d.draw(affectedBoardRectangles[i].rect);
			
			
		}
		Color[] color = {new Color(c.getRed(),c.getGreen(),c.getBlue(),0),new Color(c.getRed(),c.getGreen(),c.getBlue(),100)};
		float[] dist = {0.1f,0.9f};
		RadialGradientPaint radialGradientPaint1 = new RadialGradientPaint(anchorBR.getPos(), radius, dist, color);
		g2d.setPaint(radialGradientPaint1);
		g2d.fill(shieldCircle);
		
		g2d.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),200));
		g2d.setStroke(new BasicStroke(5));
		g2d.draw(shieldCircle);
		for(int j = 0;j<pulseRadius.length;j++) {
			pulseRadius[j]-= 0.3f;
			if(pulseRadius[j] <= 0) {
				pulseRadius[j] = radius;
			}
			g2d.setStroke(new BasicStroke((radius-pulseRadius[j])/10.0f));
			Ellipse2D pulseCircle = new Ellipse2D.Float(anchorBR.getCenterX()-pulseRadius[j],anchorBR.getCenterY()-pulseRadius[j],pulseRadius[j]*2,pulseRadius[j]*2);
			g2d.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),30));
			g2d.draw(pulseCircle);
		}
		drawHealthValues(g2d, anchorBR.getCenterX(), anchorBR.getCenterY(), 30);
	}
	
	private void drawHealthValues(Graphics2D g2d,int x, int y, int fontSize) {
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
	
	public boolean contains(BoardRectangle curBR) {
		for(int i = 0;i<affectedBoardRectangles.length;i++) {
			if(affectedBoardRectangles[i] == curBR) {
				return true;
			}
		}
		return false;
	}
	
	public void getDamaged(float dmg) {
		StagePanel.addDmgLabel(anchorBR.getCenterX(),anchorBR.getCenterY(),dmg);
		if(health-dmg > 0) {
			health-=dmg;
		}else {
			health = 0;
		}
	}
	
	
}
