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
				// ...
				
				break;
			}
			
			// ...
			
			default:
			{
				System.err.println("Received message of unknown type!");
				break;
			}
		}
	}
}
