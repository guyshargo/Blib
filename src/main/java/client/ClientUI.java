package client;

import gui.ServerGUI.ConnectClientToServerController;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientUI extends Application {
	public static void main(String args[]) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ConnectClientToServerController aFrame = new ConnectClientToServerController();
		aFrame.start(primaryStage);
	}

}
