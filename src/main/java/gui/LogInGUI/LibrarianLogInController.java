package gui.LogInGUI;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.Librarian;

/**
 * Description:
 * Controller class for the librarian login page
 */
public class LibrarianLogInController {
    @FXML
    private TextField txtlibUsername;

    @FXML
    private PasswordField librarianPassword;

    @FXML
    private Button btnReturn;

    @FXML
    private Button btnLogIn;

    @FXML
    private Button BtnSignup;

    private Librarian librarian;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = FXMLLoader.load(getClass().getResource("/gui/LogInGUI/LibrarianLogInFrame.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getResource("/gui/LogInGUI/MainLogInFrame.css").toExternalForm());
        primaryStage.setTitle("Type Librarian information");
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
     * Method for returning inserted librarian username
     *
     * @return String.class (librarian username)
     */
    public String getLibrarianUsername() {
        return txtlibUsername.getText();
    }

    /**
     * Description:
     * Method for returning inserted librarian password
     *
     * @return String.class (librarian password)
     */
    public String getLibrarianPassword() {
        return librarianPassword.getText();
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
            String lib_username = getLibrarianUsername().trim();
            String lib_password = getLibrarianPassword().trim();

            // Validate input fields
            if (lib_password.isEmpty() || lib_username.isEmpty()) {
                showAlertError("Membership Number or Password is Empty", "Member ID and Password cannot be empty.");
                return;
            }

            // Reset the librarian field to null before sending a new request
            ChatClient.librarianLogin = null;

            // Send librarian username to the server for validation
            String command = "LogInLibrarian:" + lib_username;
            ClientUI.chat.accept(command);

            // Check if the librarian exists
            librarian = ChatClient.librarianLogin;
            if (librarian == null) {
                showAlertError("Username Not Found", "The Username does not exist. Please sign up!");
                return;
            } else if (!librarian.getPassword().equals(lib_password)) {
                showAlertError("Username and Password Mismatch", "The librarian password is incorrect.");
                return;
            } else if (librarian.getLoginStatus() == true) {
                showAlertError("Login Status", "The user is already logged into the system.");
                return;
            }
            librarian.setLoginStatus(true);
            ChatClient.librarianLogin = librarian;
            command = "LibrarianSetLoginStatus:" + librarian.getLibrarianID() + "," + librarian.getLoginStatus();
            ClientUI.chat.accept(command);

            LibrarianMainPageController view = new LibrarianMainPageController();
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