package com.ynot.androidengine;

import java.util.ArrayList;
import java.util.List;

import com.ynot.arobsvacation.GameObject;

/*************************************************************************************************
 * A spatial hash grid is an invisible grid laid over a 2D game world.  The number of cells
 * in the grid is determined by the cell size and the width/height of the game world.  It is
 * recommended that each cell should be 5 times larger than the largest collidable object in
 * the game.
 * <p>
 * The purpose of the grid is to limit the number of collision checks that must be performed
 * each frame.  Using the grid, a game object (such as the player's character) only needs to
 * check for collisions against other objects that occupy the same cell(s) (a single object
 * can occupy up to four cells if the above cell size guideline is followed).  This prevents
 * having to check for a collision against every object in the game, thus increasing performance
 * significantly.
 * 
 * @author A.Rob
 * @credit Mario Zechner "Beginning Android Games" 
 *************************************************************************************************/
public class SpatialHashGrid 
{
	List<GameObject> foundObjects;    // List of objects that might collide at the moment
	
	List<GameObject>[] dynamicCells;  // List of game objects that CAN change position
	List<GameObject>[] staticCells;   // List of game objects that CANNOT change position
	
	int cellsPerRow;                  // # of cells in each row
	int cellsPerCol;                  // # of cells in each column
	int objectsPerCell = 10;          // Max # of game objects per cell
	
	int[] cellIDs = new int[4];       // Max # of cells that a single object can occupy
	
	float cellSize;                   // The width or height of each cell (each cell is a square)
	
	/******************************************************************************
	 * Constructs a new SpatialHashGrid object
	 * 
	 * @param worldWidth - The width of the game world
	 * @param worldHeight - The height of the game world
	 * @param cellSize - The width or height of each cell (each cell is a square)
	 ******************************************************************************/
	@SuppressWarnings("unchecked")
	public SpatialHashGrid(float worldWidth, float worldHeight, float cellSize)
	{
		this.cellSize = cellSize;
		this.cellsPerRow = (int) Math.ceil(worldWidth / cellSize);
		this.cellsPerCol = (int) Math.ceil(worldHeight / cellSize);
		
		// Determine total # of cells
		int numCells = cellsPerRow * cellsPerCol;
		dynamicCells = new List[numCells];
		staticCells = new List[numCells];
		
		// Give each cell 2 lists of game objects
		for (int i = 0; i < numCells; i++)
		{
			dynamicCells[i] = new ArrayList<GameObject>(objectsPerCell);
			staticCells[i] = new ArrayList<GameObject>(objectsPerCell);
		}
		foundObjects = new ArrayList<GameObject>(objectsPerCell);
	}
	
	/*******************************************************
	 * Adds a static game object to the appropriate cell(s)
	 * 
	 * @param obj - The game object to add
	 *******************************************************/
	public void insertStaticObject(GameObject obj)
	{
		// Determine which cell(s) the game object occupies
		int[] cellIDs = getCellIDs(obj);
		
		int i = 0;
		int cellID = -1;
		
		// An object can occupy 4 cells at most
		while (i <= 3 && (cellID = cellIDs[i++]) != -1)
		{
			// Add the object as a static occupant of this cell
			staticCells[cellID].add(obj);
		}
	}
	
	/*******************************************************
	 * Adds a dynamic game object to the appropriate cell(s)
	 * 
	 * @param obj - The game object to add
	 *******************************************************/
	public void insertDynamicObject(GameObject obj)
	{
		// Determine which cell(s) the game object occupies
		int[] cellIDs = getCellIDs(obj);
		
		int i = 0;
		int cellID = -1;
		
		// An object can occupy 4 cells at most
		while (i <= 3 && (cellID = cellIDs[i++]) != -1)
		{
			// Add the object as a dynamic occupant of this cell
			dynamicCells[cellID].add(obj);
		}
	}
	
	/*********************************************************
	 * Removes a game object from all cells that it occupies
	 * 
	 * @param obj - The game object to remove
	 *********************************************************/
	public void removeObject(GameObject obj)
	{
		// Determine which cell(s) the game object occupies
		int[] cellIDs = getCellIDs(obj);
		
		int i = 0;
		int cellID = -1;
		
		// An object can occupy 4 cells at most
		while (i <= 3 && (cellID = cellIDs[i++]) != -1)
		{
			// Remove the object from this cell's lists of objects
			staticCells[cellID].remove(obj);
			dynamicCells[cellID].remove(obj);
		}
	}
	
	/*********************************************************************
	 * Removes all dynamic objects from the grid.  This should be called
	 * each frame before dynamic objects are reinserted as they move 
	 * across the grid.
	 *********************************************************************/
	public void clearDynamicCells()
	{
		int len = dynamicCells.length;
		for (int i = 0; i < len; i++)
		{
			// Remove all dynamic objects from this cell
			dynamicCells[i].clear();
		}
	}
	
