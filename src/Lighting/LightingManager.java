package Lighting;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Stage.Camera;
import Stage.Commons;
import Stage.StagePanel;

public class LightingManager {
	int w,h;
	ArrayList<Light> lights = new ArrayList<Light>();
	BufferedImage lightMapBufferedImage;
	Camera camera;
	
	public LightingManager(int w, int h,Camera camera) {
		this.w = w;
		this.h = h;
		this.camera = camera;
		lights.add(new Light(Commons.boardRectSize*StagePanel.amountOfColumns/2, Commons.boardRectSize*StagePanel.amountOfRows/2, Commons.boardRectSize*StagePanel.amountOfRows*2, new Color(255,255,255,0)));

		lightMapBufferedImage = new BufferedImage(w*4, h*4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D lightmapG2d = lightMapBufferedImage.createGraphics();

		// draws the lightmap as a big rectangle 
		lightmapG2d.setColor(new Color(10,10,10,230));
		lightmapG2d.fillRect(0, 0, w*4, h*4);
		
		// sets the alphaComposit so next drawn lights alpha can be subtracted from prev lightmap
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1);
		lightmapG2d.setComposite(ac);
		lightmapG2d.translate(w*2, h*2);
		for(Light curL : lights) {
			curL.subtractFromLightMap(lightmapG2d);
		}
		lightmapG2d.translate(-w*2, -h*2);
	}
	
	public void drawLight(Graphics2D g2d) {
		g2d.drawImage(lightMapBufferedImage, null, -w*2, -h*2);
	}
	
	
}
