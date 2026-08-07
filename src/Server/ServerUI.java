package Server;

import gui.ServerGUI.ServerConnectionTable;
import javafx.application.Application;
import javafx.stage.Stage;
import logic.Message;
import logic.TimeManagementControl.TimeManagement;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	private static TimeManagement timeManagement = new TimeManagement();



	public static void main(String args[]) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerConnectionTable aFrame = new ServerConnectionTable(); // create StudentFrame
		aFrame.start(primaryStage);
	}

	// run the server
	public static void runServer(String p) {
		int port = 0; // initialize port
		try {
			port = Integer.parseInt(p); // Set port to 5555
		} catch (Throwable t) {
			System.out.println("ERROR - Could not connect!");
		}
		EchoServer sv = new EchoServer(port);
		try {
			sv.listen(); // Start listening for connections
			// Start the TimeManagement scheduler
			timeManagement.startReminderScheduler();
			System.out.println("TimeManagement scheduler started.");
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

}