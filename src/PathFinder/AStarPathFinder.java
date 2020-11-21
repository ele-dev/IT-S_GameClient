package PathFinder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class AStarPathFinder {
	public ArrayList<PathCell> pathCells = new ArrayList<PathCell>();
	private PathCell startPathCell;
	private PathCell endPathCell;
	
	private ArrayList<PathCell> openSetPathCells = new ArrayList<PathCell>();
	private ArrayList<PathCell> closedSetPathCells = new ArrayList<PathCell>();
	
	private ArrayList<PathCell> pathPathCells = new ArrayList<PathCell>();
	boolean isDone = false;
	public boolean noSolution = false;
	
	public AStarPathFinder(ArrayList<PathCell> pathCells) {
		this.pathCells = pathCells;
		for(PathCell curPC : this.pathCells) {
			curPC.initAdjecantGridCells(this.pathCells);
		}
	}
	
	public ArrayList<PathCell> getPathPathCells(){
		return pathPathCells;
	}
	
	public void setPathEnds(PathCell startPathCell, PathCell endPathCell) {
		this.startPathCell = startPathCell;
		this.endPathCell = endPathCell;
		resetPath();
	}
	
	private void resetPath() {
		pathPathCells.clear();
		closedSetPathCells.clear();
		openSetPathCells.clear();
		isDone = false;
		noSolution = false;
		openSetPathCells.add(startPathCell);
		while (!isDone && !noSolution) {
			updatePathFinder();
		}
	}
	
	// draws all the PathCells and draws them in different Color if they are wall,start,end,path...
	public void drawPathFinder(Graphics2D g2d) {
		if(startPathCell != null && endPathCell != null) {
			for(PathCell curPC : openSetPathCells) {
				g2d.setColor(Color.GREEN);
				g2d.fill(curPC.getRect());
			}
			for(PathCell curPC : closedSetPathCells) {
				g2d.setColor(Color.RED);
				g2d.fill(curPC.getRect());
			}
			for(int i = 0;i<pathPathCells.size();i++) {
				g2d.setColor(Color.ORANGE);
				g2d.fill(pathPathCells.get(i).getRect());
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("Arial",Font.BOLD,10));
				g2d.drawString(i+"", pathPathCells.get(i).getX(), pathPathCells.get(i).getY()+pathPathCells.get(i).getSize()/2);
	
			}
			g2d.setColor(Color.BLUE);
			g2d.fill(startPathCell.getRect());
			g2d.setColor(Color.MAGENTA);
			g2d.fill(endPathCell.getRect());
		}
	}
	
	// function which tries to find the shortest path (needs to be iterated a lot of times)
	public void updatePathFinder() {
		// if there are still unexplored PathCells
		if(openSetPathCells.size() > 0) {
			int wIndex = 0;
			for(int i = 0;i<openSetPathCells.size();i++) {
				PathCell curPC = openSetPathCells.get(i);
				if(curPC.f < openSetPathCells.get(wIndex).f) {
					// finds the lowest f costing Cell-Index in the unexplored cells list
					wIndex = i;
				}
			}
			PathCell current = openSetPathCells.get(wIndex);
			// checks if the lowest cost PathCell is the endCell if yes then the Path is Done
			if(current == endPathCell) {
//				System.out.print("Done with pathfinding!");
				isDone = true;
				
			}
			
			// removes the lowest f costing PathCell from the unexplored cell list and adds it to the explored cells list
			openSetPathCells.remove(wIndex);
			closedSetPathCells.add(current);
			
			// adds all the neighboring cells of the current PathCell to the unexplored cells list (only if they are not already explored)
			// it also calculates all the cost of the neighboring cells
			// in case they are already in the unexplored cells list it checks if it has found a lower cost for that cell and calculates it
			// then each of the neighbor cells get asigned a parent which is the current cell(lowest f costing yet unexplored)
			for(PathCell curAPC : current.adjecantPathCells) {
				// only considers non-wall-cells
				if(!closedSetPathCells.contains(curAPC) && !curAPC.isWall) {
					float tempG = current.g+1;
					if(openSetPathCells.contains(curAPC)) {
						if(tempG < curAPC.g) {
							curAPC.g = tempG;
						}
					}else {
						curAPC.g = tempG;
						openSetPathCells.add(curAPC);
					}
					
					curAPC.h = getHeuristic(curAPC);
					curAPC.f = curAPC.g + curAPC.h;
					curAPC.parentGridCell = current;
				}	
			}
			// Trace The Path
			pathPathCells.clear();
			PathCell tempGC = current;
			pathPathCells.add(tempGC);
			// traces up the tree trough the parent until it finds the cell that has no parent(this is the starting cell)
			while (tempGC.parentGridCell != null) {
				pathPathCells.add(tempGC.parentGridCell);
				tempGC = tempGC.parentGridCell;
			}
		} else {
			// no solution
			noSolution = true;
//			System.out.println("No path solution");
		}
	}
	
	private float getHeuristic(PathCell curPC) {
		int ak = endPathCell.getColumn() - curPC.getColumn();
		int gk = endPathCell.getRow() - curPC.getRow();
		return ak+gk;
	}
	
	
}
