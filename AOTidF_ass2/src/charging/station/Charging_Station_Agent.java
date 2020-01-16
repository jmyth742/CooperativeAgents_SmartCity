package charging.station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import simulation.Field;
import simulation.Location;
import vehicle.VehicleAgent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.Property;

import simulation.*;
/**
 * 
 * @author anoulis
 * TODO
 * ********IGNORE FOR NOW********
 * List of charging events for every charger.
 * Return cost for chargingPeriod,unitPrice under the communication with CarAgent.
 * Or equivalent message if it's full booked or damaged.
 * ******************************
 * 
 * First we don't use chargers, charging event and bookinglist files, only this one
 * Yellow Page service for publishing the requests and services  
 * - In book Developing Multi Agent systems with JADE page 72 (or Title 4.4)
 * 
 * Let's keep it simple for now
 * Every Charging Station has 1 fast and 1 slow charger.
 * Two time-slots for operation 12-14,14-16
 * Price for fast/slow charging.
 * Location values that can be used to calculate distance from the car
 * Functions to get booked or not condition
 */

public class Charging_Station_Agent extends Agent{
	
	public int id;
	private String name;
    //private BookingList<ChargingEvent> fastBookingList;
    //private BookingList<ChargingEvent> slowBookingList;
    public int fastChargers;
    public int slowChargers;
    private double unitPriceFast;
    private double unitPriceSlow;
    //private boolean automaticQueueHandling;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    //private long timestamp;
    //private Timer timer;
    private double chargingRateFast;
    private double chargingRateSlow;
    public ArrayList<Charger> chargers;
    public ArrayList<ChargingEvent> chargingEvents = new ArrayList<>();
    //final ArrayList<Integer> numberOfChargers = new ArrayList<>();
    public String[][] shedule;
    

    //For simulation
    private Field field;
    private Location location;
    
    /**
     * Creates a new ChargingStation instance. It sets the handling of the queue to automatic, as well. The fast charging rate,
     * and slow charging rate are set to 0.02 and 0.01 Watt/millisecond in each case.
     * @param name The name of the Charging Station.
     * @param fastCharges The number of fast chargers.
     * @param slowChargers The number of slow chargers.
     * @param fastPrice The price for fast charging.
     * @param slowPrice The price for slow charging.
     */
    public void ChargingStation(Field field, final int fastChargers, final int slowChargers, final double slowPrice ) {
    	this.field = field;
    	this.id = idGenerator.incrementAndGet();
        this.fastChargers = fastChargers;
        this.slowChargers = slowChargers;
        this.unitPriceFast = 2 * slowPrice;
        this.unitPriceSlow = slowPrice;
        this.chargingRateFast = 0.02;
        this.chargingRateSlow = 0.01;
        
//        this.automaticQueueHandling = true;
//        this.fastBookingList = new BookingList<>();
//        this.slowBookingList= new BookingList<>();
        this.chargers = new ArrayList<>();

        for (int i=0; i<fastChargers;i++) {
        	chargers.add(new Charger(this, "fast"));
        }
        
        for (int i=0; i<slowChargers;i++) {
        	chargers.add(new Charger(this, "slow"));	
        }
        

        //shedule table: for each minute we have informations of all chargers if they are free or not
        this.shedule = new String[fastChargers + slowChargers][24 * 60];
    }
        
    /**
     * Sets a name to the ChargingStation.
     * @param nam The name to be set.
     */
    public void setName(final String name){
        this.name = name;
    }
    
    /**
     * set charging events
     * 
     */
    public void setChargingEvents (String s, String e, VehicleAgent v, String charging) {
    	ChargingEvent ce = new ChargingEvent(this, v, charging, s ,e );
    	chargingEvents.add(ce);
    	
    }

    /**
     * @return The id of the ChargingStation.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id for the ChargingStation.
     * @param d The id to be set.
     */
    public void setId(final int d) {
        this.id = d;
    }

