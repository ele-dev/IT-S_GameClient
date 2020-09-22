package networking;

public class MsgSetTurn extends GenericMessage {

	private static final long serialVersionUID = -3536074724639138470L;

	// Message payload 
	private String team;
	
	// Constructors 
	public MsgSetTurn() 
	{
		super();
		this.msgID = GenericMessage.MSG_SET_TURN;
		this.team = "";
	}
	
	public MsgSetTurn(String team)
	{
		this();
		this.team = team;
	}
	
	// Getter for the message payload
	public String getTeam() 
	{
		return this.team;
	}
	
}
