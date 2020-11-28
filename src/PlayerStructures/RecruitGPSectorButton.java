package PlayerStructures;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Buttons.GenericButton;
import Particles.TrailParticle;
import Stage.StagePanel;

public class RecruitGPSectorButton extends GenericButton{
	
	private RecruitGPInfoPanel[] recruitGamePieceInfoPanels;
	
	ArrayList<TrailParticle> trailParticles = new ArrayList<TrailParticle>();
	float length = 0;
	float[] counters = new float[6];

	public RecruitGPSectorButton(int startx, int starty, int w, int h, String name, Color c, Color cHover,int fontSize,PlayerFortress playerFortress) {
		super(startx, starty, w, h, name, c, cHover, fontSize);
		isActive = false;
		init(playerFortress);
		length = (rect.width+rect.height) *2;
		for(int i = 0;i<counters.length;i++) {
			counters[i] = length* i/counters.length;
		}
		for(int i = 0;i<1000;i++){
			updateParticles();
		}
	}
	
	public RecruitGPInfoPanel[] getRecruitGamePieceInfoPanels() {
		return recruitGamePieceInfoPanels;
	}
	
	private void init(PlayerFortress playerFortress) {
		String[] gamePieceNames = null;
		switch (name) {
		case "Normal":
			recruitGamePieceInfoPanels = new RecruitGPInfoPanel[3];
			String[] gamePieceNames1 = {"Gunner","Shotgun","Sniper"};
			gamePieceNames = gamePieceNames1;
			break;
		case "Fire":
			recruitGamePieceInfoPanels = new RecruitGPInfoPanel[3];
			String[] gamePieceNames2 = {"Detonator","FlameThrower","RocketLauncher"};
			gamePieceNames = gamePieceNames2;
			break;
		case "Electro":
			recruitGamePieceInfoPanels = new RecruitGPInfoPanel[3];
			String[] gamePieceNames3 = {"EMP","RapidElectro","Tazer"};
			gamePieceNames = gamePieceNames3;
			break;
		default:
			break;
		}
		int gap = StagePanel.w/32;
		for(byte i = 0; i < recruitGamePieceInfoPanels.length; i++) {
			if(i == 0) {
				recruitGamePieceInfoPanels[i] = new RecruitGPInfoPanel(StagePanel.w/40 , StagePanel.w/40*6, StagePanel.w/2, StagePanel.w/10, gamePieceNames[i], playerFortress,i);
			} else {
				recruitGamePieceInfoPanels[i] = new RecruitGPInfoPanel(StagePanel.w/40, recruitGamePieceInfoPanels[i-1].getStarty()+StagePanel.w/10+gap, StagePanel.w/2, StagePanel.w/10, gamePieceNames[i], playerFortress,i);
			}
		}
	}
	
	@Override
	public void drawButton(Graphics2D g2d) {
		if(isHover) {
			g2d.setColor(cHover);
		}else {
			g2d.setColor(c);
		}
		if(isActive) {
			g2d.setColor(c);
			g2d.fill(rect);
			g2d.setColor(new Color(cHover.getRed(),cHover.getGreen(),cHover.getBlue(),100));
		}
		g2d.fill(rect);
		
		
		if(isHover) {
			g2d.setColor(c);
		}else {
			g2d.setColor(cHover);
		}
		if(isActive) {
			g2d.setColor(c);
		}
		g2d.draw(rect);
		
		g2d.setFont(f);
		FontMetrics fMetrics = g2d.getFontMetrics();
		int textHeight = fMetrics.getHeight();
		int textWidth = fMetrics.stringWidth(name);
		g2d.drawString(name,(int)(rect.x+rect.getWidth()/2 - textWidth/2),(int)(rect.y + rect.getHeight()/2 +textHeight/3));
		
		drawParticles(g2d);
	}
	
	public void drawParticles(Graphics2D g2d) {
		if(!isActive) {
			return;
		}
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			curTP.drawParticle(g2d);
		}
	}
	
	public void updateParticles() {
		tryAddParticle();
		for(int i = 0;i<trailParticles.size();i++) {
			TrailParticle curTP = trailParticles.get(i);
			curTP.update();
			curTP.setCNoAlpha(cHover);
			if(curTP.isDestroyed()) {
				trailParticles.remove(i);
			}
		}
	}
	
	private void tryAddParticle() {
		for(int i = 0;i<counters.length;i++) {
			int x = rect.x;
			int y = rect.y;
			if(counters[i]<length) {
				counters[i]+= 1f;
			}else {
				counters[i] = 0;
			}
			float counter = counters[i];
			if(counter > rect.width) {
				if(counter > rect.width+rect.height) {
					if(counter > rect.width*2+rect.height) {
						y += rect.height -(counter-rect.height-rect.width*2);
					}else {
						x += rect.width-(counter-rect.width-rect.height);
						y += rect.height;
					}
				}else {
					y += counter-rect.width;
					x += rect.width;
				}
			}else {
				x += counter;
			}
			if(counter%6 == 0) {
				int size = (int)(Math.random()*StagePanel.w/300+StagePanel.w/300);
				size = size > 0?size:1;
				trailParticles.add(new TrailParticle((int)(x+(Math.random()-0.5)*4), (int)(y+(Math.random()-0.5)*4),size, (float)Math.random()*360, cHover, 0, 3, 0));
			}
		}
	}
	
	
}
