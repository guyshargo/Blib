package gui.LibrarianGUI.ReturnBookPage;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogoutUtil;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.stage.Stage;
import logic.ActivityControl.ActivityType;
import logic.BookControl.Book;
import logic.BookControl.BorrowedBook;
import logic.FreezeStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Description:
 * Controller class for the librarian to return a member's borrowed book
 */
public class ReturnBookPageController {
    // Attributes
    @FXML
    private TextField memberNameTxt;

    @FXML
    private Label extendLabel1;

    @FXML
    private Label extendLabel11;

    @FXML
    private TextField MemberNumTextField;

    @FXML
    private Button searchBtn;

    @FXML
    private Button ReturnButton;

    @FXML
    private Label librarianName;

    @FXML
    private Button LogoutBtn;

    @FXML
    private TableView<BorrowedBook> BorrowedBooksTable;

    @FXML
    private TableColumn<BorrowedBook, Integer> BookIdColumn;

    @FXML
    private TableColumn<BorrowedBook, String> BookNameColumn;

    private final ObservableList<BorrowedBook> borrowedBooks = FXCollections.observableArrayList();

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReturnBookPage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Return Borrowed Books");
        primaryStage.show();
        LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
    }

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        librarianName.setText(ChatClient.librarianLogin.getFullName());
        // Set up the columns in the table
        BookIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCopyOfBookId()).asObject());
        BookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNameOfBook()));

        // Set the table's items to the observable list
        BorrowedBooksTable.setItems(borrowedBooks);
        BorrowedBooksTable.setOnMouseClicked(event -> handleReturnConfirmation());
    }

    /**
     * Description:
     * Method for searching from the database the borrowed books of the member selected in the table
     */
    @FXML
    public void handleSearchBorrowedBooksForMember() {
        String membershipNumberStr = MemberNumTextField.getText().trim();
        // Check if the input is empty
        if (membershipNumberStr.isEmpty()) {
            // Clear the table if needed and exit
            BorrowedBooksTable.getItems().clear();
            memberNameTxt.setText("");

            return;
        }
        try {
            int membershipNumber = Integer.parseInt(membershipNumberStr);
            // Send the command to the server to check if the subscriber exists
            String checkSubscriberCommand = "ViewReaderCard:" + membershipNumber;
            ClientUI.chat.accept(checkSubscriberCommand);
            // Check the server response for the subscriber details
            if (ChatClient.sub == null) {
                memberNameTxt.setText("");
                showAlertError("Member Not Found", "The member does not exist. Please enter a valid membership number.");
                return;
            }

            // Change member name text field to member's name
            memberNameTxt.setText(ChatClient.sub.getMemberFullName());

            String command = "FetchAllBorrowedBooks:" + membershipNumber;
            ClientUI.chat.accept(command);
            if (ChatClient.borrowList != null && !ChatClient.borrowList.isEmpty()) {
                List<BorrowedBook> books = ChatClient.borrowList; // Fetch the list from the server
                ObservableList<BorrowedBook> observableBooks = FXCollections.observableArrayList(books);
                BorrowedBooksTable.setItems(observableBooks);
            } else {
                showAlertError("There is no borrowed books", "This member has no borrowed books.");
            }

        } catch (NumberFormatException e) {
            memberNameTxt.setText("");
            showAlertError("Input Error", "Membership number must be a valid integer.");
        }
    }

    /**
     * Description:
     * Method for validating a selected row in the borrow table
     */
    private void handleReturnConfirmation() {
        BorrowedBook selectedBook = BorrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlertError("No Selection", "Please select a book.");
            return;
        }
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to return this book?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.setTitle("Return Confirmation");
        confirmationAlert.setHeaderText(null);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                processReturn(selectedBook);
            }
        });
    }

    /**
     * Description:
     * Method for returning a borrowed book when clicking the 'Return' button
     *
     * @param book BorrowedBook.class
     */
    private void processReturn(BorrowedBook book) {
        LocalDate returnDate = LocalDate.now();
        LocalDate dueDate = book.getReturnDate();
        String activityDetails;

        if (returnDate.isAfter(dueDate.plusWeeks(1))) {
            String command = "SetSubscriberStatus:" + book.getMembershipNumber() + ",Frozen," + LocalDate.now();
            ClientUI.chat.accept(command);
            // Add activity to activity_db
             activityDetails = String.format("freeze_status,status changed from %s to %s ", FreezeStatus.NOT_FROZEN.getDbValue(), FreezeStatus.FROZEN.getDbValue());
            addActivity(book, activityDetails);
        }

        String deleteBorrowCommand = "DeleteBorrowedBook:" + book.getCopyOfBookId() + "," + MemberNumTextField.getText().trim();
        ClientUI.chat.accept(deleteBorrowCommand);

        if (ChatClient.isBorrowDeleted) {
            String changeArrivalCommand = "ChangeArrivalStatus:" + book.getNameOfBook();
            ClientUI.chat.accept(changeArrivalCommand);


            String findBookCommand = "FindBookById:" + book.getBookId();
            ClientUI.chat.accept(findBookCommand);

            Book returnedBook = ChatClient.foundBook; // Use the book fetched from the server
            if (returnedBook != null) {

                String decreaseCopiesCommand = "DecreaseBorrowedCopies:" + returnedBook.getBookID();
                ClientUI.chat.accept(decreaseCopiesCommand);
            }

            if(returnDate.isAfter(dueDate)) {
                activityDetails = String.format("%s,returned %s late by %s days",ActivityType.LATE_BOOK_RETURN.getDBValue(), book.getNameOfBook(), ChronoUnit.DAYS.between(dueDate, returnDate));

            }
            // Add activity to activity_db
            else
                activityDetails = String.format("returning,return %s ", book.getNameOfBook());
            addActivity(book, activityDetails);

            showAlertSuccess("Return Successful", "The book has been successfully returned.");
            BorrowedBooksTable.getItems().remove(book);
        } else {
            showAlertError("Return Error", "The book has been deleted by another librarian.");
            BorrowedBooksTable.getItems().remove(book);
        }
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
     * Method for returning to the librarian main page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void handleReturnAction(ActionEvent event) throws Exception {
        LibrarianMainPageController view = new LibrarianMainPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method to logout from librarian's account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void handleLogoutAction(ActionEvent event) throws Exception {
        // Handle logout logic
        LogoutUtil.handleLogoutButtonAction(event);

        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method for adding an activity to the activity_db (return book)
     *
     * @param activityDetails String.class
     */
    private void addActivity(BorrowedBook book, String activityDetails) {
        String command = "addActivity:" + book.getMembershipNumber() + "," + activityDetails;
        ClientUI.chat.accept(command);
    }
}