package gui.LibrarianGUI;

import client.ChatClient;
import gui.LibrarianGUI.BorrowBook.BorrowBookPageController;
import gui.LibrarianGUI.InvoicePage.InvoicePageController;
import gui.LibrarianGUI.ManageMember.ManageMemberController;
import gui.LibrarianGUI.MemberRegistration.MemberRegistrationPageController;
import gui.LibrarianGUI.ReturnBookPage.ReturnBookPageController;
import gui.LibrarianGUI.ReportsPage.ReportsPageController;
import gui.LogInGUI.LogoutUtil;
import gui.SearchBookPage.SearchPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * Description:
 * Controller class for the librarian main page
 */
public class LibrarianMainPageController {

    @FXML
    private Button searchBookButton;

    @FXML
    private Button registerMemberButton;

    @FXML
    private Button manageMembersButton;

    @FXML
    private Button borrowPageButton;

    @FXML
    private Button returnPageButton;

    @FXML
    private Button reportPageButton;

    @FXML
    private Button invoiceButton;

    @FXML
    private Label librarianName;

    /**
     * Description:
     * Method to show greetings text with librarian's full name
     */
    private void librarianShowInfo() {
        String librarian_name;
        if (ChatClient.librarianLogin == null) {
            librarian_name = "librarian";
        } else {
            librarian_name = ChatClient.librarianLogin.getFullName();
        }
        librarianName.setText(librarian_name);
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianGUI/LibrarianMainPage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/LibrarianMainPage.css").toExternalForm());
        primaryStage.setTitle("Librarian Dashboard");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/HeaderImage/book_logo.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
        // Register window close listener for logout
        LogoutUtil.addWindowCloseListener(primaryStage);
    }

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    private void initialize() {
        librarianShowInfo(); // Safe to call here, after FXML injection
    }

    /**
     * Description:
     * Method to goto Search Book Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleSearchBookButton(ActionEvent event) {
        SearchPageController view = new SearchPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method to goto Register Member Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void RegisterMemberButton(ActionEvent event) {
        try {
            MemberRegistrationPageController view = new MemberRegistrationPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to goto Manage Member Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleManageMembersButton(ActionEvent event) {
        try {
            ManageMemberController view = new ManageMemberController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to goto Book Borrow Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleBorrowPageButton(ActionEvent event) {

        try {
            BorrowBookPageController view = new BorrowBookPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to logout from subscribers account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    private void handleLogOutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method to goto Return Book Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleReturnPageButton(ActionEvent event) {
        try {
            ReturnBookPageController view = new ReturnBookPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to goto Report Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleReportPageButton(ActionEvent event) {
        try {
            ReportsPageController view = new ReportsPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to goto Invoice Page
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleInvoiceBtn(ActionEvent event) {
        try {
            InvoicePageController view = new InvoicePageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}