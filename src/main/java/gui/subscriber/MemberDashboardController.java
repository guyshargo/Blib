package gui.subscriber;

import client.SessionManager;
import gui.auth.LogoutUtil;
import gui.catalog.SearchCatalogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

/**
 * Description:
 * Controller class for the member's main page
 */
public class MemberDashboardController {
    @FXML public Button extendBorrowPageButton;
    @FXML public Button searchBookButton;
    @FXML public Button viewPersonalInfoButton;
    @FXML public Button orderPageButton;
    @FXML public Button logoutButton;
    @FXML private Label subscriberName;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/subscriber/MemberDashboard.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/subscriber/MemberDashboard.css").toExternalForm());
        primaryStage.setTitle("Subscriber Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Register window close listener for logout
        LogoutUtil.addWindowCloseListener(primaryStage);
    }

    /**
     * Description:
     * Method to show greetings text with subscriber's full name
     */
    private void subscriberShowInfo() {
        String subname;
        if (SessionManager.currentSubscriber == null)
            subname = "subscriber";
        else
            subname = SessionManager.currentSubscriber.getFullName();
        subscriberName.setText("" + subname);
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
     * Method to goto Search Book Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleSearchBookButton(ActionEvent event) {
        SearchCatalogController view = new SearchCatalogController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method to logout from subscribers account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    private void handleLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method to goto Update Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleViewPersonalInfoButton(ActionEvent event) {
        PersonalInfoController view = new PersonalInfoController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method to goto Extend Borrow Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleExtendBorrowPageButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/subscriber/borrowextend/BorrowExtendPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/gui/subscriber/borrowextend/BorrowExtendPage.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("My borrowed books");

            // Register window close listener for logout
            LogoutUtil.addWindowCloseListener(currentStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to goto Order Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleOrderPageButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/subscriber/order/OrderPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene.getStylesheets().add(getClass().getResource("/gui/subscriber/order/OrderPage.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("My Orders");
            LogoutUtil.addWindowCloseListener(currentStage); // Register window close listener for logout
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}