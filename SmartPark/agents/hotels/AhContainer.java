package SmartPark.agents.hotels;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AhContainer extends Application {

	protected AhAgent ahAgent;
	private ObservableList<String> observableList;
	private AgentContainer agentContainer;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		
		primaryStage.setTitle("Hotel Agent GUI " );
		/* Ajouter un boutton pour automatiser le deploiment des agents 
		 * 
		 * */
		HBox hBox = new HBox(); hBox.setPadding(new Insets(10));hBox.setSpacing(10);
		Label label = new Label("Agent name : ");
		TextField fieldAgentName= new TextField();
		Button buttonDeploy = new Button("Deploy");
		hBox.getChildren().addAll(label,fieldAgentName,buttonDeploy);
		
		BorderPane borderPane = new BorderPane();
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(10));
		observableList = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(observableList);
		vBox.getChildren().add(listView);
		
		borderPane.setCenter(vBox);
		borderPane.setTop(hBox);
		
		Scene scene = new Scene(borderPane,400,500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonDeploy.setOnAction(evt -> {
			AgentController agentController;
			try {
				agentController = agentContainer.createNewAgent(fieldAgentName.getText(), AhAgent.class.getName(), new Object[] {this});
				agentController.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}


	private void startContainer() throws Exception{
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(Profile.MAIN_HOST, "localhost");
		agentContainer = runtime.createAgentContainer(profileImpl);
		agentContainer.start();
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(()->{			
			observableList.add(aclMessage.getSender().getName()+" => "+aclMessage.getContent());
		});
	}
}
