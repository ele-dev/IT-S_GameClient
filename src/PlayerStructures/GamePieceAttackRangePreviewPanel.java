package PlayerStructures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import GamePieces.DetonatorPiece;
import GamePieces.EMPPiece;
import GamePieces.FlamethrowerPiece;
import GamePieces.GamePiece;
import GamePieces.GunnerPiece;
import GamePieces.RapidElectroPiece;
import GamePieces.RocketLauncherPiece;
import GamePieces.ShotgunPiece;
import GamePieces.SniperPiece;
import GamePieces.TazerPiece;
import Stage.Commons;
import Stage.StagePanel;

public class GamePieceAttackRangePreviewPanel {
	Rectangle[][] rectangles;
	Color[][] colors;
	private int sizeRectangle;
	private int rows,columns;
	private int size;
	
	public GamePieceAttackRangePreviewPanel(int rows ,int columns, int size, String GPName) {
		rectangles = new Rectangle[rows][columns];
		colors = new Color[rows][columns];
		sizeRectangle = size / rows; 
		this.rows =  rows;
		this.columns =  columns;
		this.size = size;
		for(int i = 0;i<rows;i++) {
			for(int j = 0;j<columns;j++) {
				rectangles[i][j] = new Rectangle(j*sizeRectangle,i*sizeRectangle,sizeRectangle,sizeRectangle);
				colors[i][j] = Color.GRAY;
			}
		}
		initShowPossiblAttack(GPName);
	}
	
	private void initShowPossiblAttack(String GPName) {
		int centerRow = rows/2;
		int centerColumn = columns/2;
		GamePiece gp = new GunnerPiece(false, StagePanel.boardRectangles.get(0));;
		switch (GPName) {
		case "Gunner":
			gp = new GunnerPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "Shotgun":
			gp = new ShotgunPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "Sniper":
			gp = new SniperPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "Detonator":
			gp = new DetonatorPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "FlameThrower":
			gp = new FlamethrowerPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "RocketLauncher":
			gp = new RocketLauncherPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "EMP":
			gp = new EMPPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "RapidElectro":
			gp = new RapidElectroPiece(false, StagePanel.boardRectangles.get(0));
			break;
		case "Tazer":
			gp = new TazerPiece(false, StagePanel.boardRectangles.get(0));
			break;
		default:
			break;
		}
		
		for(int i = 0;i<rows;i++) {
			for(int j = 0;j<columns;j++) {
				if(gp.checkAttacks(i, j, centerRow, centerColumn)) {
					colors[i][j] = Commons.cAttack;
				}
			}
		}
		colors[centerRow][centerColumn] = Commons.cMove;
	}
	
	public void drawGamePieceAttackRangePreviewPanel(Graphics2D g2d,int x, int y) {
		g2d.translate(x+size/2, y);
		for(int i = 0;i<rows;i++) {
			for(int j = 0;j<columns;j++) {
				g2d.setColor(colors[i][j]);
				g2d.fill(rectangles[i][j]);
				g2d.setColor(new Color(10,10,10));
				g2d.setStroke(new BasicStroke(StagePanel.boardRectSize/16));
				g2d.draw(rectangles[i][j]);
			}
		}
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		g2d.setColor(Commons.cAttack);
		g2d.drawString("attack pattern", size/2-fontMetrics.stringWidth("AttackPattern")/2, size+fontMetrics.getHeight());
		g2d.translate(-x-size/2, -y);
	}
}
