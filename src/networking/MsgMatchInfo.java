package networking;

/*
 * written by Elias Geiger
 * 
 * This class extends the abstract super class GenericMessage
 * 
 * It defines the Match info message that is sent from the server to clients
 * directly after they have found and joined a match.
 * It is supposed to feed clients with necessary data about the match
 * e.g. the enemy players name and account stats, the initial game board, etc
 * 
 */

public class MsgMatchInfo extends GenericMessage {

	private static final long serialVersionUID = -2025417349888729111L;
	
	private String enemyPlayerName;
	private byte yourTeamColor;			// 1 -> blue  2 -> red
	// ...
	
	// Default Constructor
	public MsgMatchInfo() 
	{
		super();
		this.msgID = GenericMessage.MSG_MATCH_INFO;
		this.enemyPlayerName = "";
		this.yourTeamColor = 1;		// default team is blue (1)
	}
	
	// Advanced Constructor taking the enemy's player name and team color id as arguments
	public MsgMatchInfo(String enemyPlayer, byte teamColor) 
	{
		this();
		this.enemyPlayerName = enemyPlayer;
		this.yourTeamColor = teamColor;
	}
	
	// Getters 
	
	public String getEnemyPlayerName() 
	{
		return this.enemyPlayerName;
	}
	
	public byte getTeamColor() 
	{
		return this.yourTeamColor;
	}
}
