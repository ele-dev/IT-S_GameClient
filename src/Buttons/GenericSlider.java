package Buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Stage.StagePanel;

public class GenericSlider {
	private float value = 1;
	private Rectangle rectValue,rectMaxValue;
	private Rectangle sliderAnchorRectangle;
	private String name;
	
	private boolean isGrabbed = false;
	
	public GenericSlider(int x, int y, int w ,int h, String name) {
		this.value = 1;
		rectMaxValue = new Rectangle(x,y,w,h);
		rectValue = new Rectangle(x,y,w,h);
		sliderAnchorRectangle = new Rectangle(x+w-w/20,y+h/2-(int)(h*1.2)/2,w/10,(int)(h*1.2));
		this.name = name;
	}
	
	public float getValue() {
		return value;
	}
	
	public boolean isGrabbed() {
		return isGrabbed;
	}
	
	public void setGrabbed(boolean isGrabbed) {
		this.isGrabbed = isGrabbed;
	}
	
	public void tryGrab() {
		isGrabbed =  sliderAnchorRectangle.contains(StagePanel.mousePosUntranslated);
	}
	
	public void drawGenericSlider(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10));
		g2d.fill(rectMaxValue);
		
		int x = rectMaxValue.x; 
		int y = rectMaxValue.y;
		int w = rectMaxValue.width;
		int h = rectMaxValue.height;
		rectValue.setBounds(x,y,(int)(w*value),h);
		sliderAnchorRectangle.setBounds(x+(int)(w*value)-w/20,y+h/2-sliderAnchorRectangle.height/2,sliderAnchorRectangle.width,sliderAnchorRectangle.height);
		
		g2d.setColor(new Color(255,0,50));
		g2d.fill(rectValue);
		g2d.setColor(isGrabbed?new Color(200,200,200):Color.WHITE);
		g2d.fill(sliderAnchorRectangle);
		if(isGrabbed) {
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.BLACK);
			g2d.draw(sliderAnchorRectangle);
		}
		
		g2d.setFont(new Font("Arial",Font.PLAIN,StagePanel.w/40));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = fontMetrics.stringWidth(name);
		g2d.drawString(name, (int)rectMaxValue.getCenterX()-textWidth/2, (int)(rectMaxValue.getCenterY() + rectMaxValue.height+textHeight/3));
	}
	
	public void moveSlieder(int x) {
		int sliderX = x-rectMaxValue.x;
		if(sliderX < rectMaxValue.width && sliderX > 0) {
			value = sliderX / (rectMaxValue.width*1.0f);
		}
		if(sliderX > rectMaxValue.width) {
			value = 1;
		}else if(sliderX < 0) {
			value = 0;
		}
	}
	
	public void update() {
		if(isGrabbed) {
			moveSlieder(StagePanel.mousePosUntranslated.x);
		}
	}
	
	
}
