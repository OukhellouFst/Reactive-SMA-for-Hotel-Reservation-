package SmartPark.agents;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;


public class AIAgent extends GuiAgent{
	
	/* reference vers GUI */
	protected AIContainer aiContainer;

	@Override
	protected void onGuiEvent(GuiEvent evt) {
		if(evt.getType()==1) {
			List<String> list= (List<String>)evt.getParameter(0);
			String Ville = list.get(1);
			ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
			aclMessage.setContent(Ville);
			aclMessage.addReceiver(new AID("ArAgent",AID.ISLOCALNAME));
			send(aclMessage);
		}
		
	}
	
	
	@Override
	protected void setup() {
		if(this.getArguments().length==1) {
			aiContainer =(AIContainer)getArguments()[0];
			aiContainer.aiAgent = this; 
		}
		
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage msg = receive();
				if(msg != null) {
					switch (msg.getPerformative()) {
					case ACLMessage.CONFIRM:						
						aiContainer.logMessage(msg);
						break;

					default:
						break;
					}
					
				}
				else {
					block();
				}
			}
		});
		
	}
	
	

}
