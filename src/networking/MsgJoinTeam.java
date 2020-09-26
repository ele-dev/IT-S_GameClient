package networking;

public class MsgJoinTeam extends GenericMessage {

	private static final long serialVersionUID = 8506200918669533759L;

	// payload
	private byte teamId;	// 0 -> unassigned | 1 -> cross | 2 -> circle
	
	// Constructors
	
	public MsgJoinTeam()
	{
		super();
		this.msgID = GenericMessage.MSG_JOIN_TEAM;
		this.teamId = 0;	// intialize without a team
	}
	
	public MsgJoinTeam(byte team) 
	{
		this();
		this.teamId = team;
	}
	
	public byte getTeam() 
	{
		return this.teamId;
	}
}
