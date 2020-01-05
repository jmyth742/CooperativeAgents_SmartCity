package charging.station;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 
 * @author anoulis
 * TODO
 * Every charging event should have time-slots, that can be characterized as free,
 * or occupied. Should return charging time from % to %, cost for charging period,
 * emergency handling (probably).
 * Alternate the state of time-slots.
 * 
 */

public class ChargingEvent
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Charging_Station_Agent station;
    private String kindOfCharging;
    private long waitingTime;
    private String vehicle;
    private long chargingTime;
    private String condition;
    private Charger charger;
    private long maxWaitingTime;
    private long timestamp;
    private double cost;
    long accumulatorOfChargingTime = 0;
    private static final List<ChargingEvent> chargingLog = new ArrayList<>();
    private static final List<ChargingEvent> exchangeLog = new ArrayList<>();

	
    /**
     * Constructs a new ChargingEvent object. It sets the condition of the event to "arrived".
     * @param station The ChargingStation object the event visited.
     * @param vehicle The ElectricVehicle of the event.
     * @param kindOfCharging The kind of charging the event demands.
     */
    public ChargingEvent(final Charging_Station_Agent station, final String vehicle, final String kindOfCharging) {
        this.id = idGenerator.incrementAndGet();
        this.station = station;
        this.kindOfCharging = kindOfCharging;
        this.vehicle = vehicle;
        this.condition = "willingToCharge";
        chargingLog.add(this);
        this.charger = null;
    }
    
    /**
     * Executes the pre-processing phase. Checks for any Charger and assignees to it if any.
     * It calculates the charging time.
     * If there is not any empty Charger   the ChargingEvent is inserted
     * in the respectively waiting list, if the waiting time is less than the set waiting time of the Driver.
     **/
    public void preProcessing() {
    	
        if ((kindOfCharging.equals("fast") && station.fastChargers == 0) ||
        (kindOfCharging.equals("slow") && station.slowChargers == 0)) {
            setCondition("nonExecutable");
            return;
        }
    }
    
    /**
     * It starts the execution of the ChargingEvent. 
     * We change the condition of time slot from booked or charging.
     * We update the charger to start the charging procedure.
     * 
     */
    public void execution()
    {
    }
    
    /**
     * Sets the condition of the ChargingEvent.
     * @param cond The condition to be set.
     */
    public void setCondition(final String cond) {
        this.condition = cond;
    }
    
    
    /**
     * @return The charging time of the ChargingEvent in milliseconds.
     */
    public long getChargingTime() {
        return this.chargingTime;
    }

    /**
     * Sets the charging time of the ChargingEvent in milliseconds. It also starts counting the reamining time of the charging.
     *
     * @param time The charging time in milliseconds.
     */
    public void setChargingTime(final long time) {
        timestamp = System.currentTimeMillis();
        this.chargingTime = time;
    }
    

    /**
     * @return The id of the ChargingEvent.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the id for the ChargingEvent.
     *
     * @param d The id to be set.
     */
    public void setId(final int d) {
        this.id = d;
    }

    /**
     * @return The cost of the ChargingEvent.
     */
    public double getCost() {
        return this.cost;
    }

    /**
     * Sets the cost for the ChargingEvent.
     * @param c The cost to be set.
     */
    public void setCost(final double c)
    {
        this.cost = c;
    }
    

    /**
     * Returns the list with all created charging events.
     * @return The list with all created charging events.
     */
    public static List<ChargingEvent> getChargingLog() {
        return chargingLog;
    }
    

    /**
     * Sets a charger to the event for charging.
     * @param ch The charger to be assigned.
     */
    void setCharger(Charger ch) {
        this.charger = ch;
    }


	
	 /**
     * @return The kind of charging of the ChargingEvent.
     */
    public String getKindOfCharging()
    {
        return this.kindOfCharging;
    }

    /**
     * @return The ChargingStation the ChargingEvent wants to be executed.
     */
    public Charging_Station_Agent getStation()
    {
    	return this.station;
    }
    
   }