	/************************************************************************************
	 * Returns a list of objects contained in the same cell(s) as the specified object.
	 * 
	 * @param obj - The game object that needs to check for collisions
	 * @return The list of potential collisions
	 ************************************************************************************/
	public List<GameObject> getPotentialCollisions(GameObject obj)
	{
		foundObjects.clear();  // Clear the previous list
		
		// Determine which cell(s) the game object occupies
		int[] cellIDs = getCellIDs(obj);
		int i = 0;
		int cellID = -1;
		
		// An object can occupy 4 cells at most
		while (i <= 3 && (cellID = cellIDs[i++]) != -1)
		{
			// Get all of the dynamic objects in this cell
			int len = dynamicCells[cellID].size();
			for (int j = 0; j < len; j++)
			{
				// Add the dynamic object to the list of potential collisions
				GameObject collider = dynamicCells[cellID].get(j);
				if (!foundObjects.contains(collider))
					foundObjects.add(collider);
			}
			
			// Get all of the static objects in this cell
			len = staticCells[cellID].size();
			for (int j = 0; j < len; j++)
			{
				// Add the static object to the list of potential collisions
				GameObject collider = staticCells[cellID].get(j);
				if (!foundObjects.contains(collider))
					foundObjects.add(collider);
			}
		}
		
		return foundObjects;
	}
	
	/****************************************************************
	 * Finds the cell IDs that the specified game object occupies.
	 * 
	 * @param obj - The game object to search for
	 * @return An array of the cell IDs occupied by the game object
	 ****************************************************************/
	public int[] getCellIDs(GameObject obj)
	{
		// Get the (x,y) coords of the bottom left corner of the game object's bounding rectangle
		// Divide by cellSize to get grid coords
		int x1 = (int) Math.floor(obj.bounds.lowerLeft.x / cellSize);
		int y1 = (int) Math.floor(obj.bounds.lowerLeft.y / cellSize);
		
		// Get the (x,y) coords of the top right corner of the game object's bounding rectangle
		// Divide by cellSize to get grid coords
		int x2 = (int) Math.floor((obj.bounds.lowerLeft.x + obj.bounds.width) / cellSize);
		int y2 = (int) Math.floor((obj.bounds.lowerLeft.y + obj.bounds.height) / cellSize);
		
		if (x1 == x2 && y1 == y2)  // OBJECT IS IN A SINGLE CELL
		{
			if (x1 >= 0 && x1 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol)
			{
				cellIDs[0] = x1 + y1 * cellsPerRow;
			}
			else
			{
				cellIDs[0] = -1;
			}
			cellIDs[1] = -1;
			cellIDs[2] = -1;
			cellIDs[3] = -1;
		}
		else if (x1 == x2)  // OBJECT OVERLAPS TWO CELLS HORIZONTALLY
		{
			int i = 0;
			if (x1 >= 0 && x1 < cellsPerRow)
			{
				if (y1 >= 0 && y1 < cellsPerCol)
				{
					cellIDs[i++] = x1 + y1 * cellsPerRow;
				}
				if (y2 >= 0 && y2 < cellsPerCol)
				{
					cellIDs[i++] = x1 + y2 * cellsPerRow;
				}
			}
			while (i <= 3) cellIDs[i++] = -1;
		}
		else if (y1 == y2)  // OBJECT OVERLAPS TWO CELLS VERTICALLY
		{
			int i = 0;
			if (y1 >= 0 && y1 < cellsPerCol)
			{
				if (x1 >= 0 && x1 < cellsPerRow)
				{
					cellIDs[i++] = x1 + y1 * cellsPerRow;
				}
				if (x2 >= 0 && x2 < cellsPerRow)
				{
					cellIDs[i++] = x2 + y1 * cellsPerRow;
				}
			}
			while (i <= 3) cellIDs[i++] = -1;
		}
		else  // OBJECT OVERLAPS FOUR CELLS
		{
			int i = 0;
			int y1CellsPerRow = y1 * cellsPerRow;
			int y2CellsPerRow = y2 * cellsPerRow;
			
			if (x1 >= 0 && x1 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol)
			{
				cellIDs[i++] = x1 + y1CellsPerRow;
			}
			if (x2 >= 0 && x2 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol)
			{
				cellIDs[i++] = x2 + y1CellsPerRow;
			}
			if (x2 >= 0 && x2 < cellsPerRow && y2 >= 0 && y2 < cellsPerCol)
			{
				cellIDs[i++] = x2 + y2CellsPerRow;
			}
			if (x1 >= 0 && x1 < cellsPerRow && y2 >= 0 && y2 < cellsPerCol)
			{
				cellIDs[i++] = x1 + y2CellsPerRow;
			}
			while (i <= 3) cellIDs[i++] = -1;
		}
		
		return cellIDs;
	}
	
	// TODO: setObjectsPerCell()
}

