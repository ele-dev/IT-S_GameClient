package networking;

/*
 * written by Elias Geiger
 * 
 * This message holds the information to describe a player move action on the game field
 * The moving Piece attribute indicates what character/figure is moving
 * The moveVector describes the position delta (points from origin/current position to the destination field)
 * This message is send to the server and forwarded to the enemy client everytime you click on the move button
 * 
 */

import java.awt.Point;

public class MsgMakeMove extends GenericMessage {

	private static final long serialVersionUID = 6015049815366864076L;

	// Attributes 
	private Point movingPiece;
	private Point moveVector;
	
	// Constructor 
	public MsgMakeMove() 
	{
		super();
		this.msgID = GenericMessage.MSG_MAKE_MOVE;
		this.movingPiece = null;
		this.moveVector = null;
	}
	
	public MsgMakeMove(Point movingPiecePos, Point targetField)
	{
		this();
		this.movingPiece = movingPiecePos;
		this.moveVector = targetField;
	}
	
	// Getters 
	
	public Point getMovingPlayerPos() 
	{
		return this.movingPiece;
	}
	
	public Point getTargetField() 
	{
		return this.moveVector;
	}
}
