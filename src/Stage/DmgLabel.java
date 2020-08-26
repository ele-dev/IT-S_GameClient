package Stage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

// shows Number of the dmg dealt on screen
public class DmgLabel {
	private float x,y;
	private String dmgAmount;
	private int fadeSpeed;
	private Color c;
	
	private Font fontDmgLabel;
	
	public DmgLabel(float x, float y, double dmgAmount,int fadeSpeed,Color c) {
		this.x = x;
		this.y = y;
		if(dmgAmount == Math.round(dmgAmount)) {
			this.dmgAmount = Math.round(dmgAmount) + "";
		}else {
			this.dmgAmount = Math.round(dmgAmount*100)/100.0 + "";
		}
		
		this.fadeSpeed = fadeSpeed; 
		this.c = c;
		fontDmgLabel = new Font("Arial",Font.BOLD,30);
	}
	
	public Color getColor() {
		return c;
	}
	// draws the dmgLabel to the screen
	public void drawDmgLabel(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.setFont(fontDmgLabel);
		g2d.drawString(dmgAmount,(int)this.x,(int)this.y);
		
	}
	
	// fades the color of the dmgLabel out and moves it up 
	public void updateFade() {
		this.c = new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha() -fadeSpeed);
		this.y -= 0.2;
	}
}
