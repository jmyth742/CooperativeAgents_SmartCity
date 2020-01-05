package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import charging.station.Charging_Station_Agent;

import java.util.*;

/**
 * 
 * @author Aristeidis Noulis, Jonathan Smyth, Cesar Gonzalez, Veranika Paulava
 * A 2D Field of Objects. At each Location (row,col) can on Object in our case agent
 * be positioned. 
 * Also the Field stores information where is a street 
 *
 */
public class Field
{    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][] field;
    
    //matrix of the street. If at a location is a street the value is 1 otherwise 0
    public int[][] street;
    /**
	 * 
	 * @param depth
	 * @param width
	 */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Object[depth][width];
        street = new int[depth][width];
    }
    
    /**
     * Empty the field. Remove all Objects from field
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }
    
    /**
     * @param row
     * @param col
     * 
     * Mark street a the location (row,col)
     */
    public void setStreet(int row, int col) {
    	street[row][col] = 1;
    }
    
    /**
     * @param row
     * @param col
     * 
     * Remove street a location (row, col)
     */
    public void resetStreet(int row, int col) {
    	street[row][col] = 0;
    }
    
    /**
     * 
     * @param row
     * @param col
     * @return street value (1 or 0) of a location
     */
    public int getStreetAt(int row, int col) {
    	return street[row][col];
    }
    
    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }
    
    
    /**
     * Place an agent at the given location.
     * If there is already an agent at the location it will
     * be lost.
     * @param agent The agent to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object agent, int row, int col)
    {
        place(agent, new Location(row, col));
    }

    /**
     * Place an agent at the given location.
     * If there is already an agent at the location it will
     * be lost.
     * @param agent The agent to be placed.
     * @param location Where to place the agent.
     */
    public void place(Object agent, Location location)
    {
        field[location.getRow()][location.getCol()] = agent;
    }
    
    /**
     * Return the agent at the given location, if any.
     * @param location Where in the field.
     * @return The agent at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the agent at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The agent at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }
        /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Search through the field and create a List of all existing charging Stations in the fields.
     *  
     * @return List of Charging_Station_Agents
     * 
     */
    public List<Charging_Station_Agent> getAllCSList() {
    	List<Charging_Station_Agent> CSLocationList= new ArrayList<Charging_Station_Agent>();
    	for(int row = 0; row < depth; row++) {
    		for(int col = 0; col < width; col++) {
    			if(getObjectAt(row,col) != null && getObjectAt(row, col).getClass() == charging.station.Charging_Station_Agent.class) {
    					CSLocationList.add((Charging_Station_Agent) getObjectAt(row,col));
    			}
    		}
    	}
    	return CSLocationList;	
    }
    
    public List<Charging_Station_Agent> nearestChargingStation(Location location, int numberofCS) {
    	List<Charging_Station_Agent> CSLocationList = getAllCSList();
    	
        Comparator<Charging_Station_Agent> CSdistanceComparator = new Comparator<Charging_Station_Agent>() {
            @Override
            public int compare(Charging_Station_Agent e1, Charging_Station_Agent e2) {
                return compareTo(e1, e2, location);
            }
        };
        
        Collections.sort(CSLocationList, CSdistanceComparator);
    	
    	for(int i = 0; i < CSLocationList.size(); i++) {
    		int x1 = location.getRow();
    		int y1 = location.getCol();
    		int x2 = (int) CSLocationList.get(i).getLocation().getRow();
    		int y2 = (int) CSLocationList.get(i).getLocation().getCol();
    		double distance = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    		
    		if(distance <= numberofCS) {
    			nearestCS.add(CSLocationList.get(i));
    		}
    	}
    	
    	return nearestCS.get(1);

    	return CSLocationList.subList(0, numberofCS);
    }

    
    public int compareTo(Charging_Station_Agent cs1, Charging_Station_Agent cs2, Location location) {
    	double distance1 = BFS(location, cs1.getLocation());
    	double distance2 = BFS(location, cs2.getLocation());
		
        if(distance1 < distance2) {
            return -1;
        } else if (distance1 > distance2) {
            return 1;
        } else {
            return 0;
        }
    	return CSLocationList.subList(0, numberofCS);
    }
   
    /**
     * Everything what is neccesary to compute the shortest path between two points in the street grid
     * Uses the BFS Algorithmus
     */
    
	// These arrays are used to get row and column 
	// numbers of 4 neighbours of a given cell 
	static int rowNum[] = {-1, 0, 0, 1}; 
	static int colNum[] = {0, -1, 1, 0}; 
	  
	// function to find the shortest path between 
	// a given source cell to a destination cell. 
	public int BFS(Location src, 
	                            Location dest) 
	{ 
		List<Location> path = new ArrayList<Location>();
	    // check source and destination cell 
	    // of the matrix have value 1 
	    if (street[src.getRow()][src.getCol()] != 1 ||  
	        street[dest.getRow()][dest.getCol()] != 1) 
	        return -1; 
	  
	    boolean [][]visited = new boolean[width][depth]; 
	      
	    // Mark the source cell as visited 
	    visited[src.getRow()][src.getCol()] = true; 
	  
	    // Create a queue for BFS 
	    Queue<queueNode> q = new LinkedList<>(); 
	      
	    // Distance of source cell is 0 
	    queueNode s = new queueNode(src, 0); 
	    q.add(s); // Enqueue source cell 
	  
	    // Do a BFS starting from source cell 
	    while (!q.isEmpty()) 
	    { 
	        queueNode curr = q.peek(); 
	        Location pt = curr.pt; 
	  
	        // If we have reached the destination cell, 
	        // we are done 
	        if (pt.getRow() == dest.getRow() && pt.getCol() == dest.getCol()) {
	        	
	        	return curr.dist;
	        }
	            
	        // Otherwise dequeue the front cell  
	        // in the queue and enqueue 
	        // its adjacent cells 
	        q.remove(); 
	  
	        for (int i = 0; i < 4; i++) 
	        { 
	            int row = pt.getRow() + rowNum[i]; 
	            int col = pt.getCol() + colNum[i]; 
	              
	            // if adjacent cell is valid, has path  
	            // and not visited yet, enqueue it. 
	            if (isValid(row, col) &&  
	                    street[row][col] == 1 &&  
	                    !visited[row][col]) 
	            { 
	                // mark cell as visited and enqueue it 
	                visited[row][col] = true; 
	                queueNode Adjcell = new queueNode(new Location(row, col), 
	                                                      curr.dist + 1 ); 
	                q.add(Adjcell); 
	            } 
	        } 
	    } 
	  
	    // Return -1 if destination cannot be reached 
	    return -1; 
		}
	
		// check whether given cell (row, col)  
		// is a valid cell or not. 
		public boolean isValid(int row, int col) 
		{ 
		    // return true if row number and  
		    // column number is in range 
		    return (row >= 0) && (row < width) && 
		           (col >= 0) && (col < depth) && 
		           (street[row][col] == 1); 
		} 
		  
		


	// A Data Structure for queue used in BFS 
	static class queueNode 
	{ 
	    Location pt; // The cordinates of a cell 
	    int dist; // cell's distance of from the source 
	  
	    public queueNode(Location pt, int dist) 
	    { 
	        this.pt = pt; 
	        this.dist = dist; 
	    } 
	};
}

