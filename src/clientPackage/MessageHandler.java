package clientPackage;

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
				
				// Store the match data from the message
				// ...
				
				// Navigate to the game panel where the actual game happens
				// ...
				
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
