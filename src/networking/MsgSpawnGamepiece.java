package networking;

import GamePieces.GamePiece;

/*
 * written by Elias Geiger
 * 
 * This class defines the message that will be sent to client to notify 
 * him about a newly spawned game piece on the game map
 * 
 */

public class MsgSpawnGamepiece extends GenericMessage {

	private static final long serialVersionUID = 7499639481913568506L;
	
	// Attributes 
	private GamePiece spawnedGamePiece;
	
	// Constructor
	public MsgSpawnGamepiece()
	{
		// call the super class constuctor
		super();
		this.msgID = GenericMessage.MSG_SPAWN_GAMEPIECE;
		this.spawnedGamePiece = null;
	}
	
	public MsgSpawnGamepiece(GamePiece newPiece) 
	{
		this();
		this.spawnedGamePiece = newPiece;
	}
	
	// Getters 
	
	public GamePiece getSpawnedPiece() 
	{
		return this.spawnedGamePiece;
	}
}
