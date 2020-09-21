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

import javax.swing.JPanel;
import javax.swing.Timer;

import Stage.Commons;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {
	
	@SuppressWarnings("unused")
	private int x,y;
	private int w,h;
	private Color c;
	private Timer tFrameRate;
	private Timer tUpdateRate;
	@SuppressWarnings("unused")
	private boolean isSelected = false;
	private TextInputField[] fields = new TextInputField[2];
	private LoginButton loginButton = new LoginButton(850, 500, 100, 50);
	public KL kl = new KL();
	
	
	public LoginPanel(int x, int y) {
		this.x = x;
		this.y = y;
		this.w = Commons.wf;
		this.h = Commons.hf;
		this.c = new Color(28,26,36);
		setBounds(x,y,w,h);
		addMouseListener(new ML());
		addMouseMotionListener(new MML());
		fields[0] = new TextInputField("Username", new Color(255,0,50), 750, 350, 300, 50);
		fields[1] = new TextInputField("Password", new Color(255,0,50), 750, 410, 300, 50);
		tFrameRate = new Timer(10, new ActionListener() {
			
			@Override
				public void actionPerformed(ActionEvent e) {
					repaint();
				
				}
			});
			tFrameRate.setRepeats(true);
			tFrameRate.start();
		
			tUpdateRate = new Timer(10, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
				
				}
			});
			tUpdateRate.setRepeats(true);
			tUpdateRate.start();
		}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(c);
		g2d.fillRect(0,0,w,h);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font("Arial",Font.BOLD,30));
		g2d.drawString("Game Title", 750, 300);
		//Draws Loginbutton
		g2d.setColor(Color.WHITE);
		g2d.fillRect(850, 500, 100, 50);
		g2d.setColor(Color.BLACK);
		g2d.drawString("Login", 860, 535);
		//Draws Username and Password input-fields
		for(TextInputField curTIF : fields) {
			curTIF.drawTextInputField(g2d);
		}
	}
	
	public void tryPressSomething(MouseEvent e) {
		
		for(TextInputField curTIF : fields) {
			curTIF.trySelectField(e);
		}
		 
	}
	
	public void tryTypeIn(KeyEvent e) {
		
		for(TextInputField curTIF : fields) { 
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && curTIF.text.length() > 0 && curTIF.isSelected()){
				curTIF.text = curTIF.text.substring(0, curTIF.text.length()-1);
			} else if(curTIF.isSelected() && curTIF.text.length() < 100){
				String validChars = "abcdefghijklmnopqrstuvwxyz1234567890!?_ ";
				if(validChars.contains((e.getKeyChar() + "").toLowerCase())) {
					curTIF.text = curTIF.text + e.getKeyChar();
				}
			} 
			
		}
		
	}
	
	public void tryLogin() {
		
		if(loginButton.isHover()) {
			
			// not finished code login programm entrypoint //
			
		}
		
	}
	private class ML implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			tryPressSomething(e);
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

	}
	
	private class KL implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			tryTypeIn(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
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
