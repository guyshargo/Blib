package gui.SubscriberGUI;

import client.ChatClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Subscriber;

public class SubscriberUpdateFrameController {

	@FXML
	private Button btnReturn;

	@FXML
	private Button btnUpdate;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtPhoneNumber;

	@FXML
	private TextField txtEmail;

	@FXML
	private TextField txtHistory;

	private Subscriber subscriber;

	// load subscriber
	public void loadSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
		txtId.setText(String.valueOf(subscriber.getSubscriber_id()));
		txtName.setText(subscriber.getSubscriber_name());
		txtPhoneNumber.setText(subscriber.getSubscriber_phone_number());
		txtEmail.setText(subscriber.getSubscriber_email());
		txtHistory.setText(String.valueOf(subscriber.getDetailed_subscription_history()));
	}

	// load scene
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/SubscriberGUI/SubscriberUpdateFrame.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberGUI/SubscriberUpdateFrame.css").toExternalForm());
		primaryStage.setTitle("Update Subscriber");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// update subscriber phone number or email address
	public void updateSubscriber(ActionEvent event) throws Exception {
		// retrieve the updated values from the text fields
		String id = txtId.getText();
		String phoneNumber = txtPhoneNumber.getText();
		String email = txtEmail.getText();
		if (phoneNumber.isEmpty() || email.isEmpty()) {
			showAlert("Email Or Phone Are Empty", "Phone and Email Cannot Be Empty");
			txtPhoneNumber.setText(subscriber.getSubscriber_phone_number());
			txtEmail.setText(subscriber.getSubscriber_email());
		} else {
			String updateMessage = "UpdateReaderCardMember:" + id + "," + phoneNumber + "," + email;
			ClientUI.chat.accept(updateMessage); // send the update request to the server
		}
		if (ChatClient.sub == null) {
			showAlert("Update Failed", "Couldnt Update Student");
		}
	}

	// return to subscriber id entry page
	public void getReturnBtn(ActionEvent event) throws Exception {
		System.out.println("Close Update Subscriber");
		SubscriberViewFrameController view = new SubscriberViewFrameController();
		view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
	}

	// alerts handling function
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
