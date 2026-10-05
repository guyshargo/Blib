package gui.subscriber;

import gui.common.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Description:
 * Controller class for the member's main page
 */
public class MemberDashboardController {
    @FXML public Button extendBorrowPageButton;
    @FXML public Button searchBookButton;
    @FXML public Button viewPersonalInfoButton;
    @FXML public Button orderPageButton;

    @FXML
    private void handleSearchBookButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/catalog/SearchCatalog.fxml");
    }

    @FXML
    private void handleViewPersonalInfoButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/subscriber/PersonalInfo.fxml");
    }

    @FXML
    private void handleExtendBorrowPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/subscriber/ExtendBorrow.fxml");
    }

    @FXML
    private void handleOrderPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/subscriber/OrderBook.fxml");
    }
}