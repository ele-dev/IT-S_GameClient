package menueGui;

/*
 * written by Elias Geiger
 * 
 * This is the home-screen that appears after successful login
 * It contains GUI elements for different purposes:
 * e.g. player statistics display, interaction buttons for joining a match, logging out,
 * searching online players, configuring personal load out, etc.
 * 
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.SwingWorker;

import Stage.Commons;
import Stage.ProjectFrame;
import networking.GenericMessage;
import networking.SignalMessage;


@SuppressWarnings("serial")
public final class HomePanel extends GuiPanel {
	
	// GUI elements inside this panel (buttons, text fields, etc.)
	private Button logoutButton = new Button(60, 40, "Logout");
	private Button quickMatchButton = new Button(130, 50, "Quickmatch");
	private Button abortMatchSearchButton = new Button(130, 50, "Abort Search");
	private TextLabel caption = new TextLabel("Homescreen", 30);
	private TextLabel welcomeMessage = new TextLabel("Welcome", 18);
	private TextLabel gameSearchMessage = new TextLabel("Waiting for an opponent ...", 18);
	private TextLabel accountVerificationMessage = new TextLabel("", 17);
	private TextLabel gameMoneyDisplay = new TextLabel("Money: ", 17);
	private TextLabel playedMatchesDisplay = new TextLabel("Played matches: ", 17);
	private TextLabel onlinePlayerCounter = new TextLabel("Currently online: ", 17);
	private TextLabel runningMatchCounter = new TextLabel("Running matches: ", 17);

	// Constructor takes initial position
	public HomePanel() {
		
		// call constructor from super class
		super();
		
		// Set the desired background color for the panel
		this.bgColor = Commons.homeScreenBackground;
		
		// init the GUI elements 
		initGuiElements();
	}
	
	// Method for creating/initializing all the GUI elements on this panel
	@Override
	protected void initGuiElements() {
		
		// give the labels relative screen positions
		this.caption.setRelativePosition(50, 20);
		this.welcomeMessage.setRelativePosition(50, 28);
		this.gameSearchMessage.setRelativePosition(75, 47);
		this.gameSearchMessage.setTextColor(Color.ORANGE);
		this.accountVerificationMessage.setRelativePosition(10, 10);
		this.gameMoneyDisplay.setRelativePosition(10, 30);
		this.playedMatchesDisplay.setRelativePosition(10, 36);
		this.onlinePlayerCounter.setRelativePosition(90, 10);
		this.runningMatchCounter.setRelativePosition(90, 15);

		// Give the buttons relative screen positions
		this.logoutButton.setRelativePosition(5, 92);
		this.quickMatchButton.setRelativePosition(75, 40);
		this.abortMatchSearchButton.setRelativePosition(75, 55);
		
		// add the GUI elements to the list
		super.guiElements.add(this.quickMatchButton);
		super.guiElements.add(this.abortMatchSearchButton);
		super.guiElements.add(this.logoutButton);
		super.guiElements.add(this.caption);
		super.guiElements.add(this.accountVerificationMessage);
		super.guiElements.add(this.gameMoneyDisplay);
		super.guiElements.add(this.gameSearchMessage);
		super.guiElements.add(this.onlinePlayerCounter);
		super.guiElements.add(this.playedMatchesDisplay);
		super.guiElements.add(this.runningMatchCounter);
		super.guiElements.add(this.welcomeMessage);
		
		// disable the search abortion button and the status label at the beginning
		this.abortMatchSearchButton.setEnabled(false);
		this.gameSearchMessage.setEnabled(false);
	}
	
	// Method for implementing clean up tasks before panel closure
	@Override
	protected void onClose() {
		
		// call the original method from the super class
		super.onClose();
		
		// reset the buttons to the default appearance and state
		this.abortMatchSearchButton.setEnabled(false);
		this.quickMatchButton.setEnabled(true);
		this.gameSearchMessage.setEnabled(false);
	}
	
	// Method for updating/processing stuff 
	@Override
	public void update() {
		
		// Call update method from super class
		super.update();
		
		// Update the account verification status label
		if(GameState.userAccountVerified) {
			this.accountVerificationMessage.setTextColor(Color.GREEN);
			this.accountVerificationMessage.setText("Account is verified");
		} else {
			this.accountVerificationMessage.setTextColor(Color.YELLOW);
			this.accountVerificationMessage.setText("Info: Account has not been verified yet!");
		}
		
		// Enable/Disable certain elements depending on guest player status
		if(ProjectFrame.conn.isLoggedIn()) {
			if(!ProjectFrame.conn.isGuestPlayer()) {
				this.accountVerificationMessage.setEnabled(true);
				this.gameMoneyDisplay.setEnabled(true);
				this.playedMatchesDisplay.setEnabled(true);
			} else {
				this.accountVerificationMessage.setEnabled(false);
				this.gameMoneyDisplay.setEnabled(false);
				this.playedMatchesDisplay.setEnabled(false);
			}
		}
		
		// Update the game statistic display
		this.onlinePlayerCounter.setText("Currently online: " + GameState.onlinePlayers + " player(s)");
		this.runningMatchCounter.setText("Running matches: " + GameState.globalRunningMatches);
		this.gameMoneyDisplay.setText("Money: " + GameState.money + "$");
		this.playedMatchesDisplay.setText("Played matches: " + GameState.playedMatches);
		
		// Update the matchmaking buttons and labels for the 3 different possible cases
		// case 1: Currently searching but not in-game
		if(GameState.isSearching) {
			this.gameSearchMessage.setText("Waiting for an opponent ...");	
		} 
		// case 2: match found means in-game now and not searching anymore
		else if (GameState.isIngame) {
			this.gameSearchMessage.setText("Match found. Joining ...");
			this.abortMatchSearchButton.setEnabled(false);
			this.quickMatchButton.setEnabled(false);
		} 
		// case 3: Not in-game at the moment and also not searching
		else {
			this.quickMatchButton.setEnabled(true);
			this.abortMatchSearchButton.setEnabled(false);
			this.gameSearchMessage.setEnabled(false);
		}
		
		// Update the welcome display label
		this.welcomeMessage.setText("Welcome " + ProjectFrame.conn.getUsername());
	}
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		
		super.drawPanelContent(g2d);
		// ...
	}
	
	// Method for changing focus by clicking somewhere
	protected void tryChangeFocus(MouseEvent e) {
		
		// Remove remaining focus on buttons
		this.logoutButton.focusNow(false);
		this.quickMatchButton.focusNow(false);
		this.abortMatchSearchButton.focusNow(false);
	}
	
	// method for handling key pressed events (e.g. typing in text-fields)
	protected void tryTypeIn(KeyEvent e) {
		
		// Make sure not to handle events for other panels
		if(!this.isVisible()) {
			return;
		}
		
		// Switch focus to next GUI element when TAB was pressed
		if(e.getKeyCode() == KeyEvent.VK_TAB) {
			Button.playHoverSound();
			// Only works for the logout button to focus it using TAB
			if(this.logoutButton.isFocused()) {
				// remove focus from the button 
				this.logoutButton.focusNow(false);
			} else {
				// set focus on the logout button
				this.logoutButton.focusNow(true);
			}
			
			return;
		}
		
		// Trigger a click event on the selected button when enter was pressed
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(this.logoutButton.tryPress()) {
				this.tryLogout();
			}
			
			return;
		}
	}
	
	// Method for processing a click on the logout button
	private void tryLogout() {
		
		// Logout button click event
		if(ProjectFrame.conn.isLoggedIn()) {
			System.out.println("--> Logout");
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					
					// Display the loading cursor
					isLoading = true;
					
					// Abort possible game search
					if(GameState.isSearching) {
						// Send the abortion message to the server
						SignalMessage abortMessage = new SignalMessage(GenericMessage.MSG_ABORT_MATCH_SEARCH);
						ProjectFrame.conn.sendMessageToServer(abortMessage);
						GameState.isSearching = false;
					}
					
					// Run the network logout routine
					ProjectFrame.conn.logout();
					
					return null;
				}
				
				@Override
				protected void done() {
					// Navigate to the login panel
					closePanel();
					ProjectFrame.loginPanel.setVisible(true);
				}
			};
			worker.execute();
		}
	}
	
	// Method for processing a click on the join quick match button
	private void tryQuickmatchJoin() {
		
		// Quick match join button click event
		if(ProjectFrame.conn.isLoggedIn()) {
			System.out.println("--> Join quickmatch (waiting queue)");
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override protected Void doInBackground() throws Exception {
					
					// send the join quick match message to the server
					SignalMessage joinQuickMatchMessage = new SignalMessage(GenericMessage.MSG_JOIN_QUICKMATCH);
					ProjectFrame.conn.sendMessageToServer(joinQuickMatchMessage);
					
					return null;
				}
				
				@Override protected void done() {

					// Enable the match search abortion button and disable this one
					quickMatchButton.setEnabled(false);
					abortMatchSearchButton.setEnabled(true);
					
					// Also enable the matchmaking status label
					gameSearchMessage.setEnabled(true);
					
					// Update the state value that indicates, we are waiting for a player now
					GameState.isSearching = true;
				}
			};
			worker.execute();
		}
	}
	
	// Method for processing a click on the abort search button
	private void tryAbortMatchSearch() {
		
		// Abort button click event
		if(ProjectFrame.conn.isLoggedIn()) {
			System.out.println("--> Abort quick-matchmaking");
			
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

				@Override protected Void doInBackground() throws Exception {
					
					// Send the abort message to the server
					SignalMessage abortMessage = new SignalMessage(GenericMessage.MSG_ABORT_MATCH_SEARCH);
					ProjectFrame.conn.sendMessageToServer(abortMessage);
					
					return null;
				}
				
				@Override protected void done() {
					// Enable the quick match search button and disable this one
					abortMatchSearchButton.setEnabled(false);
					quickMatchButton.setEnabled(true);
					
					// Also disable the matchmaking status label
					gameSearchMessage.setEnabled(false);
					
					// update state value that indicates, we aren't waiting for a player anymore
					GameState.isSearching = false;
				}
				
			};
			worker.execute();
		}
	}
	
	// ----- Event handling section ----- //
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// React on the mouse click
		if(this.quickMatchButton.tryPress()) { tryQuickmatchJoin(); }
		if(this.abortMatchSearchButton.tryPress()) { tryAbortMatchSearch(); }
		if(this.logoutButton.tryPress()) { tryLogout(); }
	}
}