	@Override
	protected void setup() {
		//System.out.println("");
		//System.out.println("Hello, let's create an Agent of Charging Station");
		
		// Register the charging-points service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Charging-Points");

		sd.setName(getLocalName()+"-Charging-Points");

//		if( this.fastChargers>0) {
//			yellowPagesIndex("fast",dfd);
//		}
//		if( this.slowChargers>0) {
//			yellowPagesIndex("slow",dfd);
//		}
		
		dfd.addServices(sd);
		
		// Initialization for the call for proposals (cfp)
//		System.out.println("Agent "+getLocalName()+ " at loction: " + location.toString() +" is waiting for CFP...");
		MessageTemplate template = MessageTemplate.and(
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
				MessageTemplate.MatchPerformative(ACLMessage.CFP) );
		
		//ContractNet code for Charging Station Agents
		contractNet(template);
		
		try {
		DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
		fe.printStackTrace();
		}
		//doDelete();
		
	}
	
	/**
//	 * Function to do the Registrations in Yellow Pages
//	 * for every charging station.
//	 * It should use charging station object to add properties.
//	 * We should start with the following ones:
//	 * mode: fast or slow (number of entries in yellow pages depending on chargers)
//	 * start/end : let's have at first point 2 time-slots for every entry (12-14,14-16)
//	 * booked: yes OR no : firstly free, after the bookings, not anymore
//	 * 
//	 */
//	public void yellowPagesIndex (String mode, DFAgentDescription dfd) {
//		ServiceDescription sd = new ServiceDescription();
//		sd.setType("Charging-Points");
////		sd.addProperties(new Property("mode", mode));
//		
//		for(int i=0; i< chargers.size(); i++) {
//			sd.setName(getLocalName()+"-Charging-Points-Charger" + i );
//
//			dfd.addServices(sd);
//			sd.addProperties(new Property("mode", 
//					chargers.get(i).getKindOfCharging()));
//		}

	/**
	 * Function to do the Registrations in Yellow Pages
	 * for every charging station.
	 * It should use charging station object to add properties.
	 * We should start with the following ones:
	 * mode: fast or slow (number of entries in yellow pages depending on chargers)
	 * start/end : let's have at first point 2 time-slots for every entry (12-14,14-16)
	 * booked: yes OR no : firstly free, after the bookings, not anymore
	 * 
	 */
	public void yellowPagesIndex (String mode, DFAgentDescription dfd) {
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Charging-Points");
		sd.setName(getLocalName()+"-Charging-Points");
//		System.out.println(mode);
		Property modeP = new Property("mode", mode);
		sd.addProperties(modeP);		
	}
	
