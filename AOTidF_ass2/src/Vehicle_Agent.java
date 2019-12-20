import java.util.*;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;



public class Vehicle_Agent extends Agent{
	//Battery status of the agent
	private int battery_life;
	// Goal to save money "m" or time "t"
	private String goal = RandomGoal();
	//Position of the agent
	
	//Vehicle Initialization
	protected void setup() {
		// msg of created agent
		System.out.println("Vehicle Agent "+getAID().getName()+" is ready.");

		/** TODO: 
		 * stages of battery life
		 * 100-80% - agent decide to go to a charging station near 
		 */
	}
	
	//Generates with 80% probability goal as "save money" (= "m")
	//and with 20% probability goal to "save time" (= "t")
	private String RandomGoal() {
		List<String> list = Collections.nCopies(8, "m");
		list.addAll(Collections.nCopies(2,"t"));
		
		Random rand = new Random();
		String randomgoal = list.get(rand.nextInt(list.size()));
		
		return randomgoal;
	}
	
	private class ChargingStationRequest extends Behaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
