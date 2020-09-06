package networking;

/*
 * written by Elias Geiger 
 * 
 * This class extends the abstract super class GenericMessage
 * It is does not carry any additional payload and is used by the client
 * to tell the server that the player wants to play a quick match (random enemy player)
 * 
 */

public class MsgJoinQuickmatch extends GenericMessage {

	private static final long serialVersionUID = -6769279859813119285L;
	
	public MsgJoinQuickmatch()
	{
		super();
		this.msgID = MSG_JOIN_QUICKMATCH;
	}
}
