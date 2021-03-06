package LevelDesignTools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Environment.DestructibleObject;
import Environment.GoldMine;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class LevelInitializer {
	
	private int mapRows = 0;
	private int mapColumns = 0;
	
	private int redBaseIndex = -1,blueBaseIndex = -1;
	
	public int getMapRows() {
		return mapRows;
	}
	public int getMapColumns() {
		return mapColumns;
	}
	public int getRedBaseIndex() {
		return redBaseIndex;
	}
	public int getBlueBaseIndex() {
		return blueBaseIndex;
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
			}else if(StagePanel.redBase.containsBR(curBR) || StagePanel.blueBase.containsBR(curBR)){
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
		ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("Maps/"+mapName+".png"));
		Image image = imageIcon.getImage();
		BufferedImage mapImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		
		Graphics2D bG2d = mapImage.createGraphics();
	    bG2d.drawImage(image, 0, 0, null);
		mapRows = mapImage.getHeight();
		mapColumns = mapImage.getWidth();
		
		StagePanel.boardRectangles.clear();
			int index = 0;
			for(int i = 0; i < mapRows; i++) {
				for(int j = 0; j < mapColumns; j++) {
					if(i % 2 == 0) {
						StagePanel.boardRectangles.add(new BoardRectangle(i, j, j % 2 == 0,false));
					}else{
						StagePanel.boardRectangles.add(new BoardRectangle(i, j, j % 2 == 1,false));
					}
					index++;
				}
			}
			
			index = 0;
			
			for(int i = 0; i < mapRows; i++) {
				for(int j = 0; j < mapColumns; j++) {
					if(redBaseIndex < 0 && mapImage.getRGB(j, i) == Commons.cRed.getRGB()) {
						redBaseIndex = index;
					} else if(blueBaseIndex < 0 && mapImage.getRGB(j, i) == Commons.cBlue.getRGB()) {
						blueBaseIndex = index;
					} 
					if(mapImage.getRGB(j, i) == Color.BLACK.getRGB()) {
						StagePanel.boardRectangles.get(index).isWall = true;
					} else if(mapImage.getRGB(j, i) == Color.BLUE.getRGB()) {
						StagePanel.boardRectangles.get(index).initGap();
					} else if(mapImage.getRGB(j, i) == Color.MAGENTA.getRGB()) {
						StagePanel.boardRectangles.get(index).initHinderingTerrain();
					} else if(mapImage.getRGB(j, i) == Color.ORANGE.getRGB()) {
						StagePanel.destructibleObjects.add(new DestructibleObject(StagePanel.boardRectangles.get(index),1,1, 1,0));
					} else if(mapImage.getRGB(j, i) == Color.YELLOW.getRGB()) {
						StagePanel.goldMines.add(new GoldMine(StagePanel.boardRectangles.get(index)));
					}
					index++;
				}
			}
			
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				curBR.initAdjecantBRs(StagePanel.boardRectangles);
				if(curBR.isWall) {
					curBR.initWallSprites();
				}
			}
	}
	
	public void saveMapAsImage(String mapName,ArrayList<BoardRectangle> boardRectangles) {
		try {
			File outputFile = new File("sprites/Maps/"+mapName+".png");
			if(mapColumns == 0 || mapRows == 0) {
				mapColumns = StagePanel.mapColumns;
				mapRows = StagePanel.mapRows;
			}
			BufferedImage mapImage = new BufferedImage(mapColumns, mapRows, BufferedImage.TYPE_INT_ARGB); 
			int index = 0;
			for(int i = 0;i<mapRows;i++) {
				for(int j = 0; j < mapColumns; j++) {
					if(StagePanel.redBase.containsBR(boardRectangles.get(index))) {
						mapImage.setRGB(j, i, Commons.cBlue.getRGB());
					} else if(StagePanel.blueBase.containsBR(boardRectangles.get(index))) {
						mapImage.setRGB(j, i, Commons.cRed.getRGB());
					} else if(boardRectangles.get(index).isWall) {
						mapImage.setRGB(j, i, Color.BLACK.getRGB());
					} else if(boardRectangles.get(index).isGap) {
						mapImage.setRGB(j, i, Color.BLUE.getRGB());
					} else if(boardRectangles.get(index).isHinderingTerrain) {
						mapImage.setRGB(j, i, Color.MAGENTA.getRGB());
					} else if(boardRectangles.get(index).isDestructibleObject()) {
						mapImage.setRGB(j, i, Color.ORANGE.getRGB());
					} else if(boardRectangles.get(index).isGoldMine()) {
						mapImage.setRGB(j, i, Color.YELLOW.getRGB());
					} else {
						mapImage.setRGB(j, i, Color.WHITE.getRGB());
					}
					index++;
				}
			}
			
			ImageIO.write(mapImage, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
