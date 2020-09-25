package game;

public class GameState {

	// static class members //
	private static String actingTeam = "";
	private static byte[][] gameField = new byte[3][3];
	
	// Method for clearing the game field
	public static void clearField() {
		for(int i = 0; i < 3; i++)
		{
			for(int k = 0; k < 3; k++)
			{
				gameField[i][k] = 0;
			}
		}
	}
	
	// Method for setting the team that is from now on allowed to take a move
	public static void setActingTeam(String team) {
		actingTeam = team;
	}
	
	// returns the acting team
	public static String getActingTeam() {
		return actingTeam;
	}
	
	// Overload function for changing the entire field at once or just a single cell of it
	public static void updateField(byte[][] newField) {
		gameField = newField;
	}
	
	public static void updateField(byte row, byte column, byte type) {
		
		// Avoid invalid paramters 
		if(row < 0 || row > 2 || column < 0 || column > 2 || type < 0 || type > 2) {
			return;
		}
		
		// udpate the given cell to the desired value
		gameField[row][column] = type;
	}
	
	// Returns the entire game field state
	public static byte[][] getCurrentFieldState() {
		return gameField;
	}
}
