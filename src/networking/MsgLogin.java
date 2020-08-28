package networking;

public class MsgLogin extends GenericMessage {

	private static final long serialVersionUID = -8509387207270110867L;
	
	private String username;
	private String passwordHash;
	private boolean playAsGuest;

	// Default constructor for login as guest
	public MsgLogin()
	{
		super();
		this.msgID = MSG_LOGIN;
		this.playAsGuest = true;
	}
	
	// Constructor for login with authentification
	public MsgLogin(String user, String passHash)
	{
		this();
		this.playAsGuest = false;
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
	
	public void setToGuest(boolean status) 
	{
		this.playAsGuest = status;
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
	
	public boolean isGuest()
	{
		return this.playAsGuest;
	}
}
