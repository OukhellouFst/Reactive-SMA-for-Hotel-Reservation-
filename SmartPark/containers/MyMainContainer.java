package SmartPark.containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MyMainContainer {
	
	// simple app java quand va demarrer , il demarre le mainContainer de Jade
	public static void main(String[] args) throws ControllerException {
		
		// instanciation de jade 
		Runtime runtime = Runtime.instance();
		
		// cet objet profileImpl vous permet de configurer le conteneur 
		ProfileImpl profileImpl = new ProfileImpl();
		
		// ajouter les parametres 
		// qd je veux demarrer je veux demarrer l'interface graphique 
		profileImpl.setParameter(ProfileImpl.GUI, "true");
		
		// creation de main container (de package jade.wrapper)
		AgentContainer mainContainer = runtime.createMainContainer(profileImpl);
		
		// demarrage de mainContainer 
		mainContainer.start();
	}
}
