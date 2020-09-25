package clientPackage;

import game.GameState;
import main.MainJFrame;

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
			case GenericMessage.MSG_SET_TURN: 
			{
				// Coerce the message into the right format
				MsgSetTurn turnMessage = (MsgSetTurn)msg;
				
				// Store the team in the game state class
				GameState.setActingTeam(turnMessage.getTeam());
				System.out.println("It's team " + turnMessage.getTeam() + " turn now");
				
				break;
			}
			
			case GenericMessage.MSG_UPDATED_FIELD_STATE:
			{
				// Coerce the message into the right format
				MsgFieldState fieldMessage = (MsgFieldState)msg;
				
				// Store the updated field state locally
				GameState.updateField(fieldMessage.getField());
				System.out.println("Received updated game field state from server");
				
				// Repaint the GUI to display the recieved updated game field
				if(MainJFrame.stagePanel != null) {
					MainJFrame.stagePanel.repaint();
				}
				
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
