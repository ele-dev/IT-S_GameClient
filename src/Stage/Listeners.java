package Stage;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Listeners {

	// Listeners
	public static KeyListener keyListener;
	public static MouseListener mouseListener;
	public static MouseMotionListener mouseMotionListener;
	
	// single instance
	private static Listeners listener = new Listeners();
	
	// Constructor
	public Listeners() {
		
	}
	
	public static Listeners getListenerInstance() {
		return listener;
	}
	
	
}
