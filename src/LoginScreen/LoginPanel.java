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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import Stage.Commons;
import Stage.ProjektFrame;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {
	
	// Dimension and background color properties
	private int w, h;
	private Color c;
	
	// Timers 
	private Timer tFrameRate;
	private Timer tUpdateRate;
	
	// GUI elements inside this panel
	private TextInputField[] fields = new TextInputField[2];
	private LoginButton loginButton = new LoginButton(850, 500, 100, 50);
	
	// Listener(s)
	public KL kl = new KL();
	
	// Constructor passing position info
	public LoginPanel(int x, int y) {

		this.w = Commons.wf;
		this.h = Commons.hf;
		this.c = new Color(28,26,36);
		setBounds(x, y, w, h);
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
		fields[0] = new TextInputField("Username", new Color(255,0,50), 750, 350, 300, 50);
		fields[1] = new TextInputField("Password", new Color(255,0,50), 750, 410, 300, 50);
		
		// Timer for repainting/redrawing
		tFrameRate = new Timer(Commons.frametime, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		tFrameRate.setRepeats(true);
		tFrameRate.start();
		
		// Timer for updating 
		tUpdateRate = new Timer(Commons.frametime, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ...
			}
		});
		tUpdateRate.setRepeats(true);
		tUpdateRate.start();
	}
	
	// Main drawing function
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		// Draw the colored background
		g2d.setColor(this.c);
		g2d.fillRect(0, 0, this.w, this.h);
		
		// Draw Text 
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		g2d.drawString("Game Title", 750, 300);
		
		// Draw Loginbutton
		g2d.setColor(Color.WHITE);
		g2d.fillRect(850, 500, 100, 50);
		g2d.setColor(Color.BLACK);
		g2d.drawString("Login", 860, 535);
		
		// Draw Username and Password input-fields
		for(TextInputField curTIF : this.fields) {
			curTIF.drawTextInputField(g2d);
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
		
		for(TextInputField curTIF : this.fields) { 
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && curTIF.text.length() > 0 && curTIF.isSelected()) {
				curTIF.text = curTIF.text.substring(0, curTIF.text.length() - 1);
			} else if(curTIF.isSelected() && curTIF.text.length() < 100) {
				String validChars = "abcdefghijklmnopqrstuvwxyz1234567890!?_ ";
				if(validChars.contains((e.getKeyChar() + "").toLowerCase())) {
					curTIF.text = curTIF.text + e.getKeyChar();
				}
			} 
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
			if(user.length() <= 0) 
			{
				// Attempt to login as guest player
				loginSuccess = ProjektFrame.conn.loginAsGuest();
			}
			else 
			{
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
				JOptionPane.showMessageDialog(this, "Logged in as " + ProjektFrame.conn.getUsername());
				
				// redirect to the home screen panel
				this.setVisible(false);
				ProjektFrame.homePanel.setVisible(true);
				// ProjektFrame.stagePanel.setVisible(true);
			}
		}
	}
	
	// Mouse Listener for detecting clicks on GUI elements
	private class ML implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			tryLogin();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}

		@Override
		public void mouseExited(MouseEvent arg0) {}

		@Override
		public void mousePressed(MouseEvent e) {
			tryPressSomething(e);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {}

	}
	
	// Key listener for typing text into textfields
	private class KL implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			tryTypeIn(e);
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
			loginButton.updateHover(arg0);			
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			loginButton.updateHover(arg0);
		}
	}
}
