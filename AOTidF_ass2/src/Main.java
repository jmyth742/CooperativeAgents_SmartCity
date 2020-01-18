import jade.core.Runtime;
import jade.core.ProfileImpl;

import java.io.IOException;

import jade.core.Profile;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import simulation.*;

/**
 * 
 * @author Aristeidis Noulis, Jonathan Smyth, Cesar Gonzalez, Veranika Paulava
 * 
 * Main Class creates a JADE Main Container and a 2D Simulation grid
 * Assignment2: Cooperative Electric Vehicles in the Electric Grid 
 *
 */
public class Main {
	
	//number of ChargingStation
	private final static int NUM_OF_CS = 20;
	//number of Vehicle agents
	private final static int NUM_OF_V = 200;

	//depth and width of the simulation window
	private final static int DEPTH = 50;
	private final static int WIDTH = 50;
	
	public static void main(String [] args) throws IOException{
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();
		p.setParameter(Profile.MAIN_HOST, "localhost");
		//Show JIAC GUI
//		p.setParameter(Profile.GUI, "true");
		ContainerController cc = rt.createMainContainer(p);
		
		//Start SImulation
		Simulator simulator = new Simulator(DEPTH, WIDTH, cc, NUM_OF_CS, NUM_OF_V);		
		
		//number of steps (=minutes) to simulate - full day with 24 hours
		int numofSteps = 24*60;
		simulator.simulate(numofSteps);
		
	}

}
