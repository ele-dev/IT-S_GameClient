package LoginScreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class LoginButton {
	private Rectangle rect;
	private boolean isHover;
	
	public LoginButton(int x, int y, int w, int h) {
		rect = new Rectangle(x,y,w,h);
	}
	
	public boolean isHover() {
		return isHover;
	}
	public void drawButton(Graphics2D g2d) {
		
		g2d.setColor(isHover? new Color(255,0,50) : new Color(20,20,20));
		g2d.fill(rect);
		g2d.setColor(!isHover? new Color(255,0,50) : new Color(20,20,20));
		g2d.setFont(new Font("Arial", Font.PLAIN, 20));
		FontMetrics m = g2d.getFontMetrics();
		g2d.drawString("Login", rect.x+rect.width/2-m.stringWidth("Login")/2, rect.y+rect.height/2+m.getHeight()/3);
		
	}
	public void updateHover(MouseEvent e) {
		
		isHover = rect.contains(e.getPoint());
		
	}
	
}
