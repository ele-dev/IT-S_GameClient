package Stage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class DmgLabel {
	double x,y;
	String dmgAmount;
	int fadeSpeed;
	public Color c;
	Font fontDmgLabel;
	
	public DmgLabel(double x, double y, double dmgAmount,int fadeSpeed,Color c) {
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

	public void drawDmgLabel(Graphics2D g2d) {
		
		g2d.setColor(c);
		g2d.setFont(fontDmgLabel);
		g2d.drawString(dmgAmount,(int)this.x,(int)this.y);
		
	}
	
	public void updateFade() {
		this.c = new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha() -fadeSpeed);
		this.y -= 0.2;
	}
}
