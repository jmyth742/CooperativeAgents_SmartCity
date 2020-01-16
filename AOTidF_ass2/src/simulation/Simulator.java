package simulation;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vehicle.*;
import charging.station.*;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

public class Simulator {
	
	private static final int DEFAULT_WIDTH = 50;
	private static final int DEFAULT_DEPTH = 50;
	
	private int numofV = 10;
	private int numofCS = 100;
	public int dead_count = 0;
	
	private List<VehicleAgent> vehicles;
	private List<Charging_Station_Agent> chargingStations;
	
	//graphical view of the simulator
	private SimulatorView view;
	
	//current step of the simulation
	private int step;
	//current state of the field
	private Field field;
	
	private List<Location> streets;
	
	private AgentController ac;
	private ContainerController cc;

	Random random = new Random(3);
	
	public Simulator(int depth, int width, ContainerController cc, int numofCS, int numofV) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        this.numofCS = numofCS;
        this.numofV = numofV;
        this.cc = cc;
       

        vehicles = new ArrayList<>();
        chargingStations = new ArrayList<>();
        streets = new ArrayList<Location>();
        field = new Field(depth, width, chargingStations);
        

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(vehicle.VehicleAgent.class, Color.RED);
        view.setColor(charging.station.Charger.class, Color.BLUE);
        
        // Setup a valid starting point.
        reset();
     }
	
	public void reset() {
		step = 0;
		vehicles.clear();
		chargingStations.clear();

		init();
		view.showStatus(step, field);	
	}
	
	public void init() {
		field.clear();
		
		createVertStreets(4);
		createVehicleAgent(numofV);
		placeChargingStations(numofCS);
		
	}
	
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     * @throws IOException 
     * @throws InterruptedException 
     */
    public void simulate(int numSteps) throws IOException
    {
        for(int step=1; step <= numSteps; step++) {

        	simulateOneStep();
            delay(100);   // uncomment this to run more slowly
        }
        
        System.out.println("Dead count of vehicles: " + dead_count);
        
        for(int k =0; k< chargingStations.size(); k++) {
        	String[][] board = chargingStations.get(k).shedule;
        	
        	StringBuilder builder = new StringBuilder();
        	for(int i = 0; i < board.length; i++)//for each row
        	{
        	   for(int j = 0; j < board[0].length; j++)//for each column
        	   {
        		   if(board[i][j] == null)
        			   builder.append("0"+"");//append to the output string
        		   else
        			   builder.append("1"+"");
        	      if(j < board[0].length - 1)//if this is not the last row element
        	         builder.append(",");//then add comma (if you don't like commas you can use spaces)
        	   }
        	   builder.append("\n");//append new line at the end of the row
        	}
        	
        	File file = new File("./shedule/shedule" + k + ".txt");

        	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        	writer.write(builder.toString());//save the string representation of the board
        	writer.close();
        }
    }
    /**
     * all the pre-Bookings before the simulation
     */
    public void preBookings() {
//    	System.out.println("Prebookings .......");
//    	System.out.println("Number of Vehicles Agents " +  vehicles.size());
//    	System.out.println("Number of Stations Agents " +  chargingStations.size());
    	
    	
    }
	
	public void simulateOneStep() {
		System.out.println("----------------------------------------------------------------------------------------------");
		System.out.println("Step: " + step);
		
		step++;
		
		for(int i = 0; i < vehicles.size(); i++) {
			vehicles.get(i).step();
			if((int)vehicles.get(i).get_battery_life() == 0) {
				System.out.println("Vehicle died");
				vehicles.remove(vehicles.get(i));
				dead_count++;
				
			}
		}
		
		
		
		view.showStatus(step, field);
		
	}
	    
    private void createVertStreets(int n) {
    	for(int row = 1; row < field.getDepth()-1; row++) {
    		for( int col = 1; col < field.getWidth()-1; col++) {
    			if((col%n) == 0 || (row%n) == 0) {
    				field.setStreet(row, col);
    				streets.add(new Location(row, col));
    			}
    		}
    	}
    }
    
    private void createHorStreet(int row) {
    	for( int col = 1; col < field.getWidth()-1; col++) {
    		field.setStreet(row, col);
    		Location location = new Location(row, col);
    		if(!(streets.contains(location))) {
        		streets.add(new Location(row, col));
    		}
    	}	
    }

	public void createVehicleAgent(int numofVA){
		
    	for(int i =1; i <= numofVA; i++) {
    	    
    		Location randomLocation = streets.get(random.nextInt(streets.size()));
    		
    		if(field.getObjectAt(randomLocation) == null) {
        		VehicleAgent vehicle = new VehicleAgent(field, randomLocation);
        		vehicles.add(vehicle);
        		
       
        		try {
        			ac = cc.acceptNewAgent("VehicleAgent" + i, vehicle);
        			ac.start();
        		} catch (Exception e) {
        			System.out.println("Could not create Agent!");
        			e.printStackTrace();
        		}
        		
    		}
    	}
	}
	
	
    private void placeChargingStations(int numOfCS) {
    	
    	for(int i = 1; i <= numOfCS; i++) {
    		Charging_Station_Agent chargingStation = new Charging_Station_Agent();
    		
    		int fastChargers = random.nextInt(6) + 1; //between 1-5 fast charger
    		int slowChargers = random.nextInt(15) + 5; // between 5 - 15 slow charger
    		//slowChargers = 0;
    		//fastChargers = 1;
    		
    		double slowPrice = 2.00 + (1.00) * random.nextDouble(); // price between 1.00 - 2.00
    		
    		chargingStation.ChargingStation(field, fastChargers, slowChargers, slowPrice);
    		
    		int row = random.nextInt(field.getDepth() - 5) + 1;
    		int col = random.nextInt(field.getWidth() - 5) + 1;
    		

    		createHorStreet(row);
    		
    		for(int chargers = 0; chargers < chargingStation.chargers.size(); chargers++) {
    			if((chargers != 0) && (chargers % 4) == 0) {
    				row++;
    				col = col-4;
    			}

    			Location location = new Location(row, col);
    			if(chargers == 0) {
    				chargingStation.setLocation(location);

    			}
    			else {
    				chargingStation.chargers.get(chargers).setField(field);
        			chargingStation.chargers.get(chargers).setLocation(location);
    			}
        		if(field.street[row][col] == 1) {
    				field.resetStreet(row, col);
    				streets.remove(location);
    			}
    			
    			col++;
    		}
    		
    		
    		try {
    			ac = cc.acceptNewAgent("CSAgent" + i, chargingStation);
    			ac.start();
    		} catch (Exception e) {
    			System.out.println("Could not create Agent!");
    			e.printStackTrace();
    		}
    		

    		chargingStations.add(chargingStation);
    	}
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
