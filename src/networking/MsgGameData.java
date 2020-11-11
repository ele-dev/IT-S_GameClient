package networking;

/*
 * written by Elias Geiger
 * 
 * This is class extends the abstract super class GenericMessage
 * 
 * It defines the message type the holds global game information
 * For example how many player are currently online, etc
 * 
 */

public class MsgGameData extends GenericMessage {

	private static final long serialVersionUID = -3252814057448683982L;

	// Attributes 
	private int onlinePlayerCount;
	
	// Constructors 
	public MsgGameData()
	{
		super();
		this.msgID = GenericMessage.MSG_GAME_DATA;
		this.onlinePlayerCount = 0;
	}
	
	public MsgGameData(int playerCount) 
	{
		this();
		this.onlinePlayerCount = playerCount;
	}
	
	// Getters 
	public int getOnlinePlayerCount() 
	{
		return this.onlinePlayerCount;
	}
}
