package networking;

import java.io.Serializable;

/*
 * written by Elias Geiger
 * 
 * This abstract class is super class of all specific message classes and
 * is intended to define basic attributes and functionalities that all messages have incommon
 * It implements Serializable interface because the concept of the network protocol is based on 
 * classes that represent different types of messages which need to be instantiated and serialized 
 * using Object I/O Streams to be sent over the tcp connection
 * 
 * The constant IDs for all existing message types are also defined in this class
 * 
 */

public abstract class GenericMessage implements Serializable {

	private static final long serialVersionUID = 3282634498547557776L;
	
	// ----- Static class members ----- //
	
	// Message types //
	public static final int MSG_SET_TURN = 1001;
	public static final int MSG_UPDATED_FIELD_STATE = 1002;
	
	public static final int MSG_JOIN_TEAM = 2001;
	public static final int MSG_LEAVE_GAME = 2002;
	public static final int MSG_SERVER_FULL = 2003;
	
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
