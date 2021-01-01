package Buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Sound.SoundEffect;
import Stage.StagePanel;

public class IngameOptionPanel {
	private Rectangle rect;
	private boolean isOpened = false;
	
	public GenericButton surrenderButton;
	public GenericSlider soundVolumeSlider;
	
	public IngameOptionPanel() {
		
		this.rect = new Rectangle(0,0,StagePanel.w,StagePanel.h);
		surrenderButton = new GenericButton((int)rect.getCenterX()-StagePanel.w/12,(int)rect.getCenterY(),StagePanel.w/6,StagePanel.w/16,
				"Surrender", new Color(20,20,20), new Color(255,0,50), StagePanel.w/16/3);
		
		soundVolumeSlider = new GenericSlider((int)surrenderButton.rect.getCenterX()-StagePanel.w/10, (int)rect.getCenterY()+200, StagePanel.w/5, StagePanel.w/40,"Sound Volume");
	}
	
	public boolean isOpened() {
		return isOpened;
	}
	
	public void tryToggleOpened() {
		if(!isOpened) {
			if(StagePanel.redBase.isSelected() || StagePanel.blueBase.isSelected() && StagePanel.winScreen != null) {
				return;
			}
		}
		isOpened = !isOpened;
	}
	
	public void tryDrawIngameOptionPanel(Graphics2D g2d) {
		if(isOpened) {
			g2d.setColor(new Color(20,20,20));
			g2d.fill(rect);
			
			g2d.setColor(Color.WHITE);
			String str = "Options";
			g2d.setFont(new Font("Arial",Font.BOLD,StagePanel.w/16));
			g2d.drawString(str, (int)rect.getCenterX() -g2d.getFontMetrics().stringWidth(str)/2, rect.height/10);
			
			surrenderButton.drawButton(g2d);
			soundVolumeSlider.drawGenericSlider(g2d);
		}
	}
	
	public void update() {
		if(isOpened) {
			surrenderButton.updateHover(StagePanel.mousePosUntranslated);
			surrenderButton.setActive(StagePanel.curActionPerformingGP == null && StagePanel.levelDesignTool == null && StagePanel.noFortressSelected() && !StagePanel.goldUncollected());
		
			soundVolumeSlider.update();	
			SoundEffect.decibelValueRaiser = (soundVolumeSlider.getValue()-0.5f)*40 -15;
		}
		
	}
	
	
	
	
}
