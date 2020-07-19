package Projectiles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import GamePieces.GamePiece;
import Particles.Explosion;
import Particles.PoisonParticleCloud;


public class PoisonDart {
	double x,y;
	double xRelTarget,yRelTarget;
	int w,h;
	Color c;
	Rectangle rect;
	Rectangle rectHitbox;
	double dmg;
	double angle;
	double v;
	public boolean isStuckToTarget = false;
	public boolean isDestroyed = false;
	public GamePiece currentTarget;
	GamePiece parentGP;
	
	public int turnsTillFade = 4;
	
	public int poisonCloudSpeed = 80;
	public int poisonCloudCounter = 0;
	
	ArrayList<PoisonParticleCloud> poisonParticleCloud = new ArrayList<PoisonParticleCloud>();
	
	public PoisonDart(int x, int y, int w, int h, Color c,double dmg,double angle,GamePiece currentTarget,GamePiece parentGP) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.c = c;
		this.rect = new Rectangle(-w/2,-h,w,h);
		this.rectHitbox = new Rectangle(x-w/2,y-h,w,h);
		this.dmg = dmg;
		this.angle = angle;
		this.v = 16;
		this.currentTarget = currentTarget;
		this.parentGP = parentGP;
	}
	
	public void move() {
		double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
		double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
		
		this.x += vX;
		this.y += vY;	
		this.rectHitbox = new Rectangle((int)(x-w/2),(int)(y-2),w,h);
		
	}
	// draws the projectile
	public void drawPoisonDart(Graphics2D g2d) {
		g2d.setColor(c);
		this.rectHitbox.setBounds((int)(x-w/2),(int)(y-h),w,h);
		g2d.translate(this.x, this.y);
		g2d.rotate(Math.toRadians(this.angle));
		g2d.fill(rect);
		g2d.rotate(Math.toRadians(-this.angle));
		g2d.translate(-this.x, -this.y);
		
		if(isStuckToTarget) {
			drawAllPoisonParticleClouds(g2d);
			drawTTD(g2d);
			
		}
	}
	
	public void drawTTD(Graphics2D g2d) {
		if(turnsTillFade > 0) {
			g2d.setColor(Color.WHITE);
		}
		g2d.setFont(new Font("Arial",Font.PLAIN,25));
		g2d.drawString(turnsTillFade+"", (int)x -5, (int)y -20);
	}
	
	public void fadeAway() {
		if(!isDestroyed) {
			isDestroyed = true;
		}
	}
	
	public void addPoisonParticleCloud() {
		poisonParticleCloud.add(new PoisonParticleCloud(x, y,(int)(Math.random() *5)+10, 0.1, (int)(Math.random()*360)));
	}
	
	public void drawAllPoisonParticleClouds(Graphics2D g2d) {
		for(PoisonParticleCloud curPPC : poisonParticleCloud) {
			curPPC.drawPoisonParticleCloud(g2d);
		}
	}
	
	public void updateAllPoisonParticleClouds() {
		for(int i = 0;i<poisonParticleCloud.size();i++) {
			PoisonParticleCloud curPPC = poisonParticleCloud.get(i);
			curPPC.move();
			if(curPPC.c.getAlpha()>10) {
				curPPC.updateFade();
			}else {
				poisonParticleCloud.remove(i);
			}
			
		}
	}
	// checks if it has hit an Enemy and will set it to be Stuck (isStuckToTarget = true)
	public void checkHitEnemy() {
		if(this.rectHitbox.intersects(currentTarget.rectHitbox)) {
			isStuckToTarget = true;
			currentTarget.resetDmgFlashCountDown();
			
			xRelTarget = x - currentTarget.boardRect.centeredX;
			yRelTarget = y - currentTarget.boardRect.centeredY;
		}
	}
	
	public void stayStuck() {
		x = currentTarget.boardRect.centeredX + xRelTarget;
		y = currentTarget.boardRect.centeredY + yRelTarget;
	}
	
}
