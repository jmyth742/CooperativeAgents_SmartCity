package vehicle;
import java.util.*;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;



public class VehicleAgent extends Agent{
	//Battery status of the agent
	private int battery_life;
	// Goal to save money "m" or save time "t"
	private String goal = RandomGoal();
	
	//Position of the agent
	
	//Vehicle Initialization
	@Override
	protected void setup() {
		//Object[] args = getArguments();
		//String goal = (String) args[0]
		// msg of created agent
		System.out.println("Vehicle Agent "+getAID().getName()+" is ready with goal " + goal);

	}
	
	//Generates with 80% probability goal as "save money" (= "m")
	//and with 20% probability goal to "save time" (= "t")
	private String RandomGoal() {
		List<String> list = Arrays.asList("m", "m", "m", "m", "m", "m", "m", "m", "t", "t");
		Random rand = new Random();
		String randomgoal = list.get(rand.nextInt(list.size()));
		
		return randomgoal;
	}
	
	public int get_battery_life() {
		return battery_life;
	}
	
	/**private class ChargingStationRequest extends Behaviour{
		 
		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}**/

}
