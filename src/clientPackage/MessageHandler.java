package clientPackage;

import networking.*;

public class MessageHandler {

	public static void handleMessage(GenericMessage msg)
	{
		// Read message id to get the type of the message
		int id = msg.getMessageID();
		
		switch(id)
		{
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
			
			default:
			{
				System.err.println("Received message of unknown type!");
				break;
			}
		}
	}
}
