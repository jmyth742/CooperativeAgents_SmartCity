package vehicle;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.*;

import charging.station.Charging_Station_Agent;
import simulation.*;
import charging.station.*;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.Property;

/**
 * @author anoulis The car has goal time or money Mode charging or moving
 *         Booking preferences for the time-slots
 * 
 *
 */
public class VehicleAgent extends Agent {
	// Battery status of the agent
	private double battery_life;
	// Goal to save money "m" or save time "t"
	private String goal = RandomGoal();

	private int[] schedule = new int[(24 * 60) - 1];
	private int step = 0;
	private double battery_decay = 0.3;
	private int nResponders;
	
	private List<Charging_Station_Agent> nearestCS;
	
	int way_time;
	int time;
	int have_reservation = 0;
	int numofCS = 20;
	
	double slow_rate = 0.7;
	double fast_rate = 2 * slow_rate;

	String mode;
	

	Location newlocation;
	// Position of the agent

	// Vehicle Initialization

	private Field field;
	public Location location;
	public Charging_Station_Agent CS;

	public VehicleAgent(Field field, Location location) {
		this.field = field;
		setLocation(location);
	}


	// Vehicle Initialization
	@Override
	protected void setup() {

		battery_life = getRandomNumberInRange(50, 100);
//		System.out.println("Vehicle Agent " + getLocalName() + " is ready with goal " + goal + " and location "
//				+ location.toString() + " and battery life is: " + battery_life);
		nResponders = 2;

		fillSchedule(8);
		way_time = getRandomNumberInRange(1,20);
	}

