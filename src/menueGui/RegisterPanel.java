package menueGui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Stage.Commons;
import Stage.ProjectFrame;

@SuppressWarnings("serial")
public class RegisterPanel extends GuiPanel {

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
		
		// init the gui elements
		initGuiElements();
	}
	
	@Override
	protected void initGuiElements() {
		
		// Give the text label relative positions
		this.caption.setRelativePosition(50, 27);
		this.statusLabel.setRelativePosition(40, 62);
		this.noteLabel.setRelativePosition(50, 30);
		
		// init the list of textfields and give them relative positions
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
		this.goToLoginButton.setRelativePosition(5, 95);
		this.registerAccountButton.setRelativePosition(62, 62);
		
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
	protected void update() {
		// Update the register status label
		this.statusLabel.setTextColor(this.failedAttempt ? Color.RED : Color.WHITE);
		this.statusLabel.setText(this.registerStatusStr);
	}
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		
		// Draw title text and note label
		this.caption.draw(g2d);
		this.noteLabel.draw(g2d);
		
		// Draw the input fields
		for(TextInputField curTIF: this.fields) 
		{
			curTIF.draw(g2d);
		}
		
		// Draw the buttons
		this.goToLoginButton.draw(g2d);
		this.registerAccountButton.draw(g2d);
		
		// Draw the status message
		this.statusLabel.draw(g2d);
	}
	
	// Method for changing focus by clicking somewhere
	private void tryChangeFocus(MouseEvent e) {
		
		for(TextInputField curTIF : this.fields) {
			curTIF.trySelectField(e);
		}
	}
	
	// Method for typing a letter in a focused text field
	private void tryTypeIn(KeyEvent e) {
		for(TextInputField curTIF: this.fields) 
		{
			curTIF.typeInText(e);
		}
	}
	
	// Method for making a register attempt
	private void tryRegisterAccount() {
		
		// Register account button click event
		if(this.registerAccountButton.isHover()) {
			// do register attempt
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
			this.failedAttempt = (tmpStr.length != 2);
			if(this.failedAttempt) {
				this.registerStatusStr = "Invalid email address!";
				return;
			}
			
			// run the registration process with the validated parameters
			boolean result = ProjectFrame.conn.registerAccount(this.fields[0].text, this.fields[1].text, this.fields[2].text);
			if(result) {
				this.failedAttempt = false;
				this.registerStatusStr = "Registration done successfully";
			} else {
				this.failedAttempt = true;
				this.registerStatusStr = GameState.registerStatusDescription;
			}
		}
	}
	
	// Method for navigating back to the login panel
	private void redirectToLogin() {
		
		// Go to login button click event
		if(this.goToLoginButton.isHover()) {
			// redirect to the login panel
			this.closePanel();
			ProjectFrame.loginPanel.setVisible(true);
		}
	}
	
	// ----- Event handling section ----- //
	
	// Mouse Listener events for detecting clicks on GUI elements
	@Override
	public void mouseClicked(MouseEvent e) {
		redirectToLogin();
		tryRegisterAccount();
	}
	
	@Override 
	public void mousePressed(MouseEvent e) {
		tryChangeFocus(e);
	}
	
	// Mouse motion listener events for updating the hover status of GUI elements
	@Override 
	public void mouseDragged(MouseEvent e) {
		goToLoginButton.updateHover(e);
		registerAccountButton.updateHover(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		goToLoginButton.updateHover(e);
		registerAccountButton.updateHover(e);
	}
	
	// Key Listener for typing text into the fields
	@Override
	public void keyPressed(KeyEvent e) {
		tryTypeIn(e);
	}
}
