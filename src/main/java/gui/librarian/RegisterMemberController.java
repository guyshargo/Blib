package gui.librarian;

import client.SessionManager;
import dto.RegisterRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gui.common.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Description:
 * Controller class for the librarian to register a new member into the system
 */
public class RegisterMemberController {
    @FXML private TextField idField;
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordtextField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Button submitButton;
    @FXML private Button clearButton;
    @FXML private Button btnReturn;
    @FXML private Label librarianName;

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    private void initialize() {
        librarianName.setText(SessionManager.currentLibrarian.getFullName());
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

        try {
            // 1. Populate the DTO
            RegisterRequest req = new RegisterRequest();
            req.setMembershipNumber(Integer.parseInt(id));
            req.setFullName(fullName);
            req.setUserName(username);
            req.setPassword(password);
            req.setPhone(phone);
            req.setEmail(email);

            // 2. Convert DTO to JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String requestBody = mapper.writeValueAsString(req);

            // 3. Send HTTP POST
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Handle Response
            if (response.statusCode() == 200) {
                showAlertSuccess("Registration Succeeded", "You have been successfully registered.");
            } else {
                // Spring Boot returns 400 Bad Request with the error string
                showAlertError("Registration Failed", response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "An unexpected error occurred.");
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
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/LibrarianDashboard.fxml");
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
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-error");

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
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-success");
        alert.showAndWait();
    }
}
