package Buttons;

import java.awt.Color;

public abstract class InterfaceButton extends GenericButton{
	
	public InterfaceButton(int startx, int starty, int w, Color cHover, String name) {
		super(startx, starty, w, 75, name, new Color(20,20,20), cHover, new Color(10,10,10),40);
	}
	
	public abstract boolean tryPress();
	
	
}
