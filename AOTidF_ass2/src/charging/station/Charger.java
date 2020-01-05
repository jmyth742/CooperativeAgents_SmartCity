package charging.station;

import java.util.ArrayList;

import java.util.concurrent.atomic.AtomicInteger;

import simulation.Field;
import simulation.Location;


/**
 * @author Aristeidis Noulis, Jonathan Smyth, Cesar Gonzalez, Veranika Paulava
 * Each ChargingStationAgent has a different number of Chargers and different types of chargers
 * We have fast and slow chargers.
 * 
 *
 */

public class Charger {	
    private int id;
    //What kind of charging station - fast or slow
    private final String kindOfCharging;
    
    private String name;
    private ChargingEvent e;
    private Charging_Station_Agent station;
    final ArrayList<Integer> planEvent = new ArrayList<>();
    final ArrayList<Long> planTime = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    //For simulation know the field and the location (row, col) of each charger
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
    
    /**
     * 
     * @param newLocation
     * set the Location of each charger as long a there is no other Agent positioned.
     */

    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * 
     * @param field
     * set the field of the charging station for the simulation
     */
    public void setField( Field field) {
    	this.field = field;
    }
    
    


}
