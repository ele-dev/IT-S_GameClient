package networking;

import java.awt.Point;

/*
 * written by Elias Geiger
 * 
 * This class describes the message that is send over the server to the enemy
 * when the player attacks an enemy GamePiece/Figure with one of his
 * 
 */

public class MsgAttack extends GenericMessage {

	private static final long serialVersionUID = 373701968512335008L;

	// Attributes 
	private Point attackingPiece;
	private Point victimPos;
	
	// Constructors
	
	public MsgAttack()
	{
		super();
		this.msgID = GenericMessage.MSG_ATTACK;
		this.attackingPiece = null;
		this.victimPos = null;
	}
	
	public MsgAttack(Point attacker, Point victim) 
	{
		this();
		this.attackingPiece = attacker;
		this.victimPos = victim;
	}
	
	// Getters 
	
	public Point getAttackerPiece() 
	{
		return this.attackingPiece;
	}
	
	public Point getVicitimPos() 
	{
		return this.victimPos;
	}
}
