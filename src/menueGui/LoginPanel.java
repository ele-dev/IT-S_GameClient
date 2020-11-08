package menueGui;

/*
 * written by Ben Brandes
 * 
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Stage.Commons;
import Stage.ProjectFrame;

@SuppressWarnings("serial")
public class LoginPanel extends GuiPanel {
	
	// GUI elements inside this panel
	private TextInputField[] fields = new TextInputField[2];
	private Button loginButton = new Button(60, 40, "Login"); 
	private Button playAsGuestButton = new Button(85, 40, "Play as Guest");
	private Button goToRegisterButton = new Button(150, 40, "Register an account");
	private TextLabel statusLabel = new TextLabel("", 17);
	private TextLabel gameTitle = new TextLabel(Commons.gameTitle, 55);
	private TextLabel noAccountYet = new TextLabel("You don't have an account yet?", 17);
	
	// Status message and success status
	private String loginStatusStr = "";
	private boolean failedAttempt = false;
	
	// Constructor passing position info
	public LoginPanel() {

		// call constructor of the super class
		super();
		
		// Set the desired background color
		this.bgColor = Commons.loginScreenBackground;
		
		// init the gui elements
		initGuiElements();
	}
	
	@Override
	protected void initGuiElements() {
		
		// give the labels relative screen positions 
		this.gameTitle.setRelativePosition(50, 30);
		this.statusLabel.setRelativePosition(46, 47);
		this.noAccountYet.setRelativePosition(50, 60);
		
		// give the buttons relative screen positions
		this.loginButton.setRelativePosition(43, 50);
		this.playAsGuestButton.setRelativePosition(56, 50);
		this.goToRegisterButton.setRelativePosition(50, 65);
		
		// create the input fields and place them at relative position
		fields[0] = new TextInputField("Username", Commons.textFieldWidth, Commons.textFieldHeight);
		fields[0].setRelativePosition(50, 36);
		fields[1] = new TextInputField("Password", Commons.textFieldWidth, Commons.textFieldHeight);
		fields[1].setRelativePosition(50, 42);
		
		// Set the text in the password field to hidden
		fields[1].hideText(true);
		
		this.loginStatusStr = "";
	}
	
	@Override
	protected void onClose() {
		// call the original method from the super class
		super.onClose();
		
		// Empty the password input field and the status label before panel closes
		this.fields[1].clearField();
		this.loginStatusStr = "";
	}
	
	// Updating method
	@Override 
	protected void update() {
		// Update the login status label
		this.statusLabel.setTextColor(this.failedAttempt ? Color.RED : Color.WHITE);
		this.statusLabel.setText(this.loginStatusStr);
	}
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		
		// Draw game title and other labels
		this.gameTitle.draw(g2d);
		this.noAccountYet.draw(g2d);
		
		// Draw Loginbutton and play as guest button
		this.loginButton.draw(g2d);
		this.playAsGuestButton.draw(g2d);
		this.goToRegisterButton.draw(g2d);
		
		// Draw Username and Password input-fields
		for(TextInputField curTIF : this.fields) {
			curTIF.draw(g2d);
		}
		
		// Draw the status message directly under the input fields
		this.statusLabel.draw(g2d);
	}
	
	// Method for changing focus by clicking somewhere
	private void tryPressSomething(MouseEvent e) {
		
		for(TextInputField curTIF : this.fields) {
			curTIF.trySelectField(e);
		}
	}
	
	// Method for typing a letter in a text field
	private void tryTypeIn(KeyEvent e) {
		
		// Switch focus to next text field when TAB was typed
		/*
			if(e.getKeyCode() == KeyEvent.VK_TAB) {
			System.out.println("TAB key pressed");
			
			if(this.fields[0].isSelected()) {
				// set focus on field[1] now
				this.fields[0].selectFieldNow(false);
				this.fields[1].selectFieldNow(true);
			} else {
				// set focus on field[0] now
				this.fields[1].selectFieldNow(false);
				this.fields[0].selectFieldNow(true);
			} 
			
			return;
		}
		 */
		
		for(TextInputField curTIF : this.fields) 
		{ 
			curTIF.typeInText(e);
		}	
	}
	
	// Method that handles play as guest login
	private void playAsGuest() {
		
		// play as guest button click event
		if(playAsGuestButton.isHover() && !ProjectFrame.conn.isLoggedIn()) {
			// Attempt to login as guest player
			boolean success = ProjectFrame.conn.loginAsGuest();
			if(!success) {
				System.err.println("Could not login to the game network!");
			} else {
				System.out.println("Logged in as guest player successfully");
				
				// redirect to the home screen panel
				this.closePanel();
				ProjectFrame.homePanel.setVisible(true);
			}
		}
	}
	
	// Method that handles a login attempt
	private void tryLogin() {
		
		// Login Button click event 
		if(loginButton.isHover() && !ProjectFrame.conn.isLoggedIn()) {
			
			// Obtain the content of the text fields
			String user = this.fields[0].text;
			String pass = this.fields[1].text;
			
			// Attempt to login as registered player if a valid password was entered
			boolean loginSuccess = false;
			if(pass.length() > 0 && user.length() > 0) {
				loginSuccess = ProjectFrame.conn.loginWithAccount(user, pass);
			} else {
				this.failedAttempt = true;
				this.loginStatusStr = "Empty fields are not allowed!";
				
				return;
			}
				
			// Show the login status to the user
			if(!loginSuccess) {
				System.err.println("Could not login to the game network!");
				this.failedAttempt = true;
				this.loginStatusStr = "Login data was incorrect!";
				
				return;
			} else {
				this.failedAttempt = false;
				this.loginStatusStr = "";
				System.out.println("Logged in successfully");
				
				// redirect to the home screen panel
				this.closePanel();
				ProjectFrame.homePanel.setVisible(true);
			}
		}
	}
	
	// Method for navigating to the register panel
	private void redirectToRegister() {
		
		// Go to register button click event 
		if(this.goToRegisterButton.isHover()) {
			// redirect to the register panel
			this.closePanel();
			ProjectFrame.registerPanel.setVisible(true);
		}
	}
	
	// ----- Event handling section -------- //
	
	// Mouse Listener events for detecting clicks on GUI elements
	@Override 
	public void mouseClicked(MouseEvent e) {
		tryLogin();
		playAsGuest();
		redirectToRegister();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		tryPressSomething(e);
	}
	
	// Mouse motion listener events for updating the hover status of GUI elements
	@Override
	public void mouseDragged(MouseEvent e) {
		loginButton.updateHover(e);
		playAsGuestButton.updateHover(e);
		goToRegisterButton.updateHover(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		loginButton.updateHover(e);		
		playAsGuestButton.updateHover(e);
		goToRegisterButton.updateHover(e);
	}

	// Key listener for typing text into textfields
	@Override
	public void keyPressed(KeyEvent e) {
		tryTypeIn(e);
	}
}
