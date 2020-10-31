package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TurnCountDown {
	private int maxCounter,counter;
	private int size;
	private Color c;
	public TurnCountDown(int maxCounter,Color c) {
		this.maxCounter = maxCounter;
		this.counter = maxCounter;
		size = 20;
		this.c = c;
	}
	
	public void countDownOne() {
		counter--;
	}
	public int getCounter() {
		return counter;
	}
	
	public void drawCountDown(Graphics2D g2d, int x, int y) {
		
		g2d.setColor(new Color(10,10,10));
		g2d.setStroke(new BasicStroke(12));
		g2d.drawArc(x-size/2, y-size/2, size, size, 90, 360);
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(4));
		g2d.drawArc(x-size/2, y-size/2, size, size, 90, (int)(counter*1.0/maxCounter*360));
		
		g2d.translate(x, y);
		g2d.setStroke(new BasicStroke(4));
		g2d.setColor(c);
		float max = maxCounter;
		for(int i = 0;i<max;i++) {
			g2d.rotate(Math.toRadians(i/max*360+90));
			
			g2d.drawLine(size/2-2, 0, size/2+2, 0);
			g2d.rotate(-Math.toRadians(i/max*360+90));
		}
		g2d.translate(-x, -y);
	}
	
}
