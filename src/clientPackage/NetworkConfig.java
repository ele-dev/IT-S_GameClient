package clientPackage;

/*
 * writte by Elias Geiger
 * 
 * This interface doesn't contain any real functionality
 * It simply stores all network related configuraiton variables for the application 
 * so that they can be accessed from anywhere in the code but never be changed
 * 
 */

public interface NetworkConfig {
	
	// Global network configuration constants
	public static final String serverAddress = "ele-server.de";
	public static final int serverPort = 50044;
	public static final String hashingSalt = "g_p8nrY3";
	
	// timing values
	public static final int loginTimeout = 3000;
	public static final int registerTimeout = 6000;
	public static final int clientSocketTimeout = 1000;
	public static final int threadSleepDelay = 30;
	// ...
	
}
