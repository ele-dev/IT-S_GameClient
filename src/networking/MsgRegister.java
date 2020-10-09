package networking;

/*
 * written by Elias Geiger
 * 
 * This class extends the abstract super class GenericMessage
 * It defines the register message that a client sends to the server when he presses
 * the 'register new account' button
 * This message contains user data: playername, email address, password
 * These values are validated and processed by the server
 * As soon as the server knows wether the new account was created successfully or not
 * it responds  with a status message that contains the result and possible error description
 * 
 */

public class MsgRegister extends GenericMessage {
	
	private static final long serialVersionUID = 5966251589025718794L;
	
	private String username;
	private String email;
	private String passwordHash;
	
	// Default constructor sets default values
	public MsgRegister() 
	{
		super();
		this.msgID = GenericMessage.MSG_REGISTER;
		this.username = "newUser";
		this.email = "example-mail@example.com";
		this.passwordHash = "password";
	}
	
	// Constructor for creating message with neccesary payload
	public MsgRegister(String user, String mail, String pass) 
	{
		this();
		this.username = user;
		this.email = mail;
		this.passwordHash = pass; 
	}
	
	// Getters 
	public String getUsername() 
	{
		return this.username;
	}
	
	public String getEmail() 
	{
		return this.email;
	}
	
	public String getPasswordHash() 
	{
		return this.passwordHash;
	}
}
