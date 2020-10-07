package menueGui;

/*
 * written by Elias Geiger
 * 
 * This is the homescreen that appears after successfull login
 * It contains GUI elements for different purposes:
 * e.g. player stats display, interaction buttons for joining a match, logging out,
 * searching online players, configuring personal loadout, etc.
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import Stage.Commons;
import Stage.ProjektFrame;

@SuppressWarnings("serial")
public class HomePanel extends GuiPanel {
	
	// Gui elements inside this panel (buttons, text fields, etc.)
	private Button logoutButton = new Button(800, 500, 100, 50, "Logout");
	private Button quickMatchButton = new Button(1200, 200, 130, 50, "Quickmatch");
	private Button abortMatchSearchButton = new Button(1200, 350, 130, 50, "Abort Search");

	// Constructor takes initial position
	public HomePanel() {
		
		// call constructor from super class
		super();
		
		// Set the desired background color for the panel
		this.bgColor = Commons.homeScreenBackground;
		
		// disable the search abortion button at the beginning
		this.abortMatchSearchButton.setEnabled(false);
	}
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		// Next draw some text
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 30));
		g2d.drawString("Home screen", 750, 300);
		
		// Draw the buttons
		this.logoutButton.draw(g2d);
		this.quickMatchButton.draw(g2d);
		this.abortMatchSearchButton.draw(g2d);
		
		// Draw additonal stuff
		// ...
	}
	
	// Method for processing a click on the logout button
	private void tryLogout() {
		
		// Logout button click event
		if(this.logoutButton.isHover() && ProjektFrame.conn.isLoggedIn()) {
			System.out.println("--> Logout");
			
			// run the logout routine and return to the login panel
			ProjektFrame.conn.logout();
			this.setVisible(false);
			ProjektFrame.loginPanel.setVisible(true);
		}
	}
	
	// Method for processing a click on the join quickmatch button
	private void tryQuickmatchJoin() {
		
		// Quickmatch join button click event
		if(this.quickMatchButton.isHover() && ProjektFrame.conn.isLoggedIn()) {
			System.out.println("--> Join quickmatch (waiting queue)");
			
			// Enable the match search abortion button and disable this one
			this.quickMatchButton.setEnabled(false);
			this.abortMatchSearchButton.setEnabled(true);
			
			// Update the state value that indicates, we are waiting for a player now
			// ...
		}
	}
	
	private void tryAbortMatchSearch() {
		
		// Abort button click event
		if(this.abortMatchSearchButton.isHover() && ProjektFrame.conn.isLoggedIn()) {
			System.out.println("--> Abort quick-matchmaking");
			
			// Enable the quick match search button and disable this one
			this.abortMatchSearchButton.setEnabled(false);
			this.quickMatchButton.setEnabled(true);
			
			// update state value that indicates, we aren't waiting for a player anymore
			// ...
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// React on the mouse click
		tryQuickmatchJoin();
		tryAbortMatchSearch();
		tryLogout();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// Update the hover states of the buttons
		logoutButton.updateHover(e);
		quickMatchButton.updateHover(e);
		abortMatchSearchButton.updateHover(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Update the hover states of the buttons
		logoutButton.updateHover(e);
		quickMatchButton.updateHover(e);
		abortMatchSearchButton.updateHover(e);
	}
}
