import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.core.Profile;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import simulation.*;

public class Main {
	
	private final static int NUM_OF_CS = 10;
	private final static int NUM_OF_V = 1;

	private final static int DEPTH = 30;
	private final static int WIDTH = 30;
	/**
	 * @param args
	 * The core code for yellow pages is done.Should be expanded.
	 * After that the assignments should be done.
	 * The simple examples logic is that:
	 * We have 3 Vehicles and 3 Stations.
	 * We do initializations
	 * We assign the bookings 
	 * As the time passes and one car wants to charge again search and negotiate
	 * @throws InterruptedException 
	 * 
	 */
	public static void main(String [] args){
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();
		p.setParameter(Profile.MAIN_HOST, "localhost");
//		p.setParameter(Profile.GUI, "true");
		ContainerController cc = rt.createMainContainer(p);
		
		Simulator simulator = new Simulator(DEPTH, WIDTH, cc, NUM_OF_CS, NUM_OF_V);
		simulator.simulate(100);
		
		
	}

}
