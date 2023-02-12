package SmartPark.agents.reservators;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ArAgent extends GuiAgent{

	protected ArContainer arContainer; 
	protected AID[] sellers;
	
	@Override
	protected void setup() {
		if(getArguments().length==1) {
			arContainer = (ArContainer)getArguments()[0];
			arContainer.bookBuyerAgent = this;
		}
		
		ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
		/* recherche des services */
		
		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,60000) {
			
			@Override
			protected void onTick() {
				DFAgentDescription dfAgentDescription = new DFAgentDescription();
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("transaction");
				serviceDescription.setName("vente-livres");
				dfAgentDescription.addServices(serviceDescription);
				try {
					DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
					sellers = new AID[results.length];
					for(int i=0;i<sellers.length;i++) {
						sellers[i] = results[i].getName();
					}
				} catch (FIPAException e) {
					e.printStackTrace();
				}
			}
		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			/*  nombre de messages recus depuis les vendeurs sellers */
			private int compteur = 0;
			/* liste des reponses des sellers */
			private List<ACLMessage> replies = new ArrayList<>();
			
			@Override
			public void action() {
				MessageTemplate messageTemplate = MessageTemplate.or( 
						MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
						MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE),
						MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
						MessageTemplate.MatchPerformative(ACLMessage.REFUSE)))
						);
				ACLMessage aclMessage = receive(messageTemplate);
				
				if(aclMessage!=null) {
					
					switch (aclMessage.getPerformative()) {
					
						case ACLMessage.REQUEST:
							/* dans le cas ou cette agent receoive une requette depuis le consommateur
							 * il va prendre son contenu , et apres il va faire un appel des propositions 
							 * a tous les agents qui ont pubilie leurs services dans DF 
							 * */
							String livre = aclMessage.getContent();
							ACLMessage aclMessage1  = new ACLMessage(ACLMessage.CFP);
							aclMessage1.setContent(livre);
							for(AID aid:sellers) {
								aclMessage1.addReceiver(aid);
							}
							send(aclMessage1);
							break;
						case ACLMessage.PROPOSE:
							++compteur;
							replies.add(aclMessage);
							/* prendre l'offre de prix minimum */
							if ( compteur == sellers.length) {
								ACLMessage meilleurOffre = replies.get(0);
								double min =Double.parseDouble( meilleurOffre.getContent());
								for(ACLMessage offre:replies) {
									double price = Double.parseDouble(offre.getContent());
									if(price<min) {
										min = price;
										meilleurOffre=offre;
									}
									
								}
								
								ACLMessage aclMessageAccept = meilleurOffre.createReply();
								aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								send(aclMessageAccept);
							}
							
							break;
						case ACLMessage.AGREE:
							ACLMessage response = new ACLMessage(ACLMessage.CONFIRM);
							response.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
							response.setContent(aclMessage.getContent());
							send(response);
							break;
							
						case ACLMessage.REFUSE:
							
							break;
	
						default:
							break;
					}
					
					
					/* log le message recu depuis le consommateur sur l'interface GUI 
					 * puis repondre au consommateur par " Trying to buy Nom de livre "
					 */
					arContainer.logMessage(aclMessage);
					ACLMessage reply = aclMessage.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("Trying to buy " +aclMessage.getContent());
					send(reply);
					
				}else {
					block();
				}
			}
		});
	}
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		
	}

}
