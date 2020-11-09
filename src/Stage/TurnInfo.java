package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import Particles.TrailParticle;
import menueGui.GameState;

public class TurnInfo {
	private Rectangle rect = new Rectangle(20, 20, 400, 200);;
	private int turnCounter = 0;
	
	ArrayList<TrailParticle> trailParticles = new ArrayList<TrailParticle>();
	float length = (rect.width+rect.height) *2;
	float[] counters = new float[6];
	
	// is drawn after translation of graphics so it does not need to be moved with the camera
	public TurnInfo() {
		for(int i = 0;i<counters.length;i++) {
			counters[i] = length* i/counters.length;
		}
	}
	
	// counts how often it was toggled
	public void toggleTurn() {
		turnCounter++;
	}
	
	// draws the TurnInfoPanel relative to who has Turn
	public void drawTurnInfo(Graphics2D g2d) {
		g2d.setColor(new Color(20,20,20,240)); 
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(6));
		g2d.setColor(GameState.myTurn ? GameState.myTeamColor: new Color(10,10,10));
		g2d.draw(rect);
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		
		g2d.setColor(GameState.enemyTeamColor);
		g2d.drawString(GameState.enemyName, rect.x+60, rect.y+50);
		g2d.setColor(GameState.myTeamColor);
		g2d.drawString(ProjectFrame.conn.getUsername()+" (You)", rect.x+60, rect.y+100);
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		
		
		if(GameState.myTurn) {
			g2d.translate(rect.x+20, rect.y+100-fontMetrics.getHeight()/3);
		}else {
			g2d.translate(rect.x+20, rect.y+50-fontMetrics.getHeight()/3);
		}
		g2d.setColor(GameState.myTurn?GameState.myTeamColor:GameState.enemyTeamColor);
		
		g2d.drawLine(0, 0, 20, 0);
		g2d.drawLine(20, 0, 10, 5);
		g2d.drawLine(20, 0, 10, -5);
		if(GameState.myTurn) {
			g2d.translate(-(rect.x+20), -(rect.y+100-fontMetrics.getHeight()/3));
		}else {
			g2d.translate(-(rect.x+20), -(rect.y+50-fontMetrics.getHeight()/3));
		}
		
		
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Total-Turns = "+turnCounter, rect.x+60, rect.y+150);
		
		addParticle();
		
		Color c = GameState.myTurn?GameState.myTeamColor:new Color(10,10,10);
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			curTP.drawParticle(g2d);
			curTP.update();
			curTP.setCNoAlpha(c);
			if(curTP.isDestroyed()) {
				trailParticles.remove(i);
			}
		}
	}
	
	private void addParticle() {
		for(int i = 0;i<counters.length;i++) {
			
			int x = rect.x;
			int y = rect.y;
			if(counters[i]<length) {
				counters[i]+= 0.3f;
			}else {
				counters[i] = 0;
			}
			float counter = counters[i];
			if(counter > rect.width) {
				if(counter > rect.width+rect.height) {
					if(counter > rect.width*2+rect.height) {
						y += rect.height -(counter-rect.height-rect.width*2);
					}else {
						x += rect.width-(counter-rect.width-rect.height);
						y += rect.height;
					}
				}else {
					y += counter-rect.width;
					x += rect.width;
				}
			}else {
				x += counter;
			}
			Color c = GameState.myTurn?GameState.myTeamColor:new Color(10,10,10);
			trailParticles.add(new TrailParticle((int)(x+(Math.random()-0.5)*10), (int)(y+(Math.random()-0.5)*10),(int)(Math.random()*3+3), (float)Math.random()*360, c, 0, 1, 0));
		}
	}
}
