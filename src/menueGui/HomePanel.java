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
import Stage.ProjectFrame;
import networking.GenericMessage;
import networking.SignalMessage;


@SuppressWarnings("serial")
public class HomePanel extends GuiPanel {
	
	// Gui elements inside this panel (buttons, text fields, etc.)
	private Button logoutButton = new Button(800, 500, 100, 50, "Logout");
	private Button quickMatchButton = new Button(1200, 200, 130, 50, "Quickmatch");
	private Button abortMatchSearchButton = new Button(1200, 300, 130, 50, "Abort Search");
	
	// Fonts 
	private Font caption1 = new Font("Arial", Font.BOLD, 30);
	private Font normal = new Font("Tahoma", Font.PLAIN, 17);

	// Constructor takes initial position
	public HomePanel() {
		
		// call constructor from super class
		super();
		
		// Set the desired background color for the panel
		this.bgColor = Commons.homeScreenBackground;
		
		// disable the search abortion button at the beginning
		this.abortMatchSearchButton.setEnabled(false);
		
		// init the gui elements 
		initGuiElements();
	}
	
	// Method for creating/initializing all the gui elements on this panel
	@Override
	protected void initGuiElements() {
		
		// Place logout button in the bottom left corner
		int btnHeight = this.logoutButton.getDimension().height;
		this.logoutButton.setPosition(10, Commons.hf - btnHeight * 2);
		
		// ...
	}
	
	// Method for implementing clean up tasks before panel closure
	@Override
	protected void onClose() {
		
		// call the original method from the super class
		super.onClose();
		
		this.abortMatchSearchButton.setEnabled(false);
		this.quickMatchButton.setEnabled(true);
	}
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		
		// Next draw some text
		g2d.setColor(Color.WHITE);
		g2d.setFont(this.caption1);
		g2d.drawString("Home screen", 750, 200);
		
		g2d.setFont(this.normal);
		g2d.drawString("Logged in as " + ProjectFrame.conn.getUsername(), 750, 250);
		
		if(this.abortMatchSearchButton.isEnabled) {
			g2d.setColor(Color.ORANGE);
			g2d.drawString("Waiting for an opponent ...", 1200, 400);
		}
		
		// Draw the buttons
		this.logoutButton.draw(g2d);
		this.quickMatchButton.draw(g2d);
		this.abortMatchSearchButton.draw(g2d);
	}
	
	// Method for processing a click on the logout button
	private void tryLogout() {
		
		// Logout button click event
		if(this.logoutButton.isHover() && ProjectFrame.conn.isLoggedIn()) {
			System.out.println("--> Logout");
			
			// Abort possible game search
			if(GameState.isSearching) {
				// Send the abortion message to the server
				SignalMessage abortMessage = new SignalMessage(GenericMessage.MSG_ABORT_MATCH_SEARCH);
				ProjectFrame.conn.sendMessageToServer(abortMessage);
			}
			
			// Run the logout routine and return to the login screen
			ProjectFrame.conn.logout();
			
			this.closePanel();
			ProjectFrame.loginPanel.setVisible(true);
		}
	}
	
	// Method for processing a click on the join quickmatch button
	private void tryQuickmatchJoin() {
		
		// Quickmatch join button click event
		if(this.quickMatchButton.isHover() && ProjectFrame.conn.isLoggedIn()) {
			System.out.println("--> Join quickmatch (waiting queue)");
			
			// send the join quickmatch message to the server
			SignalMessage joinQuickMatchMessage = new SignalMessage(GenericMessage.MSG_JOIN_QUICKMATCH);
			ProjectFrame.conn.sendMessageToServer(joinQuickMatchMessage);
			
			// Enable the match search abortion button and disable this one
			this.quickMatchButton.setEnabled(false);
			this.abortMatchSearchButton.setEnabled(true);
			
			// Update the state value that indicates, we are waiting for a player now
			GameState.isSearching = true;
		}
	}
	
	// Method for processing a click on the abort search button
	private void tryAbortMatchSearch() {
		
		// Abort button click event
		if(this.abortMatchSearchButton.isHover() && ProjectFrame.conn.isLoggedIn()) {
			System.out.println("--> Abort quick-matchmaking");
			
			// Send the abort message to the server
			SignalMessage abortMessage = new SignalMessage(GenericMessage.MSG_ABORT_MATCH_SEARCH);
			ProjectFrame.conn.sendMessageToServer(abortMessage);
			
			// Enable the quick match search button and disable this one
			this.abortMatchSearchButton.setEnabled(false);
			this.quickMatchButton.setEnabled(true);
			
			// update state value that indicates, we aren't waiting for a player anymore
			GameState.isSearching = false;
		}
	}
	
	// ----- Event handling section ----- //
	
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
