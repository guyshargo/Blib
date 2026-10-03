package gui.auth;

import client.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gui.subscriber.MemberMainPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Subscriber;

/**
 * Description:
 * Controller class for the member's login page
 */
public class MemberLogInController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField MemberPassword;
    @FXML private Button btnReturn;
    @FXML private Button btnLogIn;
    @FXML private Subscriber subscriber;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/auth/MemberLogin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/auth/MemberLogin.css").toExternalForm());
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
        LoginController view = new LoginController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method for returning inserted member username
     *
     * @return String.class (member username)
     */
    public String getUsername() {
        return txtUsername.getText();
    }

    /**
     * Description:
     * Method for returning inserted member password
     *
     * @return String.class (member password)
     */
    public String getPassword() {
        String password = MemberPassword.getText();
        return password;
    }

    public void Login(ActionEvent event) {
        try {
            String username = getUsername().trim();
            String memberpassword = getPassword().trim();

            if (username.isEmpty() || memberpassword.isEmpty()) {
                showAlertError("Member Password Mismatch", "The member password is incorrect.");
                return;
            }

            // 1. HTTP GET request to check the user
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/login/subscriber?username=" + username + "&password=" + memberpassword))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401 || response.statusCode() == 404) {
                showAlertError("Login Failed", "Username or Password are incorrect. Please try again.");
                return;
            }

            // 2. Parse JSON response into a Subscriber object
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // For LocalDate handling
            subscriber = mapper.readValue(response.body(), Subscriber.class);

            // 3. Validate password and login status locally
            if (!subscriber.getPassword().equals(memberpassword)) {
                showAlertError("Member Password Mismatch", "The member password is incorrect.");
                return;
            } else if (subscriber.getLoginStatus()) {
                showAlertError("Login Status", "The user is already logged into the system.");
                return;
            }

            // 4. Store session locally
            subscriber.setLoginStatus(true);
            SessionManager.currentSubscriber = subscriber;

            // 5. HTTP PUT request to update login status in the database
            HttpRequest statusRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/" + subscriber.getMembershipNumber() + "/login-status?status=true"))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            client.send(statusRequest, HttpResponse.BodyHandlers.ofString());

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
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}