package clientPackage;

import java.awt.Color;

import GamePieces.GamePiece;
import Stage.BoardRectangle;
import Stage.ProjectFrame;
import Stage.StagePanel;
import menueGui.GameState;

/*
 * written by Elias Geiger
 * 
 * This class is intented do message handling and is only used in a static context
 * Every time the listener thread receives a new message the method inside this class 
 * is called to identify the message type and to decide how to react or what to do with
 * the data insided the newly received message.
 * 
 * This class was mainly introduced to outsource the message handling and not to overload other classes
 * since the handleMessage() Method is relatively long and was freqently extended during development
 * 
 */

import networking.*;

public class MessageHandler {

	public static void handleMessage(GenericMessage msg)
	{
		// Read message id to get the type of the message
		int id = msg.getMessageID();
		
		switch(id)
		{
			// Message that contains game statistics about the player account
			case GenericMessage.MSG_ACCOUNT_STATS:
			{
				// First parse the message into the right format
				MsgAccountStats accountStats = (MsgAccountStats) msg;
				
				// Now store the received stats from the message
				// ...
				
				// Show stats on the console for debugging
				System.out.println("Received account stats from the server");
				System.out.println("Played Matches: " + accountStats.getPlayedMatches());
				System.out.println("Account Balance: " + accountStats.getAccountBalance());
				
				break;
			}
			
			// A Signal Message that says a match was found
			case GenericMessage.MSG_FOUND_MATCH:
			{
				// Ignore this message if the player wasn't searching for a match
				if(!GameState.isSearching) {
					System.err.println("Received invalid match found message from the server!");
					break;
				}
				
				System.out.println("Match was found --> joining match ...");
				
				// First update the players state flags
				GameState.isSearching = false;
				GameState.isIngame = true;
				
				break;
			}
			
			// Message provides player with neccessary data before the match begins
			case GenericMessage.MSG_MATCH_INFO:
			{
				// Ignore this message if the player isn't in game at the moment
				if(!GameState.isIngame) {
					System.err.println("Received invalid match info message from the server!");
					break;
				}
				
				// First parse the message into the right format
				MsgMatchInfo matchInfo = (MsgMatchInfo) msg;
				
				// Store the match data from the message
				GameState.enemyName = matchInfo.getEnemyPlayerName();
				switch(matchInfo.getTeamColor()) 
				{
					case 1:
					{
						GameState.myTeamColor = Color.BLUE;
						GameState.enemyTeamColor = Color.RED;
						break;
					}
				
					case 2:
					{
						GameState.myTeamColor = Color.RED;
						GameState.enemyTeamColor = Color.BLUE;
						break;
					}
					
					default:
					{
						GameState.myTeamColor = Color.GRAY;
						break;
					}
				}
				
				// If your are team blue then it's your turn at first
				if(GameState.myTeamColor == Color.BLUE) {
					GameState.myTurn = true;
				} else {
					GameState.myTurn = false;
				}
				
				System.out.println("Received Match data --> navigating to stage panel");
				
				// Navigate to the game panel where the actual game happens
				ProjectFrame.homePanel.closePanel();
				ProjectFrame.stagePanel.setVisible(true);
				
				break;
			}
			
			// This message notifies the player that the match is over because the enemy has 
			// left the game volunterally
			case GenericMessage.MSG_ENEMY_SURRENDER:
			{
				// Ignore this message if the player isn't ingame at the moment
				if(!GameState.isIngame) {
					System.err.println("Received invalid enemy surrender message from the server!");
					break;
				}
				
				// show popup message informing that the match is over because the enemy left the game
				System.out.println("The enemy surrendered --> leaving match");
				// JOptionPane.showMessageDialog(null, "Match is over. Enemy has left the game");
				
				// Update the player states
				GameState.enemySurrender = true;
				GameState.isIngame = false;
				GameState.isSearching = false;
				
				// Run the winning detection
				StagePanel.checkIfSomeOneWon();
				
				break;
			}
			
			// This message is received from the server as soon as the enemy has finished his turn
			// When this message is received then our own turn begins 
			case GenericMessage.MSG_BEGIN_TURN:
			{
				// Ignore this message if the player isn't ingame at the moment
				if(!GameState.isIngame) {
					System.err.println("Received invalid begin turn message from the server!");
					break;
				}
				
				// Update the global state variable and show info box 
				GameState.myTurn = true;
				
				// Now update the GUI to switch the turn graphically and process all connected events
				ProjectFrame.stagePanel.updateTurn();
				
				break;
			}
			
			// This message is received when the enemy is acting and has moved one of his Game Pieces
			// to a new position on the game board
			case GenericMessage.MSG_MAKE_MOVE:
			{
				// Ignore message when we aren't ingame or if it's our turn at the moment
				if(!GameState.isIngame || GameState.myTurn) {
					System.err.println("Received invalid make move message from the server!");
					break;
				}
				
				// Coerce the message into the right format
				MsgMakeMove moveMsg = (MsgMakeMove) msg;
				
				// check for all required attributes to be present (not null)
				if(moveMsg.getMovingPlayerPos() == null || moveMsg.getTargetField() == null) {
					System.err.println("Move message is missing required attributes!");
					break;
				}
				
				// Find the moving GamePiece and it's destination Field on the local game map 
				GamePiece movingGP = GamePiece.getGamePieceFromCoords(moveMsg.getMovingPlayerPos());
				BoardRectangle destinationBR = BoardRectangle.getBoardRectFromCoords(moveMsg.getTargetField());
				
				// Execute the move of the enemy game piece 
				if(destinationBR != null && movingGP != null) {
					movingGP.startMove(destinationBR);
					System.out.println("Executed enemy move action");
				} else {
					System.err.println("Could not execute enemy move action!");
				}
				
				// For debugging
				System.out.println("movingGP: row=" + movingGP.boardRect.row + "column=" + movingGP.boardRect.column);
				System.out.println("destinationBR: row=" + destinationBR.row + "column="+ destinationBR.column);
				
				break;
			}
			
			// This message is received when the enemy is acting and has attacked one of our GamePieces
			// with one of his 
			case GenericMessage.MSG_ATTACK:
			{
				// Ignore message when we aren't ingame or if it's our turn at the moment
				if(!GameState.isIngame || GameState.myTurn) {
					System.err.println("Received invalid make move message from the server!");
					break;
				}
				
				// Coerce the message into the right format
				MsgAttack attackMsg = (MsgAttack) msg;
				
				// check for all required attributes to be present (not null)
				if(attackMsg.getAttackerPiece() == null || attackMsg.getVicitimPos() == null) {
					System.err.println("Attack message is missing required parameters");
					break;
				}
				
				// Find the attacker gmaePiece and the victim game piece on the local game map
				GamePiece attackerGP = GamePiece.getGamePieceFromCoords(attackMsg.getAttackerPiece());
				GamePiece victimGP = GamePiece.getGamePieceFromCoords(attackMsg.getVicitimPos());
				
				// Execute the enemy's attack
				if(attackerGP != null && victimGP != null) {
					// ...
				} else {
					System.err.println("Could not execute the enemy attack action!");
				}
				
				// For debugging
				System.out.println("attackerGP: row=" + attackerGP.boardRect.row + " column=" + attackerGP.boardRect.column);
				System.out.println("victimGP: row=" + victimGP.boardRect.row + " column=" + victimGP.boardRect.column);
				
				// Check if someone has won the game 
				StagePanel.checkIfSomeOneWon();
				
				break;
			}
			
			default:
			{
				System.err.println("Received message of unknown type!");
				break;
			}
		}
	}
}
