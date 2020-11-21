package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import Particles.TrailParticle;
import Stage.ProjectFrame;
import Stage.StagePanel;
import menueGui.GameState;
import networking.GenericMessage;
import networking.SignalMessage;

public class ButtonEndTurn extends GenericButton{

	ArrayList<TrailParticle> trailParticles = new ArrayList<TrailParticle>();
	float length = 0;
	float[] counters = new float[6];
	
	private byte maxCountDownInSeconds = 60;
	private int autoEndTurnCountdown = maxCountDownInSeconds*10;
	
	public Timer tAutoEndTurn;
	
	public ButtonEndTurn() {
		super(0,0, 250, 100, "End Turn", new Color(20,20,20), new Color(50,255,0), StagePanel.w/16/3);
		int border = StagePanel.w/100;
		
		rect.setBounds(StagePanel.w-(border+StagePanel.w/6),StagePanel.h-(border*2+StagePanel.w/16),StagePanel.w/6,StagePanel.w/16);
		startx = rect.x;
		starty = rect.y;
		
		length = (rect.width+rect.height) *2;
		for(int i = 0;i<counters.length;i++) {
			counters[i] = length* i/counters.length;
		}
		
		tAutoEndTurn = new Timer(100, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(autoEndTurnCountdown > 0) {
					autoEndTurnCountdown--;
				}else {
					// Update the global state variable
					GameState.myTurn = false;
					
					// Send the signal message to the server
					SignalMessage endTurn = new SignalMessage(GenericMessage.MSG_END_TURN);
					ProjectFrame.conn.sendMessageToServer(endTurn);
					autoEndTurnCountdown = (maxCountDownInSeconds*10);
					StagePanel.updateTurn();
				}
			}
		});
		
	}
	
	public void restartAutoEndTurnCountDown() {
		tAutoEndTurn.restart();
		autoEndTurnCountdown = (maxCountDownInSeconds*10);
	}
	
	@Override
	public void drawButton(Graphics2D g2d) {
		if(isActive) {
			g2d.setColor(isHover?cHover:c);
		}else {
			g2d.setColor(c);
		}
		g2d.fill(rect);
		
		g2d.setColor(new Color(10,10,10));
		
		
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		g2d.draw(rect);
		
		if(isActive) {
			g2d.setColor(isHover?c:cHover);
		}else {
			g2d.setColor(new Color(10,10,10));
		}
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics();
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	public void drawAmountOfAttacksLeft(Graphics2D g2d) {
		Rectangle rectangle = new Rectangle(rect.x, rect.y-rect.height/2, rect.width, rect.height/2);
		g2d.setColor(new Color(20,20,20,240)); 
		g2d.fill(rectangle);
		g2d.setColor(new Color(10,10,10));
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		g2d.draw(rectangle);
		
		
		
		if(!GameState.myTurn) {
			return;
		}
		Rectangle autoEndTurnMax = new Rectangle(rectangle.x,rectangle.y-rectangle.height/2,rectangle.width,rectangle.height/2);
		g2d.setColor(new Color(20,20,20,240)); 
		g2d.fill(autoEndTurnMax);
		
		
		Rectangle autoEndTurnCounterBar = new Rectangle(rectangle.x,rectangle.y-rectangle.height/2,(int)(rectangle.width*autoEndTurnCountdown/(maxCountDownInSeconds*10.0)),rectangle.height/2);
		cHover = new Color((int)(255-255*autoEndTurnCountdown/(maxCountDownInSeconds*10.0)),(int)(255*autoEndTurnCountdown/(maxCountDownInSeconds*10.0)),0);
		g2d.setColor(cHover);
		g2d.fill(autoEndTurnCounterBar);
		
		
		
		
		g2d.setColor(new Color(10,10,10));
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		g2d.draw(autoEndTurnMax);
		g2d.setFont(new Font("Arial",Font.PLAIN,StagePanel.w/16/5));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		String str = StagePanel.amountOfActionsLeft > 0?"remaining attacks: " + StagePanel.amountOfActionsLeft:"No remaining attacks";
		
		int textWidth = fontMetrics.stringWidth(str);
		g2d.setColor(StagePanel.amountOfActionsLeft > 0?Color.WHITE:new Color(255,0,50));
		g2d.drawString(str, (int)rectangle.getCenterX()-textWidth/2, (int)rectangle.getCenterY()+textHeight/3);
	}
	
	public void drawParticles(Graphics2D g2d) {
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			curTP.drawParticle(g2d);
		}
	}
	
	public void updateParticles() {
		tryAddParticle();
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			Color c = new Color(10,10,10);
			if(isActive) {
				c = cHover;
			}
			curTP.update();
			curTP.setCNoAlpha(c);
			if(curTP.isDestroyed()) {
				trailParticles.remove(i);
			}
		}
	}
	
	private void tryAddParticle() {
		for(int i = 0;i<counters.length;i++) {
			
			int x = startx;
			int y = starty;
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
			if(counter%6 == 0) {
				int size = (int)(Math.random()*StagePanel.w/300+StagePanel.w/300);
				size = size > 0?size:1;
				trailParticles.add(new TrailParticle((int)(x+(Math.random()-0.5)*4), (int)(y+(Math.random()-0.5)*4),size, (float)Math.random()*360,
						new Color((int)(255-255*autoEndTurnCountdown/(maxCountDownInSeconds*10.0)),(int)(255*autoEndTurnCountdown/(maxCountDownInSeconds*10.0)),0), 0, 3, 0));
			}
		}
	}
	
	
	
}
