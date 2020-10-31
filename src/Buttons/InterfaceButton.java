package Buttons;

import java.awt.Color;

public abstract class InterfaceButton extends GenericButton{
	
	public InterfaceButton(int startx, int starty, int w, Color cHover, String name) {
		super(startx, starty, w, 75, name, new Color(20,20,20), cHover, 40);
		isActive = false;
	}
	
	public abstract boolean tryPress();
	
	
}
