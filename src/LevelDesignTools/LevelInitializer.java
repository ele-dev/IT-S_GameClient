package LevelDesignTools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import Environment.DestructibleObject;
import PlayerStructures.GoldMine;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class LevelInitializer {
	private int mapRows = 0;
	private int mapColumns = 0;
	
	private int enemyFortressIndex = -1,notEnemyFortressIndex = -1;
	
	public int getMapRows() {
		return mapRows;
	}
	public int getMapColumns() {
		return mapColumns;
	}
	public int getEnemyFortressIndex() {
		return enemyFortressIndex;
	}
	public int getNotEnemyFortressIndex() {
		return notEnemyFortressIndex;
	}
	
	public void printLevelLAyout() {
		System.out.println("###########################################################################");
		System.out.println("Level-Wall-Layout");
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("[] = Wall                 |  " + "BoardRectAmount = " + StagePanel.boardRectangles.size());
		System.out.println("<> = Destructible Object  |  " + "Rows = " + getMapRows());
		System.out.println(":: = Gap                  |  " + "Columns = " + getMapColumns());
		System.out.println("~~ = Fortress");
		System.out.println("---------------------------------------------------------------------------");
		int row = 0;
		for(BoardRectangle curBR : StagePanel.boardRectangles) {
			if(curBR.row > row) {
				row++;
				System.out.println();
			}
			if(curBR.isWall) {
				System.out.print("[]");
			}else if(StagePanel.enemyFortress.containsBR(curBR) || StagePanel.notEnemyFortress.containsBR(curBR)){
				System.out.print("~~");
			}else if(curBR.isDestructibleObject()){
				System.out.print("<>");
			}else if(curBR.isGap){
				System.out.print("::");
			}else {
				System.out.print("  ");
			}
		}
		System.out.println();
		System.out.println("###########################################################################");
	}
	
	public void readMapFromImage(String mapName) {
		File inputFile = new File("src/LevelDesignTools/"+mapName+".png");
		BufferedImage mapImage; 
		try {
			mapImage = ImageIO.read(inputFile);
			mapRows = mapImage.getHeight();
			mapColumns = mapImage.getWidth();
			
			int index = 0;
			for(int i = 0;i<mapRows;i++) {
				for(int j = 0;j<mapColumns;j++) {
					if(i%2==0) {
						StagePanel.boardRectangles.add(new BoardRectangle(i, j, j%2==0, index,false));
					}else{
						StagePanel.boardRectangles.add(new BoardRectangle(i, j, j%2==1, index,false));
					}
					index++;
				}
			}
			index = 0;
			for(int i = 0;i<mapRows;i++) {
				for(int j = 0;j<mapColumns;j++) {
					if(enemyFortressIndex<0 && mapImage.getRGB(j, i) == Commons.enemyColor.getRGB()) {
						enemyFortressIndex = index;
					}else if(notEnemyFortressIndex<0 && mapImage.getRGB(j, i) == Commons.notEnemyColor.getRGB()) {
						notEnemyFortressIndex = index;
					}if(mapImage.getRGB(j, i) == Color.BLACK.getRGB()) {
						StagePanel.boardRectangles.get(index).isWall = true;
					}else if(mapImage.getRGB(j, i) == Color.BLUE.getRGB()) {
						StagePanel.boardRectangles.get(index).isGap = true;
					}else if(mapImage.getRGB(j, i) == Color.MAGENTA.getRGB()) {
						StagePanel.boardRectangles.get(index).isHinderingTerrain = true;
					}else if(mapImage.getRGB(j, i) == Color.ORANGE.getRGB()) {
						StagePanel.destructibleObjects.add(new DestructibleObject(StagePanel.boardRectangles.get(index),1,1, 1,0));
					}else if(mapImage.getRGB(j, i) == Color.YELLOW.getRGB()) {
						StagePanel.goldMines.add(new GoldMine(StagePanel.boardRectangles.get(index)));
					}
					index++;
				}
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Given map does not exist!");
			e1.printStackTrace();
		}
	}
	
	public void saveMapAsImage(String mapName,ArrayList<BoardRectangle> boardRectangles) {
		try {
			File outputFile = new File("src/LevelDesignTools/"+mapName+".png");
			if(mapColumns == 0 || mapRows == 0) {
				mapColumns = StagePanel.gameMap.getColumns();
				mapRows = StagePanel.gameMap.getRows();
			}
			BufferedImage mapImage = new BufferedImage(mapColumns, mapRows, BufferedImage.TYPE_INT_ARGB); 
			int index = 0;
			for(int i = 0;i<mapRows;i++) {
				for(int j = 0;j<mapColumns;j++) {
					if(StagePanel.enemyFortress.containsBR(boardRectangles.get(index))) {
						mapImage.setRGB(j, i, Commons.notEnemyColor.getRGB());
					}else if(StagePanel.notEnemyFortress.containsBR(boardRectangles.get(index))) {
						mapImage.setRGB(j, i, Commons.enemyColor.getRGB());
					}else if(boardRectangles.get(index).isWall) {
						mapImage.setRGB(j, i, Color.BLACK.getRGB());
					}else if(boardRectangles.get(index).isGap) {
						mapImage.setRGB(j, i, Color.BLUE.getRGB());
					}else if(boardRectangles.get(index).isHinderingTerrain) {
						mapImage.setRGB(j, i, Color.MAGENTA.getRGB());
					}else if(boardRectangles.get(index).isDestructibleObject()) {
						mapImage.setRGB(j, i, Color.ORANGE.getRGB());
					}else if(boardRectangles.get(index).isGoldMine()) {
						mapImage.setRGB(j, i, Color.YELLOW.getRGB());
					}else {
						mapImage.setRGB(j, i, Color.WHITE.getRGB());
					}
					index++;
				}
			}
			
			ImageIO.write(mapImage, "png", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
