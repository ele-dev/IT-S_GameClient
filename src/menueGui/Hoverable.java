package menueGui;

import java.awt.event.MouseEvent;

public interface Hoverable {

	void updateHover(MouseEvent e);
	void resetHover();
	
	boolean isHovered();
}
