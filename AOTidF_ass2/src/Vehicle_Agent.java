import jade.core.Agent;

public class Vehicle_Agent extends Agent{
	//Battery status of the agent
	private int battery_life;
	// Goal to save money "m" or time "t"
	private String goal;
	
	//Vehicle Initialization
	protected void setup() {
		// msg of created agent
		System.out.println("Vehicle Agent "+getAID().getName()+" is ready.");

		/** TODO: 
		 * stages of battery life
		 * 100-80% - agent decide to go to a charging station near 
		 */
	}

}
