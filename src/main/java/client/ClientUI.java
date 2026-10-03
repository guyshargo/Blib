package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientUI extends Application {
	public static void main(String args[]) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/gui/auth/MainLogInFrame.fxml"));
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Blib Library System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
