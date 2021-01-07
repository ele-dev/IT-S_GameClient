package Particles;

import java.awt.Color;

public class WaveParticle extends TrailParticle {
	private int starty;
	public WaveParticle(float x, float y, int size, float angle, Color c) {
		super(x, y, size, angle, c, 0, 0, 0);
		this.starty = (int) y;
	}
	
	public void setYOffset(int elongation) {
		y = starty + elongation;
	}

}
