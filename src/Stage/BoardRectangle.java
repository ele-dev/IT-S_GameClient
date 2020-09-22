package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import Environment.DestructibleObject;
import GamePieces.GamePiece;

public class BoardRectangle {
	public int row,column;
	Color c;
	public Rectangle rect;
	public Color cPossibleMove,cPossibleAttack,cPossibleAbility;
	int index;
	public boolean isTile1;
	public boolean isPossibleMove,isPossibleAttack,isPossibleAbility;
	public boolean isShowPossibleMove,isShowPossibleAttack,isShowPossibleAbility;
	public boolean isWall,isGap;
	
	private boolean isHover;
	double animationSpeed;
	public float so = 4;
	private int rotation = 0; 
	
	private Sprite wallSprite;
	private Sprite groundSprite;
	
	public BoardRectangle northBR,southBR,eastBR,westBR;
	ArrayList<BoardRectangle> adjecantBoardRectangles = new ArrayList<BoardRectangle>();
	
	public BoardRectangle(int row, int column,boolean isTile1,int index) {
		this.row = row;
		this.column = column;
		int size = Commons.boardRectSize;
		rect = new Rectangle(column*size,row*size,size,size);
		
		int alpha = 200;
		cPossibleMove = new Color(Commons.cMove.getRed(),Commons.cMove.getGreen(),Commons.cMove.getBlue(),alpha);
		cPossibleAttack = new Color(Commons.cAttack.getRed(),Commons.cAttack.getGreen(),Commons.cAttack.getBlue(),alpha);
		cPossibleAbility = new Color(Commons.cAbility.getRed(),Commons.cAbility.getGreen(),Commons.cAbility.getBlue(),alpha);
		
		this.isTile1 = isTile1;
		
		this.index = index;
		
		if(isTile1) {
			c = new Color(10,10,10);
		}else {
			c = new Color(200,200,200);
		}
		
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Tiles/GrassTile.png");
		groundSprite = new Sprite(spriteLinks, size,size, 0);
	}
	public int getSize() {
		return (int) rect.getWidth();
	}
	public int getX() {
		return (int) rect.getX();
	} 
	public int getY() {
		return (int) rect.getY();
	}
	public int getCenterX(){
		return (int) rect.getCenterX();
	}
	public int getCenterY(){
		return (int) rect.getCenterY();
	}
	public Point getPos() {
		return new Point(getCenterX(),getCenterY());
	}
	public boolean isHover() {
		return isHover;
	}
	public boolean isDestructibleObject() {
		for(DestructibleObject curDO : StagePanel.destructibleObjects) {
			if(curDO.containsBR(this)) {
				return true;
			}
			
		}
		return false;
	}
	public void initAdjecantBRs(ArrayList<BoardRectangle> boardRectangles) {
		for(BoardRectangle curBR : boardRectangles ) {
			if(curBR != this) {
				if(curBR.row == row-1 && curBR.column == column) {
					northBR = curBR;
					adjecantBoardRectangles.add(northBR);
				}
				if(curBR.row == row+1 && curBR.column == column) {
					southBR = curBR;
					adjecantBoardRectangles.add(southBR);
				}
				if(curBR.row == row && curBR.column == column+1) {
					eastBR = curBR;
					adjecantBoardRectangles.add(eastBR);
				}
				if(curBR.row == row && curBR.column == column-1) {
					westBR = curBR;
					adjecantBoardRectangles.add(westBR);
				}
			}
		}
	}
	
