package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ValueLabel {
	private float x,y;
	private String str;
	private float fadeSpeed,riseSpeed;
	private Color c;
	private float alpha;
	
	
	public ValueLabel(float x, float y, String str, Color c) {
		this.x = x;
		this.y = y;
		this.str = str;
		
		this.fadeSpeed = 2; 
		this.riseSpeed = 1.5f;
		this.c = c;
		alpha = 255;
	}
	
	public Color getColor() {
		return c;
	}
	// draws the dmgLabel to the screen
	public void drawValueLabel(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.BOLD,StagePanel.boardRectSize/3));
		FontMetrics metrics = g2d.getFontMetrics();
		int textWidth = metrics.stringWidth(str);
		int textHeight = metrics.getHeight();
		int size = (int) (textWidth+20);
		Rectangle r = new Rectangle((int)x-size/2, (int)y-textHeight/2, size, textHeight);
		g2d.setColor(new Color(20,20,20,(int)alpha));
		g2d.fill(r);	
		g2d.setColor(new Color(5,5,5,(int)alpha));
		g2d.setStroke(new BasicStroke(5));
		g2d.draw(r);
		
		g2d.setColor(c);
		g2d.drawString(str,(int)this.x-textWidth/2,(int)this.y+textHeight/3);
	}
	
	public static void drawValueLabelAtPos(Graphics2D g2d, int x ,int y, String str, int w, Color c, int fontsize) {
		g2d.setFont(new Font("Arial",Font.BOLD,fontsize));
		FontMetrics metrics = g2d.getFontMetrics();
		int textWidth = metrics.stringWidth(str);
		int textHeight = metrics.getHeight();
		int size = (int) (textWidth+20);
		Rectangle r = new Rectangle((int)x-size/2, (int)y-textHeight/2, size, textHeight);
		g2d.setColor(new Color(20,20,20));
		g2d.fill(r);	
		g2d.setColor(new Color(5,5,5));
		g2d.setStroke(new BasicStroke(5));
		g2d.draw(r);
		
		g2d.setColor(c);
		g2d.drawString(str,(int)x-textWidth/2,(int)y+textHeight/3);
	}
	
	// fades the color of the ValueLabel out and moves it up 
	public void updateFade() {
		c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha);
		y -= riseSpeed;
		if(riseSpeed-0.03f > 0) {
			riseSpeed-=0.03f ;	
		}else {
			riseSpeed = 0;
			alpha-= fadeSpeed;
		}
		
	}
}
