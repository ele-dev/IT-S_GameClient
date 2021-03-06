package menueGui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import Stage.Commons;
import Stage.ProjectFrame;

@SuppressWarnings("serial")
public final class RegisterPanel extends GuiPanel {

	// GUI elements inside this panel
	private Button goToLoginButton = new Button(70, 50, "Back to Login");
	private Button registerAccountButton = new Button(60, 50, "Register");
	private TextInputField[] fields = new TextInputField[4];
	private TextLabel caption = new TextLabel("Create new Account", 40);
	private TextLabel statusLabel = new TextLabel("", 17);
	private TextLabel noteLabel = new TextLabel("Note: Use a real email address you have access to!", 15);
	
	// status message and register status
	private String registerStatusStr;
	private boolean failedAttempt;
	
	// Constructor
	public RegisterPanel() {
		
		// call the constructor of the super class
		super();
		
		// set the desired background color 
		this.bgColor = Commons.loginScreenBackground;
		
		// init the GUI elements
		initGuiElements();
	}
	
	@Override
	protected void initGuiElements() {
		
		// Give the text label relative positions
		this.caption.setRelativePosition(50, 27);
		this.statusLabel.setRelativePosition(40, 62);
		this.noteLabel.setRelativePosition(50, 30);
		
		// init the list of text-fields and give them relative positions
		this.fields[0] = new TextInputField("Username", Commons.textFieldWidth + 100, Commons.textFieldHeight);
		this.fields[0].setRelativePosition(50, 37);
		this.fields[1] = new TextInputField("Email address", Commons.textFieldWidth + 100, Commons.textFieldHeight);
		this.fields[1].setRelativePosition(50, 43);
		this.fields[2] = new TextInputField("Password", Commons.textFieldWidth + 100, Commons.textFieldHeight);
		this.fields[2].setRelativePosition(50, 49);
		this.fields[3] = new TextInputField("Repeat password", Commons.textFieldWidth + 100, Commons.textFieldHeight);
		this.fields[3].setRelativePosition(50, 55);

		// Configure parameters of the input fields
		this.fields[2].hideText(true);
		this.fields[3].hideText(true);
		this.fields[1].changeMaxInputLength((short) 35);
		char[] additionalChars = {'.', '@', '-'};
		this.fields[1].addValidChars(additionalChars);
		
		// Give the buttons relative position
		this.goToLoginButton.setRelativePosition(5, 92);
		this.registerAccountButton.setRelativePosition(62, 62);
		
		// add the GUI elements to the list
		super.guiElements.add(this.fields[0]);
		super.guiElements.add(this.fields[1]);
		super.guiElements.add(this.fields[2]);
		super.guiElements.add(this.fields[3]);
		super.guiElements.add(this.registerAccountButton);
		super.guiElements.add(this.goToLoginButton);
		super.guiElements.add(this.caption);
		super.guiElements.add(this.noteLabel);
		super.guiElements.add(this.statusLabel);
		
		// set the initial state and according message
		this.failedAttempt = false;
		this.registerStatusStr = "";
	}
	
	@Override
	protected void onClose() {
		
		// call the original method from the super class
		super.onClose();
		
		// Clear all text fields before panel closes
		for(TextInputField curTIF : this.fields) {
			curTIF.clearField();
		}
	}
	
