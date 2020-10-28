package Abilities;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;

import Environment.DestructibleObject;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class RadialShield extends DestructibleObject{
	public boolean isEnemy;
	private Ellipse2D shieldCircle;
	private float radius;
	private Float[] pulseRadius = new Float[3];
	private Color c;
	
	public RadialShield(BoardRectangle originBR, boolean isEnemy) {
		super(originBR, 3, 3, 6, 0);
		this.isEnemy = isEnemy;
		radius = Commons.boardRectSize*2-Commons.boardRectSize/4;
		shieldCircle = new Ellipse2D.Float(occupiedBRs[4].getCenterX()-radius,occupiedBRs[4].getCenterY()-radius,radius*2,radius*2);
		c = isEnemy?Commons.enemyColor:Commons.notEnemyColor;
		c = new Color(c.getRed(),c.getGreen(),c.getBlue(),60);
		int j = 0;
		for(int i = 0;i<radius;i+=radius/pulseRadius.length) {
			if(j <pulseRadius.length) {
				pulseRadius[j] = (float) i;
			}
			j++;
		}
	}
	
	public Point getPos() {
		return occupiedBRs[4].getPos();
	}
	
	public Ellipse2D getShieldCircle() {
		return shieldCircle;
	}
	
	@Override
	public void drawDestructibleObject(Graphics2D g2d) {
		for(int i = 0;i<occupiedBRs.length;i++) {
			g2d.setColor(Commons.cAbility);
			g2d.draw(occupiedBRs[i].rect);
		}
		Color[] color = {new Color(c.getRed(),c.getGreen(),c.getBlue(),0),new Color(c.getRed(),c.getGreen(),c.getBlue(),100)};
		float[] dist = {0.1f,0.9f};
		RadialGradientPaint radialGradientPaint1 = new RadialGradientPaint(occupiedBRs[4].getPos(), radius, dist, color);
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
			Ellipse2D pulseCircle = new Ellipse2D.Float(occupiedBRs[4].getCenterX()-pulseRadius[j],occupiedBRs[4].getCenterY()-pulseRadius[j],pulseRadius[j]*2,pulseRadius[j]*2);
			g2d.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),30));
			g2d.draw(pulseCircle);
		}
		drawHealthValues(g2d, occupiedBRs[4].getCenterX(), occupiedBRs[4].getCenterY(), 30);
	}
	
	public boolean contains(BoardRectangle curBR) {
		for(int i = 0;i<occupiedBRs.length;i++) {
			if(occupiedBRs[i] == curBR) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void getDamaged(float dmg,float attackAngle, boolean isEnemyAttack) {
		StagePanel.addValueLabel(occupiedBRs[4].getCenterX(),occupiedBRs[4].getCenterY(),dmg,Commons.cAttack);
		if(health-dmg > 0) {
			health-=dmg;
		}else {
			health = 0;
			isDestroyed = true;
		}
		
	}
	
	
}
