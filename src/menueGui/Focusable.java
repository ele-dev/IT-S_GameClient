package menueGui;

/*
 * written by Elias Geiger
 * 
 * Interface for focusable gui elements
 */

public interface Focusable {
	
	public abstract void focusNow(boolean status);
	public abstract boolean isFocused();
	
}
