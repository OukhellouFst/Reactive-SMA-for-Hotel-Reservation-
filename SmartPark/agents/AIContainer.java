package SmartPark.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AIContainer extends Application {
	
	/* reference vers l'Agent */
	protected AIAgent aiAgent;
	private ObservableList<String> observableList;


	public static void main(String[] args) throws Exception {
		launch(args);
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("AI GUI");
		/* sasir le 
		 * pays , 
		 * ville , 
		 * chambre, 
		 * (single,double), 
		 * nombre de personnes
		 * Date de debut et date de fin de sejour 
		 */
		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane,600,600);
		
		/* pays */
		HBox hBoxPays = new HBox();
		Label labelPays = new Label("Nom de Pays : ");
		TextField textFieldPays = new TextField();
		hBoxPays.setPadding(new Insets(10));
		hBoxPays.setSpacing(10);
		hBoxPays.getChildren().addAll(labelPays,textFieldPays);
		
		/* ville */
		HBox hBoxVille = new HBox();
		Label labelVille = new Label("Nom de Ville : ");
		TextField textFieldVille = new TextField();
		hBoxVille.setPadding(new Insets(10));
		hBoxVille.setSpacing(10);
		hBoxVille.getChildren().addAll(labelVille,textFieldVille);
		
		/* chambre */
		HBox hBoxChambre = new HBox();
		Label labelChambre = new Label("Nom de chambre : ");
		TextField textFieldChambre = new TextField();
		hBoxChambre.setPadding(new Insets(10));
		hBoxChambre.setSpacing(10);
		hBoxChambre.getChildren().addAll(labelChambre,textFieldChambre);
		
		/* Couple ou Single */
		final ToggleGroup group = new ToggleGroup();
		RadioButton radioButton1 = new RadioButton("Single");
		radioButton1.setToggleGroup(group);
		radioButton1.setSelected(true);
		RadioButton radioButton2 = new RadioButton("Couple");
		radioButton2.setToggleGroup(group);
		
		HBox hBoxRadio = new HBox();
		hBoxRadio.setPadding(new Insets(10));
		hBoxRadio.setSpacing(10);
		hBoxRadio.getChildren().addAll(radioButton1,radioButton2);
	
		/* Nombre de personnes */
		HBox hBoxPersonnes = new HBox();
		Label labelPersonnes= new Label("nbr de personnes : ");
		TextField textFieldPersonnes = new TextField();
		hBoxPersonnes.setPadding(new Insets(10));
		hBoxPersonnes.setSpacing(10);
		hBoxPersonnes.getChildren().addAll(labelPersonnes,textFieldPersonnes);
		
		
		/* date */
		HBox hBoxDate = new HBox();
		hBoxDate.setPadding(new Insets(10));
		hBoxDate.setSpacing(10);

		Label labelDate = new Label("Date : ");
		DatePicker datePicker = new DatePicker();

		hBoxDate.getChildren().addAll(labelDate, datePicker);
		
		/* button */
		Button button = new Button("Submit");
		button.setTextFill(Color.BLACK);
		button.setOnAction(evt -> {
			
			String pays = textFieldPays.getText();
			String ville = textFieldVille.getText();
			String chambre = textFieldChambre.getText();
			String SingCoup = group.toString();
			String nbrPersonnes = textFieldPersonnes.getText();
		   
			GuiEvent guiEvent = new GuiEvent(evt, 1);
			List<String> list = new ArrayList<>(Arrays.asList(pays,ville,chambre,SingCoup,nbrPersonnes));;
			guiEvent.addParameter(list);
			aiAgent.onGuiEvent(guiEvent);
			
		});

		HBox hBoxButton = new HBox();
		hBoxButton.setAlignment(Pos.CENTER);
		hBoxButton.getChildren().add(button);
		
		
		observableList = FXCollections.observableArrayList();
		ListView<String> listViewMessages = new ListView<String>(observableList);
		VBox vBox2 = new VBox();
		vBox2.setPadding(new Insets(10));
		vBox2.setSpacing(10);
		vBox2.getChildren().add(listViewMessages);
		
		/* vbox */
		VBox vBoxTous = new VBox();
		vBoxTous.setPadding(new Insets(10));
		vBoxTous.setSpacing(10);
		vBoxTous.getChildren().addAll(hBoxPays,hBoxVille,hBoxChambre,hBoxRadio,hBoxPersonnes,hBoxDate,hBoxButton);
		borderPane.setBottom(vBox2);
		
		borderPane.setCenter(vBoxTous);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		
	}
	
	private void startContainer()  throws Exception {
		
		Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl = new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer container = runtime.createAgentContainer(profileImpl); 
		
		AgentController agentController = container.createNewAgent("AiAgent",AIAgent.class.getName(), new Object[] {this});
		
		agentController.start();
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(()->{			
			observableList.add(aclMessage.getSender().getName()+" => "+aclMessage.getContent()+ " From "+aclMessage.getSender().getName());
		});
	}

}
