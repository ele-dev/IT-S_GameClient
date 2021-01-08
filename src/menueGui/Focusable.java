package menueGui;

/*
 * written by Elias Geiger
 * 
 * Interface for focusable GUI elements
 */

public interface Focusable {
	
	void focusNow(boolean status);
	boolean isFocused();
	
}
