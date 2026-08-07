package gui.LibrarianGUI.MemberRegistration;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogoutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Description:
 * Controller class for the librarian to register a new member into the system
 */
public class MemberRegistrationPageController {
    @FXML
    private TextField idField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordtextField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Button submitButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button btnReturn;

    @FXML
    private Label librarianName;

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    private void initialize() {
        librarianName.setText(ChatClient.librarianLogin.getFullName());
    }

    /**
     * Description:
     * Method for registering a new member when clicking on the 'Submit' button
     *
     * @param event ActionEvent.class
     */
    @FXML
    public void handleSubmit(ActionEvent event) {
        String id = idField.getText();
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = passwordtextField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        // Validation for empty fields
        if (id.trim().isEmpty() || fullName.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()
                || phone.trim().isEmpty() || email.trim().isEmpty()) {
            showAlertError("Empty Fields", "Please fill all fields to complete registration.");
            return;
        }

        // Check if phone number contains only digits
        if (!phone.matches("\\d+") || phone.length() != 10) {
            showAlertError("Invalid Phone Number", "Please enter: a valid phone number with only digits and with only 10 digits.");
            return;
        }

        // Check if id contains only digits
        if (!id.matches("\\d+") || id.length() != 9) {
            showAlertError("Invalid ID", "Please enter a valid ID with only digits and with only 9 digits.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlertError("Invalid Email Address", "Please enter a valid email address.");
            return;
        }

        // If validation passes, send data to the server
        String command = String.format("RegisterMember:%s,%s,%s,%s,%s,%s", id, fullName, username, password, phone, email);
        ClientUI.chat.accept(command);

        // Display success or failure message
        if (ChatClient.sub != null) {
            // Add activity to activity_db
            int membershipNumber = ChatClient.sub.getMembershipNumber();
            String activityDetails = "registerMember," + ChatClient.sub.getMemberFullName() + " has been registered to the library";
            addActivity(membershipNumber, activityDetails);
            showAlertSuccess("Registration Succeeded", "You have been successfully registered.");
        } else {
            showAlertError("Registration Failed", "Error in Registration:\n" + ChatClient.messageRegisterFailed);
        }
    }

    /**
     * Description:
     * Method for clearing all data from the text fields
     *
     * @param event ActionEvent.class
     */
    @FXML
    public void handleClear(ActionEvent event) {
        idField.clear();
        fullNameField.clear();
        usernameField.clear();
        passwordtextField.clear();
        phoneField.clear();
        emailField.clear();
    }

    /**
     * Description:
     * Method for returning to the librarian main page
     *
     * @param event ActionEvent.class
     */
    @FXML
    public void handleReturn(ActionEvent event) {
        try {
            LibrarianMainPageController view = new LibrarianMainPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to logout from librarian's account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method for showing an alert window based on the title and message given
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

    /**
     * Description:
     * Method for showing an alert window based on the title and message given
     *
     * @param title   String.class
     * @param message String.class
     */
    private void showAlertSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(message);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/success.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUI/MemberRegistration/MemberRegistrationPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/MemberRegistration/MemberRegistrationPage.css").toExternalForm());
        primaryStage.setTitle("Registration Management Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
        LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
    }

    /**
     * Description:
     * Method for adding an activity to the activity_db (registration)
     *
     * @param membershipNumber int
     * @param activityDetails  String.class
     */
    private void addActivity(int membershipNumber, String activityDetails) {
        String command = "addActivity:" + membershipNumber + "," + activityDetails;
        ClientUI.chat.accept(command);
    }
}