package networking;

public class MsgLogin extends GenericMessage {

	private static final long serialVersionUID = -8509387207270110867L;
	
	private String username;
	private String passwordHash;

	public MsgLogin()
	{
		super();
		this.msgID = MSG_LOGIN;
	}
	
	public MsgLogin(String user, String passHash)
	{
		this();
		this.username = user;
		this.passwordHash = passHash;
	}
	
	// Setters 
	public void setUsername(String user)
	{
		this.username = user;
		return;
	}
	
	public void setPasswordHash(String passHash)
	{
		this.passwordHash = passHash;
		return;
	}
	
	// Getters 
	public String getUsername()
	{
		return this.username;
	}
	
	public String getPasswordHash()
	{
		return this.passwordHash;
	}
}
