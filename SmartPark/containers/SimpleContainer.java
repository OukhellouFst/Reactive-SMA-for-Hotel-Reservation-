package SmartPark.containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class SimpleContainer {
	// creation d'un simple conteneur pour 
	// demarrer dans n'impotre quelle machine
	public static void main(String[] args) throws Exception{
		
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		
		AgentContainer container = runtime.createAgentContainer(profileImpl); 
		container.start();

	}

}
