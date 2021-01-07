package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Sound.SoundEffect;
import Stage.Commons;
import Stage.StagePanel;

public class Explosion extends Particle{
	private ArrayList<ExplosionCloud> explosionClouds = new ArrayList<ExplosionCloud>();
	private ArrayList<ExplosionFragment> explosionFragments = new ArrayList<ExplosionFragment>();
	private int numberOfClouds;
	private int numberOfFragments;
	
	//fragment angle makes effect of shooting the explosion out of the impactZone away from target
	public Explosion(float x, float y, float size, float fragmentAngle) {
		super(x, y, fragmentAngle, size, null, 0,0);
		initExplosion(size);
		for(int i = 0;i<numberOfFragments;i++) {
			explosionFragments.add(new ExplosionFragment(x, y,(int)(Math.random() * 6 * size) + (int)(4 * size),
					Color.RED, (float)(Math.random()+1), (float)(fragmentAngle+180) + (float)(Math.random()-0.5)*240));
		}
		SoundEffect.play("Explosion.wav");
	}
	
	public Explosion(float x, float y, float size) {
		super(x, y, 0, size, null, 0,0);
		initExplosion(size);
		for(int i = 0;i<numberOfFragments;i++) {
			explosionFragments.add(new ExplosionFragment(x, y,(int)(Math.random() * 6 * size) + (int)(4 * size),
					Color.RED, (float)(Math.random()+1), (float)(Math.random()+360) + (float)(Math.random()-0.5)*240));
		}
		SoundEffect.play("Explosion.wav");
	}
	private void initExplosion(float size) {
		numberOfClouds = (int) (10*size);
		numberOfFragments = (int) (5*size);
		for(int i = 0;i<numberOfClouds;i++) {
			explosionClouds.add(new ExplosionCloud(x, y,(int)(Math.random() * 40 * size) + (int)(10 * size), (float)(Math.random() * size*2), (float)(Math.random()*360)));
		}
		StagePanel.applyScreenShake(2, 10);
	}
	@Override
	public void drawParticle(Graphics2D g2d) {
		for(int i = 0;i<explosionClouds.size();i++) {
			ExplosionCloud curEC = explosionClouds.get(i);
			if(curEC.getColor().getAlpha()>10) {
				curEC.drawExplosionCloud(g2d);
			}else {
				explosionClouds.remove(i);
			}
		}
		
		for(int i = 0;i<explosionFragments.size();i++) {
			ExplosionFragment curEF = explosionFragments.get(i);
			if(curEF.getColor().getAlpha()>10) {
				curEF.drawExplosionFragments(g2d);
			}else {
				explosionFragments.remove(i);
			}
		}
	}

	@Override
	public void update() {
		moveAllFrags();
		updateExplosion();
		if(checkIfExplosionFaded()) {
			isDestroyed = true;
		}
	}
	
	// moves every Fragment
	public void moveAllFrags(){
		for(ExplosionCloud curEC : explosionClouds) {
			curEC.move();
		}
		for(ExplosionFragment curEF : explosionFragments) {
			curEF.move();
		}
	}
	// returns true if the explosion has faded and false if it has not
	public boolean checkIfExplosionFaded() {
		if(explosionClouds.size() == 0 && explosionFragments.size() == 0) {
			return true;
		}
		return false;
	}
	// updates each piece of the explosion (all fragments and all clouds)
	public void updateExplosion() {
		for(int i = 0;i<explosionFragments.size();i++) {
			if(explosionFragments.get(i).getColor().getAlpha()>10) {
				explosionFragments.get(i).updateFade();
				explosionFragments.get(i).updateTrail();
			}else {
				explosionFragments.remove(i);
			}
		}	
		for(int i = 0;i<explosionClouds.size();i++) {
			if(explosionClouds.get(i).getColor().getAlpha()>10) {
				explosionClouds.get(i).updateFade();
			}else {
				explosionClouds.remove(i);
			}
		}
	}

	@Override
	public void move() {}
}
