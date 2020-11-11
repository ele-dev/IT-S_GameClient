package menueGui;

import java.awt.Color;

/*
 * written by Elias Geiger
 * 
 * This class is only used in a static context, mainly for storing 
 * global state variables or data that is required for application
 * control and menue redirection during runtime
 * 
 */

public class GameState {

	// state variables // 
	public static boolean isIngame = false;
	public static boolean isSearching = false;
	
	public static String registerStatusDescription = "";
	public static boolean userAccountVerified = false;
	
	// Global game data // 
	public static int onlinePlayers = 0;
	
	// User account stats //
	public static int playedMatches = 0;
	public static int money = 0;
	
	// Ingame related variables //
	public static String enemyName = "";
	public static boolean enemySurrender = false;
	// public static byte teamColor = 1;		// 1 -> blue  2 -> red
	public static Color myTeamColor = null;
	public static Color enemyTeamColor =  null;
	public static boolean myTeamIsRed = false;
	public static boolean myTurn = false;
}
