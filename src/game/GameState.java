package game;

public class GameState {

	private static String actingTeam = "cross";
	private static byte[][] gameField = new byte[3][3];
	
	public static void clearField() {
		for(int i = 0; i < 3; i++)
		{
			for(int k = 0; k < 3; k++)
			{
				gameField[i][k] = 0;
			}
		}
	}
	
	public static void setActingTeam(String team) {
		actingTeam = team;
	}
	
	public static String getActingTeam() {
		return actingTeam;
	}
	
	public static void updateField(byte[][] newField) {
		gameField = newField;
	}
	
	public static byte[][] getCurrentFieldState() {
		return gameField;
	}
}
