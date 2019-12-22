package charging.station;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ChargingEvent
{
    private int id;
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Charging_Station_Agent station;
    private String kindOfCharging;
    private long waitingTime;
    //private ElectricVehicle vehicle;
    private long chargingTime;
    private String condition;
    private Charger charger;
    private long maxWaitingTime;
    private long timestamp;
    private double cost;
    long accumulatorOfChargingTime = 0;
    private static final List<ChargingEvent> chargingLog = new ArrayList<>();
    private static final List<ChargingEvent> exchangeLog = new ArrayList<>();

	
	public ChargingEvent() {
        this.id = idGenerator.incrementAndGet();
    }
	
	public void execution() {
		
	}
	
	 /**
     * @return The kind of charging of the ChargingEvent.
     */
    public String getKindOfCharging()
    {
        return kindOfCharging;
    }

    /**
     * @return The ChargingStation the ChargingEvent wants to be executed.
     */
    public Charging_Station_Agent getStation()
    {
    	return station;
    }
    
   }




