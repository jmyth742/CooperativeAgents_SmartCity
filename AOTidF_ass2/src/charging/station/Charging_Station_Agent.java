package charging.station;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import jade.core.Agent;

public class Charging_Station_Agent extends Agent{
	
	private int id;
	private String name;
    private BookingList<ChargingEvent> fastBookingList;
    private BookingList<ChargingEvent> slowBookingList;
    public int fastChargers;
    public int slowChargers;
    private double unitPriceFast;
    private double unitPriceSlow;
    private boolean automaticQueueHandling;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private long timestamp;
    private Timer timer;
    private double chargingRateFast;
    private double chargingRateSlow;
    private ArrayList<Charger> chargers;
    final ArrayList<ChargingEvent> events = new ArrayList<>();
    final ArrayList<Integer> numberOfChargers = new ArrayList<>();
    
    /**
     * Creates a new ChargingStation instance. It sets the handling of the queue to automatic, as well. The fast charging rate,
     * and slow charging rate are set to 0.02 and 0.01 Watt/millisecond in each case.
     * @param name The name of the Charging Station.
     * @param fastCharges The number of fast chargers.
     * @param slowChargers The number of slow chargers.
     * @param fastPrice The price for fast charging.
     * @param slowPrice The price for slow charging.
     */
    public void ChargingStation(final String name, final int fastChargers, final int slowChargers, final double fastPrice, final double slowPrice ) {
    	this.id = idGenerator.incrementAndGet();
        this.name = name;
        this.fastChargers = fastChargers;
        this.slowChargers = slowChargers;
        this.unitPriceFast = fastPrice;
        this.unitPriceSlow = slowPrice;
        this.chargingRateFast = 0.02;
        this.chargingRateSlow = 0.01;
        this.automaticQueueHandling = true;
        this.fastBookingList = new BookingList<>();
        this.slowBookingList= new BookingList<>();
        this.chargers = new ArrayList<>();

        for (int i=0; i<fastChargers;i++) {
        	chargers.add(new Charger(this, "fast"));
        }
        
        for (int i=0; i<slowChargers;i++) {
        	chargers.add(new Charger(this, "slow"));	
        }
    }
    
    /**
     * Sets a name to the ChargingStation.
     * @param nam The name to be set.
     */
    public void setName(final String name)
    {
        this.name = name;
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
    
    /**
     * Adds a ChargingEvent to the corresponding waiting list.
     * @param event The ChargingEvent to be added.
     */
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
    
    /**
     * @return The BookingList for fast charging.
     */
    public BookingList getFast() {
        return fastBookingList;
    }

    /**
     * @return The Booking for slow charging.
     */
    public BookingList getSlow() {
        return slowBookingList;
    }
    
 

    /**
     * Looks for an empty Charger. If there is one, the event is assigned to it.
     * @param event The event that looks for a Charger.
     * @return The Charger that was assigned, or null if not any available Charger found.
     */
    public Charger assignCharger(final ChargingEvent event) {
		return null;

    }

	@Override
	protected void setup() {
		System.out.println("Hello, let's create an Agent of Charging Station");
		System.out.println("");
		//doDelete();
		
	}

}
