package Stage;

import java.awt.Color;

public interface Commons {
	int boardRectSize = 80;
	// width of frame
	int wf = 1800;
	// height of frame
	int hf = 1000;
	
	Color cUltCharge = new Color(234,255,70);
	
	Color cMove = new Color(255,220,90);
	Color cMoveActive = new Color(84,75,35);
	Color cAttack = new Color(255,0,20);
	Color cAttackActive = new Color(87,0,10);
	
	Color cGPMovesPanel = new Color(10,10,10,230);
	
	
	Color enemyColor = new Color(255,0,43);
	Color notEnemyColor = new Color(16,68,255);
	Color enemyColorTurret = new Color(230,0,33);
	Color notEnemyColorTurret = new Color(10,48,230);
	
	Color cHealth = new Color(85,255,80);
	
	String pathToSpriteSource = "sprites/";
	
	// GamePieces 
	int dmgFlashCountDown = 5;
	// GunnerPiece
	double dmgGunner = 3;
	String nameGunner = "G";
	int maxHealthGunner = 10;
	// FlameThrowerPiece
	double dmgFlameThrower = 4;
	String nameFlameThrower = "F";
	int maxHealthFlameThrower = 10;
	// DetonatorPiece
	double dmgDetonator = 4;
	String nameDetonator = "D";
	int maxHealthDetonator = 10;
	// RocketLauncherPiece
	double dmgRocketLauncher = 3;
	String nameRocketLauncher = "R";
	int maxHealthRocketLauncher = 10;
	
	
}
