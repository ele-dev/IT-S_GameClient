package Projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import Abilities.RadialShield;
import Environment.DestructibleObject;
import GamePieces.GamePiece;

public class Bullet extends Projectile{

	public Bullet(int x, int y, int w, int h, Color c,float v,float angle, Shape targetShape,DestructibleObject targetDestructibleObject) {
		super(x, y, w, h, c, angle, v, 0, targetShape, targetDestructibleObject);
		shapeShow = new Rectangle(-w/2,-h/2,w,h);
	}
	// draws Bullet rotated (pointing tilted because of the angle)
	public void drawProjectile(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));
		g2d.fill(shapeShow);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
	} 
}
