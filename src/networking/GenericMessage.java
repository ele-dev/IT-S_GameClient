package networking;

import java.io.Serializable;

/*
 * written by Elias Geiger
 */

public abstract class GenericMessage implements Serializable {

	private static final long serialVersionUID = 3282634498547557776L;
	
	// ----- Static class members ----- //
	
	// Message types //
	public static final int MSG_LOGIN = 1000;
	public static final int MSG_LOGIN_STATUS = 1001;
	public static final int MSG_LOGOUT = 1002;
	
	public static final int MSG_KEEP_ALIVE = 2000;
	
	public static final int MSG_JOIN_GAME = 3003;
	public static final int MSG_LEAVE_GAME = 3004;
	public static final int MSG_ACCOUNT_STATS = 3005;
	
	// ...
	
	// ----- non-static members ----- //
	
	protected int msgID;
	
	public GenericMessage()
	{
		this.msgID = 0;
	}
	
	public int getMessageID()
	{
		return this.msgID;
	}
	
}
