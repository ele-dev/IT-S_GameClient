package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.sun.javafx.font.Metrics;

public class ValueLabel {
	private float x,y;
	private String str;
	private float fadeSpeed,riseSpeed;
	private Color c;
	private float alpha;
	
	private Font fontDmgLabel;
	
	public ValueLabel(float x, float y, String str,float fadeSpeed, float riseSpeed,Color c) {
		this.x = x;
		this.y = y;
		this.str = str;
		
		this.fadeSpeed = fadeSpeed; 
		this.riseSpeed = riseSpeed;
		this.c = c;
		fontDmgLabel = new Font("Arial",Font.BOLD,25);
		alpha = 255;
	}
	
	public Color getColor() {
		return c;
	}
	// draws the dmgLabel to the screen
	public void drawValueLabel(Graphics2D g2d) {
		g2d.setFont(fontDmgLabel);
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
	
	// fades the color of the dmgLabel out and moves it up 
	public void updateFade() {
		alpha-= fadeSpeed;
		this.c = new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha);
		this.y -= riseSpeed;
	}
}
