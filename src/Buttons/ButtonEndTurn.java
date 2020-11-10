package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Particles.TrailParticle;
import Stage.StagePanel;

public class ButtonEndTurn extends GenericButton{

	ArrayList<TrailParticle> trailParticles = new ArrayList<TrailParticle>();
	float length = 0;
	float[] counters = new float[6];
	
	public ButtonEndTurn() {
		super(0,0, 250, 100, "End Turn", new Color(20,20,20), new Color(50,255,0), StagePanel.w/16/3);
		int border = StagePanel.w/100;
		
		this.rect.setBounds(StagePanel.w-(border+StagePanel.w/8),StagePanel.h-(border*2+StagePanel.w/16),StagePanel.w/8,StagePanel.w/16);
		startx = rect.x;
		starty = rect.y;
		
		length = (rect.width+rect.height) *2;
		for(int i = 0;i<counters.length;i++) {
			counters[i] = length* i/counters.length;
		}
	}
	
	@Override
	public void drawButton(Graphics2D g2d) {
		if(isActive) {
			g2d.setColor(isHover?cHover:c);
		}else {
			g2d.setColor(c);
		}
		g2d.fill(rect);
		
		g2d.setColor(cInactive);
		
		
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		g2d.draw(rect);
		
		if(isActive) {
			g2d.setColor(isHover?c:cHover);
		}else {
			g2d.setColor(cInactive);
		}
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics();
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	
	public void drawParticles(Graphics2D g2d) {
		
		addParticle();
		
		Color c = new Color(10,10,10);
		if(isActive) {
			c = new Color(50,255,0);
		}
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
			Color c = new Color(10,10,10);
			if(isActive) {
				c = new Color(50,255,0);
			}
			if(counter%4 == 0) {
				int size = (int)(Math.random()*StagePanel.w/300+StagePanel.w/300);
				size = size > 0?size:1;
				trailParticles.add(new TrailParticle((int)(x+(Math.random()-0.5)*4), (int)(y+(Math.random()-0.5)*4),size, (float)Math.random()*360, c, 0, 3, 0));
			}
		}
	}
	
	
	
}
