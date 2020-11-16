package Stage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import PlayerStructures.PlayerFortress;

public class Camera {
	private float x,y;
	// velocity x/y direction
	private float vx,vy;
	private float v = 6;
	// rectangle in that is seen and where things are rendered 
	//(things outside of view do not need to be rendered)
	private Rectangle rectOfView;
	// variables that control the screen shake
	private int screenShakeCountDown;
	private int screenShakeMagnitude;
	
	public Camera() {
		this.x = 0;
		this.y = 0;
		int w = StagePanel.w;
		int h = StagePanel.h;
		rectOfView = new Rectangle((int)-x-StagePanel.boardRectSize,(int)-y-StagePanel.boardRectSize,w+StagePanel.boardRectSize*2,h+StagePanel.boardRectSize*2);

	}
	
	public void drawRectOfView(Graphics2D g2d) {
		g2d.setColor(new Color(255,0,50,100));
		g2d.draw(rectOfView);
	}
	
	public void setCameraToBasePos(PlayerFortress playerFortress) {
		this.x = -playerFortress.getPos().x+StagePanel.w/2;
		this.y = -playerFortress.getPos().y+StagePanel.h/2;
	}
	
	public void move(Rectangle mapRectangle) {
		if(screenShakeCountDown > 0) {
			screenShakeCountDown--;
		}
		x += vx;
		if(!mapRectangle.contains(getCenterOfScreen())) x -= vx;
		y += vy;
		if(!mapRectangle.contains(getCenterOfScreen())) y -= vy;
		rectOfView = new Rectangle((int)-x-StagePanel.boardRectSize,(int)-y-StagePanel.boardRectSize,rectOfView.width,rectOfView.height);
	}
	
	public void applyScreenShake(int screenShakeAmountOfFRames,int screenShakeMagnitude) {
		this.screenShakeCountDown = screenShakeAmountOfFRames;
		this.screenShakeMagnitude = screenShakeMagnitude;
	}
	
	public boolean isInView(Rectangle rect) {
		return rectOfView.intersects(rect);
	}
	
	public boolean isInView(Point p) {
		return rectOfView.contains(p);
	}
	
	public Point getPos() {
		return screenShakeCountDown>0?new Point((int)(x+(Math.random()-0.5)*screenShakeMagnitude),(int)(y+(Math.random()-0.5)*screenShakeMagnitude))
				:new Point((int)x,(int)y);
	}
	
	public Point getCenterOfScreen() {
		return new Point((int)-x+StagePanel.w/2, (int)-y+StagePanel.h/2);
	}
	
	public void updateMovementPressedKey(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
			vy = v;
		}
		if(e.getKeyCode() == KeyEvent.VK_S) {
			vy = -v;
		}
		if(e.getKeyCode() == KeyEvent.VK_A) {
			vx = v;
		}
		if(e.getKeyCode() == KeyEvent.VK_D) {
			vx = -v;
		}
	}
	
	public void updateMovementReleasedKey(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_W) {
			vy = 0;
		}
		if(e.getKeyCode() == KeyEvent.VK_S) {
			vy = 0;
		}
		if(e.getKeyCode() == KeyEvent.VK_A) {
			vx = 0;
		}
		if(e.getKeyCode() == KeyEvent.VK_D) {
			vx = 0;
		}
	}
}