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
	final String nameGunner = "G";
	final int maxHealthGunner = 10;
	// FlameThrowerPiece
	final float dmgFlameThrower = 4;
	final String nameFlameThrower = "F";
	final int maxHealthFlameThrower = 10;
	// DetonatorPiece
	final float dmgDetonator = 4;
	final String nameDetonator = "D";
	final int maxHealthDetonator = 10;
	// RocketLauncherPiece
	final float dmgRocketLauncher = 3;
	final String nameRocketLauncher = "R";
	final int maxHealthRocketLauncher = 10;
	
	
}
