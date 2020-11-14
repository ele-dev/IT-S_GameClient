package Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import Environment.DestructibleObject;
import GamePieces.GamePiece;
import PlayerStructures.GoldMine;

public class BoardRectangle {
	public int row,column;
	private Color c;
	public Rectangle rect;
	public Color cPossibleMove,cPossibleAttack,cPossibleAbility;
	int index;
	public boolean isTile1;
	public boolean isPossibleMove,isPossibleAttack;
	public boolean isShowPossibleMove,isShowPossibleAttack;
	public boolean isWall,isGap,isHinderingTerrain;
	
	private boolean isHover;
	private double animationSpeed;
	public float so = 4;
	private int rotation = 0; 
	
	private Sprite wallSprite;
	private Sprite groundSprite;
	private float spriteRotation;
	
	public BoardRectangle northBR,southBR,eastBR,westBR;
	ArrayList<BoardRectangle> adjecantBoardRectangles = new ArrayList<BoardRectangle>();
	
	String[] extendedInfoStrings = new String[7];
	
	public BoardRectangle(int row, int column,boolean isTile1,int index,boolean isHinderingTerrain) {
		this.row = row;
		this.column = column;
		int size = StagePanel.boardRectSize;
		rect = new Rectangle(column*size,row*size,size,size);
		int alpha = 200;
		cPossibleMove = new Color(Commons.cMove.getRed(),Commons.cMove.getGreen(),Commons.cMove.getBlue(),alpha);
		cPossibleAttack = new Color(Commons.cAttack.getRed(),Commons.cAttack.getGreen(),Commons.cAttack.getBlue(),alpha);
		this.isTile1 = isTile1;
		this.index = index;
		this.isHinderingTerrain = isHinderingTerrain;
		
		c = isTile1?new Color(10,10,10):new Color(200,200,200);
		 
//		spriteRotation = (byte) (Math.random()*4)* 90;
//		ArrayList<String> spriteLinks = new ArrayList<String>();
//		String spriteDirector = "";
//		String biomType = "Grass";
//		if(Math.random() < 0.3) {
//			spriteDirector = "Tiles/"+biomType+"Tile0.png";
//		}else if(Math.random() < 0.5){
//			spriteDirector = "Tiles/"+biomType+"Tile1.png";
//		}else {
//			spriteDirector = "Tiles/"+biomType+"Tile2.png";
//		}
//		if(isHinderingTerrain) {
//			spriteDirector = "Tiles/MudTile.png";
//		}
//		spriteLinks.add(Commons.pathToSpriteSource+spriteDirector);
//		groundSprite = new Sprite(spriteLinks, size,size, 0);
		initExtendedInfoStrings();
	}
	
