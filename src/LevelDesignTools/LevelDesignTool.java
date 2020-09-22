package LevelDesignTools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import Environment.DestructibleObject;
import Stage.BoardRectangle;
import Stage.StagePanel;

public class LevelDesignTool {
	private String[] buildObjects = {"Wall","Gap","Crate"};
	private int index = 0;
	private String equippedBuildObject = buildObjects[index];
	public MWL mwl = new MWL();
	
	
	public void drawEquippedBuildObject(Graphics2D g2d) {
		
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
	
	public void tryPlaceObject() {
		BoardRectangle sbr =  StagePanel.curHoverBR;
		switch (equippedBuildObject) {
		case "Wall":
			if(!sbr.isGap && !sbr.isWall) {
				for(DestructibleObject curDO : StagePanel.destructibleObjects) {
					if(curDO.containsBR(sbr)) {
						return;
					}
				}
				sbr.isWall = true;
			}
			
			break;
		case "Gap":
			if(!sbr.isGap && !sbr.isWall) {
				for(DestructibleObject curDO : StagePanel.destructibleObjects) {
					if(curDO.containsBR(sbr)) {
						return;
					}
				}
				sbr.isGap = true;
			}
		case "Crate":
			if(!sbr.isGap && !sbr.isWall) {
				for(DestructibleObject curDO : StagePanel.destructibleObjects) {
					if(curDO.containsBR(sbr)) {
						return;
					}
				}
				StagePanel.destructibleObjects.add(new DestructibleObject(sbr, 1, 1, 1, 0));
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
		case "Crate":
			
			for(DestructibleObject curDO : StagePanel.destructibleObjects) {
				if(curDO.containsBR(sbr)) {
					 StagePanel.destructibleObjects.remove(curDO);
					 return;
				}
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
