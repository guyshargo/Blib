package client;

import gui.ServerGUI.ConnectClientToServerController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
	public static ClientController chat; // only one instance

	// setting client controller to given server ip with port 5555
	public static boolean setClientController(String serverIp) {
		try {
			chat = new ClientController(serverIp, 5555); // 5555 port
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String args[]) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ConnectClientToServerController aFrame = new ConnectClientToServerController(); // create the frame
		aFrame.start(primaryStage);
	}

}
