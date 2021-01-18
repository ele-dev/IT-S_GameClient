package networking;

/*
 * written by Elias Geiger
 * 
 * This class extends the abstract super class GenericMessage
 * 
 * It defines the Register status message that the server sends as a respond 
 * to a client that sent a register message to create a new account
 * The message contains the success status of the register attempt 
 * and also an short error description to inform the client about the reason 
 * for failure
 * 
 */

public class MsgRegisterStatus extends GenericMessage {

	private static final long serialVersionUID = -6008976298688592927L;

	private boolean status;
	private String description;
	
	// Default constructor
	public MsgRegisterStatus() 
	{
		super();
		this.msgID = GenericMessage.MSG_REGISTER_STATUS;
		this.status = false;
		this.description = "";
	}
	
	// Constructor for creating a message with all necessary payload (status, description)
	public MsgRegisterStatus(boolean success, String desc) 
	{
		this();
		this.status = success;
		this.description = desc;
	}
	
	// Getters 
	public boolean getSuccessStatus() 
	{
		return this.status;
	}
	
	public String getDescription() 
	{
		return this.description;
	}
}
