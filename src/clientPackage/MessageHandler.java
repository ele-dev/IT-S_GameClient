package clientPackage;

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
				GameState.teamColor = matchInfo.getTeamColor();
				
				if(GameState.teamColor == 1) {
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
			
			default:
			{
				System.err.println("Received message of unknown type!");
				break;
			}
		}
	}
}
