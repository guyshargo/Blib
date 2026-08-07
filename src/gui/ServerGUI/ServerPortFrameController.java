package gui.ServerGUI;

import Server.ServerUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerPortFrameController {

	@FXML
	private TextField txtIp;

	@FXML
	private TextField txtHost;

	@FXML
	private TextField txtStatus;

	@FXML
	private Button btnExit = null;

	private static final String DEFAULT_PORT = "5555"; // Define port number

	// loading the scene
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerGUI/ServerPort.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ServerGUI/ServerPort.css").toExternalForm());
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// run server with DEFAULT_PORT
	public void runServer() {
		ServerUI.runServer(DEFAULT_PORT, this);
	}

	// method to update the connection info (IP, Hostname, and Status)
	public void updateConnectionInfo(String ip, String hostName, String status) {
		txtIp.setText(ip);
		txtHost.setText(hostName);
		txtStatus.setText(status);
	}

	public void getExitBtn(ActionEvent event) throws Exception {
		System.exit(0);
	}
}