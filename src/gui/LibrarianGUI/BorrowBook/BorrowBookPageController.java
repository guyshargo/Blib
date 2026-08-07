package gui.LibrarianGUI.BorrowBook;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogoutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.BookControl.BorrowedBook;
import logic.BookControl.CopyOfBook;
import logic.FreezeStatus;
import logic.Subscriber;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Description:
 * Controller class for borrowing a book from the library
 */
public class BorrowBookPageController {
    // Field for entering book ID
    @FXML
    private TextField bookIdField;

    // Field for displaying member's full name
    @FXML
    private TextField memberFullNameField;

    // Field for entering membership number
    @FXML
    private TextField membershipNumberField;

    // Field for displaying book name
    @FXML
    private TextField bookNameField;

    @FXML
    private TextField copyTxt;

    // Button to scan a book's barcode
    @FXML
    private Button scanBookButton;

    // Button to find a book by its ID
    @FXML
    private Button findBookButton;

    // Button to commit a borrow action
    @FXML
    private Button commitBorrowButton;

    @FXML
    private Label librarianName;

    // State tracking variables
    private boolean isBookFound = false;  // Tracks whether the book is found
    private boolean isMemberFound = false;  // Tracks whether the member is found
    private Subscriber subscriber;  // Represents the member
    private CopyOfBook copyOfBook;  // Represents the book copy being borrowed

    /**
     * Description:
     * Method for initializing the scene
     */
    @FXML
    public void initialize() {
        librarianName.setText(ChatClient.librarianLogin.getFullName());
    }

    /**
     * Description:
     * Method for finding a book in the database
     */
    @FXML
    private void handleFindBook() {
        // Retrieve the book ID from the field
        String bookID = bookIdField.getText();

        // Validate if the book ID is empty
        if (bookID == null || bookID.trim().isEmpty()) {
            showAlertError("Error", "Book Name cannot be empty.");  // Show error if empty
            return;
        }

        // Send command to find the available copy of the book
        String command = "findAvailableCopyOfBook:" + bookID + ',' + subscriber.getMembershipNumber();
        ClientUI.chat.accept(command);

        // Check if a copy of the book is available
        if (ChatClient.availableCopy == null) {
            showAlertError("Error", "No available copy of the book found.");
            return;
        } else {
            copyOfBook = ChatClient.availableCopy;
            bookNameField.setText(copyOfBook.getCopyOfBookName());  // Display the book's name
            isBookFound = true;

            // Find copy of books
            command = "FindBookById:" + copyOfBook.getBookId();
            ClientUI.chat.accept(command);
            int availableCopies = ChatClient.foundBook.getNumberOfCopies() - ChatClient.foundBook.getNumberOfBorrowedCopies();
            copyTxt.setText("" + availableCopies);
        }
        updateCommitBorrowButtonState();  // Update the borrow button state
    }

