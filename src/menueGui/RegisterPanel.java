package menueGui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import Stage.Commons;
import Stage.ProjectFrame;

@SuppressWarnings("serial")
public class RegisterPanel extends GuiPanel {

	// GUI elements inside this panel
	private Button goToLoginButton = new Button(0, 0, 180, 50, "Back to Login");
	private Button registerAccountButton = new Button(750, 600, 170, 50, "Register");
	private TextInputField[] fields = new TextInputField[4];
	
	// Fonts
	private Font caption1 = new Font("Arial", Font.PLAIN, 30);
	private Font statusDisplay = new Font("Tahoma", Font.PLAIN, 17);
	
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
		
		// init the list of textfields
		this.fields[0] = new TextInputField("Username", 750, 300, 350, 50);
		this.fields[1] = new TextInputField("Email address", 750, 360, 350, 50);
		this.fields[2] = new TextInputField("Password", 750, 420, 350, 50);
		this.fields[3] = new TextInputField("Repeat password", 750, 480, 350, 50);

		// Configure parameters of the input fields
		this.fields[2].hideText(true);
		this.fields[3].hideText(true);
		this.fields[1].changeMaxInputLength((short) 35);
		char[] additionalChars = {'.', '@', '-'};
		this.fields[1].addValidChars(additionalChars);
		
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
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		
		// Draw title text
		g2d.setColor(Color.WHITE);
		g2d.setFont(this.caption1);
		g2d.drawString("Create new account", 750, 240);
		
		// Draw the input fields
		for(TextInputField curTIF: this.fields) 
		{
			curTIF.draw(g2d);
		}
		
		// Draw the buttons
		this.goToLoginButton.draw(g2d);
		this.registerAccountButton.draw(g2d);
		
		// Draw the status message
		g2d.setColor(this.failedAttempt ? Color.RED : Color.WHITE);
		g2d.setFont(this.statusDisplay);
		g2d.drawString(this.registerStatusStr, 760, 570);
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
			
			// Send register message to the server
			// ...
			
			// Wait for the response and read the status 
			// ...
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
