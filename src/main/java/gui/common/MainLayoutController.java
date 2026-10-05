package gui.common;

import client.SessionManager;
import gui.auth.LogoutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class MainLayoutController {

    @FXML private BorderPane mainBorderPane;
    @FXML private HBox navBar;
    @FXML private Label subscriberName;
    @FXML private Button backButton;
    @FXML private Button forwardButton;
    @FXML private Button logoutButton;

    private Stack<String> backHistory = new Stack<>();
    private Stack<String> forwardHistory = new Stack<>();
    private String currentView = null;
    
    private static MainLayoutController instance;

    @FXML
    public void initialize() {
        instance = this;

        // Determine user role and setup UI
        if (SessionManager.currentLibrarian != null) {
            subscriberName.setText(SessionManager.currentLibrarian.getFullName());
            setupLibrarianNavigation();
            loadCenterView("/gui/librarian/LibrarianDashboard.fxml", false);
        } else if (SessionManager.currentSubscriber != null) {
            subscriberName.setText(SessionManager.currentSubscriber.getFullName());
            setupMemberNavigation();
            loadCenterView("/gui/subscriber/MemberDashboard.fxml", false);
        }

        // 'X' button logout listener
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            if (stage != null) {
                LogoutUtil.addWindowCloseListener(stage);
            }
        });
    }

    public static MainLayoutController getInstance() {
        return instance;
    }

    /**
     * Core routing method handling history tracking and injection
     */
    public void loadCenterView(String fxmlPath, boolean isHistoryNavigation) {
        if (fxmlPath == null || fxmlPath.equals(currentView)) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);

            // If it's a new organic click, save current view to history and clear forward stack
            if (!isHistoryNavigation && currentView != null) {
                backHistory.push(currentView);
                forwardHistory.clear();
            }

            currentView = fxmlPath;
            updateNavigationButtons();
            updateNavStyles(fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Overloaded method for standard internal clicks from other controllers
    public void loadCenterView(String fxmlPath) {
        loadCenterView(fxmlPath, false);
    }

    // --- Dynamic Navigation Builders ---

    private void setupLibrarianNavigation() {
        navBar.getChildren().clear();
        navBar.getChildren().addAll(
            createNavButton("Dashboard", "/gui/librarian/LibrarianDashboard.fxml"),
            createNavButton("Catalog Management", "/gui/catalog/ManageCatalog.fxml") // Example extra button
        );
    }

    private void setupMemberNavigation() {
        navBar.getChildren().clear();
        navBar.getChildren().addAll(
            createNavButton("Home", "/gui/subscriber/MemberDashboard.fxml"),
            createNavButton("Browse Catalog", "/gui/catalog/SearchCatalog.fxml"),
            createNavButton("My Borrows", "/gui/subscriber/ExtendBorrow.fxml"),
            createNavButton("Order Requests", "/gui/subscriber/OrderBook.fxml")
        );
    }

    private Button createNavButton(String text, String fxmlPath) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-button");
        btn.setUserData(fxmlPath); // Store target path for active styling
        
        btn.setOnAction(e -> loadCenterView(fxmlPath));
        return btn;
    }

    private void updateNavStyles(String fxmlPath) {
        if (navBar == null) return;
        for (Node node : navBar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.getStyleClass().remove("nav-button-active");
                if (fxmlPath.equals(btn.getUserData())) {
                    btn.getStyleClass().add("nav-button-active");
                }
            }
        }
    }

    private void updateNavigationButtons() {
        if (backButton != null) backButton.setDisable(backHistory.isEmpty());
        if (forwardButton != null) forwardButton.setDisable(forwardHistory.isEmpty());
    }

    // --- History & Header Actions ---

    @FXML
    private void handleBack() {
        if (!backHistory.isEmpty()) {
            forwardHistory.push(currentView);
            String prevView = backHistory.pop();
            loadCenterView(prevView, true);
        }
    }

    @FXML
    private void handleForward() {
        if (!forwardHistory.isEmpty()) {
            backHistory.push(currentView);
            String nextView = forwardHistory.pop();
            loadCenterView(nextView, true);
        }
    }

    @FXML
    private void handleProfileClick() {
        loadCenterView("/gui/subscriber/PersonalInfo.fxml"); // Adjust for librarians if needed
    }

    @FXML
    private void handleLogout(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}