    /**
     * Description:
     * Method for checking entered barcode
     */
    @FXML
    private void handleScanBarcode() {
        TextInputDialog barcodeDialog = new TextInputDialog();  // Create barcode input dialog
        barcodeDialog.setTitle("Scan Barcode");
        barcodeDialog.setHeaderText("Please enter the barcode to scan:");
        barcodeDialog.setContentText("Barcode:");
        barcodeDialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/dialog.css").toExternalForm());
        barcodeDialog.getDialogPane().getStylesheets().add("custom-alert");

        // Get the user input for the barcode
        Optional<String> result = barcodeDialog.showAndWait();
        if (!result.isPresent()) {
            return;  // Exit if the user cancels
        }
        // If a barcode is entered, search for the available book by barcode
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String enteredBarcode = result.get().trim();
            findAvailableCopyOfBookByBarcode(enteredBarcode);  // Call method to find book by barcode
        } else {
            showAlertError("Error", "No barcode entered.");
        }
    }

    /**
     * Description:
     * Method for returning to the librarian main page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void getReturnBtn(ActionEvent event) throws Exception {
        LibrarianMainPageController view = new LibrarianMainPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    /**
     * Description:
     * Method for searching a book using the barcode
     *
     * @param barcode String.class
     */
    private void findAvailableCopyOfBookByBarcode(String barcode) {
        String command = "findAvailableCopyByBarcode:" + barcode + "," + subscriber.getMembershipNumber();
        ClientUI.chat.accept(command);

        if (ChatClient.availableCopy == null) {
            showAlertError("Error", "The copy is not available.");
            bookNameField.setText("");  // Display book name
            isBookFound = false;
        } else {
            copyOfBook = ChatClient.availableCopy;
            bookIdField.setText(String.valueOf(copyOfBook.getBookId()));  // Display book ID
            bookNameField.setText(copyOfBook.getCopyOfBookName());  // Display book name
            isBookFound = true;
        }
        updateCommitBorrowButtonState();
    }

    /**
     * Description:
     * Method for showing a member's reader card based on given barcode
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void scanReaderCard(ActionEvent event) {
        TextInputDialog barcodeDialog = new TextInputDialog();  // Create input dialog for barcode
        barcodeDialog.setTitle("Scan ReaderCard Barcode");
        barcodeDialog.setHeaderText("Please enter the barcode to scan:");
        barcodeDialog.setContentText("Barcode:");
        barcodeDialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/dialog.css").toExternalForm());
        barcodeDialog.getDialogPane().getStylesheets().add("custom-alert");

        Optional<String> result = barcodeDialog.showAndWait();
        if (!result.isPresent()) {
            return;  // Exit if user cancels
        }
        // If barcode is entered, search for the subscriber by barcode
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String enteredBarcode = result.get().trim();
            findSubscriberByBarcode(enteredBarcode);
        } else {
            showAlertError("Error", "No barcode entered.");
        }
    }

    /**
     * Description:
     * Method for searching a subscriber using the reader card barcode
     *
     * @param enteredBarcode String.class
     */
    private void findSubscriberByBarcode(String enteredBarcode) {
        String command = "ScanReaderCard:" + enteredBarcode;
        ChatClient.sub = null;
        ClientUI.chat.accept(command);  // Send the request to the server

        if (ChatClient.sub != null) {
            loadSubscriber(ChatClient.sub);  // If subscriber is found, load details
        } else {
            showAlertError("No Members Found", "No member found with barcode.");
        }
    }

    /**
     * Description:
     * Method for searching a subscriber using the member's membership number
     */
    @FXML
    private void handleFindMember() {
        String membershipNumberText = membershipNumberField.getText();  // Retrieve membership number

        // Validate if the membership number is empty
        if (membershipNumberText == null || membershipNumberText.trim().isEmpty()) {
            showAlertError("Error", "Membership Number cannot be empty.");
            return;
        }

        // Validate if the membership number contains only digits
        if (!membershipNumberText.matches("\\d+")) {
            showAlertError("Invalid Id", "The ID must contain only digits.");
        } else {
            // Reset the subscriber before sending the request
            ChatClient.sub = null;
            String command = "ViewReaderCard:" + membershipNumberText;
            ClientUI.chat.accept(command);
            loadSubscriber(ChatClient.sub);
        }
    }

    /**
     * Description:
     * Method for loading a subscribers information based on given subscriber
     *
     * @param sub Subscriber.class
     */
    @FXML
    private void loadSubscriber(Subscriber sub) {
        subscriber = sub;

        // Handle if no subscriber is found
        if (sub == null) {
            showAlertError("Member Not Found", "No member found with the given ID.");
            return;
        }

        // Handle if the subscriber's status is frozen
        else if (sub.getMemberFreezeStatus() == FreezeStatus.FROZEN) {
            showAlertError("Member Is Frozen", "Member Status Is Frozen.");
            return;
        }

        // Display subscriber details in the UI
        membershipNumberField.setText(String.valueOf(subscriber.getMembershipNumber()));
        memberFullNameField.setText(subscriber.getMemberFullName());
        isMemberFound = true;
        findBookButton.setDisable(false);
        scanBookButton.setDisable(false);
        bookIdField.setDisable(false);
    }

    /**
     * Description:
     * Method for showing the 'Borrow' button when found a member and a book to borrow
     */
    private void updateCommitBorrowButtonState() {
        commitBorrowButton.setDisable(!(isBookFound && isMemberFound));
    }

    /**
     * Description:
     * Method for the librarian to process the borrowing action
     */
    @FXML
    private void handleCommitBorrow() {
        // Reset the borrowed book field
        ChatClient.borrowedBook = null;
        int librarianID = 101;  // Default librarian ID
        String librarianName = "Lucy";  // Default librarian name

        // If librarian details are available, update them
        if (ChatClient.librarianLogin != null) {
            librarianID = ChatClient.librarianLogin.getLibrarianID();
            librarianName = ChatClient.librarianLogin.getFullName();
        }

        int membershipNumber = subscriber.getMembershipNumber();
        String command = "FetchAllBorrowedBooks:" + membershipNumber; // Construct the command to fetch borrowed books
        ClientUI.chat.accept(command); // Send the request to the server to fetch the books
        List<BorrowedBook> borrowedBooksList = ChatClient.borrowList; // Retrieve the list of borrowed books from the server

        // Check if there is a borrowed book that the member didn't return in time
        LocalDate current = LocalDate.now();
        StringBuilder borrowString = new StringBuilder();
        for (BorrowedBook book : borrowedBooksList)
            if (book.getReturnDate().isBefore(current))
                borrowString.append(String.format("\"%s\",", book.getNameOfBook()));
        if (borrowString.length() > 0) {
            borrowString.deleteCharAt(borrowString.length() - 1);
            showAlertError("Book Return Date", "The following borrowed books haven't been returned in time:\n"
                    + borrowString + "\nPlease contact the library.");
            return;
        }
        // Send command to borrow the book
        command = "BorrowBook:" + membershipNumber + "," + copyOfBook.getCopyOfBookId() + "," + librarianID + "," + librarianName;
        ClientUI.chat.accept(command);

        // Check if the borrow was successful
        if (ChatClient.borrowedBook != null) {
            showAlertSuccess("Borrow Successful", "Book Borrowed successfully.");
            addActivity();  // Add to activity log
        } else {
            showAlertError("Borrow Unsuccessful", "Book Borrowed unsuccessfully.");
        }
    }

    /**
     * Description:
     * Method for adding an activity to the activity_db (borrow book)
     */
    private void addActivity() {
        String activityDetails = String.format("borrowed %s ", ChatClient.borrowedBook.getNameOfBook());
        String command = "addActivity:" + subscriber.getMembershipNumber() + "," + "borrow" + "," + activityDetails;
        ClientUI.chat.accept(command);

        // Try to refresh the page after borrowing
        try {
            Stage currentStage = (Stage) commitBorrowButton.getScene().getWindow();  // Get current stage
            start(currentStage);  // Reload the page
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "Failed to refresh the page.");
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
     * Method to logout from librarian's account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUI/BorrowBook/BorrowBookPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/BorrowBook/BorrowBookPage.css").toExternalForm());
        primaryStage.setTitle("Registration Management Tool");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Register window close listener for logout
        LogoutUtil.addWindowCloseListener(primaryStage);
    }
}