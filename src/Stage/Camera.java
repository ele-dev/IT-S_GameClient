package Stage;

import java.awt.Point;
import java.awt.event.KeyEvent;

public class Camera {
	float x,y;
	private float vx,vy;
	float v = 6;
	
	public Camera() {
		this.x = 0;
		this.y = 0;
	}
	
	public void updateCamera(float x,float y) {
		this.x = x;
		this.y = y;
	}
	
	public void move() {
		x += vx;
		y += vy;
	}
	
	public Point getPos() {
		return new Point((int)x,(int)y);
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