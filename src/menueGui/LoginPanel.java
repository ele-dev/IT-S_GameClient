package menueGui;

/*
 * written by Ben Brandes
 * 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import Stage.Commons;
import Stage.ProjektFrame;

@SuppressWarnings("serial")
public class LoginPanel extends GuiPanel {
	
	// GUI elements inside this panel
	private TextInputField[] fields = new TextInputField[2];
	private Button loginButton = new Button(850, 500, 100, 50, "Login"); 
	
	// Constructor passing position info
	public LoginPanel() {

		// call constructor of the super class
		super();
		
		// Set the desired background color
		this.bgColor = Commons.loginScreenBackground;
		
		// init the list of text input fields 
		fields[0] = new TextInputField("Username", 750, 350, 300, 50);
		fields[1] = new TextInputField("Password", 750, 410, 300, 50);
		
		// Set the text in the password field to hidden
		fields[1].hideText(true);
	}
	
	// Drawing method for GUI elements
	@Override
	protected void drawPanelContent(Graphics2D g2d) {
		
		// Draw Text 
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial", Font.BOLD, 30));
		g2d.drawString("Game Title", 750, 300);
		
		// Draw Loginbutton
		this.loginButton.draw(g2d);
		
		// Draw Username and Password input-fields
		for(TextInputField curTIF : this.fields) {
			curTIF.draw(g2d);
		}
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
	
	// Method that handles a login attempt
	private void tryLogin() {
		
		// Login Button click event 
		if(loginButton.isHover() && !ProjektFrame.conn.isLoggedIn()) {
			
			// Obtain the content of the text fields
			String user = this.fields[0].text;
			String pass = this.fields[1].text;
			
			// Login as guest (-> empty username) or as registered player (-> not empty username)
			boolean loginSuccess = false;
			if(user.length() <= 0) {
				// Attempt to login as guest player
				loginSuccess = ProjektFrame.conn.loginAsGuest();
			} else {
				// Attempt to login as registered player if a valid password was entered
				if(pass.length() > 0) {
					loginSuccess = ProjektFrame.conn.loginWithAccount(user, pass);
				}
			}
			
			// Show the login status to the user
			if(!loginSuccess) {
				System.err.println("Could not login to the game network!");
				return;
			} else {
				System.out.println("Logged in successfully");
				// JOptionPane.showMessageDialog(this, "Logged in as " + ProjektFrame.conn.getUsername());
				
				// redirect to the home screen panel
				this.setVisible(false);
				ProjektFrame.homePanel.setVisible(true);
			}
		}
	}
	
	// ----- Event handling section -------- //
	
	// Mouse Listener events for detecting clicks on GUI elements
	@Override 
	public void mouseClicked(MouseEvent e) {
		tryLogin();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		tryPressSomething(e);
	}
	
	// Mouse motion listener events for updating the hover status of GUI elements
	@Override
	public void mouseDragged(MouseEvent e) {
		loginButton.updateHover(e);		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		loginButton.updateHover(e);		
	}

	// Key listener for typing text into textfields
	@Override
	public void keyPressed(KeyEvent e) {
		tryTypeIn(e);
	}
}
