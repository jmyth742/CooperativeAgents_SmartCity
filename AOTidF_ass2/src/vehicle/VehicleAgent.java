package vehicle;

import java.io.IOException;

import java.util.*;

import charging.station.Charging_Station_Agent;
import simulation.*;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * @author Aristeidis Noulis, Jonathan Smyth, Cesar Gonzalez, Veranika Paulava
 * 
 * Class Vehicle Agent 
 *
 */
public class VehicleAgent extends Agent {
	
	private static final long serialVersionUID = 1L;
	// Battery status of the agent
	private double battery_life;
	//Lost battery life per step
	private double battery_decay = 0.3;
	// Goal to save money "m" or save time "t"
	private String goal = RandomGoal();

	//Daily schedule is generated randomly
	private int[] schedule = new int[(24 * 60) - 1];

	private int step = 0;
	private int nResponders;
	
	//List of nearest CS
	private List<Charging_Station_Agent> nearestCS;
	
	//random generated way_time to get to a Charging Station
	int way_time;
	//time till fully charged
	int time;
	//If already got a reservation the value is 1, otherwise 0
	int have_reservation = 0;
	
	int numofCS = 20;
	
	//charging rate for slow and fast charger
	double slow_rate = 0.7;
	double fast_rate = 2 * slow_rate;

	//depending on goal which charger is wished
	String mode;
	
	// Position of the 
	Location newlocation;

	//For the simulation
	private Field field;
	public Location location;
	

	// Vehicle Initialization in the field with a position	
	public VehicleAgent(Field field, Location location) {
		this.field = field;
		setLocation(location);
	}

	@Override
	protected void setup() {

		battery_life = getRandomNumberInRange(50, 100);

		nResponders = 2;

		//create a random day shedule
		fillSchedule(8);

		//random time till a charging station
		way_time = getRandomNumberInRange(1,20);

		if(goal == "t")
			mode = "fast";
		else
			mode = "slow";
	}
	
	/**
     * random function to return number between min and max for different
     * Initialization
     */
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
	
