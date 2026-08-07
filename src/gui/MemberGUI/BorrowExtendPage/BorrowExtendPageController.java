package gui.MemberGUI.BorrowExtendPage;

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
import logic.BookControl.BorrowedBook;
import logic.FreezeStatus;
import logic.Subscriber;

import java.time.LocalDate;
import java.util.List;

/**
 * Description:
 * Controller class for extending a borrowed book
 */
public class BorrowExtendPageController {
    // Changeable list to change the table of borrowed books when entering the name or id
    private final ObservableList<BorrowedBook> borrowedBooks = FXCollections.observableArrayList();

    // Table of borrowed books of subscriber
    @FXML
    private TableView<BorrowedBook> borrowedTable;

    // Copy of borrowed book ID colum in the table (int)
    @FXML
    private TableColumn<BorrowedBook, Integer> borrowedIDColumn;

    // Copy of borrowed book name colum in the table (String)
    @FXML
    private TableColumn<BorrowedBook, String> bookNameColumn;

    // Copy of borrowed book return date colum in the table (Date)
    @FXML
    private TableColumn<BorrowedBook, String> returnDateColumn;

    // Borrowed book's name for searching
    @FXML
    private TextField bookNameTxt;

    // Borrowed book's ID for extending
    @FXML
    private TextField copyOfBookIDTxt;

    // Overdue filter button
    @FXML
    private RadioButton overdueRadioBtn;

    @FXML
    private Label subscriberName;

    @FXML
    private Button logoutButton;

    // Subscriber details based on login
    private Subscriber sub = null;

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        // Fetch Subscriber's borrowed books from borrowed_book database
        this.sub = ChatClient.subscriberLogin;
        subscriberName.setText(sub.getMemberFullName());

