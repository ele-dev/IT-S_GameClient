package networking;

/*
 * written by Elias Geiger
 * 
 * This abstract class is super class of all specific message classes and
 * is intended to define basic attributes and functionalities that all messages have in common
 * It implements the Serializable interface because the concept of the network protocol is based on 
 * classes that represent different types of messages which need to be instantiated and serialized 
 * using Object I/O Streams to be sent over the TCP connection
 * 
 * The constant IDs for all existing message types are also defined in this class
 * 
 */

import java.io.Serializable;

public abstract class GenericMessage implements Serializable {

	private static final long serialVersionUID = 3282634498547557776L;
	
	// ----- Static class members ----- //
	
	// Message types //
	public static final int MSG_LOGIN = 1000;
	public static final int MSG_LOGIN_STATUS = 1001;
	public static final int MSG_LOGOUT = 1002;
	
	public static final int MSG_REGISTER = 1010;
	public static final int MSG_REGISTER_STATUS = 1011;
	
	public static final int MSG_KEEP_ALIVE = 2000;
	
	public static final int MSG_GAME_DATA = 3004;
	public static final int MSG_ACCOUNT_STATS = 3005;
	
	public static final int MSG_JOIN_QUICKMATCH = 3010;
	public static final int MSG_ABORT_MATCH_SEARCH = 3011;
	public static final int MSG_FOUND_MATCH = 3012;
	public static final int MSG_MATCH_INFO = 3013;
	public static final int MSG_ENEMY_SURRENDER = 3014;
	public static final int MSG_LEAVE_MATCH = 3015;
	
	public static final int MSG_BEGIN_TURN = 3020;
	public static final int MSG_END_TURN = 3021;
	public static final int MSG_MAKE_MOVE = 3022;
	public static final int MSG_ATTACK = 3023;
	// public static final int MSG_USE_ABILITY = 3024;
	public static final int MSG_SPAWN_GAMEPIECE = 3025;
	
	// ...
	
	// ----- non-static members ----- //
	
	protected int msgID;
	
	public GenericMessage()
	{
		this.msgID = 0;
	}
	
	public final int getMessageID() 
	{
		return this.msgID;
	}
	
}
