package PathFinder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

public class PathCell {
	private Rectangle rect;
	private int row, column;
	private int index;
	
	float f;
	float g;
	float h;
	ArrayList<PathCell> adjecantPathCells = new ArrayList<PathCell>();
	PathCell parentGridCell;
	boolean isWall;
	
	
	public PathCell(int x, int y, int size, int row, int column, int index) {
		this.rect = new Rectangle(x,y,size,size);
		this.row = row;
		this.column = column;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getX() {
		return (int) rect.getX();
	}
	public int getY() {
		return (int) rect.getY();
	}
	public int getSize() {
		return (int) rect.getWidth();
	}
	public Rectangle getRect() {
		return rect;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	
	public void setIsWall(boolean isWall) {
		this.isWall = isWall;
	}
	
	public void initAdjecantGridCells(ArrayList<PathCell> pathCells) {
		for(PathCell curGC : pathCells) {
			if(curGC.row == row-1 && curGC.column == column) {
				adjecantPathCells.add(curGC);
			}else
			if(curGC.row == row && curGC.column == column-1) {
				adjecantPathCells.add(curGC);
			}else
			if(curGC.row == row && curGC.column == column+1) {
				adjecantPathCells.add(curGC);	
			}else
			if(curGC.row == row+1 && curGC.column == column) {
				adjecantPathCells.add(curGC);	
			}
		}
	}

	public void drawPathCell(Graphics2D g2d){
		if(isWall) {
			g2d.setColor(new Color(0));
			g2d.fill(rect);
		}else {
			g2d.setColor(new Color(30,30,30));
			g2d.fill(rect);
		}
		g2d.setStroke(new BasicStroke(2));
		g2d.setColor(new Color(20,20,20));
		g2d.draw(rect);
		
	}
}
