package networking;

public class MsgFieldState extends GenericMessage {

	private static final long serialVersionUID = -8743992121832745006L;

	// message payload
	private byte[][] updatedField;		// 0 = empty | 1 = cross | 2 = circle
	
	// Constructors 
	public MsgFieldState()
	{
		super();
		this.msgID = GenericMessage.MSG_UPDATED_FIELD_STATE;
		this.updatedField = generateEmptyField();
	}
	
	public void changeField(byte row, byte column, byte value)
	{
		// Prevent invalid values
		if(row < 0 || row > 2 || column < 0 || column > 2 || value < 0 || value > 2) {
			return;
		}
		
		// change the field as desired
		this.updatedField[row][column] = value;
	}
	
	// Getter
	public byte[][] getField() 
	{
		return this.updatedField;
	}
	
	// static helper functions for field generation
	public static byte[][] generateEmptyField() 
	{
		byte[][] emptyField = new byte[3][3];
		
		for(int i = 0; i < 3; i++) 
		{
			for(int k = 0; k < 3; k++) 
			{
				emptyField[i][k] = 0;
			}
		}
		
		return emptyField;
	}
}
