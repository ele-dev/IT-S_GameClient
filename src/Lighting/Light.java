package Lighting;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Light {
	int x,y;
	int size;
	Color c;
	
	public Light(int x, int y, int size, Color c) {
		this.size = size;
		this.x = x;
		this.y = y;
		this.c =c;
	}
	
	public void subtractFromLightMap(Graphics2D lightmapG2d) {
        lightmapG2d.setColor(new Color(0,0,0,60));
        for(int i = size/3;i<size;i+=size/5) {
        	lightmapG2d.fillOval(x-i/2, y-i/2, i, i);
        }
	}
	
	public void drawLight(Graphics2D g2d) {
		g2d.setColor(c);
        for(int i = size/3;i<size;i+=size/5) {
        	g2d.fillOval(x-i/2, y-i/2, i, i);
        }
	}
	
	public void updatePos(Point mousePos) {
		x = mousePos.x;
		y = mousePos.y;
	}
	
	
	
}
