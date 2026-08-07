package gui.MemberGUI.OrderPage;

import client.ChatClient;
import client.ClientUI;
import gui.LogInGUI.LogoutUtil;
import gui.MemberGUI.MemberMainPageController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.BookControl.Book;
import logic.BookControl.OrderedBook;
import logic.Subscriber;

import java.util.List;

/**
 * Description:
 * Controller class for the member to order books
 */
public class OrderPageController {
    // Changeable list to change the table of ordered books
    private final ObservableList<OrderedBook> orderedBooks = FXCollections.observableArrayList();

    // Found book after searching it
    private Book foundBook = null;

    // Found book after searching it
    private OrderedBook cancelOrder = null;

    private Book cancelBook = null;

    // Subscriber details based on login
    private Subscriber sub = null;

    // Search book by ID for ordering it
    @FXML
    private TextField bookIDTxt;

    // Search book by name for ordering it
    @FXML
    private TextField bookNameTxt;

    // Table of ordered books of subscriber
    @FXML
    private TableView<OrderedBook> orderTable;

    // Copy of ordered book ID colum in the table (int)
    @FXML
    private TableColumn<OrderedBook, Integer> bookIDColumn;

    // Copy of ordered book name colum in the table (String)
    @FXML
    private TableColumn<OrderedBook, String> bookNameColumn;

    // Copy of ordered book arrival status colum in the table (ArivalStatus)
    @FXML
    private TableColumn<OrderedBook, String> arrivedStatusColumn;

    @FXML
    private Button returnBtn;

    @FXML
    private Button orderBtn;

    // Label to indicate if found the book to order
    @FXML
    private Label foundLabel;

    @FXML
    private Label foundLabel2;

    @FXML
    private Label subscriberName;

    @FXML
    private Button logoutButton;

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        this.sub = ChatClient.subscriberLogin;
        if (sub != null) {
            subscriberName.setText(sub.getMemberFullName());
        } else {
            subscriberName.setText("Guest");
        }

