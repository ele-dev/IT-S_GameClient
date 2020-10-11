package networking;

/*
 * written by Elias Geiger
 * 
 * This class extends the abstract super class GenericMessage
 * It servers as a container for all signal messages.
 * It means all messages that do not contain any additional data but only an ID
 * can be stored and transmitted using instanced of this class
 * 
 * To avoid message parsing/handling errors it is important to make sure not to send
 * SignalMessage instances with IDs of non-signal messages
 * 
 */

public class SignalMessage extends GenericMessage {

	private static final long serialVersionUID = 5567355867911860016L;

	public SignalMessage(int id)
	{
		super();
		this.msgID = id;
	}
}
