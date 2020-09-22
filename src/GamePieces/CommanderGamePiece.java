package GamePieces;



import Stage.BoardRectangle;
import Stage.StagePanel;

public abstract class CommanderGamePiece extends GamePiece{
	protected int maxAbilityCharge,abilityCharge;
	public CommanderGamePiece(boolean isEnemy, String name, BoardRectangle boardRect, float dmg, int maxAbilityCharge, int baseTypeIndex) {
		super(isEnemy, name, boardRect, dmg, baseTypeIndex);
		this.maxAbilityCharge = maxAbilityCharge;
		this.abilityCharge = maxAbilityCharge;
		
	}
	
	public void showPossibleAbilities(BoardRectangle curHoverBoardRectangle) {
		if(isSelected) {
			for(BoardRectangle curBR : StagePanel.boardRectangles) {
				curBR.isPossibleAbility = false;	
			}
			updatePossibleAbilities(curHoverBoardRectangle);	
		}
	}
	
	public int getMaxAbilityCharge() {
		return maxAbilityCharge;
	}
	
	public int getAbilityCharge() {
		return abilityCharge;
	}
	
	public abstract void updatePossibleAbilities(BoardRectangle curHoverBoardRectangle);
	
	public abstract void startAbility(BoardRectangle targetBoardRectangle);
	
	public void regenAbilityCharge() {
		abilityCharge = abilityCharge+1 < maxAbilityCharge?abilityCharge+1:maxAbilityCharge;
	}
	
}