        // Colum of copy of book ID -> initialized based on copyOfBookIDColumn
        bookIDColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getBookID()).asObject());

        // Colum of book names -> initialized based on bookNameColumn
        bookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookName()));

        // Colum of has Arrived status -> initialized based on returnDateColumn
        arrivedStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getArrivalStatus().toString()));

        // Table of all ordered books
        orderTable.setItems(orderedBooks);

        // Load the user ordered books
        loadOrderedBooks();

        // Null the found label to later use
        foundLabel.setText("");
        foundLabel2.setText("");
        orderBtn.setDisable(true);

        // When clicking on the table, make the copy of book ID be seen to extend
        orderTable.setOnMouseClicked(event -> handleOrderedBookClick());

    }

    /**
     * Description:
     * Method when clicking the table of ordered books -> save ordered book to cancel order
     */
    void handleOrderedBookClick() {
        // Selected row in the table
        OrderedBook selectedOrderedBook = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrderedBook == null)
            showAlertError("No Selection", "Please select a book to extend.");
        else {
            this.cancelOrder = selectedOrderedBook;
            String command = "SearchBookToCancel:" + cancelOrder.getBookID();
            ClientUI.chat.accept(command);
            this.cancelBook = ChatClient.bookList.get(0);
        }
    }

    /**
     * Description:
     * Method to get all ordered books of the subscriber
     */
    private void loadOrderedBooks() {
        int membershipNumber = this.sub.getMembershipNumber();
        String command = "FetchAllOrderedBooks:" + membershipNumber;
        ClientUI.chat.accept(command);
        if (ChatClient.orderList != null)
            updateOrderedBookList(ChatClient.orderList);
    }

    /**
     * Description:
     * Method for changing the orderedTable based on subscribers fetched ordered books
     *
     * @param subOrderedBooks List<OrderedBook>.class
     */
    public void updateOrderedBookList(List<OrderedBook> subOrderedBooks) {
        orderedBooks.clear();
        // Case the subscriber has ordered books
        if (subOrderedBooks != null)
            orderedBooks.addAll(subOrderedBooks);
        // Add the borrowed books to the table
        orderTable.setItems(orderedBooks);
    }

    /**
     * Description:
     * Method for re-ordering a selected book
     *
     * @param event ActionEvent.class
     * @throws Exception (when ordering a book)
     */
    @FXML
    void handleReorderClick(ActionEvent event) throws Exception {
        if (cancelOrder == null)
            showAlertError("Cancel order request", "Error: must select an order to cancel");
        else {
            OrderedBook selectedOrderedBook = orderTable.getSelectionModel().getSelectedItem();
            String command = "SearchBookToOrder:" + selectedOrderedBook.getBookName() + "," + selectedOrderedBook.getBookID();
            ClientUI.chat.accept(command);
            if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
                this.foundBook = this.cancelBook;
                orderBook(event);
                this.cancelBook = null;
            } else {
                showAlertError("Search Request", "No books found for the given search criteria");
            }
        }
    }

    /**
     * Description:
     * Method for ordering the book based on the found book
     *
     * @param event ActionEvent.class
     */
    public void orderBook(ActionEvent event) {
        // Check if subscriber searched a book to order
        if (foundBook == null)
            showAlertError("Order request", "Error: must search a book to order");
        else
            // Check if member is frozen
            if (sub.getMemberFreezeStatus().getDbValue().equals("Frozen")) {
                showAlertError("Order request", "Member is Frozen");
            } else {
                // Check if there are copies of the book that are not borrowed
                if (foundBook.getNumberOfCopies() == foundBook.getNumberOfBorrowedCopies()) {
                    showAlertError("Order request", foundBook.getBookName() + " reached maximum ordering to the book");
                    return;
                }
                // Send to Server -> Order Request
                String command = "OrderBook:" + foundBook.getBookName() + "," + foundBook.getBookID() + "," +
                        sub.getMembershipNumber() + "," + sub.getMemberFullName() + "," + sub.getMemberPhoneNumber() + "," +
                        sub.getEmailAddress();
                ClientUI.chat.accept(command);

                // Check if the request has been approved
                if (ChatClient.isOrdered.equals("approve")) {
                    // Add activity to activity_db
                    addActivity(foundBook, true);

                    command = "ChangeOrderStatus:" + foundBook.getBookID() + "," + foundBook.getNumberOforders() + "," + true;
                    ClientUI.chat.accept(command);

                    showAlertSuccess("Order request", foundBook.getBookName() + " has been ordered");
                    foundBook = null;
                    foundLabel.setText("");
                    foundLabel2.setText("");
                    bookIDTxt.setText("");
                    bookNameTxt.setText("");
                    orderBtn.setDisable(true);
                    loadOrderedBooks();
                } else
                    showAlertError("Order request", "Error in ordering '" + foundBook.getBookName() + "because of "
                            + ChatClient.isOrdered);
            }
    }

    /**
     * Description:
     * Method for canceling an ordered book
     *
     * @param event ActionEvent.class
     */
    public void cancelOrderedBook(ActionEvent event) {
        if (cancelOrder == null)
            showAlertError("Cancel order request", "Error: must select an order to cancel");
        else {
            String command = "CancelOrderedBook:" + cancelOrder.getOrderID() + "," + cancelOrder.getBookName() + "," +
                    cancelOrder.getArrivalStatus();
            ClientUI.chat.accept(command);

            // Check if the extension happened
            if (ChatClient.isCanceled) {
                // Add activity to activity_db
                addActivity(cancelBook, false);
                command = "ChangeOrderStatus:" + cancelBook.getBookID() + "," + cancelBook.getNumberOforders() + "," + false;
                ClientUI.chat.accept(command);
                showAlertSuccess("Cancel order request", cancelOrder.getBookName() + " has been canceled from list");
                cancelOrder = null;
                cancelBook = null;
                loadOrderedBooks();
            } else
                showAlertSuccess("Cancel order request", cancelOrder.getBookName() + " has been canceled from list");
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

    /**
     * Description:
     * Method for showing an alert window based on the title and message given
     *
     * @param title   String.class
     * @param message String.class
     */
    private void showAlertSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(message);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/success.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }

    /**
     * Description:
     * Method for returning to the member main page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void getReturnBtn(ActionEvent event) throws Exception {
        MemberMainPageController view = new MemberMainPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method for searching a book to order
     *
     * @param event ActionEvent.class
     */
    @FXML
    public void searchOrderBook(ActionEvent event) {
        String bookName = bookNameTxt.getText().trim();
        String bookID = bookIDTxt.getText().trim();
        if (bookName.isEmpty() || bookName == null) {
            bookName = "null";
        }
        if (bookID.isEmpty() || bookID == null) {
            bookID = "null";
        }
        if ((bookName.isEmpty() || bookName == null) && (bookID.isEmpty() || bookID == null)) {
            showAlertError("Search Request", "Must enter a criteria to search");
            foundLabel.setText("");
            foundLabel2.setText("");
            foundBook = null;
        } else
            initiateBookSearch(bookName, bookID);
    }

    /**
     * Description:
     * Method for searching a book to order
     *
     * @param bookName String.class
     * @param bookID   String.class
     */
    private void initiateBookSearch(String bookName, String bookID) {
        // Send to Server
        String command = "SearchBookToOrder:" + bookName + "," + bookID;
        ClientUI.chat.accept(command);

        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
            displaySearchResults(ChatClient.bookList);
        } else {
            foundBook = null;
            foundLabel.setText("");
            foundLabel2.setText("");
            showAlertError("Search Request", "No books found for the given search criteria");
        }
    }

    /**
     * Description:
     * Method for showing the search results into the order table
     *
     * @param books List<Book>.class
     */
    private void displaySearchResults(List<Book> books) {
        orderBtn.setDisable(false);
        if (books == null || books.isEmpty()) {
            foundLabel2.setText("No books found");
            showAlertError("Search Request", "No books found for the given search criteria");
            foundBook = null;
            return;
        }
        foundBook = books.get(0);
        foundLabel2.setText("Found book:");
        foundLabel.setText(foundBook.getBookName());
    }

    /**
     * Description:
     * Method for adding an activity to the activity_db (order or cancel order)
     *
     * @param book  Book.class
     * @param order boolean
     */
    private void addActivity(Book book, boolean order) {
        if (order) {
            String activityDetails = String.format("order %s ", book.getBookName());
            String command = "addActivity:" + sub.getMembershipNumber() + "," + "order" + "," + activityDetails;
            ClientUI.chat.accept(command);
        } else {
            String activityDetails = String.format("cancel order %s ", book.getBookName());
            String command = "addActivity:" + sub.getMembershipNumber() + "," + "cancelOrder" + "," + activityDetails;
            ClientUI.chat.accept(command);
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
    private void handleLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}