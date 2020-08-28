package Stage;

import java.awt.Color;

public interface Commons {
	final int boardRectSize = 80;
	// width of frame
	final int wf = 1800;
	// height of frame
	final int hf = 1000;
	
	final Color cUltCharge = new Color(234,255,70);
	
	final Color cMove = new Color(255,220,90);
	final Color cMoveActive = new Color(84,75,35);
	final Color cAttack = new Color(255,0,20);
	final Color cAttackActive = new Color(87,0,10);
	
	final Color cGPMovesPanel = new Color(10,10,10,230);
	
	
	final Color enemyColor = new Color(255,0,43);
	final Color notEnemyColor = new Color(16,68,255);
	final Color enemyColorTurret = new Color(230,0,33);
	final Color notEnemyColorTurret = new Color(10,48,230);
	
	final Color cHealth = new Color(85,255,80);
	
	final String pathToSpriteSource = "sprites/";
	
	// GamePieces 
	final int dmgFlashCountDown = 5;
	// GunnerPiece
	final float dmgGunner = 3;
	final int MovementRangeGunner = 5;
	final String nameGunner = "G";
	final int maxHealthGunner = 10;
	// FlameThrowerPiece
	final float dmgFlameThrower = 4;
	final int MovementRangeFlameThrower = 5;
	final String nameFlameThrower = "F";
	final int maxHealthFlameThrower = 10;
	// DetonatorPiece
	final float dmgDetonator = 4;
	final int MovementRangeDetonator = 5;
	final String nameDetonator = "D";
	final int maxHealthDetonator = 10;
	// RocketLauncherPiece
	final float dmgRocketLauncher = 3;
	final int MovementRangeRocketLauncher = 5;
	final String nameRocketLauncher = "R";
	final int maxHealthRocketLauncher = 10;
	
	// creates two vectors. One Vector(Vector with Angle angle) gets scaled with the scalar(rotationDelay)
	// the two Vectors get added together and the angle of the resulting Vector is the return-product
	public static float calculateAngleAfterRotation(float angle, float angleDesired, float rotationDelay) {
		double ak1 = Math.cos(Math.toRadians(angleDesired+90));
		double gk1 = Math.sin(Math.toRadians(angleDesired+90));
		
		double ak2 = Math.cos(Math.toRadians(angle+90)) * rotationDelay;
		double gk2 = Math.sin(Math.toRadians(angle+90)) * rotationDelay;

		double ak3 = (ak1+ak2*rotationDelay)/(rotationDelay+1);
		double gk3 = (gk1+gk2*rotationDelay)/(rotationDelay+1);
		
		return (float) Math.toDegrees(Math.atan2(ak3*-1, gk3));
	}
	
}
