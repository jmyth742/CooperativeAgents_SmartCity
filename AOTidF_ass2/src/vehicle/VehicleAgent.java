package vehicle;
import java.text.SimpleDateFormat;
import java.util.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.Property;
import java.util.Date;



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
		//System.out.println("Vehicle Agent "+getAID().getName()+" is ready with goal " + goal);
	    //System.out.println("Agent "+getLocalName()+" searching for services of type \"Charging-Points\"");
		
		// Check in yellow pages every 5s
		addBehaviour(new TickerBehaviour(this, 5000) {
			protected void onTick() {
				try {
					// Build the description used as template for the search
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription templateSd = new ServiceDescription();
					templateSd.setType("Charging-Points");
					if (getLocalName().equalsIgnoreCase("VehicleAgent3")) {
						templateSd.addProperties(new Property("mode", "fast"));
						templateSd.addProperties(new Property("start", "12"));
						templateSd.addProperties(new Property("end", "14"));
					}
					else if (getLocalName().equalsIgnoreCase("VehicleAgent5")) {
						templateSd.addProperties(new Property("mode", "fast"));
						templateSd.addProperties(new Property("start", "14"));
						templateSd.addProperties(new Property("end", "16"));
					}
					else {
						templateSd.addProperties(new Property("mode", "slow"));
						templateSd.addProperties(new Property("start", "16"));
						templateSd.addProperties(new Property("end", "18"));
					}
					//templateSd.setType("Charging-Points");
					//templateSd.addProperties(new Property("start", "12"));
					//templateSd.addProperties(new Property("end", "14"));
					template.addServices(templateSd);

					//SearchConstraints sc = new SearchConstraints();
					//sc.setMaxResults();
					DFAgentDescription[] results = DFService.search(this.getAgent(),template);
					//DFAgentDescription[] results = DFService.search(this, template);
					//System.out.println(results);
					System.out.println("");
					String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
					System.out.println("On Time " + timeStamp);
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
								if (sd.getType().equalsIgnoreCase("Charging-Points")) {
									System.out.println("- Service \""+sd.getName()+"\" provided by agent "+provider.getLocalName());
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
		} );
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
