package Stage;

import java.awt.Rectangle;
import java.util.ArrayList;

import LevelDesignTools.Levelinitializer;

public class GameMap {

	private int rows,columns;
	private ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	public static Rectangle mapRectangle;
	
	public GameMap(Levelinitializer levelinitializer) {
		this.rows = levelinitializer.getMapRows();
		this.columns = levelinitializer.getMapColumns();
		this.boardRectangles = levelinitializer.getBoardRectangles();
		mapRectangle = new Rectangle(columns*Commons.boardRectSize,rows*Commons.boardRectSize);
		
		for(BoardRectangle curBR : boardRectangles) {
			curBR.initAdjecantBRs(boardRectangles);
			if(curBR.isWall) {
				curBR.initWallSprites();
			}
		}
	}
	
	public GameMap(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		mapRectangle = new Rectangle(columns*Commons.boardRectSize,rows*Commons.boardRectSize);
		int index = 0;
		for(int i = 0;i<rows;i++) {
			for(int j = 0;j<columns;j++) {
				if(i%2==0) {
					boardRectangles.add(new BoardRectangle(i, j, j%2==0, index));
				}else{
					boardRectangles.add(new BoardRectangle(i, j, j%2==1, index));
				}
				index++;
			}
		}
		generateOuterWall();
		for(BoardRectangle curBR : boardRectangles) {
			curBR.initAdjecantBRs(boardRectangles);
			if(curBR.isWall) {
				curBR.initWallSprites();
			}
		}
	}
	
	public void generateOuterWall() {
		for(BoardRectangle curBR : boardRectangles) {
			curBR.isWall = curBR.row == 0 || curBR.row == getRows()-1 || curBR.column == 0 || curBR.column == getColumns()-1;
		}
	}
	
	public int getRows() {
		return rows;
	}
	public int getColumns() {
		return columns;
	}
	public ArrayList<BoardRectangle> getBoardRectangles() {
		return boardRectangles;
	}
	
	
	
}
