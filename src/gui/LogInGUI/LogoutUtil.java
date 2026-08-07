package gui.LogInGUI;

import client.ChatClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import logic.Message;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.List;

/**
 * Description:
 * Class for logging out of the library
 */
public class LogoutUtil {
    /**
     * Description:
     * Method for handling the logout process (clearing login status and redirecting to the login screen)
     */
    public static void handleLogoutAction() {
        // Handle logout logic
        if (ChatClient.librarianLogin != null) {
            String command = "LibrarianSetLoginStatus:" + ChatClient.librarianLogin.getLibrarianID() + "," + false;
            ClientUI.chat.accept(command);
        }
        if (ChatClient.subscriberLogin != null) {
            String command = "SubscriberSetLoginStatus:" + ChatClient.subscriberLogin.getMembershipNumber() + "," + false;
            ClientUI.chat.accept(command);
        }
        ChatClient.librarianLogin = null;
        ChatClient.subscriberLogin = null;
    }

    /**
     * Description:
     * Method for adding a 'close listener' window to trigger logout when the window is closed
     *
     * @param stage Stage.class
     */
    public static void addWindowCloseListener(Stage stage) {
        stage.setOnCloseRequest(event -> {
            try {
                handleLogoutAction(); // Perform logout when window is closed
                String command = "Quit:Program";
                ClientUI.chat.accept(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Description:
     * Method for adding a 'close listener' window to trigger exit server when the window is closed
     *
     * @param stage       Stage.class
     * @param connections List<ConnectionToClient>.class
     */
    public static void addWindowCloseListenerAndExitServer(Stage stage, List<ConnectionToClient> connections) {
        stage.setOnCloseRequest(event -> {
            try {
                exitServer(connections);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Description:
     * Method for adding an 'exit server' option
     *
     * @param clientConnections List<ConnectionToClient>.class
     */
    public static void exitServer(List<ConnectionToClient> clientConnections) {
        try {
            for (ConnectionToClient client : clientConnections) {
                try {
                    // Send Quit message to the client
                    Message msgReturn = new Message("Quit", null);
                    client.sendToClient(msgReturn);  // Send message
                } catch (IOException e) {
                    System.out.println("Error disconnecting client: " + e.getMessage());
                }
            }
            System.exit(0);  // Exit the server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method for being called from a button event handler in the GUI to perform the logout
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public static void handleLogoutButtonAction(ActionEvent event) throws Exception {
        handleLogoutAction();  // Call the same logout logic when the button is clicked
        // Redirect to the login screen
        LogInController view = new LogInController();
        view.start(new Stage()); // Open a new stage for the login screen
    }
}