	public int time_til_charged(double battery_life, String type) {
		int time= 0;
		//0 means fast charge
		if(type.equalsIgnoreCase("fast")) {
			time = (int)((100-battery_life) / fast_rate);
		}else if(type.equalsIgnoreCase("slow")) {
			time = (int)((100-battery_life) / slow_rate);
		}
		
		return time;
	}
	
	
	public void step() {
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		int job = schedule[step];
	
		//random movement
		if(job == 1) {
			battery_life = battery_life - battery_decay;
			random_move();
		} else if(job == 0) {
			battery_life = battery_life - 0.5 * battery_decay;
			//chill
			
		} else if (job == 2) {
//			System.out.println("Go to charging station! Battery Life: " + battery_life );
			go_charging();
			
			//go to charging Station
		}
//
//		if (battery_life >= 80.00) {
//			// pay attention to schedule and go do what needs to be done.
//
//			//randome movement
//			if (job == 1) {
//				battery_life = battery_life - battery_decay;
//				random_move();
//			//do nothing stay at your location
////			} else if(job == 2){				
//			
////				if(walk_time == 0) {
////					field.clear(location);
////					//field.place(this, new Location(new_row,new_col));
////					field.place(this, newlocation);
////					setLocation(newlocation);
////					System.out.println("Go charging");
////				}
//			}
//				
//				//charging()
//				
//			else {
//				// chill()
//			}
//		}
		
		// pay attention to schedule and go do what needs to be done.
		// }
		if(job!= 2 && have_reservation == 1) {
			have_reservation = 0;
		}
		if(have_reservation == 0) {
			nearestCS = new ArrayList<Charging_Station_Agent>();
			if(battery_life >= 65 && battery_life < 80) {
				//search for the nearest two stations
				nearestCS = field.nearestChargingStation(location, numofCS/5);
			}
			else if(battery_life >= 45 && battery_life < 65 ) {
				//search for the nearest 4 stations
				nearestCS = field.nearestChargingStation(location, numofCS/4);
			}
			else if(battery_life >= 30 && battery_life < 45) {
				//search for the nearest 6 stations
				nearestCS = field.nearestChargingStation(location, numofCS/3);
			}
			else if(battery_life < 30) {
				System.out.println("Warning battery life below 30 -- EMERGENCY!");
				nearestCS = field.nearestChargingStation(location, numofCS/2);
			}
			if(nearestCS != null) {
				try { 
					// Build the description used as template for the search
					DFAgentDescription dfAgent= new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("Charging-Points");
					
	//				createYellowPageEntry(sd);
					dfAgent.addServices(sd);
	
					DFAgentDescription[] results = DFService.search(this, dfAgent);
					try {
						yellowPagesResults(msg, results);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		}
		step++;
	}

	private void go_charging() {
		if(mode == "fast") {
			battery_life = battery_life + fast_rate; 
		}else if(mode == "slow") {
			battery_life = battery_life + slow_rate; 
		}
		
	}


	public void fillSchedule(int numofBlocks) {
		int start = 0;
		int end = schedule.length;
		
		for(int i = 0; i < numofBlocks; i++) {
			int temp = (int) getRandomNumberInRange(start, (end / numofBlocks) * (i+1)); 
			
			int value = getRandomNumberInRange(0, 1);
			
			Arrays.fill(schedule, start, temp, value);
			start = temp;
		}
	}

    /**
     * random function to return number between min and max for different
     * initialisations
     */
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
	
	/**
	 * Taking the results for searching for the suitable services in yellow-pages
	 * the vehicle agent start contractNet with the appropriate CS Agents.
	 * 
	 * @param msg
	 * @param results
	 * @throws IOException 
	 */
	public void yellowPagesResults(ACLMessage msg, DFAgentDescription[] results) throws IOException {
		
		if (results.length > 0) {
//			System.out.println("Agent " + getLocalName() + " found the following Charging-Points services:");
			for (int i = 0; i < results.length; ++i) {
				DFAgentDescription dfd = results[i];
				AID provider = dfd.getName();
				// The same agent may provide several services; we are only interested
				// in the Charging-Points one
				Iterator it = dfd.getAllServices();
				while (it.hasNext()) {
					ServiceDescription sd = (ServiceDescription) it.next();
					if (sd.getType().equalsIgnoreCase("Charging-Points")) {
//						System.out.println(
//								"- Service \"" + sd.getName() + "\" provided by agent " + provider.getLocalName());
						//Get all Provider but just add the nearest one to the Receiver list
						for(int j =0; j < nearestCS.size(); j++) {
							if(provider.getLocalName().contentEquals(nearestCS.get(j).getLocalName())) {
								msg.addReceiver(new AID((String) provider.getLocalName(), AID.ISLOCALNAME));
							}
						}
					}
				}
			}
			
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			// We want to receive a reply in 10 secs
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			
//			System.out.println(schedule);
			getNextfreeSlot();
			
			if(goal == "t")
				mode = "fast";
			else
				mode = "slow";
			
			
			String[] oMsg=new String[7];
	         oMsg[0] = "charging-action";
	         oMsg[1] = String.valueOf(battery_life);
	         oMsg[2] = String.valueOf(getNextfreeSlot()[0]);
	         oMsg[3] = String.valueOf(getNextfreeSlot()[1]);
	         oMsg[4] = String.valueOf(way_time);
	         oMsg[5] = mode;
	         

	 		time = time_til_charged(battery_life, mode);
	         
	         oMsg[6] = String.valueOf(time);
	         
	        msg.setContentObject(oMsg);
			contractNet(msg);
		} else {
			System.out.println("Agent " + getLocalName() + " did not find any Charging-Points service");
		}
	}
	
	private int[] getNextfreeSlot() {
		int temp = -1;
		int start = -1;
		int end = -1;
		for(int i=step; i< schedule.length; i++) {
//			System.out.println(" " + schedule[i] + " temp " + temp);
			if((schedule[i] == 1 || i == schedule.length - 1) && temp == 0) {
				temp = 1;
				end = i;
//				System.out.println("add end " + i);
				break;
			}
			else if (schedule[i] == 0 && temp != 0){
				temp = 0;
				start = i;
//				System.out.println("add start" + i);
			}
			else if(schedule[i] == 1 && temp == -1) {
				temp = 1;
			}
			
		}
		
		int[] start_end = new int[] {start + way_time, end};
//		System.out.println("start " + start + " end " + end );
		return start_end;
	}

	/**
	 * ContractNet function where we do the negotiations from the Vehicle Agent
	 * Part. Functions to handle the responses Code for the evaluation of the
	 * offers.
	 * 
	 * @param msg
	 */
	public void contractNet(ACLMessage msg) {
		addBehaviour(new ContractNetInitiator(this, msg) {

			protected void handlePropose(ACLMessage propose, Vector v) {
				System.out.println("Agent " + propose.getSender().getLocalName() + " proposed!");
			}

			protected void handleRefuse(ACLMessage refuse) {
				System.out.println("Agent " + refuse.getSender().getLocalName() + " refused");
				
			}

			protected void handleFailure(ACLMessage failure) {
				if (failure.getSender().equals(myAgent.getAMS())) {
					// FAILURE notification from the JADE runtime: the receiver
					// does not exist
					System.out.println("Responder does not exist");
				} else {
					System.out.println("Agent " + failure.getSender().getLocalName() + " failed");
				}
				// Immediate failure --> we will not receive a response from this agent
				nResponders--;
			}

			protected void handleAllResponses(Vector responses, Vector acceptances) {
				int[] oArgs = null;
//				if (responses.size() < nResponders) {
//					// Some responder didn't reply within the specified timeout
//					System.out.println("Timeout expired: missing " + (nResponders - responses.size()) + " responses");
//				}

				// Evaluate proposals.
				// Consider that the offer is an int ratio of an importance combination
				// of time, money, distance or whatever.
				int bestProposal = -1;
				AID bestProposer = null;
				ACLMessage accept = null;
				Enumeration e = responses.elements();
				while (e.hasMoreElements()) {
					ACLMessage msg = (ACLMessage) e.nextElement();
					if (msg.getPerformative() == ACLMessage.PROPOSE) {
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						
						acceptances.addElement(reply);

						
						try {
							oArgs = (int[]) msg.getContentObject();
						} catch (UnreadableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						int proposal = oArgs[0];
						System.out.println("THE PROPOSAL IS " + proposal);
						if (proposal > bestProposal) {
							bestProposal = proposal;
							bestProposer = msg.getSender();
							accept = reply;
						}
					}
				}

				// Accept the proposal of the best proposer
				if (accept != null) {
					System.out.println(
							"Accepting proposal " + bestProposal + " from responder " + bestProposer.getLocalName());
					accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					assignment(oArgs);
				}
			}

			private void assignment(int[] oArgs) {
				int start = oArgs[2] + way_time;
				int time = oArgs[4];
				int end = start + time;
//				newlocation = new Location(oArgs[6], oArgs[7]);
//				System.out.println("start " + start + " end: " + end);
				if(end < 24 * 60) {
					Arrays.fill(schedule, start, end, 2);
					have_reservation = 1;
				}
				
			}

			protected void handleInform(ACLMessage inform) {
				System.out.println(
						"Agent " + inform.getSender().getLocalName() + " successfully performed the requested action");
				
			}
		});
	}

	public VehicleAgent getAgent(String name) {
		if (name == this.getName()) {
			return this;
		}
		return null;
	}

	// Generates with 80% probability goal as "save money" (= "m")
	// and with 20% probability goal to "save time" (= "t")
	private String RandomGoal() {
		List<String> list = Arrays.asList("m", "m", "m", "m", "m", "m", "m", "m", "t", "t");
		Random rand = new Random();
		String randomgoal = list.get(rand.nextInt(list.size()));

		return randomgoal;
	}

	public double get_battery_life() {
		return battery_life;
	}
	
	public void set_battery_life(double new_battery) {
		battery_life = new_battery;
	}
	
	
	public boolean checkCharging() {
		Location CSlocation = CS.getLocation();
		Location vLocation = getLocation();
		setLocation(CSlocation);
		if(CSlocation == vLocation) {
			System.out.println("car is on charge");
			return true;
		}
		return false;
	}
	

	/**
	 * Place the fox at the new location in the given field.
	 * 
	 * @param newLocation The fox's new location.
	 */
    
	private void setLocation(Location newLocation) {
		if (location != null) {
			field.clear(location);
		}
		location = newLocation;
		field.place(this, newLocation);
	}

    public Location getLocation() {
    	return location;
    }

	public void get_job(int job) {
		switch (job) {
		case 1:
			// Perform operation 1: print out a message
			System.out.println("Random Move on street");
			// here we put the random move function on the street.
			break;
		case 2:
			System.out.println("Do Nothing -- chill");
			// here nothing is happening.
			break;

		}
	}	
	
	
	public void random_move() {
		int row = location.getRow();
		int col = location.getCol();
		int new_col = getRandomNumberInRange(col -1,col+1);
		int new_row = getRandomNumberInRange(row-1,row+1);
		int s = field.getStreetAt(new_row, new_col);
		//System.out.println("street at " + s);
		// this checks we are on a street.
		if(s == 1) {
			field.clear(location);
			//field.place(this, new Location(new_row,new_col));
			Location newLocation = new Location(new_row, new_col);
			field.place(this, newLocation);
			setLocation(newLocation);
		}
	}
}