        // Colum of copy of book ID -> initialized based on copyOfBookIDColumn
        borrowedIDColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCopyOfBookId()).asObject());

        // Colum of book names -> initialized based on bookNameColumn
        bookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNameOfBook()));

        // Colum of return dates -> initialized based on returnDateColumn
        returnDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReturnDate().toString()));

        // Table of all borrowed books -> initialized based on the listener list borrowedBooks
        borrowedTable.setItems(borrowedBooks);

        // Overdue borrowed books marked red
        borrowedTable.setRowFactory(tv -> new TableRow<BorrowedBook>() {
            @Override
            protected void updateItem(BorrowedBook item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || item.getReturnDate() == null) {
                    setStyle("");
                } else if (item.getReturnDate().isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #FF0000;");
                } else {
                    setStyle("");
                }
            }
        });

        // Listener for the overdue filter
        overdueRadioBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                filterOverdueBooks();
            } else {
                borrowedTable.setItems(borrowedBooks); // Reset table to show all books
            }
            copyOfBookIDTxt.setText("");
        });

        // Make a listener to bookNameTxt to change the table of copies
        bookNameTxt.textProperty().addListener((observable, oldValue, newValue)
                -> filterBorrowedBooks(newValue));

        // load the user borrowed books
        loadBorrowedBooks();

        // When clicking on the table, make the copy of book ID be seen to extend
        borrowedTable.setOnMouseClicked(event -> handleBorrowedBookClick());
    }

    /**
     * Description:
     * Method when clicking the table of borrowed books -> change the copyOfBookIDTxt
     */
    void handleBorrowedBookClick() {
        // Selected row in the table
        BorrowedBook selectedBorrowedBook = borrowedTable.getSelectionModel().getSelectedItem();
        if (selectedBorrowedBook == null)
            showAlertError("No Selection", "Please select a book to extend.");
        else
            // Changing copyOfBookIDTxt to the selected copy ID
            copyOfBookIDTxt.setText("" + selectedBorrowedBook.getCopyOfBookId());
    }

    /**
     * Description:
     * Method for filtering overdue borrowed books when clicking overdueRadioBtn
     */
    private void filterOverdueBooks() {
        ObservableList<BorrowedBook> overdueBooks = FXCollections.observableArrayList();
        for (BorrowedBook book : borrowedBooks)
            // filter books by overdue date
            if (book.getReturnDate().isBefore(LocalDate.now()))
                overdueBooks.add(book);
        borrowedTable.setItems(overdueBooks);
    }

    /**
     * Description:
     * Method for filtering the borrowed books in the table based on bookNameTxt
     *
     * @param query String.class
     */
    private void filterBorrowedBooks(String query) {
        // Case when there is no input in text field
        if (query == null || query.isEmpty())
            borrowedTable.setItems(borrowedBooks);
        else {
            // Filter books based on the name of the inputed book
            ObservableList<BorrowedBook> filteredList = FXCollections.observableArrayList();
            for (BorrowedBook borrow : borrowedBooks) {
                if (borrow.getNameOfBook().toLowerCase().contains(query.toLowerCase()))
                    filteredList.add(borrow);
            }
            // Make the table of borrowed books be filled with the filtered borrowed books
            borrowedTable.setItems(filteredList);
        }
    }

    /**
     * Description:
     * Method to get all borrowed books of the subscriber
     */
    private void loadBorrowedBooks() {
        int membershipNumber = this.sub.getMembershipNumber();
        String command = "FetchAllBorrowedBooks:" + membershipNumber;
        ClientUI.chat.accept(command);

        // Check if the borrowList in the ChatClient has books
        if (ChatClient.borrowList != null)
            updateBorrowedBookList(ChatClient.borrowList);
    }

    /**
     * Description:
     * Method for changing the borrowedTable based on subscribers fetched borrowed books
     *
     * @param subBorrowedBooks List<BorrowedBook>.class
     */
    public void updateBorrowedBookList(List<BorrowedBook> subBorrowedBooks) {
        borrowedBooks.clear();
        // Case the subscriber has borrowed books
        if (subBorrowedBooks != null)
            borrowedBooks.addAll(subBorrowedBooks);
        // Add the borrowed books to the table
        borrowedTable.setItems(borrowedBooks);
    }

    /**
     * Description:
     * Method for extending the return date of the book that was chosen
     *
     * @param event ActionEvent.class
     */
    public void extendBorrow(ActionEvent event) {
        if (copyOfBookIDTxt.getText().equals(""))
            showAlertError("Extenstion Request", "Must insert the ID of the book you want to extend the return date");
        else {
            // Extend Borrowed book of user
            BorrowedBook selectedBorrowedBook = borrowedTable.getSelectionModel().getSelectedItem();

            // Member entered a number and not selected the borrowed book
            if (selectedBorrowedBook == null) {
                for (BorrowedBook borrow : borrowedBooks)
                    if (String.valueOf(borrow.getCopyOfBookId()).equals(copyOfBookIDTxt.getText())) {
                        selectedBorrowedBook = borrow;
                        break;
                    }
            }
            if (selectedBorrowedBook == null)
                showAlertError("Extenstion Request", "There is no book ID in the with the same inserted ID");
            else {
                // Check if returnDate at least 1 week from return date
                LocalDate returnDate = selectedBorrowedBook.getReturnDate();
                if (LocalDate.now().isAfter(returnDate))
                    showAlertError("Extenstion Request", "Must return book, please contact a librarian from the library");
                else if (!checkWeekTillReturnDate(returnDate))
                    showAlertError("Extenstion Request", "Borrowed book must be at least one week till return date");
                else if (this.sub.getMemberFreezeStatus() == FreezeStatus.FROZEN)
                    showAlertError("Extenstion Request", "Member must not be in frozen status. Please contact the library");
                else {
                    // Send to extension request
                    String data = selectedBorrowedBook.getMembershipNumber() + "," + selectedBorrowedBook.getCopyOfBookId();
                    // Send from server
                    String command = "ExtendBorrow:" + data;
                    ClientUI.chat.accept(command);

                    // Check if the extension happened
                    if (ChatClient.isExtend) {
                        showAlertSuccess("Extenstion Request", "Approved");
                        // Add activity to activity_db
                        addActivity(selectedBorrowedBook);
                        loadBorrowedBooks();
                    } else
                        showAlertError("Extenstion Request", "Not Approved there are orders for the book");
                }
            }
        }
    }

    /**
     * Description:
     * Method for checking if a return date of a borrowed book is within 1 week from current time
     *
     * @param returnDate LocalDate.class
     * @return isWeekToReturn boolean
     */
    private boolean checkWeekTillReturnDate(LocalDate returnDate) {
        boolean isWeekToReturn;
        // Current date
        LocalDate currentDate = LocalDate.now();
        // Calculate a week before the return date
        LocalDate oneWeekBefore = returnDate.minusDays(7);
        // Check if the current date is on or after one week before the return date
        isWeekToReturn = !currentDate.isBefore(oneWeekBefore) && currentDate.minusDays(1).isBefore(returnDate);
        return isWeekToReturn;
    }

    /**
     * Description:
     * Method for showing an alert window based on the title and message given
     *
     * @param title   String.class
     * @param message String.class
     */
    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(message);

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
     * Method for adding an activity to the activity_db (extend order)
     *
     * @param selectedBorrowedBook BorrowedBook.class
     */
    private void addActivity(BorrowedBook selectedBorrowedBook) {
        String activityDetails = String.format("Extending borrowed book %s ", selectedBorrowedBook.getNameOfBook());
        String command = "addActivity:" + sub.getMembershipNumber() + "," + "extendBorrow" + "," + activityDetails;
        ClientUI.chat.accept(command);
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