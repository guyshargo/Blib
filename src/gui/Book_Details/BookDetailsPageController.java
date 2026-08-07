package gui.Book_Details;

import client.ChatClient;
import client.ClientUI;
import gui.LogInGUI.LogoutUtil;
import gui.LogInGUI.LogInController;
import gui.SearchBookPage.SearchPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.BookControl.Book;
import logic.BookControl.BorrowedBook;
import logic.BookControl.CopyOfBook;

/**
 * Description:
 * Controller class for showing a book's details
 */
public class BookDetailsPageController {
    @FXML
    private TextField bookName;

    @FXML
    private TextArea summaryTxt;

    @FXML
    private TextField BookStatus;

    @FXML
    private TextField ShelfLocation;

    @FXML
    private TextField ReturnDate;

    @FXML
    private Button returnbtn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private Button LoginBtn;

    @FXML
    private Label usernameLoggedIn;


    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        // Check the login state
        LogoutBtn.setVisible(true);
        LoginBtn.setVisible(false);
        usernameLoggedIn.setText("Guest");

        if (ChatClient.subscriberLogin != null && ChatClient.librarianLogin == null){
            usernameLoggedIn.setText(ChatClient.subscriberLogin.getMemberFullName());
        } else if (ChatClient.subscriberLogin == null && ChatClient.librarianLogin != null){
            usernameLoggedIn.setText(ChatClient.librarianLogin.getFullName());
        } else {
            LogoutBtn.setVisible(false);
            LoginBtn.setVisible(true);
        }
    }

    /**
     * Description:
     * Method for returning to the librarian main page
     *
     * @param event ActionEvent.class
     */
    @FXML
    void getReturnButton(ActionEvent event) {
        SearchPageController view = new SearchPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method for showing a book's soonest available copy based on the book given
     *
     * @param selectedBook Book.class
     */
    public void loadBooks(Book selectedBook) {
        // Display the book name from the selectedBook object
        bookName.setText(selectedBook.getBookName());
        String command = "findAvailableCopyOfBook:" + selectedBook.getBookID() + ',' + 0;
        ClientUI.chat.accept(command);
        CopyOfBook availableCopy = ChatClient.availableCopy;
        summaryTxt.setText(selectedBook.getBookSummary());

        // Check if any copy of the book is available
        if (availableCopy != null) {
            // Display details of the available copy
            bookName.setText(availableCopy.getCopyOfBookName());
            BookStatus.setText("Available");
            ShelfLocation.setText(availableCopy.getShelfLocation());
            ReturnDate.setText("-"); // No return date since it's available

        } else {
            // Fetch the closest return date if no copies are available
            command = "ClosestReturnDateBook:" + selectedBook.getBookID();
            ClientUI.chat.accept(command);
            BorrowedBook closestReturnBook = ChatClient.closetReturnBook;
            if (closestReturnBook != null) {
                bookName.setText(closestReturnBook.getNameOfBook());  // Get the book name from BorrowedBook
                BookStatus.setText("Borrowed");
                ShelfLocation.setText("-"); // No shelf location since it's borrowed
                ReturnDate.setText(closestReturnBook.getReturnDate().toString());
            } else {
                // If no borrowed books found, display default message
                BookStatus.setText("No copies found");
                ShelfLocation.setText("-");
                ReturnDate.setText("-");
                summaryTxt.setText("-");
            }
        }
    }

    /**
     * Description:
     * Method to logout from librarian's or member's account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method to goto login page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    void getLoginButton(ActionEvent event) throws Exception {
        LogInController view = new LogInController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }
}
