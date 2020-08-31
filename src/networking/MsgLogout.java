package networking;

/*
 * writte by Elias Geiger 
 * 
 * This class extends the abstract super class GenericMessage
 * It defines the Logout message which is used by clients to inform the server
 * about a logout and typically sent just before disconnect and applicaiton closup
 * It's main purpose is to help the server to keep track of the amount of clients that are 
 * active and reachable at the moment
 * 
 */

public class MsgLogout extends GenericMessage {

	private static final long serialVersionUID = -6857382937812837706L;

	public MsgLogout()
	{
		super();
		this.msgID = MSG_LOGOUT;
	}
	
}
