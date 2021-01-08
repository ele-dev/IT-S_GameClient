package networking;

import java.awt.Color;
import java.awt.Point;

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
	private String gamePieceClass;
	private Point fieldCoordinates;
	private Color teamColor;
	
	// Constructor
	public MsgSpawnGamepiece()
	{
		// call the super class constructor
		super();
		this.msgID = GenericMessage.MSG_SPAWN_GAMEPIECE;
		this.fieldCoordinates = new Point(1, 1);		// default field coords
		this.teamColor = Color.BLUE;					// default team color
		this.gamePieceClass = "undefined";				// default GP class 
	}
	
	public MsgSpawnGamepiece(String gpClass, Point spawnPoint, Color teamColor) 
	{
		this();
		this.fieldCoordinates = spawnPoint;
		this.teamColor = teamColor;
		this.gamePieceClass = gpClass;
	}
	
	// Setters
	
	public void setGamePieceClass(String gpClass) 
	{
		this.gamePieceClass = gpClass;
	}
	
	// Getters 
	
	public String getGamePieceClass() 
	{
		return this.gamePieceClass;
	}
	
	public Point getFieldCoordinates() 
	{
		return this.fieldCoordinates;
	}
	
	public Color getTeamColor() 
	{
		return this.teamColor;
	}
}
