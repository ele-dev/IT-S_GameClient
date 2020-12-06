package menueGui;

/*
 * written by Ben Brandes
 * 
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

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
		this.statusLabel.setRelativePosition(46, 57);
		this.noAccountYet.setRelativePosition(50, 65);
		
		// give the buttons relative screen positions
		this.loginButton.setRelativePosition(43, 50);
		this.playAsGuestButton.setRelativePosition(56, 50);
		this.goToRegisterButton.setRelativePosition(50, 70);
		
		// create the input fields and place them at relative position
		fields[0] = new TextInputField("Username", Commons.textFieldWidth, Commons.textFieldHeight);
		fields[0].setRelativePosition(50, 36);
		fields[1] = new TextInputField("Password", Commons.textFieldWidth, Commons.textFieldHeight);
		fields[1].setRelativePosition(50, 42);
		
		// Set the text in the password field to hidden
		fields[1].hideText(true);
		
		// Now add the gui elements to the list of the panel
		super.guiElements.add(fields[0]);
		super.guiElements.add(fields[1]);
		super.guiElements.add(loginButton);
		super.guiElements.add(playAsGuestButton);
		super.guiElements.add(goToRegisterButton);
		super.guiElements.add(this.gameTitle);
		super.guiElements.add(this.noAccountYet);
		super.guiElements.add(this.statusLabel);
		
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
	public void update() {
		
		// Call update method from super class
		super.update();
		
		// Update the login status label
		this.statusLabel.setTextColor(this.failedAttempt ? Color.RED : Color.WHITE);
		this.statusLabel.setText(this.loginStatusStr);
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
		this.loginButton.focusNow(false);
		this.playAsGuestButton.focusNow(false);
		this.goToRegisterButton.focusNow(false);
		
		// Check if a text field was selected through the mouse click
		for(TextInputField curTIF : this.fields) {
			curTIF.trySelectField(e);
		}
	}
	
	// method for handling key pressed events (e.g. typing in textfields)
	protected void tryTypeIn(KeyEvent e) {
		
		// Make sure not to handle events for other panels
		if(!this.isVisible()) {
			return;
		}
		
		// Switch focus to next GUI element when TAB was pressed
		if(e.getKeyCode() == KeyEvent.VK_TAB) {
			if(this.fields[0].isFocused()) {
				// set focus on field[1] now
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(true);
				this.loginButton.focusNow(false);
				this.playAsGuestButton.focusNow(false);
				this.goToRegisterButton.focusNow(false);
				
			} else if(this.fields[1].isFocused()) {
				// set focus on login button
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.loginButton.focusNow(true);
				this.playAsGuestButton.focusNow(false);
				this.goToRegisterButton.focusNow(false);
				
			} else if(this.loginButton.isFocused()) {
				// set focus on the play as guest button
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.loginButton.focusNow(false);
				this.playAsGuestButton.focusNow(true);
				this.goToRegisterButton.focusNow(false);
				
			} else if(this.playAsGuestButton.isFocused()) {
				// set focus on go to register button
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.loginButton.focusNow(false);
				this.playAsGuestButton.focusNow(false);
				this.goToRegisterButton.focusNow(true);
				
			} else if(this.goToRegisterButton.isFocused()) {
				// set focus on field[0] now
				this.fields[0].focusNow(true);
				this.fields[1].focusNow(false);
				this.loginButton.focusNow(false);
				this.playAsGuestButton.focusNow(false);
				this.goToRegisterButton.focusNow(false);
				
			} else {
				// set focus on field[0] now
				this.fields[0].focusNow(true);
				this.fields[1].focusNow(false);
				this.loginButton.focusNow(false);
				this.playAsGuestButton.focusNow(false);
				this.goToRegisterButton.focusNow(false);
			}
			
			return;
		}
		
		// Trigger a click event on the selected button when enter was pressed
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(this.playAsGuestButton.isFocused()) {
				this.playAsGuest();
			} else if(this.goToRegisterButton.isFocused()) {
				this.redirectToRegister();
			} else if(this.loginButton.isFocused()) {
				System.out.println("login attempt because enter pressed");
				this.tryLogin();
			}
			
			return;
		}
		
		// Try to enter a character of the key event into a textfield
		for(TextInputField curTIF : this.fields) { 
			curTIF.typeInText(e);
		}	
	}
	
	// Method that handles play as guest login
	private void playAsGuest() {
		
		// play as guest button click event
		if(!ProjectFrame.conn.isLoggedIn()) {
			
			// create and execute a swing worker for login processesing in the background
			SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
				@Override protected Boolean doInBackground() throws Exception {
					
					// set the loading cursor
					isLoading = true;
					
					// Attempt to login as guest player
					boolean success = ProjectFrame.conn.loginAsGuest();
					if(!success) {
						System.err.println("Could not login to the game network!");
						return false;
					} else {
						System.out.println("Logged in as guest player successfully");
					}
					
					return true;
				}
				
				// Safely update the GUI as soon as the background task has been processed
				@Override protected void done() {
					
					// Retrieve the success status from the background task
					boolean status = false;
					try {
						status = get();
					} catch(InterruptedException e) {
						System.err.println("Interrupted Exception thrown while processing background task!");
					} catch(ExecutionException e) {
						System.err.println("Exception thrown during login task!");
					}
					
					// When everything worked successfully then update GUI
					if(status) {
						// redirect to the home screen panel
						closePanel();
						ProjectFrame.homePanel.setVisible(true);
					}
				}
			};
			worker.execute();
		}
	}
	
	// Method that handles a login attempt
	private void tryLogin() {
		
		// Login Button click event 
		if(!ProjectFrame.conn.isLoggedIn()) {
			
			SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

				boolean loginSuccess = false;
				
				@Override protected Boolean doInBackground() throws Exception {
					
					// Obtain the content of the text fields
					String user = fields[0].text;
					String pass = fields[1].text;
					
					// Attempt to login as registered player if a valid password was entered
					boolean validSyntax = pass.length() > 0 && user.length() > 0;
					if(validSyntax) {
						// set the loading cursor
						isLoading = true;
						
						loginSuccess = ProjectFrame.conn.loginWithAccount(user, pass);
						
						// Switch back to default cursor
						isLoading = false;
					} else {
						failedAttempt = true;
						loginStatusStr = "Empty fields are not allowed!";
						return false;
					}
					
					return true;
				}
				
				@Override protected void done() {
					
					// First retrieve the success status of the background task
					boolean status = false;
					try {
						status = get();
					} catch(InterruptedException e) {
						System.err.println("Interrupted Exception thrown while processing background task!");
					} catch(ExecutionException e) {
						System.err.println("Exception thrown during login task!");
					}
					
					if(status) {
						// Show the login status to the user
						if(!loginSuccess) {
							System.err.println("Could not login to the game network!");
							failedAttempt = true;
							loginStatusStr = "Login data was incorrect!";
							return;
						} else {
							failedAttempt = false;
							loginStatusStr = "";
							System.out.println("Logged in successfully");
							
							// redirect to the home screen panel
							closePanel();
							ProjectFrame.homePanel.setVisible(true);
						}
					}
				}
			};
			worker.execute();
		}
	}
	
	// Method for navigating to the register panel
	private void redirectToRegister() {
		// redirect to the register panel
		this.closePanel();
		ProjectFrame.registerPanel.setVisible(true);
	}
	
	// ----- Event handling section -------- //
	
	// Mouse Listener events for detecting clicks on GUI elements
	@Override 
	public void mouseClicked(MouseEvent e) {
		if(this.loginButton.isHovered()) { tryLogin(); }
		if(this.playAsGuestButton.isHovered()) { playAsGuest(); }
		if(this.goToRegisterButton.isHovered()) { redirectToRegister(); }
	}
}
