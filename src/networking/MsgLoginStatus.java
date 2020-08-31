package networking;

/*
 * written by Elias Geiger
 * 
 * This class extends the abstract super class GenericMessage
 * 
 * It defines the Login Status message which is send from server to clients to
 * respond to their Login Requests 
 * 
 */

public class MsgLoginStatus extends GenericMessage {

	private static final long serialVersionUID = -4884148962350653015L;
	
	private boolean status;
	private String assignedPlayerName;
	
	// Default Constructor for answering account login requests
	public MsgLoginStatus(boolean status)
	{
		super();
		this.msgID = MSG_LOGIN_STATUS;
		this.status = status;
		this.assignedPlayerName = "";
	}
	
	// Additional Constructor for answering guest login requests
	public MsgLoginStatus(boolean status, String name) 
	{
		this(status);
		this.assignedPlayerName = name;
	}
	
	// Getter 
	public boolean success()
	{
		return this.status;
	}
	
	public String getAssignedName() 
	{
		return this.assignedPlayerName;
	}

}
