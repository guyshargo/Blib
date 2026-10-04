package gui.common;

import client.SessionManager;
import gui.auth.LogoutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class MainLayoutController {

    @FXML private BorderPane mainBorderPane;
    @FXML private Label subscriberName;
    @FXML private Button backButton;
    @FXML private Button forwardButton;

    private Stack<String> backHistory = new Stack<>();
    private Stack<String> forwardHistory = new Stack<>();
    private String currentView = null;

    @FXML
    public void initialize() {
        // Set profile name
        if (SessionManager.currentSubscriber != null) {
            subscriberName.setText(SessionManager.currentSubscriber.getFullName());
        }

        // 'X' button logout listener
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            if (stage != null) {
                LogoutUtil.addWindowCloseListener(stage);
            }
        });

        loadCenterView("/gui/subscriber/MemberDashboard.fxml", false);
        updateNavigationButtons();
    }

    /**
     * Core routing method handling history tracking
     */
    private void loadCenterView(String fxmlPath, boolean isHistoryNavigation) {
        if (fxmlPath == null || fxmlPath.equals(currentView)) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);

            // If it's a new organic click, save the current view to history and clear forward stack
            if (!isHistoryNavigation && currentView != null) {
                backHistory.push(currentView);
                forwardHistory.clear();
            }

            currentView = fxmlPath;
            updateNavigationButtons();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Overloaded method for standard navbar clicks
    private void loadCenterView(String fxmlPath) {
        loadCenterView(fxmlPath, false);
    }

    private void updateNavigationButtons() {
        if (backButton != null) backButton.setDisable(backHistory.isEmpty());
        if (forwardButton != null) forwardButton.setDisable(forwardHistory.isEmpty());
    }

    // --- History Navigation ---

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

    // --- Navigation Routing ---

    @FXML
    private void loadHome() {
        loadCenterView("/gui/subscriber/MemberDashboard.fxml");
    }

    @FXML
    private void handleProfileClick() {
        loadCenterView("/gui/subscriber/PersonalInfo.fxml");
    }

    @FXML
    private void loadSearch() {
        loadCenterView("/gui/catalog/SearchCatalog.fxml");
    }

    @FXML
    private void loadBorrows() {
        loadCenterView("/gui/subscriber/ExtendBorrow.fxml");
    }

    @FXML
    private void loadOrders() {
        loadCenterView("/gui/subscriber/OrderBook.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
    }
}