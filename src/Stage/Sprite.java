package Stage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Sprite {
	// Array of Sprites used in an animation
	private ArrayList<Image> sprites = new ArrayList<Image>();
	private int w,h;
	// index of the animation (which sprite it's at)
	private int animationIndex = 0;
	// frames of delay the animation takes to slide to next index
	private int animationDelay;
	private int animationCounter = 0;
	
	
	public Sprite(ArrayList<String> spriteLinks,int w,int h,int animationDelay) {
		this.w = w;
		this.h = h;
		this.animationDelay = animationDelay;
		for(String curSL : spriteLinks) {
			sprites.add(resizeImage(curSL));
		}
	}
	
	// returns the resized image
	private Image resizeImage(String imageString) {
		ImageIcon imageIcon = new ImageIcon(imageString);
		Image image = imageIcon.getImage();
		Image modImage = image.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(modImage);
		return imageIcon.getImage();
	}
	
	// draw the sprite at a centered Location can also be rotated or made transparent
	public void drawSprite(Graphics2D g2d,int x, int y,double rotation, float alpha) {
		if(alpha < 1) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
			g2d.setComposite(ac);
		}
		
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));
		g2d.drawImage(sprites.get(animationIndex),-w/2,-h/2,null);
		g2d.rotate(Math.toRadians(-rotation));
		g2d.translate(-x, -y);
		
		if(alpha < 1) {
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1);
			g2d.setComposite(ac);
		}
	}
	
	// animates the sprite if it has more spriteLinks/sprites than one (also relative speed to the animation delay)
	public void animate() {
		if(animationCounter>0) {
			animationCounter--;
		}else {
			animationCounter = animationDelay;
			animationIndex = animationIndex+1 < sprites.size()?animationIndex+1:0;
		}
	}
	
	public Color getPixelColor(int x, int y) {
		Color c = new Color(0,0,0,0);
		BufferedImage buffered = new BufferedImage(w, h, Image.SCALE_SMOOTH);
		buffered.getGraphics().drawImage(sprites.get(animationIndex), 0, 0 , null);
		c = new Color(buffered.getRGB(x, y));
		return c;
	}
	
	// gets a random Pixel that is not totally black or transparent
	public Color getRandomPixelColor() {
		Color c = Color.BLACK;
		while (c.getRed() == 0 && c.getGreen() == 0 && c.getBlue() == 0) {
			int x = (int) (Math.random()*w);
			int y = (int) (Math.random()*h);
			BufferedImage buffered = new BufferedImage(w, h, Image.SCALE_SMOOTH);
			buffered.getGraphics().drawImage(sprites.get(animationIndex), 0, 0 , null);
			c = new Color(buffered.getRGB(x, y));
		}
		return c;
	}
	
	
}
