package Stage;

import java.awt.Rectangle;

public class GameMap {
	
	public GameMap(int rows, int columns) {
		StagePanel.mapRows = rows;
		StagePanel.mapColumns  = columns;
		StagePanel.mapRectangle = new Rectangle(columns*StagePanel.boardRectSize,rows*StagePanel.boardRectSize);
		@SuppressWarnings("unused")
		int index = 0;
		for(int i = 0;i<rows;i++) {
			for(int j = 0;j<columns;j++) {
				StagePanel.boardRectangles.add(new BoardRectangle(i, j, false,false));
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
			curBR.isWall = curBR.row == 0 || curBR.row == StagePanel.mapRows-1 || curBR.column == 0 || curBR.column == StagePanel.mapColumns-1;
		}
	}
	
	
	
}
