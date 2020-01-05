package charging.station;

import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;

import simulation.Field;
import simulation.Location;


/**
 * 
 * @author anoulis
 * TODO
 * The charging event should be linked to the charger.
 * The charger depending on type should determine the charging time from % to %.
 * Maybe also the cost.
 * 
 *
 */

public class Charger {	
    private int id;
    private final String kindOfCharging;
    private String name;
    private ChargingEvent e;
    private Charging_Station_Agent station;
    final ArrayList<Integer> planEvent = new ArrayList<>();
    final ArrayList<Long> planTime = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    //For simulation
    private Field field;
    private Location location;
    /**
     * Creates a new Charger instance.
     * @param stat The ChargingStation object the Charger is linked with.
     * @param kindOfChar The kind of charging the Charger supports.
     */
    public Charger(final Charging_Station_Agent station, final String kindOfCharging) {
        this.id = idGenerator.incrementAndGet();
        this.kindOfCharging = kindOfCharging;
        this.station = station;
        this.name = "Charger" + String.valueOf(id);
    }

    /**
     * @return The name of the Charger.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The kind of charging the Charger supports.
     */
    public String getKindOfCharging() {
        return this.kindOfCharging;
    }
    
    /**
     * Sets a ChargingEvent to the Charger.
     * @param ev The ChargingEvent to be linked with the Charger.
     */
    synchronized void setChargingEvent(final ChargingEvent ev) {
        this.e = ev;
    }
    
    /**
     * @return The ChargingEvent that is linked with the Charger.
     */
    public synchronized ChargingEvent getChargingEvent() {
        return e;
    }
    

    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    public void setField( Field field) {
    	this.field = field;
    }
    
    


}
