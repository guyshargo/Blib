package gui.SubscriberGUI;

import client.ChatClient;
import client.ClientUI;
import gui.ServerGUI.ConnectClientToServerController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SubscriberViewFrameController {

	private SubscriberUpdateFrameController sfc;

	@FXML
	private Button btnReturn = null;

	@FXML
	private Button btnSend = null;

	@FXML
	private TextField idtxt;

	private String getID() {
		return idtxt.getText();
	}

	// send
	public void Send(ActionEvent event) throws Exception {
		String id;
		FXMLLoader loader = new FXMLLoader();

		id = getID();
		if (id.trim().isEmpty()) {
			showAlert("Empty Id Field", "Please Enter An Id..");
		}
		// Check if the id contains only digits
		else if (!id.matches("\\d+")) {
			showAlert("Invalid Id", "The ID must contain only digits.");
		} else {
			// Reset the sub field to null before sending a new request
			ChatClient.sub = null;
			// Send the ID to the server for validation
			String command = "ViewReaderCard:" + id;
			ClientUI.chat.accept(command);

			// Check if the subscriber was found
			if (ChatClient.sub == null) {
				showAlert("Subscriber Not Found", "No subscriber found with the given ID.");

			} else {
				// Hide the current window
				((Node) event.getSource()).getScene().getWindow().hide();

				// Load the SubscriberUpdateFrame instead of StudentForm
				Stage primaryStage = new Stage();
				Pane root = loader.load(getClass().getResource("/gui/SubscriberGUI/SubscriberUpdateFrame.fxml").openStream());
				SubscriberUpdateFrameController subscriberUpdateFrame = loader.getController();

				// Load the subscriber details into the update frame
				subscriberUpdateFrame.loadSubscriber(ChatClient.sub);

				// Set up the scene and show the update frame
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("/gui/SubscriberGUI/SubscriberUpdateFrame.css").toExternalForm());
				primaryStage.setTitle("Update Subscriber");

				primaryStage.setScene(scene);
				primaryStage.show();
			}
		}
	}

	// load scene
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/SubscriberGUI/SubscriberViewFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberGUI/SubscriberViewFrame.css").toExternalForm());
		primaryStage.setTitle("Subscriber Managment Tool");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	// alerts handling function
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.showAndWait();
	}

	// return to ip entry page
	public void getReturnBtn(ActionEvent event) throws Exception {
		ClientUI.chat.accept("Quit:Program");
		ConnectClientToServerController view = new ConnectClientToServerController();
		view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
	}

}
