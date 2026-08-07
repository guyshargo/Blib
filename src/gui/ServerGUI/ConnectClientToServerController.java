package gui.ServerGUI;

import client.ClientUI;
import gui.LogInGUI.LogInController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Description:
 * Class for the Client to connect to the server using an ip
 */
public class ConnectClientToServerController {

    @FXML
    private Button btnExit;

    @FXML
    private Button btnConnect;

    @FXML
    private TextField IPtxt;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerGUI/SubscriberConnectToServer.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerGUI/SubscriberConnectToServer.css").toExternalForm());
        primaryStage.setTitle("Connect To Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Description:
     * Method for the client to try connecting to the Server when pressing on the 'Connect' button
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    public void ConnectToServer(ActionEvent event) throws Exception {
        String ip = getIP();
        if (ip.trim().isEmpty()) {
            showAlertError("Empty IP Field", "Please enter an IP.");
            return;
        }
        // attempt to connect
        boolean connectionSuccess = ClientUI.setClientController(ip);
        if (!connectionSuccess) {
            showAlertError("Connection Failed", "Couldn't connect to the server. Please check the IP and try again.");
            IPtxt.setText("");
        } else {
            try {
                // hide the current window
                // open the subscriber view frame
                LogInController view = new LogInController();
                view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
            } catch (Exception e) {
                System.err.println("Error loading the next scene: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for shutting down the client when pressing on the 'Exit' button
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void getExitBtn(ActionEvent event) {
        System.exit(0);  // Exit the server
    }

    /**
     * Description:
     * Method for retrieving the ip given in the ip text field
     *
     * @return String.class (ip)
     */
    private String getIP() {
        return IPtxt.getText();
    }

    /**
     * Description:
     * Method for showing an alert window
     *
     * @param title   String.class
     * @param message String.class
     */
    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(message);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();
        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}