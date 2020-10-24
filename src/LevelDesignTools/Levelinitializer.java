package LevelDesignTools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.sun.prism.Image;

import Environment.DestructibleObject;
import Stage.BoardRectangle;
import Stage.StagePanel;
import Stage.ProjectFrame;

public class Levelinitializer {
	ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	private int mapRows = 0;
	private int mapColumns = 0;
	
	public ArrayList<BoardRectangle> getBoardRectangles() {
		return boardRectangles;
	}
	public int getMapRows() {
		return mapRows;
	}
	public int getMapColumns() {
		return mapColumns;
	}
	
	private void printLevelLAyout() {
		System.out.println("###########################################################################");
		System.out.println("Level-Wall-Layout");
		System.out.println("---------------------------------------------------------------------------");
		System.out.println("[] = Wall                 |  " + "BoardRectAmount = " + boardRectangles.size());
		System.out.println("<> = Destructible Object  |  " + "Rows = " + getMapRows());
		System.out.println(":: = Gap                  |  " + "Columns = " + getMapColumns());
		System.out.println("---------------------------------------------------------------------------");
		int row = 0;
		for(BoardRectangle curBR : boardRectangles) {
			if(curBR.row > row) {
				row++;
				System.out.println();
			}
			if(curBR.isWall) {
				System.out.print("[]");
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
	
	public void readMapFromImage(String mapName,ProjectFrame pf) {
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
						boardRectangles.add(new BoardRectangle(i, j, j%2==0, index,false));
					}else{
						boardRectangles.add(new BoardRectangle(i, j, j%2==1, index,false));
					}
					if(mapImage.getRGB(j, i) == Color.BLACK.getRGB()) {
						boardRectangles.get(index).isWall = true;
					}else if(mapImage.getRGB(j, i) == Color.BLUE.getRGB()) {
						boardRectangles.get(index).isGap = true;
					}else if(mapImage.getRGB(j, i) == Color.MAGENTA.getRGB()) {
						boardRectangles.get(index).isHinderingTerrain = true;
					}else if(mapImage.getRGB(j, i) == Color.ORANGE.getRGB()) {
						StagePanel.destructibleObjects.add(new DestructibleObject(boardRectangles.get(index),1,1, 1,0));
					}
					index++;
				}
			}
			printLevelLAyout();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Given map does not exist!");
			pf.dispose();
			e1.printStackTrace();
		}
	}
	
	public void saveMapAsImage(String mapName,ArrayList<BoardRectangle> boardRectangles) {
		try {
			File outputFile = new File("src/LevelDesignTools/"+mapName+".png");
			BufferedImage mapImage = new BufferedImage(mapColumns, mapRows, BufferedImage.TYPE_INT_ARGB); 
			int index = 0;
			for(int i = 0;i<mapRows;i++) {
				for(int j = 0;j<mapColumns;j++) {
					if(boardRectangles.get(index).isWall) {
						mapImage.setRGB(j, i, Color.BLACK.getRGB());
					}else if(boardRectangles.get(index).isGap) {
						mapImage.setRGB(j, i, Color.BLUE.getRGB());
					}else if(boardRectangles.get(index).isHinderingTerrain) {
						mapImage.setRGB(j, i, Color.MAGENTA.getRGB());
					}else if(boardRectangles.get(index).isDestructibleObject()) {
						mapImage.setRGB(j, i, Color.ORANGE.getRGB());
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
