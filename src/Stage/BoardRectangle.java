package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import GamePieces.GamePiece;

public class BoardRectangle {
	public int x,y;
	public int centeredX,centeredY;
	public int size;
	public int posRow,posColumn;
	Color c;
	public Rectangle rect;
	public Color cPossibleMove;
	public Color cPossibleAttack;
	int index;
	boolean isTile1;
	public boolean isPossibleMove;
	public boolean isPossibleAttack;
	public boolean isGap = false;
	public boolean isWall = false;
	public boolean isDestructibleWall = false;
	
	boolean isHover;
	double animationSpeed;
	double so = 4;
	
	Sprite wallSprite,destructibleWallsprite;
	Sprite groundSprite;
	
	public BoardRectangle(int x, int y, int size, int posRow, int posColumn,boolean isTile1,int index) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.posRow = posRow;
		this.posColumn = posColumn;
		this.rect = new Rectangle(x,y,size,size);
		
		this.cPossibleMove = new Color(Commons.cMove.getRed(),Commons.cMove.getGreen(),Commons.cMove.getBlue(),80);
		
		this.cPossibleAttack = new Color(Commons.cAttack.getRed(),Commons.cAttack.getGreen(),Commons.cAttack.getBlue(),80);
		
		this.isTile1 = isTile1;
		this.centeredX = this.x + size/2;
		this.centeredY = this.y + size/2;
		
		this.index = index;
		
