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
	private Rectangle rect;
	private int turnCounter = 0;
	
	ArrayList<TrailParticle> trailParticles = new ArrayList<TrailParticle>();
	float length;
	float[] counters = new float[6];
	
	// is drawn after translation of graphics so it does not need to be moved with the camera
	public TurnInfo() {
		int border = StagePanel.w/100;
		rect = new Rectangle(border, border, 10, StagePanel.w/10);
		initCounters();
	}
	
	private void initCounters() {
		length = (rect.width+rect.height) *2;
		for(int i = 0;i<counters.length;i++) {
			counters[i] = length* i/counters.length;
		}
	}
	
	// counts how often it was toggled
	public void toggleTurn() {
		turnCounter++;
	}
	
	public void update() {
		addParticle();
		
		Color c = GameState.myTurn?GameState.myTeamColor:new Color(10,10,10);
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			curTP.update();
			curTP.setCNoAlpha(c);
			if(curTP.isDestroyed()) {
				trailParticles.remove(i);
			}
		}
	}
	
	// draws the TurnInfoPanel relative to who has Turn
	public void drawTurnInfo(Graphics2D g2d) {
				
		g2d.setColor(new Color(20,20,20,240)); 
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(6));
		g2d.setColor(new Color(10,10,10));
		g2d.draw(rect);
		
		
		g2d.setFont(new Font("Arial",Font.BOLD,rect.height/6));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		g2d.setColor(GameState.enemyTeamColor);
		g2d.drawString(GameState.enemyName, rect.x+60, (int)(rect.y+textHeight*1.5));
		g2d.setColor(GameState.myTeamColor);
		g2d.drawString(ProjectFrame.conn.getUsername()+" (You)", rect.x+60, (int)(rect.y+textHeight*3));
		
		g2d.setColor(Commons.cCurrency);
//		int enemyCoinAmount = GameState.myTeamIsRed?StagePanel.blueBase.getCoinAmount():StagePanel.redBase.getCoinAmount();
		int myCoinAmount =  GameState.myTeamIsRed?StagePanel.redBase.getGoldAmount():StagePanel.blueBase.getGoldAmount();
//		g2d.drawString(enemyCoinAmount +"g", rect.x+rect.width-fontMetrics.stringWidth(enemyCoinAmount +"g")-rect.width/20, (int)(rect.y+textHeight*1.5));
		g2d.drawString(myCoinAmount +"g", rect.x+rect.width-fontMetrics.stringWidth(myCoinAmount +"g")-rect.width/20, (int)(rect.y+textHeight*3));
		
		int largestCoinAmount = (StagePanel.blueBase.getGoldAmount()>StagePanel.redBase.getGoldAmount()?StagePanel.blueBase.getGoldAmount():StagePanel.redBase.getGoldAmount());
		int largestCoinAmountStringWidth = fontMetrics.stringWidth(largestCoinAmount+"g");
		
		int newWidth = fontMetrics.stringWidth(ProjectFrame.conn.getUsername()+" (You)")+largestCoinAmountStringWidth+120;
		if(newWidth > rect.width) {
			rect.setBounds(rect.x,rect.y,newWidth,rect.height);
			initCounters();
		}
		newWidth = fontMetrics.stringWidth(GameState.enemyName)+largestCoinAmountStringWidth+120;
		if(newWidth > rect.width) {
			rect.setBounds(rect.x,rect.y,newWidth,rect.height);
			initCounters();
		}
		
		if(GameState.myTurn) {
			g2d.translate(rect.x+20, rect.y+textHeight*3-fontMetrics.getHeight()/3);
		}else {
			g2d.translate(rect.x+20, rect.y+textHeight*1.5-fontMetrics.getHeight()/3);
		}
		g2d.setColor(GameState.myTurn?GameState.myTeamColor:GameState.enemyTeamColor);
		
		g2d.drawLine(0, 0, 20, 0);
		g2d.drawLine(20, 0, 10, 5);
		g2d.drawLine(20, 0, 10, -5);
		if(GameState.myTurn) {
			g2d.translate(-(rect.x+20), -(rect.y+textHeight*3-fontMetrics.getHeight()/3));
		}else {
			g2d.translate(-(rect.x+20), -(rect.y+textHeight*1.5-fontMetrics.getHeight()/3));
		}
		
		g2d.setColor(Color.WHITE);
		g2d.drawString("Total-Turns = "+turnCounter, rect.x+60, (int)(rect.y+textHeight*4.5));
		
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			curTP.drawParticle(g2d);
		}
	}
	
	private void addParticle() {
		for(int i = 0;i<counters.length;i++) {
			
			int x = rect.x;
			int y = rect.y;
			if(counters[i]<length) {
				counters[i]+= 1f;
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
			if(counter%6 == 0) {
				int size = (int)(Math.random()*StagePanel.w/300+StagePanel.w/300);
				size = size > 0?size:1;
				trailParticles.add(new TrailParticle((int)(x+(Math.random()-0.5)*4), (int)(y+(Math.random()-0.5)*4),size, (float)Math.random()*360, c, 0, 2, 0));
			}		
		}
	}
}
