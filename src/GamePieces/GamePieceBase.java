package GamePieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import Stage.BoardRectangle;
import Stage.Commons;

// Lower half of the GamePiece (The half that does all the movement animating)
public class GamePieceBase {
	private float x,y;
	private Rectangle rectShow,rectHitbox;
	private Color c;
	private float angle,angleDesired,v = 1.5f;
	private float rotationDelay = 2f;
	
	ArrayList<BoardRectangle> pathBoardRectangles = new ArrayList<BoardRectangle>();
	int curTargetPathCellIndex = 0;
	
	public GamePieceBase(float x, float y, int w, int h, Color c) {
		this.x = x;
		this.y = y;
		this.rectShow = new Rectangle(-w/2,-h/2,w,h);
		this.rectHitbox = new Rectangle((int)x-w/2,(int)y-h/2,w,h);
		this.c = c;
	}
	
	// getters
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public Rectangle getRectHitbox() {
		return rectHitbox;
	}
	
	// rendering method
	public void drawGamePieceBase(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(angle));  
		g2d.setStroke(new BasicStroke(2));
		g2d.fill(rectShow);
		g2d.setColor(Color.BLACK);
		g2d.draw(rectShow);
		g2d.rotate(Math.toRadians(-angle));
		g2d.translate(-x, -y);
	}
	
	// moves the GamePieceBase only if the angle is somewhat in the direction of the desiredangle, so it only moves if it points in the right direction
	public void move(GamePiece parentGP) {
		// tolerance of angle at which the Base starts moving
		int tolerance = 10;
		if(Math.abs(angle) < Math.abs(angleDesired)+tolerance && Math.abs(angleDesired)-tolerance < Math.abs(angle)) {
			double vX = Math.cos(Math.toRadians(this.angle + 90)) * v;
			double vY = Math.sin(Math.toRadians(this.angle + 90)) * v;
			
			this.x += vX;
			this.y += vY;	
			this.rectHitbox = new Rectangle((int)(x-rectHitbox.width/2),(int)(y-rectHitbox.height/2),rectHitbox.width,rectHitbox.height);
		}
		// curTargetPathBoardRectangle
		Rectangle curTPBR = pathBoardRectangles.get(curTargetPathCellIndex).rect;
		Rectangle rectSmallerBR = new Rectangle((int)curTPBR.getCenterX()-5,(int)curTPBR.getCenterY()-5,10,10);
		// updates the index when it crosses a pathCell so it counts down from pathCell to pathCell,
		// always having the next pathCell in the array as the target until the end is reached then it stops
		if(rectSmallerBR.contains(new Point((int)x,(int)y))) {
			if(curTargetPathCellIndex < pathBoardRectangles.size()-1) {
				curTargetPathCellIndex++;
			}else {
				parentGP.isMoving = false;
				parentGP.boardRect = pathBoardRectangles.get(curTargetPathCellIndex);
			}
		}
	}
	
	// slowly moves the angle towards the desired angle (rotationDelay controls how fast this happens)
	public void updateAngle() {
		float ak = 0;
		float gk = 0;
		
		ak = (float) (pathBoardRectangles.get(curTargetPathCellIndex).getCenterX() - x);
		gk = (float) (pathBoardRectangles.get(curTargetPathCellIndex).getCenterY() - y);
		
		angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		
		// if the angle and the angleDesired are opposites the Vector point into the opposite direction
		// this means the angle will be the angle of the longer vector which is always angle
		// so if that happens the angleDesired is offset so this won't happen
		if((angleDesired +178< angle && angleDesired +182< angle) || (angleDesired -178< angle && angleDesired -182< angle)) {
			angleDesired+= 3;
		}
		
		angle = Commons.calculateAngleAfterRotation(angle, angleDesired, rotationDelay);
	}
	
	
}
