package gui.MemberGUI.PersonalInfo;

import client.ChatClient;
import client.ClientUI;
import gui.LogInGUI.LogoutUtil;
import gui.MemberGUI.MemberMainPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.ActivityControl.Activity;
import logic.Subscriber;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Description:
 * Controller class for the member to show and update his saved information
 */
public class PersonalInfoController {
    @FXML
    private TextField txtFullName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtMembershipNumber;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtFreezeStatus;

    @FXML
    private Button btnUpdatePhoneNumber;

    @FXML
    private Button btnUpdateEmail;

    @FXML
    private Button btnReturn;

    @FXML
    private ListView<String> listViewActivities;

    @FXML
    private Label subscriberName;

    //  current Subscriber object
    private Subscriber subscriber;

    // Flag to check if it's the first load
    private boolean isFirstLoad = true;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     */
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MemberGUI/PersonalInfo/PersonalInfoPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Member Personal Information");
            primaryStage.setScene(scene);
            primaryStage.show();
            LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
        } catch (IOException e) {
            e.printStackTrace();
            showAlertError("Loading Error", "Failed to load the search page.");
        }
    }

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    private void initialize() {
        subscriberShowInfo();
    }

    /**
     * Description:
     * Method for showing all member's information into the text fields
     */
    private void subscriberShowInfo() {
        String name;
        if (ChatClient.subscriberLogin == null) {
            subscriberName.setText("subscriber");
        } else {
            this.subscriber = ChatClient.subscriberLogin;
            subscriberName.setText(subscriber.getMemberFullName());
            txtEmail.setText(subscriber.getEmailAddress());
            txtMembershipNumber.setText(String.valueOf(subscriber.getMembershipNumber()));
            txtPhoneNumber.setText(String.valueOf(subscriber.getMemberPhoneNumber()));
            txtFreezeStatus.setText(String.valueOf(subscriber.getMemberFreezeStatus()));
            // Clear and load activities
            listViewActivities.getItems().clear();
            LoadActivities();
        }
    }

    /**
     * Description:
     * Method for showing all member's activities into the text fields
     */
    public void LoadActivities() {
        // Construct the command to fetch activities
        String command = "fetchAllActivitiesForASubscriber:" + subscriber.getMembershipNumber();
        ClientUI.chat.accept(command); // Send the request to the server
        List<Activity> activities = ChatClient.ActivityList;
        ChatClient.sub = subscriber;

        // Check if the ActivityList is not null
        if (activities != null && !activities.isEmpty()) {
            // Populate the listViewActivities with activity details
            listViewActivities.getItems().clear(); // Clear any existing items
            int activitiesCnt = 1;
            for (Activity activity : activities) {
                listViewActivities.getItems().add(activitiesCnt + ") " + activity.toString());
                activitiesCnt++;
            }
        } else {
            // Handle the case where no activities are found
            listViewActivities.getItems().clear();
            listViewActivities.getItems().add("No activities found yet.");
        }
    }

    /**
     * Description:
     * Method for updating information dialog window
     *
     * @param title String.class
     * @param type  String.class
     * @return String.class (inserted update information type)
     */
    public String showUpdateWindow(String title, String type) {
        String updateInput;
        TextInputDialog dialogUpdate = new TextInputDialog();
        dialogUpdate.setTitle(title);
        dialogUpdate.setHeaderText("Update" + type);
        dialogUpdate.setContentText("Please enter new " + type + " here:");
        // Capture the input value to update
        Optional<String> input = dialogUpdate.showAndWait();
        if (input.isPresent()) {
            return input.get();
        } else {
            return null;
        }
    }

    /**
     * Description:
     * Method for changing member phone number when pressing the 'Update phone number' button
     *
     * @param event ActionEvent.class
     */
    public void handleUpdatePhoneNumberBtn(ActionEvent event) {
        String updatePhone = "";
        int flag = 1;
        while (flag == 1) {
            // Get input and validate
            updatePhone = showUpdateWindow("Update Phone Number", "Phone Number");
            if (updatePhone == null) {
                return;
            } else if ((updatePhone.length() != 10 || !updatePhone.matches("\\d+"))) {
                showAlertError("Invalid Phone Number", "Phone number must be 10 digits and contain only numbers.");
            } else {
                flag = 0;
            }
        }
        // Save the updated phone number
        saveMemberData(updatePhone, subscriber.getEmailAddress());
    }

    /**
     * Description:
     * Method for changing member email when pressing the 'Update email' button
     *
     * @param event ActionEvent.class
     */
    public void handleUpdateEmailBtn(ActionEvent event) {
        // Get input for email update
        String updateEmail = "";
        int flag = 1;
        while (flag == 1) {
            updateEmail = showUpdateWindow("Update Email Adress", "Email Adress");
            // Check valid input conditions
            if (updateEmail == null) {
                return;
            } else if (!updateEmail.contains("@") || !updateEmail.contains(".")) {
                showAlertError("Invalid Email Address", "Please enter a valid email address.");
            } else {
                flag = 0;
            }
        }
        // Save the updated email address
        saveMemberData(subscriber.getMemberPhoneNumber(), updateEmail);
    }

    /**
     * Description:
     * Method for saving a member's changes
     *
     * @param memberPhoneNumber String.class
     * @param emailAddress      String.class
     */
    private synchronized void saveMemberData(String memberPhoneNumber, String emailAddress) {
        String command = "UpdateReaderCardMember:" + subscriber.getMembershipNumber() + "," + memberPhoneNumber + "," + emailAddress;

        // Send the update command to the server
        ClientUI.chat.accept(command);

        // Fetch the updated subscriber data
        String fetchCommand = "ViewReaderCard:" + subscriber.getMembershipNumber();
        ClientUI.chat.accept(fetchCommand);

        // Update the local subscriber and UI
        if (ChatClient.subscriberLogin != null) {
            ChatClient.subscriberLogin = ChatClient.sub; // Update the global subscriber object
            this.subscriber = ChatClient.subscriberLogin;
            showAlertSuccess("Success", "Personal information updated successfully.");
            subscriberShowInfo();
        } else {
            showAlertError("Error", "Failed to fetch updated subscriber details. Please try again.");
        }
    }

    /**
     * Description:
     * Method for returning to the member main page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void getReturnBtn(ActionEvent event) throws Exception {
        MemberMainPageController view = new MemberMainPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method for showing an error alert
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

    /**
     * Description:
     * Method for showing a success alert
     *
     * @param title   String.class
     * @param content String.class
     */
    private void showAlertSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(content);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/success.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    /**
     * Description:
     * Method for logging out from the member
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading a scene)
     */
    @FXML
    private void handleLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}

