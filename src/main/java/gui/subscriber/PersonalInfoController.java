package gui.subscriber;

import client.SessionManager;
import gui.auth.LogoutUtil;
import model.Activity;
import model.Subscriber;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Description:
 * Controller class for the member to show and update his saved information
 */
public class PersonalInfoController {
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtMembershipNumber;
    @FXML private TextField txtPhoneNumber;
    @FXML private TextField txtFreezeStatus;
    @FXML private Button btnUpdatePhoneNumber;
    @FXML private Button btnUpdateEmail;
    @FXML private TableView<Activity> tableActivities;
    @FXML private TableColumn<Activity, String> colActivityDate;
    @FXML private TableColumn<Activity, String> colActivityType;
    @FXML private TableColumn<Activity, String> colActivityDesc;

    private Subscriber subscriber;

    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/subscriber/PersonalInfo.fxml"));
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colActivityDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getActivityDateTime() != null) {
                return new SimpleStringProperty(cellData.getValue().getActivityDateTime().format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colActivityType.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getActivityType() != null ? cellData.getValue().getActivityType().toString() : "")
        );

        colActivityDesc.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getActivityDescription())
        );

        tableActivities.setPlaceholder(new Label("No activities found yet."));

        subscriberShowInfo();
    }

    /**
     * Description:
     * Method for showing all member's information into the text fields
     */
    private void subscriberShowInfo() {
        if (SessionManager.currentSubscriber != null) {
            this.subscriber = SessionManager.currentSubscriber;
            txtEmail.setText(subscriber.getEmailAddress());
            txtMembershipNumber.setText(String.valueOf(subscriber.getMembershipNumber()));
            txtPhoneNumber.setText(String.valueOf(subscriber.getPhoneNumber()));
            txtFreezeStatus.setText(String.valueOf(subscriber.getFreezeStatus()));
            
            // Clear and load activities
            tableActivities.getItems().clear();
            LoadActivities();
        }
    }

    public void LoadActivities() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/activities/subscriber/" + subscriber.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<Activity> activities = mapper.readValue(response.body(), new TypeReference<List<Activity>>(){});
                
                if (activities != null && !activities.isEmpty()) {
                    ObservableList<Activity> activityData = FXCollections.observableArrayList(activities);
                    tableActivities.setItems(activityData);
                } else {
                    tableActivities.setItems(FXCollections.observableArrayList());
                }
            } else {
                tableActivities.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> 
                showAlertError("Data Error", "Could not load activities.\nDetails: " + e.getMessage())
            );
        }
    }


    public String showUpdateWindow(String title, String type) {
        TextInputDialog dialogUpdate = new TextInputDialog();
        dialogUpdate.setTitle(title);
        dialogUpdate.setHeaderText("Update " + type);
        dialogUpdate.setContentText("Please enter new " + type + " here:");
        
        Optional<String> input = dialogUpdate.showAndWait();
        return input.orElse(null);
    }


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
        saveMemberData(subscriber.getPhoneNumber(), updateEmail);
    }


    private synchronized void saveMemberData(String memberPhoneNumber, String emailAddress) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String encodedPhone = URLEncoder.encode(memberPhoneNumber, StandardCharsets.UTF_8);
            String encodedEmail = URLEncoder.encode(emailAddress, StandardCharsets.UTF_8);
            
            // 1. Send update PUT request
            String putUrl = String.format("http://localhost:8080/api/subscribers/%d/edit-contact?phone=%s&email=%s", 
                    subscriber.getMembershipNumber(), encodedPhone, encodedEmail);
            HttpRequest putReq = HttpRequest.newBuilder().uri(URI.create(putUrl)).PUT(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> putRes = client.send(putReq, HttpResponse.BodyHandlers.ofString());

            if (putRes.statusCode() == 200) {
                // 2. Fetch updated subscriber details GET request
                HttpRequest getReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/subscribers/" + subscriber.getMembershipNumber()))
                        .GET().build();
                HttpResponse<String> getRes = client.send(getReq, HttpResponse.BodyHandlers.ofString());
                
                if (getRes.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                    Subscriber updatedSub = mapper.readValue(getRes.body(), Subscriber.class);
                    
                    SessionManager.currentSubscriber = updatedSub;
                    this.subscriber = updatedSub;
                    showAlertSuccess("Success", "Personal information updated successfully.");
                    subscriberShowInfo();
                } else {
                    showAlertError("Error", "Failed to fetch updated subscriber details.");
                }
            } else {
                showAlertError("Error", "Failed to update contact info on server.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "Network connection failed.");
        }
    }
    
    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(content);
        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();
        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/Alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }


    private void showAlertSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(content);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/Success.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

}

