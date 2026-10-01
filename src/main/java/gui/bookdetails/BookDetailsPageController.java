package gui.bookdetails;

import client.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import gui.auth.LogInController;
import gui.auth.LogoutUtil;
import gui.search.SearchPageController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import model.Book;
import model.BorrowedBook;
import model.CopyOfBook;

/**
 * Description:
 * Controller class for showing a book's details
 */
public class BookDetailsPageController {
    @FXML private TextField bookName;
    @FXML private TextArea summaryTxt;
    @FXML private TextField BookStatus;
    @FXML private TextField ShelfLocation;
    @FXML private TextField ReturnDate;
    @FXML private Button returnbtn;
    @FXML private Button LogoutBtn;
    @FXML private Button LoginBtn;
    @FXML private Label usernameLoggedIn;


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

        if (SessionManager.currentSubscriber != null && SessionManager.currentLibrarian == null){
            usernameLoggedIn.setText(SessionManager.currentSubscriber.getFullName());
        } else if (SessionManager.currentSubscriber == null && SessionManager.currentLibrarian != null){
            usernameLoggedIn.setText(SessionManager.currentLibrarian.getFullName());
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

    public void loadBooks(Book selectedBook) {
        bookName.setText(selectedBook.getBookName());
        summaryTxt.setText(selectedBook.getBookSummary());
        
        int memberId = (SessionManager.currentSubscriber != null) ? SessionManager.currentSubscriber.getMembershipNumber() : 0;

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // 1. Fetch available copy
            HttpRequest availableReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/book/" + selectedBook.getBookID() + "/available-copy/subscriber/" + memberId))
                    .GET().build();
            
            HttpResponse<String> availableRes = client.send(availableReq, HttpResponse.BodyHandlers.ofString());
            
            if (availableRes.statusCode() == 200) {
                CopyOfBook availableCopy = mapper.readValue(availableRes.body(), CopyOfBook.class);
                bookName.setText(availableCopy.getCopyOfBookName());
                BookStatus.setText("Available");
                ShelfLocation.setText(availableCopy.getShelfLocation());
                ReturnDate.setText("-");
            } else {
                // 2. Fallback: Fetch closest return date
                HttpRequest closestReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/borrows/book/" + selectedBook.getBookID() + "/closest-return-date"))
                        .GET().build();
                        
                HttpResponse<String> closestRes = client.send(closestReq, HttpResponse.BodyHandlers.ofString());
                
                if (closestRes.statusCode() == 200) {
                    BorrowedBook closestReturnBook = mapper.readValue(closestRes.body(), BorrowedBook.class);
                    bookName.setText(closestReturnBook.getNameOfBook());
                    BookStatus.setText("Borrowed");
                    ShelfLocation.setText("-");
                    ReturnDate.setText(closestReturnBook.getReturnDate().toString());
                } else {
                    BookStatus.setText("No copies found");
                    ShelfLocation.setText("-");
                    ReturnDate.setText("-");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