	public static int getDistanceBetweenBRs(BoardRectangle br1,BoardRectangle br2) {
		return Math.abs(br1.row - br2.row)+Math.abs(br1.column - br2.column);
	}
	
	
	// initalizes what Sprite is needed depending on neighboring walls
	public void initWallSprites() {
		boolean rightConnected = false;
		boolean leftConnected = false;
		boolean upConnected = false;
		boolean downConnected = false;
		for(BoardRectangle curBR : adjecantBoardRectangles) {
			if(curBR.isWall) {
				if(curBR.row == row+1 && curBR.column == column) {
					downConnected = true;
				}
				if(curBR.row == row-1 && curBR.column == column) {
					upConnected = true;
				}
				if(curBR.row == row && curBR.column == column+1) {
					rightConnected = true;
				}
				if(curBR.row == row && curBR.column == column-1) {
					leftConnected = true;
				}
			}
		}
		
		int size = getSize();
		if(rightConnected) {
			ArrayList<String> spriteLinks = new ArrayList<String>();
			spriteLinks.add(Commons.pathToSpriteSource+"Tiles/DarkWall_R.png");
			wallSprite = new Sprite(spriteLinks, size,size, 0);
			if(leftConnected) {
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LR.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
				if(downConnected) {
					ArrayList<String> spriteLinks2 = new ArrayList<String>();
					spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LRD.png");
					wallSprite = new Sprite(spriteLinks2, size,size, 0);
					if(upConnected) {
						ArrayList<String> spriteLinks3 = new ArrayList<String>();
						spriteLinks3.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LRUD.png");
						wallSprite = new Sprite(spriteLinks3, size,size, 0);
					}
				}else if(upConnected){
					ArrayList<String> spriteLinks2 = new ArrayList<String>();
					spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LRU.png");
					wallSprite = new Sprite(spriteLinks2, size,size, 0);
				}
			}else
			if(upConnected) {
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_RU.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
				if(downConnected) {
					ArrayList<String> spriteLinks2 = new ArrayList<String>();
					spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_RUD.png");
					wallSprite = new Sprite(spriteLinks2, size,size, 0);
				}
			}else 
			if(downConnected) {
				ArrayList<String> spriteLinks2 = new ArrayList<String>();
				spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_RD.png");
				wallSprite = new Sprite(spriteLinks2, size,size, 0);
			}
		}else 
		if(leftConnected) {
			ArrayList<String> spriteLinks3 = new ArrayList<String>();
			spriteLinks3.add(Commons.pathToSpriteSource+"Tiles/DarkWall_L.png");
			wallSprite = new Sprite(spriteLinks3, size,size, 0);
			if(upConnected) {
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LU.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
				if(downConnected) {
					ArrayList<String> spriteLinks2= new ArrayList<String>();
					spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LUD.png");
					wallSprite = new Sprite(spriteLinks2, size,size, 0);
				}
			}else if(downConnected){
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LD.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
			}
		}else 
		if(upConnected){
			ArrayList<String> spriteLinks2 = new ArrayList<String>();
			spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_U.png");
			wallSprite = new Sprite(spriteLinks2, size,size, 0);
			if(downConnected) {
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_UD.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
			}
		}else 
		if(downConnected){
			ArrayList<String> spriteLinks1 = new ArrayList<String>();
			spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_D.png");
			wallSprite = new Sprite(spriteLinks1, size,size, 0);
		}else {
			ArrayList<String> spriteLinks3 = new ArrayList<String>();
			spriteLinks3.add(Commons.pathToSpriteSource+"Tiles/DarkWall.png");
			wallSprite = new Sprite(spriteLinks3, size,size, 0);
		}
	}
	// draws The Rectangle depending on if it is a Possible move/attack (draws another rectangle with another color over it)
	public void drawBoardRectangle(Graphics2D g2d,ArrayList<BoardRectangle> boardRectangles) {
		g2d.setColor(isGap?Color.BLUE:c);
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(5));
		
