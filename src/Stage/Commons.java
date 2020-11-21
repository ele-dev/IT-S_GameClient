package Stage;

import java.awt.Color;

public interface Commons {
	
	// The game title
	public final String gameTitle = "Crossfire PvP";
	
	// Game money per match win
	public static final int winnerMoney = 100;
	
	// Refresh rates for timers 
	public static final int frametime = 10;
	
	// Menue gui elements dimensions
	public static final int textFieldHeight = 46;
	public static final int textFieldWidth = 210;
	
	// Important colors
	final Color loginScreenBackground = new Color(28, 26, 36);
	final Color homeScreenBackground = new Color(28, 26, 36);
	final Color buttonHover = Color.BLUE;
	final Color textFieldSelected = new Color(136, 24, 206);
	// final Color textFieldSelected = new Color(255,0,50);
	
	final Color cMove = new Color(255,220,90);
	final Color cAttack = new Color(255,0,50);
	
	final Color cRed = new Color(255,0,43);
	final Color cBlue = new Color(16,68,255);
	
	final Color cHealth = new Color(85,255,80);
	final Color cShield = new Color(39,86,155);
	
	final Color cCurrency = new Color(255,230,50);
	
	// Gui elements constants
	public final short maxInputLength = 18;
	
	// File path for sprites
	final String pathToSpriteSource = "sprites/";
	
	
	// Game Piece config variables //
	final int dmgFlashCountDown = 5;
	final int shieldRegen = 1;
	
	final int startCoinAmount = 30;
	final int incCoinAmountFortress = 10;
	final int goldDropGoldMine = 10;
	final float PlayerFortressHealth = 15;
	
	final float goldMineHealth = 5;
	// BaseTypes
	// type 0
	final int maxHealthType0 = 8;
	final int maxShieldType0 = 0;
	final int MovementRangeType0 = 5;
	// type 1
	final int maxHealthType1 = 10;
	final int maxShieldType1 = 4;
	final int MovementRangeType1 = 3;
	
	// TurretTypes
	// GunnerPiece
	final int baseTypeGunner = 1;
	final float dmgGunner = 3;
	final String nameGunner = "G";
	final boolean neededLOSGunner = true;
	// FlameThrowerPiece
	final int baseTypeFlameThrower = 1;
	final float dmgFlameThrower = 4;
	final String nameFlameThrower = "F";
	final boolean neededLOSFlameThrower = true;
	// DetonatorPiece
	final int baseTypeDetonator = 0;
	final float dmgDetonator = 4;
	final String nameDetonator = "D";
	final boolean neededLOSDetonator = true;
	// RocketLauncherPiece
	final int baseTypeRocketLauncher = 0;
	final float dmgRocketLauncher = 3;
	final String nameRocketLauncher = "R";
	final boolean neededLOSRocketLauncher = false;
	// ShotgunPiece
	final int baseTypeShotgun = 0;
	final float dmgShotgun = 3;
	final String nameShotgun = "S";
	final boolean neededLOSShotgun = true;
	// EMPPiece
	final int baseTypeEMP = 0;
	final float dmgEMP = 2;
	final String nameEMP = "E";
	final boolean neededLOSEMP = false;
	// SniperPiece
	final int baseTypeSniper = 0;
	final float dmgSniper = 5;
	final String nameSniper = "SC";
	final boolean neededLOSSniper = true;
	// TazerPiece
	final int baseTypeTazer = 0;
	final float dmgTazer = 2;
	final String nameTazer = "T";
	final boolean neededLOSTazer = false;
	// RapidElectroPiece
	final int baseTypeRapidElectro = 0;
	final float dmgRapidElectro = 2;
	final String nameRapidElectro = "RE";
	final boolean neededLOSRapidElectro = true;
	
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
