package Buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class InterfaceButton extends GenericButton{
	
	public InterfaceButton(int startx, int starty, int w, Color cHover, String name) {
		super(startx, starty, w, 75, name, new Color(20,20,20), cHover, 40);
		isActive = false;
	}
	
	public abstract boolean tryPress();
	
	
}
