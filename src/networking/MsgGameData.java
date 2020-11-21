package networking;

/*
 * written by Elias Geiger
 * 
 * This is class extends the abstract super class GenericMessage
 * 
 * It defines the message type the holds global game information
 * For example how many player are currently online, how many running matches, etc
 * 
 */

public class MsgGameData extends GenericMessage {

	private static final long serialVersionUID = -3252814057448683982L;

	// Attributes 
	// Negative values mean they should be ignored
	private int onlinePlayerCount;
	private int runningMatchCount;
	
	// Constructors 
	public MsgGameData()
	{
		super();
		this.msgID = GenericMessage.MSG_GAME_DATA;
		this.onlinePlayerCount = 0;
		this.runningMatchCount = 0;
	}
	
	public MsgGameData(int playerCount, int matchCount) 
	{
		this();
		this.onlinePlayerCount = playerCount;
		this.runningMatchCount = matchCount;
	}
	
	// Getters 
	public int getOnlinePlayerCount() 
	{
		return this.onlinePlayerCount;
	}
	
	public int getRunningMatchCount() 
	{
		return this.runningMatchCount;
	}
}
