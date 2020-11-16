package LevelDesignTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import Environment.DestructibleObject;
import PlayerStructures.GoldMine;
import PlayerStructures.PlayerFortress;
import Stage.BoardRectangle;
import Stage.StagePanel;

public class LevelDesignTool {
	private String[] buildObjects = {"Wall","Gap","Crate","HinderingTerrain","GoldMine","RedFortress","BlueFortress"};
	private int index = 0;
	private String equippedBuildObject = buildObjects[index];
	public MWL mwl = new MWL();
	
	
	public void drawEquippedBuildObject(Graphics2D g2d) {
		drawRowsAndColumnLabels(g2d);
		g2d.setFont(new Font("Arial",Font.PLAIN,30));
		int textHeight = g2d.getFontMetrics().getHeight();
		int x = StagePanel.camera.getCenterOfScreen().x-StagePanel.w/2 +30;
		int y = StagePanel.camera.getCenterOfScreen().y -StagePanel.h/2 +30;
		g2d.setColor(new Color(20,20,20,200));
		g2d.fillRect(x, y, 300, textHeight*buildObjects.length +60);
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(4));
		g2d.drawRect(x, y, 300, textHeight*buildObjects.length +60);
		
		for(int i = 0;i<buildObjects.length;i++) {
			g2d.setColor(i==index ?Color.GREEN:Color.WHITE);
			
			g2d.drawString(i==index ?buildObjects[i]+"":buildObjects[i], x +30, y+textHeight*i + textHeight + 30);
		}
	}
	
	public void drawRowsAndColumnLabels(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.PLAIN,30));
		g2d.setColor(Color.WHITE);
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		for(int i = 0; i < StagePanel.mapColumns; i++) {
			String str= i+1+"";
			textWidth = fontMetrics.stringWidth(str);
			g2d.drawString(str, i*StagePanel.boardRectSize+textWidth/2, -StagePanel.boardRectSize/2+textHeight/3);
		}
		for(int i = 0;i<StagePanel.mapRows;i++) {
			String str= i+1+"";
			textWidth = fontMetrics.stringWidth(str);
			g2d.drawString(str, textWidth/2-StagePanel.boardRectSize, i*StagePanel.boardRectSize+StagePanel.boardRectSize/2+textHeight/3);
		}
	}
	
	public void tryPlaceObject() {
		BoardRectangle sBR =  StagePanel.curHoverBR;
		
		if(!sBR.isGap && !sBR.isWall && !sBR.isHinderingTerrain && !sBR.isDestructibleObject() && !sBR.isGoldMine()){
			switch (equippedBuildObject) {
			case "Wall":
				sBR.isWall = true;
				break;
			case "Gap":
				sBR.isGap = true;
				break;
			case "Crate":
				StagePanel.destructibleObjects.add(new DestructibleObject(sBR, 1, 1, 1, 0));
				break;
			case "HinderingTerrain":
				sBR.isHinderingTerrain = true;
				break;
			case "GoldMine":
				StagePanel.goldMines.add(new GoldMine(sBR));
				break;
			case "RedFortress":
				StagePanel.redBase = new PlayerFortress(sBR, Color.RED);
				break;
			
			case "BlueFortress":
				StagePanel.blueBase = new PlayerFortress(sBR, Color.BLUE);
				break;
			}
		}
	}
	
	public void tryRemoveObject() {
		BoardRectangle sBR =  StagePanel.curHoverBR;
		switch (equippedBuildObject) {
		case "Wall":
			sBR.isWall = false;
			break;
		case "Gap":
			sBR.isGap = false;
			break;
		case "Crate":
			for(DestructibleObject curDO : StagePanel.destructibleObjects) {
				if(curDO.containsBR(sBR)) {
					 StagePanel.destructibleObjects.remove(curDO);
					 return;
				}
			}
			break;
		case "HinderingTerrain":
			sBR.isHinderingTerrain = false;
			break;
		case "GoldMine":
			for(GoldMine curGM : StagePanel.goldMines) {
				if(curGM.containsBR(sBR)) {
					 StagePanel.goldMines.remove(curGM);
					 return;
				}
			}
			break;
		case "RedFortress":
			if(StagePanel.redBase != null && StagePanel.redBase.containsBR(sBR)) {
				StagePanel.redBase = null;
			}
			break;
		case "BlueFortress":
			if(StagePanel.blueBase != null && StagePanel.blueBase.containsBR(sBR)) {
				StagePanel.blueBase = null;
			}
			break;
		}
	}
	
	private class MWL implements MouseWheelListener{

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			index += e.getWheelRotation();
			if(index < 0) {
				index = buildObjects.length-1;
			}else if(index >= buildObjects.length){
				index = 0;
			}
			equippedBuildObject = buildObjects[index];
		}
		
	}
}
