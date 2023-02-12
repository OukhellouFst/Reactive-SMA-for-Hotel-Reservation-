package SmartPark.agents.hotels;

import java.util.Random;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class AhAgent extends GuiAgent{

	protected AhContainer ahContainer; 
	@Override
	protected void setup() {
		if(getArguments().length==1) {
			ahContainer = (AhContainer)getArguments()[0];
			ahContainer.ahAgent = this;
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		
		/* publication de la service */
		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
			
			@Override
			public void action() {
				DFAgentDescription agentDescription = new DFAgentDescription();
				agentDescription.setName(getAID());
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("vente-livres");
				agentDescription.addServices(serviceDescription);
				try {
					DFService.register(myAgent, agentDescription);
				} catch (FIPAException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage aclMessage = receive();
				if(aclMessage!=null) {
					/* log le message dans l'interface graphique SellerContainer*/
					ahContainer.logMessage(aclMessage);
					switch (aclMessage.getPerformative()) {
						/* dans le cas ou l'agent seller recoit un message CFP 
						 * il va repondre a l'acheteur (buyerAgent) par un prix 
						 * aleatoire entre 500 et 1500
						 */
						case ACLMessage.CFP:
							ACLMessage reply = aclMessage.createReply();
							reply.setPerformative(ACLMessage.PROPOSE);
							reply.setContent(String.valueOf(500+new Random().nextInt(1000)));
							send(reply);
							break;
							
						case ACLMessage.ACCEPT_PROPOSAL:
							/* si l'agent buyer a accepte l'offre de cet agent*/
							/* on n'a pas programme les cas ou il accept 
							 * et les cas ou il refuse 
							 */
							ACLMessage aclMessage2 = aclMessage.createReply();
							aclMessage2.setPerformative(ACLMessage.AGREE);
							send(aclMessage2);
							break;
	
						default:
							break;
					}
				}else {
					block();
				}
			}
		});
	}
	
	@Override
	protected void onGuiEvent(GuiEvent evt) {
		
	}
	
	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	
	}
	
	

}
