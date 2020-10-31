package Stage;

import java.awt.Rectangle;
import java.util.ArrayList;

import LevelDesignTools.LevelInitializer;

public class GameMap {

	private int rows,columns;
	public static Rectangle mapRectangle;
	
	public GameMap(LevelInitializer levelInitializer) {
		this.rows = levelInitializer.getMapRows();
		this.columns = levelInitializer.getMapColumns();
		mapRectangle = new Rectangle(columns*Commons.boardRectSize,rows*Commons.boardRectSize);
		
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			curBR.initAdjecantBRs(StagePanel.boardRectangles);
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
				StagePanel.boardRectangles.add(new BoardRectangle(i, j, false, index,false));
				index++;
			}
		}
		generateOuterWall();
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			curBR.initAdjecantBRs(StagePanel.boardRectangles);
			if(curBR.isWall) {
				curBR.initWallSprites();
			}
		}
	}
	
	public void generateOuterWall() {
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			curBR.isWall = curBR.row == 0 || curBR.row == getRows()-1 || curBR.column == 0 || curBR.column == getColumns()-1;
		}
	}
	
	public int getRows() {
		return rows;
	}
	public int getColumns() {
		return columns;
	}
	
	
	
}
