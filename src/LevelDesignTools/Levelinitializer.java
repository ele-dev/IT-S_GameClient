package LevelDesignTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import Stage.BoardRectangle;
import Stage.Commons;

public class Levelinitializer {
	
ArrayList<BoardRectangle> boardRectangles = new ArrayList<BoardRectangle>();
	
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
				int WallIndex = Integer.parseInt(scanner.next());		
				
				System.out.println("BoardRectIndex: "+index);
				System.out.println("TileIndex: "+tileIndex);
				System.out.println("Row/Coloumn: "+row+"/"+column);
				if(WallIndex == 0) {
					System.out.println("IsWall");
				}else {
					System.out.println("IsNotWall");
				}
				System.out.println("");

				boardRectangles.add(new BoardRectangle(Commons.boardRectSize, row, column, tileIndex==1, lineIndex));
				if(WallIndex == 1) {
					boardRectangles.get(lineIndex).isWall = true;
				}
				lineIndex++;
			}
			scanner.close();
			
			System.out.println("Level-Wall-Layout");
			int row = 0;
			for(BoardRectangle curBR : boardRectangles) {
				if(curBR.row > row) {
					row++;
					System.out.println();
				}
				if(curBR.isWall) {
					System.out.print("[]");
				}else {
					System.out.print("  ");
				}
			}
			System.out.println();
			System.out.println("BoardRectAmount:" + boardRectangles.size());
			System.out.println("closed scanner");
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<BoardRectangle> getBoardRectangles() {
		return boardRectangles;
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
