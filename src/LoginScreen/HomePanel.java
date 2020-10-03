package LoginScreen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import Buttons.Button;
import Stage.Commons;

@SuppressWarnings("serial")
public class HomePanel extends JPanel {
	
	// Dimension and background color properties
	private int w, h;
	private Color bgColor;
	
	// Timers 
	private Timer tFrameRate;
	private Timer tUpdateRate;
	
	// Gui elements inside this panel
	private Button logoutButton = new Button(800, 500, 100, 50, "Logout");
	private Button quickMatchButton = new Button(800, 200, 120, 50, "Quickmatch");
	
	// Listeners

	public HomePanel(int x, int y) {
		
		// init the gui relevant variables
		this.w = Commons.wf;
		this.h = Commons.hf;
		this.bgColor = new Color(28, 26, 36);
		this.setBounds(x, y, w, h);
		
		// add listeners
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
		addKeyListener(new KL());
		
		// Timer for painting/redrawing
		this.tFrameRate = new Timer(Commons.frametime, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		this.tFrameRate.setRepeats(true);
		this.tFrameRate.start();
		
		// Timer for updating
		this.tUpdateRate = new Timer(Commons.frametime, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// ...
			}
		});
		this.tUpdateRate.setRepeats(true);
		this.tUpdateRate.start();
	}
	
	// Drawing function 
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw the colored background
		g2d.setColor(this.bgColor);
		g2d.fillRect(0, 0, this.w, this.h);
		
		// Next draw some text
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 30));
		g2d.drawString("Home screen", 750, 300);
		
		// Draw the buttons
		this.logoutButton.drawButton(g2d);
		this.quickMatchButton.drawButton(g2d);
		
		// Draw additonal stuff
		// ...
	}
	
	// Listener classes
	private class ML implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// React on the mouse click
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}
	
	// Key listener for typing text into textfields
	private class KL implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			// ...
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}
		
	}
	
	// Mouse motion listener for updating the hover status of GUI elements
	private class MML implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// Update the hover states of the buttons
			logoutButton.updateHover(arg0);
			quickMatchButton.updateHover(arg0);
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// Update the hover states of the buttons
			logoutButton.updateHover(arg0);
			quickMatchButton.updateHover(arg0);
		}
	}
}