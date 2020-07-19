package Stage;

import java.awt.Point;

public class Camera {
	int x,y;
	double vx,vy;
	double v = 6;
	
	public Camera() {
		this.x = 0;
		this.y = 0;
	}
	
	public void updateCamera(double x,double y) {
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public void move() {
		this.x += vx;
		this.y += vy;
	}
	
	public Point getPos() {
		return new Point(x,y);
	}
}