		if(!isTile1 && !isGap) {
			groundSprite.drawSprite(g2d, getCenterX(), getCenterY(), 0, 1);
		}else {

		}
		g2d.setStroke(new BasicStroke(2));
		if(isPossibleMove || isPossibleAttack || isPossibleAbility) {
			if(isPossibleMove) {
				g2d.setColor(cPossibleMove);
			}else if(isPossibleAttack){
				g2d.setColor(cPossibleAttack);
			}else if(isPossibleAbility){
				g2d.setColor(cPossibleAbility);
			}
			
			g2d.fill(rect);
			g2d.setColor(new Color(5,5,5));
			g2d.draw(rect);
		}
		if((isShowPossibleAbility && !isPossibleAbility) || (isShowPossibleMove && !isPossibleMove) || (isShowPossibleAttack && !isPossibleAttack)) {
			g2d.setStroke(new BasicStroke(4));
			if(isShowPossibleAbility) {
				g2d.setColor(new Color(cPossibleAbility.getRed(),cPossibleAbility.getGreen(),cPossibleAbility.getBlue(),150));
			}else if(isShowPossibleMove){
				g2d.setColor(new Color(cPossibleMove.getRed(),cPossibleMove.getGreen(),cPossibleMove.getBlue(),150));
			}else {
				g2d.setColor(new Color(cPossibleAttack.getRed(),cPossibleAttack.getGreen(),cPossibleAttack.getBlue(),150));
			}
			g2d.drawLine(getX(), getY(), getX()+getSize(), getY());
			g2d.drawLine(getX(), getY()+getSize(), getX()+getSize(), getY()+getSize());
			g2d.drawLine(getX(), getY(), getX(), getY()+getSize());
			g2d.drawLine(getX()+getSize(), getY(), getX()+getSize(), getY()+getSize());
			for(int i = 0;i<getSize();i+=getSize()/6) {
				g2d.drawLine(getX()+i, getY(), getX(), getY()+i);
			}
			for(int i = getSize()/6;i<getSize();i+=getSize()/6) {
				g2d.drawLine(getX()+i, getY()+getSize(), getX()+getSize(), getY()+i);
			}
		}
	}
	
	// draw the Hover Rect
	public void tryDrawHover(Graphics2D g2d) {
		if(isHover) {
			int alpha = 255;
			g2d.setStroke(new BasicStroke(5));
			if(isPossibleAttack) {
				g2d.setColor(Commons.cAttack);
				g2d.setColor(new Color(10,10,10));
			}else 
			if(isPossibleMove){
				g2d.setColor(Commons.cMove);
				g2d.setColor(new Color(Commons.cMove.getRed(),Commons.cMove.getGreen(),Commons.cMove.getBlue(),alpha));
			}else if(isPossibleAbility) {
				g2d.setColor(new Color(10,10,10));
			}else {
				g2d.setColor(new Color(Commons.cMove.getRed(),Commons.cMove.getGreen(),Commons.cMove.getBlue(),200));
			}
			int x = getX();
			int y = getY();
			int s = getSize();
			int soI = (int)so;
			g2d.translate(x, y);
			g2d.drawLine(-soI/2, -soI/2, s/4-soI/2, -soI/2);
			g2d.drawLine(s+soI/2, -soI/2, s*3/4+soI/2, -soI/2);
					
			g2d.drawLine(-soI/2, s+soI/2, s/4-soI/2, s+soI/2);
			g2d.drawLine(s+soI/2, s+soI/2, s*3/4+soI/2, s+soI/2);
					
			g2d.drawLine(-soI/2, -soI/2, -soI/2, s/4-soI/2);
			g2d.drawLine(-soI/2, s+soI/2, -soI/2, s*3/4+soI/2);
					
			g2d.drawLine(s+soI/2, -soI/2, s+soI/2, s/4-soI/2);
			g2d.drawLine(s+soI/2, s+soI/2, s+soI/2, s*3/4+soI/2);
			
			if(isPossibleAttack) {
				g2d.translate(s/2, s/2);
				g2d.rotate(Math.toRadians(rotation));
				g2d.drawLine(-s/3, 0, s/3, 0);
				g2d.drawLine(0,-s/3, 0, s/3);
				g2d.drawOval(-s/6, -s/6, s/3, s/3);
				g2d.rotate(Math.toRadians(-rotation));
				g2d.translate(-s/2, -s/2);
			}
			
			if(isPossibleAbility) {
				g2d.translate(s/2, s/2);
				g2d.rotate(Math.toRadians(rotation));
				Polygon polygon = new Polygon();
				int radius = s/3;
				int j = 0;
	            for (int i = 0; i < 360; i+= 360/6) {
	                int xHex = (int) (radius* Math.cos(Math.toRadians(i)));
	                int yHex = (int) (radius * Math.sin(Math.toRadians(i)));
	                if(j%2==0) {
	                	g2d.drawLine(0, 0, xHex, yHex);
	                }
	                j++;
	                polygon.addPoint(xHex, yHex);
	            }
	            g2d.draw(polygon);
	            g2d.rotate(Math.toRadians(-rotation));
				g2d.translate(-s/2, -s/2);
			}
			g2d.translate(-x, -y);
		}
	}
	
	// makes the Hover Rect bigger and then smaller gives it "pop" 
	public void tryAnimate() {
		if(rotation > 360) {
			rotation = 0;
		}
		rotation++;
		if(isPossibleAttack || isPossibleAbility) {
			if(so < 5) {
				animationSpeed = 0.3;
			}
			if(so > 10) {
				animationSpeed = -0.3;
			}
			so+=animationSpeed;
		}else {
			so = 4;
		}
	}
	// updates the Hover boolean to be Hover == true if the mouse is on the BoardRectangle
	public void updateHover(Point mousePos) {
		if(rect.contains(mousePos)) {
			isHover = true;
		}else {
			isHover = false;
		}
	}
	// draws wall but makes it transparent if it would overdraw a GamePiece
	public void drawWall(Graphics2D g2d,ArrayList<GamePiece> gamePieces) {
		
		if(wallSprite != null) {
			wallSprite.drawSprite(g2d, getCenterX(), getCenterY(), 0, 1);
		}else {
			g2d.setColor(new Color(10,10,10));
			g2d.fill(rect);
			g2d.setColor(new Color(100,100,100));
			g2d.setStroke(new BasicStroke(6));
			g2d.draw(rect);
		}
	}
	
	public void drawIndex(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.PLAIN,15));
		if(!isTile1) {
			g2d.setColor(new Color(30,30,30));
		}else {
			g2d.setColor(new Color(200,200,200));
		}
		g2d.drawString(index+"", getCenterX(), getCenterY());
	}
	// returns the resized image
	public Image resizeImage(String imageString) {
		ImageIcon imageIcon = new ImageIcon(imageString);
		Image image = imageIcon.getImage();
		Image modImage = image.getScaledInstance(getSize(), getSize(), java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(modImage);
		return imageIcon.getImage();
	}
	
}
