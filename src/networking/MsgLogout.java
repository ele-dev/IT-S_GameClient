package networking;

public class MsgLogout extends GenericMessage {

	private static final long serialVersionUID = -6857382937812837706L;

	public MsgLogout()
	{
		super();
		this.msgID = MSG_LOGOUT;
	}
	
}
