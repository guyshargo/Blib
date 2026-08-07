package gui.ServerGUI;

import client.ClientUI;
import gui.SubscriberGUI.SubscriberViewFrameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectClientToServerController {

	@FXML
	private Button btnExit;

	@FXML
	private Button btnConnect;

	@FXML
	private TextField IPtxt;

	// loading the scene
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerGUI/SubscriberConnectToServer.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ServerGUI/SubscriberConnectToServer.css").toExternalForm());
		primaryStage.setTitle("Connect To Server");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// connect to server
	@FXML
	public void ConnectToServer(ActionEvent event) throws Exception {
		String ip = getIP();
		if (ip.trim().isEmpty()) {
			showAlert("Empty IP Field", "Please enter an IP.");
			return;
		}
		// attempt to connect
		boolean connectionSuccess = ClientUI.setClientController(ip);
		if (!connectionSuccess) {
			showAlert("Connection Failed", "Couldn't connect to the server. Please check the IP and try again.");
			IPtxt.setText("");
		} else {
			// hide the current window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.hide();
			// open the subscriber view frame
			SubscriberViewFrameController view = new SubscriberViewFrameController();
			view.start(new Stage());
		}
	}

	@FXML
	private void getExitBtn(ActionEvent event) {
		System.exit(0);
	}

	private String getIP() {
		return IPtxt.getText();
	}

	// alerts handling function
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