	// Updating method
	@Override
	public void update() {
		
		// Call update method from super class
		super.update();
		
		// Update the register status label
		this.statusLabel.setTextColor(this.failedAttempt ? Color.RED : Color.WHITE);
		this.statusLabel.setText(this.registerStatusStr);
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
		this.goToLoginButton.focusNow(false);
		this.registerAccountButton.focusNow(false);
		
		// Check if a text field was selected through the mouse click
		for(TextInputField curTIF : this.fields) {
			curTIF.trySelectField(e);
		}
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
			if(this.fields[0].isFocused()) {
				// set focus on field[1]
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(true);
				this.fields[2].focusNow(false);
				this.fields[3].focusNow(false);
				this.registerAccountButton.focusNow(false);
				this.goToLoginButton.focusNow(false);
				
			} else if(this.fields[1].isFocused()) {
				// set focus on field[2]
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.fields[2].focusNow(true);
				this.fields[3].focusNow(false);
				this.registerAccountButton.focusNow(false);
				this.goToLoginButton.focusNow(false);
				
			} else if(this.fields[2].isFocused()) {
				// set focus on field[3]
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.fields[2].focusNow(false);
				this.fields[3].focusNow(true);
				this.registerAccountButton.focusNow(false);
				this.goToLoginButton.focusNow(false);
				
			} else if(this.fields[3].isFocused()) {
				// set focus on register button
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.fields[2].focusNow(false);
				this.fields[3].focusNow(false);
				this.registerAccountButton.focusNow(true);
				this.goToLoginButton.focusNow(false);
				
			} else if(this.registerAccountButton.isFocused()) {
				// set focus on go to login button
				this.fields[0].focusNow(false);
				this.fields[1].focusNow(false);
				this.fields[2].focusNow(false);
				this.fields[3].focusNow(false);
				this.registerAccountButton.focusNow(false);
				this.goToLoginButton.focusNow(true);
				
			} else if(this.goToLoginButton.isFocused()) {
				// set focus on field[0]
				this.fields[0].focusNow(true);
				this.fields[1].focusNow(false);
				this.fields[2].focusNow(false);
				this.fields[3].focusNow(false);
				this.registerAccountButton.focusNow(false);
				this.goToLoginButton.focusNow(false);
				
			} else {
				// set focus on field[0]
				this.fields[0].focusNow(true);
				this.fields[1].focusNow(false);
				this.fields[2].focusNow(false);
				this.fields[3].focusNow(false);
				this.registerAccountButton.focusNow(false);
				this.goToLoginButton.focusNow(false);
			}
			
			return;
		}
		
		// Trigger a click event on the selected button when enter was pressed
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(this.goToLoginButton.tryPress()) {
				this.redirectToLogin();
			} else if(this.registerAccountButton.tryPress()) {
				System.out.println("register attempt because enter pressed");
				this.tryRegisterAccount();
			}
			
			return;
		}
		
		// Try to enter a character of the key event into a text-field
		for(TextInputField curTIF: this.fields) {
			curTIF.typeInText(e);
		}
	}
	
	// Method for making a register attempt
	private void tryRegisterAccount() {
		
		// Register account button click event
		System.out.println("--> Register attempt");
		this.failedAttempt = false;
		System.out.println("Processing ...");
		this.registerStatusStr = "Processing ...";
		
		// validation of input fields
		for(TextInputField curTIF : this.fields) 
		{
			this.failedAttempt = (curTIF.text.length() <= 0);
		}
		
		if(this.failedAttempt) {
			this.registerStatusStr = "Empty fields are not allowed!";
			return;
		}
		
		// check if the password fields were equal
		this.failedAttempt = !(this.fields[2].text.equals(this.fields[3].text));
		if(this.failedAttempt) {
			this.registerStatusStr = "The passwords must be equal!";
			return;
		}
		
		// check the syntax of the entered email
		String[] tmpStr = this.fields[1].text.split("@");
		boolean noAtChar = (tmpStr.length != 2);
		if(noAtChar) {
			this.failedAttempt = true;
			this.registerStatusStr = "Invalid email address!";
			return;
		}
		
		SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

			@Override protected Boolean doInBackground() throws Exception {
				
				// set the loading cursor
				isLoading = true;
				
				// run the registration process with the validated parameters
				boolean result = ProjectFrame.conn.registerAccount(fields[0].text, fields[1].text, fields[2].text);
				
				// switch back to the default cursor
				isLoading = false;
				
				return result;
			}
			
			@Override protected void done() {
				
				// Retrieve success status of the background task
				boolean status = false;
				try {
					status = get();
				} catch(InterruptedException e) {
					System.err.println("Interrupted Exception thrown while processing background task!");
				} catch(ExecutionException e) {
					System.err.println("Exception thrown during login task!");
				}
				
				if(status) {
					failedAttempt = false;
					registerStatusStr = "Registration done successfully";
				} else {
					failedAttempt = true;
					registerStatusStr = GameState.registerStatusDescription;
				}
			}
		};
		worker.execute();
	}
	
	// Method for navigating back to the login panel
	private void redirectToLogin() {
		
		// redirect to the login panel
		this.closePanel();
		ProjectFrame.loginPanel.setVisible(true);
	}
	
	// ----- Event handling section ----- //
	
	// Mouse Listener events for detecting clicks on GUI elements
	@Override
	public void mouseClicked(MouseEvent e) {
		if(this.goToLoginButton.tryPress()) { redirectToLogin(); }
		if(this.registerAccountButton.tryPress()) { tryRegisterAccount(); }
	}
}