	private void initExtendedInfoStrings() {
		extendedInfoStrings[0] = "just a basic tile\ncannot be attacked\ncan be moved on";
		extendedInfoStrings[1] = "a gap in the landscape\ncannot be attacked\ncannot be moved on\ncan be seen through";
		extendedInfoStrings[2] = "movement hindering terrain\ncannot be attacked\ncan be moved on\ncosts double movement to cross.";
		extendedInfoStrings[3] = "a basic wall\ncannot be attacked\ncannot be moved on.\ncannot be seen through.";
		extendedInfoStrings[4] = "object that can be destroyed.\ncan be attacked.\ncannot be moved on.";
		extendedInfoStrings[5] = "goldmine that collects gold every turn for team that captures it\ncan be attacked (only if captured by opposing team)\ncannot be moved on.\nmove next to goldmine to capture it (if not captured already).";
		extendedInfoStrings[6] = "main base of players\ncan be attacked\ncannot be moved on\ndestroy enemy base to win\nleft click on own base to recruit new units";
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
	
	public ArrayList<BoardRectangle> getAdjecantBoardRectangles() {
		return adjecantBoardRectangles;
	}
	public boolean isDestructibleObject() {
		if(StagePanel.redBase != null && StagePanel.redBase.containsBR(this)) return true;
		if(StagePanel.blueBase != null && StagePanel.blueBase.containsBR(this)) return true;
		for(DestructibleObject curDO : StagePanel.destructibleObjects) {
			if(curDO.containsBR(this)) {
				return true;
			}
		}
		return false;
	}
	public boolean isGoldMine() {
		for(GoldMine curGM : StagePanel.goldMines) {
			if(curGM.containsBR(this)) {
				return true;
			}
		}
		return false;
	}
	
	public GamePiece getGamePieceOnIt() {
		for(GamePiece curGP : StagePanel.gamePieces) {
			if(curGP.boardRect == this) {
				return curGP;
			}
		}
		return null;
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
	public static int getDistanceBetweenBRs(int row1, int column1,int row2, int column2) {
		return Math.abs(row1 - row2)+Math.abs(column1 - column2);
	}
	
	
	// initializes what Sprite is needed depending on neighboring walls
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
				} else if (upConnected) {
					ArrayList<String> spriteLinks2 = new ArrayList<String>();
					spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LRU.png");
					wallSprite = new Sprite(spriteLinks2, size,size, 0);
				}
			} else
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
			}else if(downConnected) {
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_LD.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
			}
		}else 
		if(upConnected) {
			ArrayList<String> spriteLinks2 = new ArrayList<String>();
			spriteLinks2.add(Commons.pathToSpriteSource+"Tiles/DarkWall_U.png");
			wallSprite = new Sprite(spriteLinks2, size,size, 0);
			if(downConnected) {
				ArrayList<String> spriteLinks1 = new ArrayList<String>();
				spriteLinks1.add(Commons.pathToSpriteSource+"Tiles/DarkWall_UD.png");
				wallSprite = new Sprite(spriteLinks1, size,size, 0);
			}
		}else 
		if(downConnected) {
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
	public void drawBoardRectangle(Graphics2D g2d) {
		g2d.setColor(isGap?new Color(35,137,218):isHinderingTerrain?Color.MAGENTA:c);
		g2d.fill(rect);
			
		if(groundSprite != null && !isGap && !isWall) {
			groundSprite.drawSprite(g2d, getCenterX(), getCenterY(), spriteRotation, 1);
		}
		
		g2d.setColor(new Color(0,0,0,100));
		g2d.fill(rect);
		
	}
	
	public void drawState(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(2));
		if(isPossibleMove || isPossibleAttack) {
			g2d.setColor(new Color(10,10,10,100));
			g2d.fill(rect);
			
			g2d.setColor(isPossibleMove?cPossibleMove:isPossibleAttack?cPossibleAttack:cPossibleAbility);
			g2d.fill(rect);
			g2d.setColor(new Color(5,5,5));
			g2d.draw(rect);
		}
		if((isShowPossibleMove && !isPossibleMove) || (isShowPossibleAttack && !isPossibleAttack)) {
			g2d.setStroke(new BasicStroke(4));
			g2d.setColor(new Color(10,10,10,100));
			g2d.fill(rect);
			g2d.setColor(isShowPossibleMove?new Color(cPossibleMove.getRed(),cPossibleMove.getGreen(),cPossibleMove.getBlue(),200):
				new Color(cPossibleAttack.getRed(),cPossibleAttack.getGreen(),cPossibleAttack.getBlue(),200));
			
			
			g2d.draw(rect);
			for(int i = 0;i<getSize();i+=getSize()/5) g2d.drawLine(getX()+i, getY(), getX(), getY()+i);
			for(int i = 0;i<getSize();i+=getSize()/5) g2d.drawLine(getX()+i, getY()+getSize(), getX()+getSize(), getY()+i);
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
			g2d.translate(-x, -y);
		}
	}
	
	// draws the Label of the BoardRectangle (only ifHover)
	// also draws additional info of state if ctrl key is held
	public void drawLabel(Graphics2D g2d, boolean ctrDown) {
		String labelState = "Basic Tile";
		String extendedInfoString = extendedInfoStrings[0];
		GamePiece gamePieceOnBoardRectangle = getGamePieceOnIt();
		if(isGap) {
			labelState = "Gap";
			extendedInfoString = extendedInfoStrings[1];
		}else if(isHinderingTerrain){
			labelState = "Hindering Terrain";
			extendedInfoString = extendedInfoStrings[2];
		}else if(isWall){
			labelState = "Wall";
			extendedInfoString = extendedInfoStrings[3];
		}
		if(isDestructibleObject()) {
			labelState = "Destructible Object";
			extendedInfoString = extendedInfoStrings[4];
		}
		if(isGoldMine()) {
			labelState = "GoldMine";
			extendedInfoString = extendedInfoStrings[5];
		}
		if(StagePanel.blueBase.containsBR(this) || StagePanel.redBase.containsBR(this)) {
			labelState = "PlayerFortress";
			extendedInfoString = extendedInfoStrings[6];
		}
		if(gamePieceOnBoardRectangle != null) {
			labelState = gamePieceOnBoardRectangle.getName();
		}
		
		int size = rect.width;
		
		g2d.setFont(new Font("Arial",Font.PLAIN,size/3));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		int textWidth = fontMetrics.stringWidth(labelState);
		int border = textHeight/3;
		int rectWidth = textWidth;
		String[] extendedInfoStringSplit = null;
		if(ctrDown && gamePieceOnBoardRectangle == null) {
			extendedInfoStringSplit = extendedInfoString.split("\n");
			for(String str:extendedInfoStringSplit) {
				int w = g2d.getFontMetrics(new Font("Arial",Font.PLAIN,size/4)).stringWidth(str);
				if(rectWidth<w) rectWidth = w;
			}
		}
		
		int shiftInYLength = ctrDown&&gamePieceOnBoardRectangle == null?size/2+g2d.getFontMetrics(new Font("Arial",Font.PLAIN,size/4)).getHeight()*extendedInfoStringSplit.length:0;
		
		Rectangle rectLabel = new Rectangle(getCenterX()-rectWidth/2-border,getCenterY()-size-shiftInYLength,rectWidth+border*2,size/2);
		g2d.setColor(new Color(20,20,20,250));
		g2d.fill(rectLabel);
		g2d.setColor(new Color(10,10,10));
		g2d.setStroke(new BasicStroke(size/10));
		g2d.draw(rectLabel);
		
		g2d.setColor(gamePieceOnBoardRectangle == null?Color.WHITE:gamePieceOnBoardRectangle.getColor());
		g2d.drawString(labelState, (int)rectLabel.x+border, (int)rectLabel.getCenterY()+textHeight/3);
		
		
		if(gamePieceOnBoardRectangle != null) {
			return;
		}
		if(ctrDown) {
			g2d.setFont(new Font("Arial",Font.PLAIN,size/4));
			fontMetrics = g2d.getFontMetrics();
			textHeight = fontMetrics.getHeight();
			textWidth = fontMetrics.stringWidth(labelState);
			Rectangle rectLabelExtend = new Rectangle(rectLabel.x,rectLabel.y+rectLabel.height,rectWidth+border*2,textHeight*extendedInfoStringSplit.length+border);
			g2d.setColor(new Color(20,20,20,240));
			g2d.fill(rectLabelExtend);
			g2d.setColor(new Color(10,10,10));
			g2d.setStroke(new BasicStroke(size/10));
			g2d.draw(rectLabelExtend);
			g2d.setColor(Color.WHITE);
			int i = 0;
			for(String str : extendedInfoStringSplit) {
				textWidth = fontMetrics.stringWidth(str);
				g2d.drawString(str, (int)rectLabelExtend.x+border, (int)(rectLabelExtend.y+textHeight+i*textHeight));
				i++;
			}
			
		}else {
			g2d.setFont(new Font("Arial",Font.PLAIN,size/6));
			fontMetrics = g2d.getFontMetrics();
			textHeight = fontMetrics.getHeight();
			textWidth = fontMetrics.stringWidth("CTRL for Info");
			Rectangle rectLabelExtend = new Rectangle(rectLabel.x,rectLabel.y+rectLabel.height,textWidth,size/4);
			g2d.setColor(new Color(20,20,20,240));
			g2d.fill(rectLabelExtend);
			g2d.setColor(new Color(10,10,10));
			g2d.setStroke(new BasicStroke(size/10));
			g2d.draw(rectLabelExtend);
			g2d.setColor(Color.WHITE);
			g2d.drawString("CTRL for Info", (int)rectLabelExtend.getCenterX()-textWidth/2, (int)rectLabelExtend.getCenterY()+textHeight/3);
			
		}
		
	}
	
	// makes the Hover Rect bigger and then smaller gives it "pop" 
	public void tryAnimate() {
		if(rotation > 360) {
			rotation = 0;
		}
		rotation++;
		if(isPossibleAttack) {
			if(so < 5) {
				animationSpeed = 0.3;
			} else if(so > 10) {
				animationSpeed = -0.3;
			}
			so+=animationSpeed;
		} else {
			so = 4;
		}
	}
	// updates the Hover boolean to be Hover == true if the mouse is on the BoardRectangle
	public void updateHover(Point mousePos) {
		isHover = rect.contains(mousePos);
	}
	// draws wall but makes it transparent if it would overdraw a GamePiece
	public void drawWall(Graphics2D g2d,ArrayList<GamePiece> gamePieces) {
		if(wallSprite != null) {
			wallSprite.drawSprite(g2d, getCenterX(), getCenterY(), 0, 1);
		} else {
			g2d.setColor(new Color(0,0,0));
			g2d.fill(rect);
			g2d.setColor(new Color(10,10,10));
			g2d.setStroke(new BasicStroke(6));
			g2d.draw(rect);
		}
	}
	
	public void drawPossibleRecruitPlace(Graphics2D g2d) {
		g2d.setColor(new Color(10,10,10,100));
		g2d.fill(rect);
		g2d.setColor(new Color(0,255,50,200));
		g2d.setStroke(new BasicStroke(4));
		if(StagePanel.curHoverBR != this) {
			g2d.draw(rect);
			for(int i = 0;i<getSize();i+=getSize()/5) g2d.drawLine(getX()+i, getY(), getX(), getY()+i);
			for(int i = 0;i<getSize();i+=getSize()/5) g2d.drawLine(getX()+i, getY()+getSize(), getX()+getSize(), getY()+i);
			
		} else {
			g2d.setColor(new Color(0,255,50,200));
			g2d.fill(rect);
		}
	}
	
	public void drawIndex(Graphics2D g2d) {
		g2d.setFont(new Font("Arial",Font.PLAIN,15));
		g2d.setColor(isTile1?new Color(200,200,200):new Color(30,30,30));
		g2d.drawString(index+"", getCenterX(), getCenterY());
	}
	
	
	// Public helper function to get the BoardRectangle with certain coordinates
	// on the game field that is stored in the stage panel class
	public static BoardRectangle getBoardRectFromCoords(Point pos) {
		BoardRectangle rect = null;
		
		// Go through the global list and search a match
		for(BoardRectangle currRect: StagePanel.boardRectangles)
		{
			if(currRect.row == pos.x && currRect.column == pos.y) {
				rect = currRect;
				break;
			}
		}
		
		return rect;
	}
}