	/**
	 * ContractNet for the station
	 * Functions to handle call for proposals (cfp) and accept or refuse messages
	 * To make proposes and inform is action is executed properly.
	 * @param template
	 */
	public void contractNet(MessageTemplate template) {
		addBehaviour(new ContractNetResponder(this, template) {
			@Override
			protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				/**
		          * Values of the vehicle
		          * oArgs[0] = action_name;
		          * oArgs[1] = battery_life;
		          * oArgs[2] = start time for a free slot;
		          * oArgs[3] = end time for a free slot;
		          * oArgs[4] = way_time
		          * oArgs[5] = mode of the charger
		          * **/
				
				String[] oArgs = null;
				
				try {
					oArgs = (String[]) cfp.getContentObject();
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Agent "+getLocalName()+": CFP received from " + cfp.getSender().getLocalName() + 
						". Action is "+ oArgs[0]);
				//int[] result = new int[]{propose, charger,start_slot, locat.getRow(),
				//locat.getCol(), way_time};
				
				
				int[] result = checkfreeSlot(oArgs, cfp.getSender().getLocalName());
				int proposal = result[0];
				if (proposal != -1) {
					// We provide a proposal
					System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					
					int[] oMsg=new int[5];
			         oMsg[0] = result[0]; //Proposenumber
			         oMsg[1] = result[1]; //charger number
			         oMsg[2] = result[2]; //start_slot
			         oMsg[3] = result[3]; //way_time
			         oMsg[4] = result[4]; //time
			         
					try {
						propose.setContentObject(oMsg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return propose;
				}
				else {
					// We refuse to provide a proposal
//					System.out.println("Agent "+getLocalName()+": Refuse");
					throw new RefuseException("evaluation-failed");
				}
			}

			@Override
			protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
				System.out.println("Agent "+getLocalName()+": Proposal accepted");
							
				ACLMessage inform = accept.createReply();
				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			}

			private void updateSchedule(String name) {
				for(int i = 0 ; i < chargers.size(); i++) {
					for(int j = 0; j < (24 * 60); j++) {
						if(shedule[i][j] == name) {
							shedule[i][j] = null;
						}
					}
				}
			}

			protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
				
				updateSchedule(cfp.getSender().getLocalName());
				
			}
		} );
		
	}
	
	/**
	 * Fake number now
	 * This function should return the ratio for the charging.
	 * Should be a number which comes from combination of distance, price of charging and whatever
	 * @return
	 */
	private int[] checkfreeSlot(String[] oArgs, String name) {
		
		/**
         * Values of the vehicle
         * oArgs[0] = action_name;
         * oArgs[1] = battery_life;
         * oArgs[2] = start time (+way_time) for a free slot;
         * oArgs[3] = end time for a free slot;
         * oArgs[4] = way_time
         * oArgs[5] = mode of the charger
         * oArgs[6] = time
         * **/
		
		int propose = -1;
		
		int start = Integer.valueOf(oArgs[2]);
		int end = Integer.valueOf(oArgs[3]);
		String mode = oArgs[5];
		
		
		int charger = -1;
		int start_slot = -1;
		
		int free_slot = -1;
	
		int way_time = Integer.valueOf(oArgs[4]);
		int time = Integer.valueOf(oArgs[6]);
		
//		System.out.println("start: " + start + "  end: " + end + "end - start = " + (end-start) + " time: " + time);
		
		for(int i = 0; i < chargers.size(); i++) {
			if(chargers.get(i).getKindOfCharging().equalsIgnoreCase(mode)) {
//				System.out.println("In charger " + i + " " + mode);
				free_slot = -1;
				if((end - start) > time) {
					for(int j = start; j < end; j++) {
//						System.out.println("time: " + j + " free_slot " + free_slot);
						if(shedule[i][j]==null) {
							free_slot++;
						} else{
							free_slot = -1;
						}
						if(free_slot == (int) (time)) {
							start_slot = j - free_slot;
							break;
						}
					}
					if(free_slot != -1 && start_slot != -1) {
						charger = i;
//						System.out.println("Found free slot for charger " + charger + " start_slot " + start_slot);
						propose = (int) proposalCal(time,way_time, mode);
						Arrays.fill(shedule[charger], start_slot + way_time, start_slot + time, name);
						break;
					}
					
				}	
			}
		}
		
		int[] result = new int[]{propose, charger, start_slot, way_time, time};
		return result;
	}
	
	private double proposalCal(double charg_time,int way_time, String mode) {

		double a = 0.8;
		double b = 0.2;
		
		if(mode.equalsIgnoreCase("fast")) {
			return (double) (b * (unitPriceFast * charg_time) + a * (charg_time + way_time));
		} else if(mode.equalsIgnoreCase("slow")) {
			return (double) (a * (unitPriceSlow * charg_time) + b * (charg_time + way_time));
		}
		return -1;

	}



	
	/**
	 * Unregister from the yellow pages
	 */
	protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
    /**
     * Place the fox at the new location in the given field.
     * @param newLocation The fox's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    public Location getLocation() {
    	return location;
    }
    
    public void setField( Field field) {
    	this.field = field;
    }
    
    /**
     * ********IGNORE FOR NOW********
     * Adds a ChargingEvent to the corresponding booking list.
     * @param event The ChargingEvent to be added.
     */
    /*
    public void updateQueue(final ChargingEvent event) {
        //lock2.lock();
        try {
            switch (event.getKindOfCharging()) {
                case "fast":
                    fastBookingList.add(event);
                    break;
                case "slow":
                    slowBookingList.add(event);
                    break;
                default:
                    break;
            }
        } finally {
            //lock2.unlock();
        }
    }
    */
    
    /**
     * @return The BookingList for fast charging.
     */
    /*
    public BookingList getFast() {
        return fastBookingList;
    }
    */

    /**
     * @return The Booking for slow charging.
     */
    /*
    public BookingList getSlow() {
        return slowBookingList;
    }
    */
 

    /**
     * Looks for an empty Charger. If there is one, the event is assigned to it.
     * @param event The event that looks for a Charger.
     * @return The Charger that was assigned, or null if not any available Charger found.
     */
    
    /*
    public Charger assignCharger(final ChargingEvent event) {
		return null;
    }*/

		

}
