package gui.librarian;

import gui.common.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller class for the librarian main management page
 */
public class LibrarianDashboardController {

    @FXML private Button searchBookButton;
    @FXML private Button registerMemberButton;
    @FXML private Button manageMembersButton;
    @FXML private Button borrowPageButton;
    @FXML private Button returnPageButton;
    @FXML private Button reportPageButton;
    @FXML private Button invoiceButton;

    @FXML
    private void handleSearchBookButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/catalog/SearchCatalog.fxml");
    }

    @FXML
    private void RegisterMemberButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/RegisterMember.fxml");
    }

    @FXML
    private void handleManageMembersButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/ManageMember.fxml");
    }

    @FXML
    private void handleBorrowPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/BorrowBook.fxml");
    }

    @FXML
    private void handleReturnPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/ReturnBook.fxml");
    }

    @FXML
    private void handleReportPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/Reports.fxml");
    }

    @FXML
    private void handleInvoiceBtn(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/Invoice.fxml");
    }
}