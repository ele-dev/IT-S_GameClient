package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import Sound.SoundEffect;
import Stage.Commons;
import Stage.StagePanel;

public class GenericButton {
	protected String name;
	protected int startx,starty;
	protected Rectangle rect;
	protected boolean isHover = false;
	protected boolean isActive = true;
	
	protected Color c;
	protected Color cHover;
	
	protected Font f;
	
	public GenericButton(int startx, int starty, int w, int h, String name, Color c, Color cHover, int fontSize) {
		this.startx = startx;
		this.starty = starty;
		rect = new Rectangle(startx,starty,w,h);
		this.name = name;
		this.c = c;
		this.cHover = cHover;
		this.f = new Font("Arial",Font.BOLD,fontSize);
	}
	
	public boolean isHover() {
		return isHover;
	}


	public boolean tryPress() {
		if(isHover && isActive) {
			playSelectSound();
		}
		return isHover;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void drawButton(Graphics2D g2d) {
		if(isActive) {
			g2d.setColor(isHover?cHover:c);
		}else {
			g2d.setColor(c);
		}
		g2d.fill(rect);
		
		if(isActive) {
			g2d.setColor(isHover?c:cHover);
		}else {
			g2d.setColor(new Color(10,10,10));
		}
		
		g2d.setStroke(new BasicStroke(StagePanel.w/160));
		g2d.draw(rect);
		
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics();
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
	}
	
	public void updateHover(Point mousePos) {
		boolean prevHover = isHover;
		isHover = rect.contains(mousePos);
		if(prevHover == false && isHover == true) {
			playHoverSound();
		}
	}
	
	public void updatePos(Point cameraPos) {
		rect.x = startx-cameraPos.x;
		rect.y = starty-cameraPos.y;
	}
	
	static void playSelectSound() {
		SoundEffect.play(Commons.soundEffectDirectory+"Select.wav");
	}
	static void playHoverSound() {
		SoundEffect.play(Commons.soundEffectDirectory+"Hover.wav");
	}

	

	

}
