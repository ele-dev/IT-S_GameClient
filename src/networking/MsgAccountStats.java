package networking;

public class MsgAccountStats extends GenericMessage {
	
	private static final long serialVersionUID = -2431115718862124275L;
	
	private int matchesPlayed;
	private int accountBalance;
	
	// Default constructor that initializes stats with standard values
	public MsgAccountStats() 
	{
		super();
		this.msgID = MSG_ACCOUNT_STATS;
		this.matchesPlayed = 0;
		this.accountBalance = 0;
	}
	
	// Additional Constructor for passing stats directly
	public MsgAccountStats(int matches, int balance) 
	{
		this();
		this.matchesPlayed = matches;
		this.accountBalance = balance;
	}
	
	// Getters //
	
	public int getPlayedMatches() 
	{
		return this.matchesPlayed;
	}
	
	public int getAccountBalance() 
	{
		return this.accountBalance;
	}
}
