import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.core.Profile;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {
	/**
	 * @param args
	 * The core code for yellow pages is done.Should be expanded.
	 * After that the assignments should be done.
	 * The simple examples logic is that:
	 * We have 3 Vehicles and 3 Stations.
	 * We do initializations
	 * We assign the bookings 
	 * As the time passes and one car wants to charge again search and negotiate
	 * 
	 */
	public static void main(String [] args) {
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();
		p.setParameter(Profile.MAIN_HOST, "localhost");
		p.setParameter(Profile.GUI, "true");
		ContainerController cc = rt.createMainContainer(p);
		for(int i=1;i<=3;i++) {
			AgentController ac;

			try {
				ac = cc.createNewAgent("CSAgent" + i, "charging.station.Charging_Station_Agent", null);
				ac.start();
				if (i==1) {
					ac = cc.createNewAgent("VehicleAgent" + i, "vehicle.VehicleAgent", null);
					ac.start();
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}

}
