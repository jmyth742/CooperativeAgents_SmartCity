import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.core.Profile;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {
	
	public static void main(String [] args) {
		Runtime rt = Runtime.instance();
		Profile p = new ProfileImpl();
		p.setParameter(Profile.MAIN_HOST, "localhost");
		p.setParameter(Profile.GUI, "true");
		ContainerController cc = rt.createMainContainer(p);
		for(int i=1;i<6;i++) {
			AgentController ac;

			try {
				ac = cc.createNewAgent("VehicleAgent" + i, "vehicle.VehicleAgent", null);
				ac.start();
				ac = cc.createNewAgent("CSAgent" + i, "charging.station.Charging_Station_Agent", null);
				ac.start();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}

}