	/**
	 * 
	 * @param numofBlocks - in how many "block" to divide the daily schedule
	 * fills the schedule of the vehicle with block of number (0 or 1)	 
	 */
	
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
	 * This function get called in the Simulator. Each step is one minute
	 * at each step the Vehicle Agent decide what to do
	 */
	public void step() {
		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
		int job = schedule[step];
	
		//if the schedule value is 1 then the agent moves random and lose battery_life
		if(job == 1) {
			battery_life = battery_life - battery_decay;
			random_move();
		//if the schedule value is 0 then the agent don't have to do anything
		// In this slot the agent would like to go to a charging station
		} else if(job == 0) {
			//battery life decreases not as much as while walking
			battery_life = battery_life - 0.5 * battery_decay;
		//if the value of the schedule is 2 it means the agent has here a reservation at a charging station. 
		// Agent charges at this time
		} else if (job == 2) {
			go_charging();
		}
		
		//after fully charged change the reservation value
		if(job!= 2 && have_reservation == 1) {
			have_reservation = 0;
		}
		//As long as you don't have any reservations and your battery life is low, search for one
		if(have_reservation == 0) {
			nearestCS = new ArrayList<Charging_Station_Agent>();
			if(battery_life >= 65 && battery_life < 80) {
				//search for the nearest numofCS/5 stations
				nearestCS = field.nearestChargingStation(location, numofCS/5);
			}
			else if(battery_life >= 45 && battery_life < 65 ) {
				//search for the nearest numofCS/4 stations
				nearestCS = field.nearestChargingStation(location, numofCS/4);
			}
			else if(battery_life >= 30 && battery_life < 45) {
				//search for the nearest numofCS/3 stations
				nearestCS = field.nearestChargingStation(location, numofCS/3);
			}
			else if(battery_life < 30) {
				System.out.println("Warning battery life below 30 -- EMERGENCY!");
				//search for the nearest numofCS/2 stations
				nearestCS = field.nearestChargingStation(location, numofCS/2);
			}
			//Now send a request to the nearest charging stations
			if(nearestCS != null) {
				try { 
					DFAgentDescription dfAgent= new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("Charging-Points");
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
	
	/**
	 * Function for random movement of the agent.
	 * checks the neighbor fields and if it is a street if moves randomly there
	 */
	public void random_move() {
		int row = location.getRow();
		int col = location.getCol();
		int new_col = getRandomNumberInRange(col -1,col+1);
		int new_row = getRandomNumberInRange(row-1,row+1);
		int s = field.getStreetAt(new_row, new_col);
		
		if(s == 1) {
			field.clear(location);
			Location newLocation = new Location(new_row, new_col);
			field.place(this, newLocation);
			setLocation(newLocation);
		}
	}
	
	/**
	 * Depending on the charger mode the agent charges with a specific charging rate
	 */
	private void go_charging() {
		if(mode == "fast") {
			battery_life = battery_life + fast_rate; 
		}else if(mode == "slow") {
			battery_life = battery_life + slow_rate; 
		}	
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
			System.out.println("Agent " + getLocalName() + " found the following Charging-Points services:");
			for (int i = 0; i < results.length; ++i) {
				DFAgentDescription dfd = results[i];
				AID provider = dfd.getName();
				// The same agent may provide several services; we are only interested
				// in the Charging-Points one
				Iterator it = dfd.getAllServices();
				while (it.hasNext()) {
					ServiceDescription sd = (ServiceDescription) it.next();
					if (sd.getType().equalsIgnoreCase("Charging-Points")) {
						System.out.println(
								"- Service \"" + sd.getName() + "\" provided by agent " + provider.getLocalName());
//						Get all Provider but just add the nearest one to the Receiver list
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
			
			
			
			//Send some infotmations as message to the charging station
			/**
			 * oMsg[0] - which action the agent is searching for 
			 * oMsg[1] - current battery life of the vehicle agent
			 * oMsg[2]/ oMsg[3] - the next free slot - so when the agent would like to charge, start/end
			 * oMsg[4] - a random generated way time
			 * oMsg[5] - what charger the vehicle need (slow or fast)
			 * oMgs[6] - and the neccesary time till the vehicle is fully charged depending on the current battery life
			 */
			String[] oMsg=new String[7];
	        oMsg[0] = "charging-action";
	        oMsg[1] = String.valueOf(battery_life);
	        oMsg[2] = String.valueOf(getNextfreeSlot()[0]);
	        oMsg[3] = String.valueOf(getNextfreeSlot()[1]);
	        oMsg[4] = String.valueOf(way_time);
	        oMsg[5] = mode;
	         
	 		time = time_til_charged(battery_life, mode);
	        oMsg[6] = String.valueOf(time);
	        
	        //send message
	        msg.setContentObject(oMsg);
			contractNet(msg);
		} else {
			System.out.println("Agent " + getLocalName() + " did not find any Charging-Points service");
		}
	}

	/**
	 * 
	 * @param battery_life
	 * @param type
	 * @return the time till the vehicle is fully charged, depending on the charger mode and the current battery life
	 */

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
	
	/**
	 * search in the schedule of the vehicle agent for the next free slot (so when job == 0, vehicle is chilling
	 * @return
	 */
	private int[] getNextfreeSlot() {
		int temp = -1;
		int start = -1;
		int end = -1;
		for(int i=step; i< schedule.length; i++) {
			if((schedule[i] == 1 || i == schedule.length - 1) && temp == 0) {
				temp = 1;
				end = i;
				break;
			}
			else if (schedule[i] == 0 && temp != 0){
				temp = 0;
				start = i;
			}
			else if(schedule[i] == 1 && temp == -1) {
				temp = 1;
			}
			
		}
		
		int[] start_end = new int[] {start + way_time, end};
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
				if (responses.size() < nResponders) {
					// Some responder didn't reply within the specified timeout
					System.out.println("Timeout expired: missing " + (nResponders - responses.size()) + " responses");
				}

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
						
						//search from all the charging station proposals for the best one
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
			/**
			 * 
			 * @param oArgs
			 * from the best proposed charging station get the start and end time for charging and update the schedule
			 */
			private void assignment(int[] oArgs) {
				int start = oArgs[2] + way_time;
				int time = oArgs[4];
				int end = start + time;
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

	/**
	 * 
	 * @return Generates with 80% probability goal as "save money" (= "m")
	 *  and with 20% probability goal to "save time" (= "t")
	 */
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

	/**
	 * Place the agent at the new location in the given field.
	 * 
	 * @param newLocation The agents's new location.
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

}
