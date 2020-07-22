package networking;

public class MsgLoginStatus extends GenericMessage {

	private static final long serialVersionUID = -4884148962350653015L;
	
	private boolean status;
	
	public MsgLoginStatus(boolean status)
	{
		super();
		this.msgID = MSG_LOGIN_STATUS;
		this.status = status;
	}
	
	// Getter 
	public boolean success()
	{
		return this.status;
	}

}
