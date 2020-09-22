package LevelDesignTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import Environment.DestructibleObject;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

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
	// Reads the File and generates the BoardRectangle accordingly
	public void readFile() {	
		try {
			File file = new File("src/LevelDesignTools/AuthoredLevels.txt");
			Scanner scanner = new Scanner(file);
			boardRectangles.clear();
			
			int lineIndex = 0;
			
			while (scanner.hasNext()) {
				int index = Integer.parseInt(scanner.next());	
				int tileIndex = Integer.parseInt(scanner.next());		
				int row = Integer.parseInt(scanner.next());	
				int column = Integer.parseInt(scanner.next());	 
				int WallorGapIndex = Integer.parseInt(scanner.next());		
				int DestructibleObjectIndex = Integer.parseInt(scanner.next());	
				
				System.out.println("BoardRectIndex: "+index);
				System.out.println("TileIndex: "+tileIndex);
				System.out.println("Row/Coloumn: "+row+"/"+column);
				if(WallorGapIndex == 0) {
					System.out.println("IsWall");
				}else if(WallorGapIndex == 2) {
					System.out.println("IsGap");
				}else {
					System.out.println("IsGround");
				}
				if(DestructibleObjectIndex == 0) {
					System.out.println("IsDestructibleObject");
				}else {
					System.out.println("IsNotDestructibleObject");
				}
				System.out.println("");

				boardRectangles.add(new BoardRectangle(row, column, tileIndex==1, lineIndex));
				if(WallorGapIndex == 1) {
					boardRectangles.get(lineIndex).isWall = true;
				}else if(WallorGapIndex == 2) {
					boardRectangles.get(lineIndex).isGap = true;
				}
				if(DestructibleObjectIndex == 1) {
					StagePanel.destructibleObjects.add(new DestructibleObject(boardRectangles.get(lineIndex),1,1, 1,0));
				}
				lineIndex++;
				
				if(mapRows < row) {
					mapRows = row;
				}
				if(mapColumns < column) {
					mapColumns = column;
				}
			}
			mapRows++;
			mapColumns++;
			scanner.close();
			
			System.out.println("Level-Wall-Layout");
			System.out.println("[] = Wall");
			System.out.println("<> = Destructible Object");
			System.out.println(":: = Gap");
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
			System.out.println("BoardRectAmount:" + boardRectangles.size());
			System.out.println("Rows:" + getMapRows());
			System.out.println("Columns:" + getMapColumns());
			System.out.println("closed scanner");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	// writes the File
	public void writeFile(ArrayList<BoardRectangle> boardRectangles) {
		try {
			File file = new File("src/LevelDesignTools/AuthoredLevels.txt");
			
			// only creates a new File if the File does not exist !!
			file.createNewFile();
			
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			
			for(int i = 0;i<boardRectangles.size();i++) {
				pw.print(i+" ");
				if(boardRectangles.get(i).isTile1) {
					pw.print(1+" ");
				}else {
					pw.print(0+" ");
				}
				pw.print(boardRectangles.get(i).row+" ");
				pw.print(boardRectangles.get(i).column+" ");
				if(boardRectangles.get(i).isWall) {
					pw.println(1);
				}else if(boardRectangles.get(i).isGap){
					pw.println(2);
				}else {
					pw.println(0);
				}
				if(boardRectangles.get(i).isDestructibleObject()) {
					pw.println(1);
				}else {
					pw.println(0);
				}
			}
			// writes string from pw into file
			pw.flush();
			pw.close();
			System.out.println("File saved");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
