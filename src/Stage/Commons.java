package Stage;

import java.awt.Color;

public interface Commons {
	
	// The game title
	public static final String gameTitle = "Crossfire PvP";
	
	public static final boolean editMap = false;
	public static final String mapName = "LargeMap";
  
	// Game money per match win
	public static final int winnerMoney = 100;
	
	// Refresh rates for timers 
	public static final int frametime = 10;
	
	// Menue gui elements dimensions
	public static final boolean fullscreen = false;
	public static final int textFieldHeight = 46;
	public static final int textFieldWidth = 210;
	
	// Important colors
	final Color loginScreenBackground = new Color(28, 26, 36);
	final Color homeScreenBackground = new Color(28, 26, 36);
	
	final Color buttonDefault = new Color(20, 20, 20);
	final Color buttonHover = Color.BLUE;
	final Color textFieldFocused = new Color(136, 24, 206);
	final Color buttonFocused = new Color(136, 24, 206);
	
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
	final String directoryToSprites = "sprites/";
	final String directoryToMaps = "sprites/Maps/";
	
	final String soundEffectDirectory = "SoundEffects/";
	
	
	
	// Game Piece config variables //
	final int dmgFlashCountDown = 5;
	final int shieldRegen = 1;
	
	final int startGoldAmount = 10;
	final int incGoldAmountFortress = 5;
	final int goldDropGoldMine = 5;
	final float PlayerFortressHealth = 15;
	
	final float goldMineHealth = 5;
	// BaseTypes
	// type 0
	final int maxHealthType0 = 8;
	final int maxShieldType0 = 2;
	final int MovementRangeType0 = 4;
	// type 1
	final int maxHealthType1 = 10;
	final int maxShieldType1 = 2;
	final int MovementRangeType1 = 4;
	// type 2
	final int maxHealthType2 = 10;
	final int maxShieldType2 = 4;
	final int MovementRangeType2 = 5;
	
	// TurretTypes
	// GunnerPiece
	final float dmgGunner = 3;
	final String nameGunner = "G";
	final boolean neededLOSGunner = true;
	// ShotgunPiece
	final float dmgShotgun = 3;
	final String nameShotgun = "S";
	final boolean neededLOSShotgun = true;
	// SniperPiece
	final float dmgSniper = 5;
	final String nameSniper = "SC";
	final boolean neededLOSSniper = true;
	
	// DetonatorPiece
	final float dmgDetonator = 4;
	final String nameDetonator = "D";
	final boolean neededLOSDetonator = true;
	// FlameThrowerPiece
	final float dmgFlameThrower = 5;
	final String nameFlameThrower = "F";
	final boolean neededLOSFlameThrower = true;
	// RocketLauncherPiece
	final float dmgRocketLauncher = 3;
	final String nameRocketLauncher = "R";
	final boolean neededLOSRocketLauncher = false;
	
	// EMPPiece
	final float dmgEMP = 2;
	final String nameEMP = "E";
	final boolean neededLOSEMP = false;
	// RapidElectroPiece
	final float dmgRapidElectro = 2;
	final String nameRapidElectro = "RE";
	final boolean neededLOSRapidElectro = true;
	// TazerPiece
	final float dmgTazer = 3;
	final String nameTazer = "T";
	final boolean neededLOSTazer = false;
	
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
