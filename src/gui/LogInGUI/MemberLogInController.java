package gui.LogInGUI;

import client.ChatClient;
import client.ClientUI;
import gui.MemberGUI.MemberMainPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.Subscriber;

/**
 * Description:
 * Controller class for the member's login page
 */
public class MemberLogInController {
    // Attributes
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField MemberPassword;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnLogIn;

    @FXML
    private Subscriber subscriber;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = FXMLLoader.load(getClass().getResource("/gui/LogInGUI/MemberLogInFrame.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LogInGUI/MainLogInFrame.css").toExternalForm());
        primaryStage.setTitle("Type Member information");
        primaryStage.setScene(scene);
        primaryStage.show();
        LogoutUtil.addWindowCloseListener(primaryStage);
    }

    /**
     * Description:
     * Method for returning to the login page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void getReturnBtn(ActionEvent event) throws Exception {
        LogInController view = new LogInController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method for returning inserted member username
     *
     * @return String.class (member username)
     */
    public String getMemberUsername() {
        return txtUsername.getText();
    }

    /**
     * Description:
     * Method for returning inserted member password
     *
     * @return String.class (member password)
     */
    public String getMembersPassword() {
        String password = MemberPassword.getText();
        return password;
    }

    /**
     * Description:
     * Method for loading username and password, then processing the login process
     *
     * @param event ActionEvent.class
     */
    public void Login(ActionEvent event) {
        try {
            // Get entered details
            String username = getMemberUsername().trim();
            String memberpassword = getMembersPassword().trim();

            // Validate input fields
            if (username.isEmpty() || memberpassword.isEmpty()) {
                showAlertError("Member Password Mismatch", "The member password is incorrect.");
                return;
            }
            // Reset the subscriber field to null before sending a new request
            ChatClient.subscriberLogin = null;

            // Send member username to the server for validation
            String command = "LogInMember:" + username;
            ClientUI.chat.accept(command);

            // Check if the member exists
            subscriber = ChatClient.subscriberLogin;
            if (subscriber == null) {
                showAlertError("Login Failed", "Username or Password are incorrect. Please try again.");
                return;
            } else if (!subscriber.getMemberPassword().equals(memberpassword)) {
                showAlertError("Member Password Mismatch", "The member password is incorrect.");
                return;
            } else if (subscriber.getLoginStatus() == true) {
                showAlertError("Login Status", "The user is already logged into the system.");
                return;
            }
            subscriber.setLoginStatus(true);
            ChatClient.subscriberLogin = subscriber;
            command = "SubscriberSetLoginStatus:" + subscriber.getMembershipNumber() + "," + subscriber.getLoginStatus();
            ClientUI.chat.accept(command);
            MemberMainPageController view = new MemberMainPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Description:
     * Method for showing an alert window based on the title and message given
     *
     * @param title   String.class
     * @param content String.class
     */
    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(content);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}