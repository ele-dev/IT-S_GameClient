package Particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import PlayerStructures.PlayerFortress;
import Stage.StagePanel;
import Stage.ValueLabel;

public class GoldParticle extends Particle{
	private float friction;
	
	private Timer tAutoCollectCountdown;
	private PlayerFortress playerFortress;
	private Rectangle targetRectangle;
	private float vRotation = (float) ((Math.random()-0.5)*10);
	private float vY,capVY;
	
	private byte spawnTrailParticleCounter = 0,spawnTrailParticleIntervall = 2;
	
	public GoldParticle(float x, float y,float angle, float rotation, float v,boolean isEnemy) {
		super(x, y,angle,rotation,new Color(204+(int)((Math.random()-0.5)*50),164+(int)((Math.random()-0.5)*50),61),v,0);
		int size = (int) (Math.random() * 15+15);
		rectHitbox = new Rectangle((int)x-size/2,(int)y-size/2,size,size);
		friction = 0.1f;
		
		tAutoCollectCountdown = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetVelocity();
			}
		});
		tAutoCollectCountdown.setRepeats(false);
		tAutoCollectCountdown.start();
		
		playerFortress = isEnemy?StagePanel.enemyFortress:StagePanel.notEnemyFortress;
		targetRectangle = playerFortress.getRectHitbox();
		vY = -v/2;
		capVY= -vY;
	}

	@Override
	public void drawParticle(Graphics2D g2d) {
		g2d.setColor(c);
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		g2d.fillRect(-rectHitbox.width/2, -rectHitbox.height/2, rectHitbox.width, rectHitbox.height);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
	}

	@Override
	public void update() {
		move();
		
		if(v==0) {
			homeInOnTarget(new Point((int)targetRectangle.getCenterX(),(int)targetRectangle.getCenterY()));
		}else {
			rotation += vRotation;
		}
		if(!tAutoCollectCountdown.isRunning()) {
			homeInOnTarget(new Point((int)targetRectangle.getCenterX(),(int)targetRectangle.getCenterY()));
			checkHitTarget();
			v+= 0.1f;
		}else{
			v = v-friction > 0?v-friction:0;
			tryGetWallBlocked();
		}
		
		spawnTrailParticleCounter--;
		if(spawnTrailParticleCounter <= 0) {
			spawnTrailParticleCounter = spawnTrailParticleIntervall;
			int randomSize = (int) (Math.random()*rectHitbox.getWidth()/4+4);
			StagePanel.particles.add(new TrailParticle(x+(int)((Math.random()-0.5)*rectHitbox.getWidth()/2), y+(int)((Math.random()-0.5)*rectHitbox.getWidth()/2)
					,randomSize,0, c,0, (int)(Math.random()*3+3),0));
		}
	}
	
	private void homeInOnTarget(Point targetPoint) {
		float ak = 0;
		float gk = 0;
		
		ak = targetPoint.x - x;
		gk = targetPoint.y - y;
		
		float angleDesired = (float) Math.toDegrees(Math.atan2(ak*-1, gk));
		angle = Stage.Commons.calculateAngleAfterRotation(angle, angleDesired, 3);
	}
	
	public void move() {
		x += Math.cos(Math.toRadians(angle+90)) * v;
		y += Math.sin(Math.toRadians(angle+90)) * v;
		rectHitbox.setBounds((int)x-rectHitbox.width/2, (int)y-rectHitbox.height/2, rectHitbox.width, rectHitbox.height);
		
		if(vY < capVY) {
			vY+=0.1f;
			y+=vY;
		}
	}
	
	private void checkHitTarget() {
		if(targetRectangle.intersects(rectHitbox)) {
			isDestroyed = true;
			playerFortress.increaseCoinAmount(1,(int)x,(int)y);
		}
	}
	
	public void resetVelocity() {
		v = (float) (Math.random()*2+2);
	}

}
