package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import game.GameState;
import game.GridCell;
import networking.MsgFieldState;

@SuppressWarnings("serial")
public class StagePanel extends JPanel {
	
	private int x,y;
	private int w,h;
	
	private GridCell[][] gridCells;
	private short gridSize;

	private byte winner = 0;
	
	// Constructor
	public StagePanel(int wF, int hF) {
		this.x = 0;
		this.y = 0;
		this.w = wF - x*2;
		this.h = hF - y*2;
		setBounds(x, y, w, h);
		addMouseListener(new ML());
		initGrid();
	}
	
	// Method initializes the game field GUI with empty cells
	private void initGrid() {
		gridSize = 3;
		gridCells = new GridCell[gridSize][gridSize];
		for(int i = 0;i<gridSize;i++) {
			for(int j = 0;j<gridSize;j++) {
				gridCells[i][j] = new GridCell(i,j);
			}
		}
	}
	
	// Method feeds the GUI Grid with the field state
	private void updateGridGui() {
		
		// Get the current field from the Game state class
		byte[][] currField = GameState.getCurrentFieldState();
		
		try {
			for(int i = 0; i < 3; i++)
			{
				for(int k = 0; k < 3; k++)
				{
					byte state = currField[i][k];
					this.gridCells[i][k].setCellState(state);
				}
			}
		} catch(IndexOutOfBoundsException e) {
			System.err.println("Index out of bounds error during field/grid update!");
		}
	}
	
	// main drawing function 
	public void paintComponent(Graphics g) {
		// synchronize field states (GUI and Network) 
		this.updateGridGui();
		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(20,20,20)); 
		g2d.fillRect(0, 0, w, h);
		drawGrid(g2d);
		String str = winner==0?"":winner==1?"x is Winner":"o is Winner";
		
		
		if(winner != 0) {
			g2d.setFont(new Font("Arial",Font.BOLD,60));
			FontMetrics metrics = g2d.getFontMetrics();
			int textWidth = metrics.stringWidth(str);
			int textHeight = metrics.getHeight();
			g2d.setColor(new Color(10,10,10,230));
			g2d.fillRect(w/2-(int)(textWidth*0.75), h/2-textHeight, (int)(textWidth*1.5), textHeight*2);
			g2d.setColor(new Color(255,0,50));
			g2d.drawString(str, w/2-textWidth/2, h/2+textHeight/3);
			g2d.dispose();
		}
		
	}
	
	// Winner detection
	public void checkSomeoneWon() {
		
		// Obtain the current field state from the Game State class
		byte[][] gameField = GameState.getCurrentFieldState();
		
		for(short i = 0; i < gridSize; i++) {
			boolean rowWin = true; 
			for(short j = 0; j < gridSize-1; j++) {
				if(gameField[i][j] == 0 || gameField[i][j] != gameField[i][j+1]) {
					rowWin = false;
				}
			}
			if(rowWin) {
				winner = gameField[i][0];
				return;
			}
		}
		for(short j = 0; j < gridSize; j++) {
			boolean columnWin = true; 
			for(short i = 0; i < gridSize-1; i++) {
				if(gameField[i][j] == 0 || gameField[i][j] != gameField[i+1][j]) {
					columnWin = false;
				}
			}
			if(columnWin) {
				winner = gameField[0][j];
				return;
			}
		}
		boolean diagonalWinLUDR = true; 
		for(short i = 0; i < gridSize-1; i++) {
			if(gameField[i][i] == 0 || gameField[i][i] != gameField[i+1][i+1]) {
				diagonalWinLUDR = false;
			}
		}
		if(diagonalWinLUDR) {
			winner = gameField[0][0];
			return;
		}
		boolean diagonalWinRUDL = true; 
		for(short i = 0; i < gridSize-1; i++) {
			if(gameField[0][gridSize-1] == 0 || gameField[i][gridSize-1-i] != gameField[i+1][gridSize-2-i]) {
				diagonalWinRUDL = false;
			}
			
		}
		if(diagonalWinRUDL) {
			winner = gameField[0][gridSize-1];
			return;
		}
	}
	
	// method for grid drawing
	private void drawGrid(Graphics2D g2d) {
		for(int i = 0; i < gridSize; i++) {
			for(int j = 0; j < gridSize; j++) {
				gridCells[i][j].drawGridCell(g2d);
			}
		}
	}
	
	private class ML implements MouseListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
			// Check if it's our turn. If not then abort
			if(MainJFrame.conn.getTeam().equalsIgnoreCase(GameState.getActingTeam()) == false) {
				System.out.println("It's not your turn at the moment. Wait for the enemy team to make a move");
				return;
			}
			
			if(winner != 0) {
				return;
			}
			
			// Go through the 2D Grid and find the cell that was clicked
			for(byte i = 0; i < gridSize; i++) {
				for(byte j = 0; j < gridSize; j++) {
					if(gridCells[i][j].rect.contains(e.getPoint()) && gridCells[i][j].getCellState() == 0) {
						
						// Determine what to send: cross or circle?
						boolean isX = MainJFrame.conn.getTeam().equalsIgnoreCase("cross");
						if(isX) {
							// send a cross
							GameState.updateField(i, j, (byte) 1);
						} else {
							// send a circled
							GameState.updateField(i, j, (byte) 2);
						}
						
						// Now send the message to the server
						MsgFieldState changedFieldMsg = new MsgFieldState(GameState.getCurrentFieldState());
						MainJFrame.conn.sendMessageToServer(changedFieldMsg);
						
						// checkSomeoneWon();
						
						return;
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}
