package vehicle;
import java.util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;



public class VehicleAgent extends Agent{
	//Battery status of the agent
	private int battery_life;
	// Goal to save money "m" or save time "t"
	private String goal = RandomGoal();
	
	// The list of known seller agents
	private Vector sellerAgents;
	
	//Position of the agent
	
	//Vehicle Initialization
	@Override
	protected void setup() {
		//Object[] args = getArguments();
		//String goal = (String) args[0]
		// msg of created agent
		System.out.println("Vehicle Agent "+getAID().getName()+" is ready with goal " + goal);
		
	  	System.out.println("Agent "+getLocalName()+" searching for services of type \"Charging-Points\"");
	  	try {
	  		// Build the description used as template for the search
	  		DFAgentDescription template = new DFAgentDescription();
	  		ServiceDescription templateSd = new ServiceDescription();
	  		templateSd.setType("Charging-Points");
	  		template.addServices(templateSd);

	  		
	  		DFAgentDescription[] results = DFService.search(this, template);
	  		if (results.length > 0) {
	  			System.out.println("Agent "+getLocalName()+" found the following Charging-Points services:");
	  			for (int i = 0; i < results.length; ++i) {
	  				DFAgentDescription dfd = results[i];
	  				AID provider = dfd.getName();
	  				// The same agent may provide several services; we are only interested
	  				// in the Charging-Points one
	  				Iterator it = dfd.getAllServices();
	  				while (it.hasNext()) {
	  					ServiceDescription sd = (ServiceDescription) it.next();
	  					if (sd.getType().equals("Charging-Points")) {
	  						System.out.println("- Service \""+sd.getName()+"\" provided by agent "+provider.getName());
	  					}
	  				}
	  			}
	  		}	
	  		else {
	  			System.out.println("Agent "+getLocalName()+" did not find any Charging-Points service");
	  		}
	  	}
	  	catch (FIPAException fe) {
	  		fe.printStackTrace();
	  	}
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