		if(isTile1) {
			this.c = new Color(10,10,10);
		}else {
			this.c = new Color(200,200,200);
		}
		
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Tiles/GrassTile.png");
		groundSprite = new Sprite(spriteLinks, size,size, 0);
	}
	
	// initalizes what Sprite is needed depending on neighboring walls
	public void initWallSprites(StagePanel stagePanel) {
		boolean rightConnected = false;
		boolean leftConnected = false;
		boolean upConnected = false;
		boolean downConnected = false;
		for(BoardRectangle curBR : stagePanel.boardRectangles) {
			if(curBR.isWall) {
				if(curBR.posRow == posRow+1 && curBR.posColumn == posColumn) {
					downConnected = true;
				}
				if(curBR.posRow == posRow-1 && curBR.posColumn == posColumn) {
					upConnected = true;
				}
				if(curBR.posRow == posRow && curBR.posColumn == posColumn+1) {
					rightConnected = true;
				}
				if(curBR.posRow == posRow && curBR.posColumn == posColumn-1) {
					leftConnected = true;
				}
			}
		}
		
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
	
	public void initDestructibleWallSprite() {
		ArrayList<String> spriteLinks = new ArrayList<String>();
		spriteLinks.add(Commons.pathToSpriteSource+"Tiles/WoodWall_LR.png");
		destructibleWallsprite = new Sprite(spriteLinks, size,size, 0);
	}
	// draws The Rectangle depending on if it is a Possible move/attack (draws another rectangle with another color over it)
	public void drawBoardRectangle(Graphics2D g2d,ArrayList<BoardRectangle> boardRectangles) {
		
		g2d.setColor(c);
		g2d.fill(rect);
		g2d.setStroke(new BasicStroke(5));
		
		if(!isTile1) {
			groundSprite.drawSprite(g2d, centeredX, centeredY, 0, 1);
		}else {

		}
		g2d.setStroke(new BasicStroke(2));
		
		if(isPossibleMove && !isPossibleAttack) {
			g2d.setColor(cPossibleMove);
			g2d.fill(rect);
			g2d.setColor(new Color(5,5,5));
			g2d.draw(rect);
		}
		if(isPossibleAttack && !isPossibleMove) {
			g2d.setColor(cPossibleAttack);
			g2d.fill(rect);
			g2d.setColor(new Color(5,5,5));
			g2d.draw(rect);
		}
	}
	
	// draw the Hover Rect
	public void tryDrawHover(Graphics2D g2d,ArrayList<GamePiece> gamePieces) {
		if(isHover) {
			int alpha = 100;
			GamePiece curHoverGP = null;
			GamePiece selectedGP = null;
			for(GamePiece curGP : gamePieces) {
				if(curGP.boardRect == this) {
					curHoverGP = curGP;
				}else
				if(curGP.isSelected) {
					selectedGP  = curGP;
				}
			}
			if(isPossibleMove || (curHoverGP != null && selectedGP != null && ((curHoverGP.getIsEnemy() && !selectedGP.getIsEnemy()) || (!curHoverGP.getIsEnemy() && selectedGP.getIsEnemy()))) || isDestructibleWall) {
				alpha = 255;
			}
			g2d.setStroke(new BasicStroke(5));
			if(isPossibleAttack) {
				g2d.setColor(Commons.cAttack);
				g2d.setColor(new Color(Commons.cAttack.getRed(),Commons.cAttack.getGreen(),Commons.cAttack.getBlue(),alpha));
			}else 
			if(isPossibleMove){
				g2d.setColor(Commons.cMove);
				g2d.setColor(new Color(Commons.cMove.getRed(),Commons.cMove.getGreen(),Commons.cMove.getBlue(),alpha));
			}else {
				g2d.setColor(c);
				g2d.setColor(new Color(240,230,100,alpha));
			}
			int s = size;
			int soI = (int)so;
			g2d.drawLine(x-soI/2, y-soI/2, x+s/4-soI/2, y-soI/2);
			g2d.drawLine(x+s+soI/2, y-soI/2, x+s*3/4+soI/2, y-soI/2);
					
			g2d.drawLine(x-soI/2, y+s+soI/2, x+s/4-soI/2, y+s+soI/2);
			g2d.drawLine(x+s+soI/2, y+s+soI/2, x+s*3/4+soI/2, y+s+soI/2);
					
			g2d.drawLine(x-soI/2, y-soI/2, x-soI/2, y+s/4-soI/2);
			g2d.drawLine(x-soI/2, y+s+soI/2, x-soI/2, y+s*3/4+soI/2);
					
			g2d.drawLine(x+s+soI/2, y-soI/2, x+s+soI/2, y+s/4-soI/2);
			g2d.drawLine(x+s+soI/2, y+s+soI/2, x+s+soI/2, y+s*3/4+soI/2);
		}
	}
	// makes the Hover Rect bigger and then smaller gives it "pop" 
	public void tryAnimate(ArrayList<GamePiece> gamePieces) {
		boolean onNoGamePiece = true;
		for(GamePiece curGP : gamePieces) {
			if(curGP.boardRect == this) {
				onNoGamePiece = false;
			}
		}
		if(isPossibleMove || !onNoGamePiece || isDestructibleWall) {
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
		g2d.setColor(new Color(20,20,20));
		g2d.fill(rect);
		if(wallSprite != null) {
			wallSprite.drawSprite(g2d, centeredX, centeredY, 0, 1);
		}
	}
	// draws destructible wall but makes it transparent if it would overdraw a GamePiece
	public void drawDestructibleWall(Graphics2D g2d,ArrayList<GamePiece> gamePieces) {
		destructibleWallsprite.drawSprite(g2d, centeredX, centeredY, 0, 1);
	}
	// draws the BoardGaps
	public void drawGapWall(Graphics2D g2d) {
		// this color makes it more dark to make the depth effect
		g2d.setColor(new Color(0,0,0,180));
		g2d.fill(rect);
	}
	
	public void drawGapWater(Graphics2D g2d) {
		g2d.setColor(new Color(18,48,156));
		g2d.fill(rect);	
	}
	
	public void drawIndex(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.PLAIN,15));
		if(!isTile1) {
			g2d.setColor(new Color(30,30,30));
		}else {
			g2d.setColor(new Color(200,200,200));
		}
		if(isGap) {
			g2d.setColor(Color.WHITE);
		}
		g2d.drawString(index+"", centeredX, centeredY);
	}
	// returns the resized image
	public Image resizeImage(String imageString) {
		ImageIcon imageIcon = new ImageIcon(imageString);
		Image image = imageIcon.getImage();
		Image modImage = image.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(modImage);
		return imageIcon.getImage();
	}
	
	public int getCenterX(){
		return x+size/2;
	}
	public int getCenterY(){
		return y+size/2;
	}
	
}
