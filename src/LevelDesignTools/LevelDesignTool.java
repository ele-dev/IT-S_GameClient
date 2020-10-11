package LevelDesignTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import Environment.DestructibleObject;
import Stage.BoardRectangle;
import Stage.Commons;
import Stage.StagePanel;

public class LevelDesignTool {
	private String[] buildObjects = {"Wall","Gap","Crate","HinderingTerrain"};
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
			
			g2d.drawString(i==index ?buildObjects[i]+" Selected":buildObjects[i], x +30, y+textHeight*i + textHeight + 30);
		}
		
		
	}
	
	public void drawRowsAndColumnLabels(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.PLAIN,30));
		g2d.setColor(Color.WHITE);
		
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = 0;
		for(int i = 0;i<StagePanel.gameMap.getColumns();i++) {
			String str= i+1+"";
			textWidth = fontMetrics.stringWidth(str);
			g2d.drawString(str, i*Commons.boardRectSize+textWidth/2, -Commons.boardRectSize/2+textHeight/3);
		}
		for(int i = 0;i<StagePanel.gameMap.getRows();i++) {
			String str= i+1+"";
			textWidth = fontMetrics.stringWidth(str);
			g2d.drawString(str, textWidth/2-Commons.boardRectSize, i*Commons.boardRectSize+Commons.boardRectSize/2+textHeight/3);
		}
	}
	
	public void tryPlaceObject() {
		BoardRectangle sbr =  StagePanel.curHoverBR;
		switch (equippedBuildObject) {
		case "Wall":
			if(!sbr.isGap && !sbr.isWall && !sbr.isHinderingTerrain && !sbr.isDestructibleObject()) {
				sbr.isWall = true;
			}
			break;
		case "Gap":
			if(!sbr.isGap && !sbr.isWall && !sbr.isHinderingTerrain && !sbr.isDestructibleObject()) {
				sbr.isGap = true;
			}
		case "Crate":
			if(!sbr.isGap && !sbr.isWall  && !sbr.isHinderingTerrain && !sbr.isDestructibleObject()) {
				StagePanel.destructibleObjects.add(new DestructibleObject(sbr, 1, 1, 1, 0));
			}	
		case "HinderingTerrain":
			if(!sbr.isGap && !sbr.isWall && !sbr.isHinderingTerrain && !sbr.isDestructibleObject()) {
				sbr.isHinderingTerrain = true;
			}	
			break;
		}
	}
	
	public void tryRemoveObject() {
		BoardRectangle sbr =  StagePanel.curHoverBR;
		switch (equippedBuildObject) {
		case "Wall":
			sbr.isWall = false;
			break;
		case "Gap":
			sbr.isGap = false;
			break;
		case "Crate":
			for(DestructibleObject curDO : StagePanel.destructibleObjects) {
				if(curDO.containsBR(sbr)) {
					 StagePanel.destructibleObjects.remove(curDO);
					 return;
				}
			}
			break;
		case "HinderingTerrain":
			sbr.isHinderingTerrain = false;
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
