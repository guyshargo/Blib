package gui.SearchBookPage;

import client.ChatClient;
import client.ClientUI;
import gui.Book_Details.BookDetailsPageController;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogInController;
import gui.LogInGUI.LogoutUtil;
import gui.MemberGUI.MemberMainPageController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.BookControl.Book;

import java.io.IOException;
import java.util.List;

/**
 * Description:
 * Controller class to search books in the library for all users (also non-members)
 */
public class SearchPageController {

    @FXML
    private TextField SearchBookByName;

    @FXML
    private TextField SearchBookBySub;

    @FXML
    private TextField SearchBookByText;

    @FXML
    private Button searchButton;

    @FXML
    private Button returnButton;

    @FXML
    private Button LogoutButton;

    @FXML
    private Button LoginButton;

    @FXML
    private Text resultText;

    @FXML
    private TableView<Book> searchBookTable;

    @FXML
    private TableColumn<Book, String> bookName;

    @FXML
    private TableColumn<Book, String> bookSub;

    @FXML
    private TableColumn<Book, String> keywords;

    @FXML
    private TableColumn<Book, String> BookSummary;
    @FXML
    private Label usernameLoggedIn;

    private final ObservableList<Book> bookData = FXCollections.observableArrayList();

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        // Check the login state
        LogoutButton.setVisible(true);
        LoginButton.setVisible(false);
        returnButton.setVisible(true);
        usernameLoggedIn.setText("Guest");
        if (ChatClient.subscriberLogin != null && ChatClient.librarianLogin == null){
            usernameLoggedIn.setText(ChatClient.subscriberLogin.getMemberFullName());
        } else if (ChatClient.subscriberLogin == null && ChatClient.librarianLogin != null){
            usernameLoggedIn.setText(ChatClient.librarianLogin.getFullName());
        } else {
            // Show the login button and hide the logout button
            LogoutButton.setVisible(false);
            LoginButton.setVisible(true);
            returnButton.setVisible(false);
        }
        // Initialize table columns
        bookName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookName()));

        bookSub.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookSubject()));

        keywords.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKeywords()));

        BookSummary.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookSummary()));

        // Set the table's items to the observable list
        searchBookTable.setItems(bookData);
        searchBookTable.setOnMouseClicked(event -> handleBookClick());
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param window Stage.class
     */
    public void start(Stage window) {
        try {
            // Load the FXML file for the search page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchBookPage/SearchPage.fxml")); // Update the path to match your project structure
            Parent root = loader.load();
            // Set the scene for the stage
            Scene scene = new Scene(root);
            // Configure the stage
            window.setTitle("Search Book Page");
            window.setScene(scene);
            window.show(); // Display the stage
            LogoutUtil.addWindowCloseListener(window); // Register window close listener for logout
        } catch (IOException e) {
            e.printStackTrace();
            showAlertError("Loading Error", "Failed to load the search page.");
        }
    }

    /**
     * Description:
     * Method for opening a new window that shows all book's details that the user selects
     */
    private void handleBookClick() {
        Book selectedBook = searchBookTable.getSelectionModel().getSelectedItem();
        if(selectedBook == null) {
            showAlertError("No Selection", "Please select a book.");
            return;
        }
        try {
            // Hide the current window
            Stage currentStage = (Stage) searchBookTable.getScene().getWindow();
            currentStage.hide();

            // Load the SubscriberUpdateFrame
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Book_Details/BookDetailsPage.fxml"));
            Pane root = loader.load();

            // Pass the selected subscriber to the update frame controller
            BookDetailsPageController controller = loader.getController();
            controller.loadBooks(selectedBook);

            // Set up the new stage
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/Book_Details/BookDetailsPage.css").toExternalForm());

            primaryStage.setTitle("Book Details Page");
            primaryStage.setScene(scene);
            primaryStage.show();
            LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method for generating strings representing the user's choice to search a book,
     * and then initiating search on them
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void handleSearchAction(ActionEvent event) throws Exception {
        String bookName = SearchBookByName.getText().trim();
        String bookSubject = SearchBookBySub.getText().trim();
        String freeText = SearchBookByText.getText().trim();
        if(bookName.isEmpty() || bookName==null) {
            bookName = "is empty";
        }
        if(bookSubject.isEmpty() || bookSubject==null) {
            bookSubject = "is empty";
        }
        if(freeText.isEmpty() || freeText==null) {
            freeText = "is empty";
        }
        initiateBookSearch(bookName, bookSubject, freeText);
    }

    /**
     * Description:
     * Method for searching a book based on the given book's criteria
     *
     * @param bookName    String.class
     * @param bookSubject String.class
     * @param freeText    String.class
     */
    private void initiateBookSearch(String bookName, String bookSubject, String freeText) {
        // Prepare the search criteria (this could be null if the user leaves some fields empty)
        String command = "SearchBooks:" + bookName + "," + bookSubject + "," + freeText;
        // Send the command to the server for searching books
        ClientUI.chat.accept(command);
        // Now handle the server response, after awaitResponse is set to false
        displaySearchResults(ChatClient.bookList);
    }

    /**
     * Description:
     * Method for showing the search book result into the table
     *
     * @param books List<Book>.class
     */
    private void displaySearchResults(List<Book> books) {
        bookData.clear();  // Clear previous results
        if (books != null && !books.isEmpty()) {
            bookData.addAll(books);
        } else {
            showAlertError("No Results", "No books match the search criteria.");
        }
    }

    /**
     * Description:
     * Method for showing an alert window
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

    /**
     * Description:
     * Method for returning to the librarian main page or the member main page, based on which user logged in
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    private void handleReturnAction(ActionEvent event) throws Exception {
        if(ChatClient.librarianLogin!=null){
            LibrarianMainPageController view = new LibrarianMainPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        }
        else if(ChatClient.subscriberLogin!=null){
            MemberMainPageController view = new MemberMainPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        }
    }

    /**
     * Description:
     * Method for logging out from the library (for logged-in users only)
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    void getLogoutButton(ActionEvent event)throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method for logging into the library
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