package networking;

/*
 * written by Elias Geiger
 * 
 * This class extends the abstract super class GenericMessage
 * 
 * It defines the Match info message that is sent from the server to clients
 * directly after they have found and joined a match.
 * It is supposed to feed clients with neccessary data about the match
 * e.g. the enemy players name and account stats, the initial game board, etc
 * 
 */

public class MsgMatchInfo extends GenericMessage {

	private static final long serialVersionUID = -2025417349888729111L;
	
	private String enemyPlayerName;
	// ...
	
	// Default Constructor
	public MsgMatchInfo() 
	{
		super();
		this.msgID = GenericMessage.MSG_MATCH_INFO;
		this.enemyPlayerName = "";
	}
	
	// Advanced Constructor taking the enemy's player name as argument
	public MsgMatchInfo(String enemyPlayer) 
	{
		this();
		this.enemyPlayerName = enemyPlayer;
	}
	
	// Getters 
	
	public String getEnemyPlayerName() 
	{
		return this.enemyPlayerName;
	}
}
