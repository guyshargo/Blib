package gui.LibrarianGUI.ViewAndEditMember;

import client.ChatClient;
import client.ClientUI;
import gui.LogInGUI.LogoutUtil;
import gui.LibrarianGUI.ManageMember.ManageMemberController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.ActivityControl.Activity;
import logic.BookControl.BorrowedBook;
import logic.FreezeStatus;
import logic.Subscriber;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Description:
 * Controller class for showing and updating a member
 */
public class ViewMemberPageController {
    // TextField for displaying the subscriber's full name
    @FXML
    private TextField txtFullName;

    // TextField for displaying the subscriber's email
    @FXML
    private TextField txtEmail;

    // TextField for displaying the subscriber's membership number
    @FXML
    private TextField txtMembershipNumber;

    // ChoiceBox to toggle between 'Frozen' and 'Not Frozen' status for the subscriber
    @FXML
    private ChoiceBox<String> btnToggleFreeze;

    // ListView for displaying the subscriber's activities
    @FXML
    private ListView<String> listViewActivities;

    // TextField to filter borrowed books in the table
    @FXML
    private TextField filterField;

    // TableView for displaying borrowed books by the subscriber
    @FXML
    private TableView<BorrowedBook> tblBooks;

    // TableColumn for book title in the borrowed books table
    @FXML
    private TableColumn<BorrowedBook, String> colBookTitle;

    // TableColumn for borrow date in the borrowed books table
    @FXML
    private TableColumn<BorrowedBook, String> colBorrowDate;

    // TableColumn for return date in the borrowed books table
    @FXML
    private TableColumn<BorrowedBook, String> colReturnDate;

    // TableColumn for librarian name in the borrowed books table
    @FXML
    private TableColumn<BorrowedBook, String> colLibrarianName;

    // TableColumn for extension date in the borrowed books table
    @FXML
    private TableColumn<BorrowedBook, String> colExtentionDate;

    @FXML
    private Button logoutButton;

    @FXML
    private Label librarianName;

    // ObservableList for dynamically updating the borrowed books list
    private ObservableList<BorrowedBook> borrowedBooks = FXCollections.observableArrayList();

    // Variable to hold the current Subscriber object
    private Subscriber subscriber;

    // Flag to check if it's the first load of the page
    private boolean isFirstLoad = true;

    // Create a date formatter
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        librarianName.setText(ChatClient.librarianLogin.getFullName());
    }
    /**
     * Description:
     * Method for loading the subscriber's data into the UI components
     *
     * @param subscriber Subscriber.class
     */
    public void loadSubscriber(Subscriber subscriber) {
        // Store the subscriber object
        this.subscriber = subscriber;

        // Set the subscriber details to the respective text fields
        txtFullName.setText(subscriber.getMemberFullName());
        txtEmail.setText(subscriber.getEmailAddress());
        txtMembershipNumber.setText(String.valueOf(subscriber.getMembershipNumber()));

        // Set the freeze status in the ChoiceBox based on the subscriber's freeze status
        if (subscriber.getMemberFreezeStatus() == FreezeStatus.FROZEN) {
            btnToggleFreeze.setValue("Frozen");
        } else {
            btnToggleFreeze.setValue("Not Frozen");
        }

        // Indicate that the page has been loaded for the first time
        isFirstLoad = false;

        // Clear any existing activities in the list
        listViewActivities.getItems().setAll();

        LoadActivities(); // Load the subscriber's activities
        loadBooks(); // Load the subscriber's borrowed books
    }

    /**
     * Description:
     * Method for loading the subscriber's activities from the server
     */
    public void LoadActivities() {
        // Construct the command to fetch all activities for the subscriber
        String command = "fetchAllActivitiesForASubscriber:" + subscriber.getMembershipNumber();
        ClientUI.chat.accept(command); // Send the request to the server to fetch activities
        List<Activity> activities = ChatClient.ActivityList; // Retrieve the list of activities from the server
        ChatClient.sub = subscriber; // Set the subscriber for ChatClient

        if (activities != null && !activities.isEmpty()) {
            listViewActivities.getItems().clear(); // Clear existing items in the list
            int activitiesCnt = 1; // Variable to count the activities
            for (Activity activity : activities) {
                // Add the activity to the list view with a numbered prefix
                listViewActivities.getItems().add(activitiesCnt + ") " + activity.toString());
                activitiesCnt++; // Increment the activity count
            }
        } else {
            listViewActivities.getItems().clear(); // Clear existing items
            listViewActivities.getItems().add("No activities found for this subscriber."); // Display a message if no activities are found
        }
    }

    /**
     * Description:
     * Method for toggling the freeze status of the subscriber
     */
    @FXML
    public void toggleFreeze() {
        String selectedStatus = btnToggleFreeze.getValue(); // Get the selected freeze status from the ChoiceBox
        if (isFirstLoad) {
            return; // Return if it's the first load
        }
        // Determine the new freeze status based on the selected value
        FreezeStatus newStatus = "Frozen".equals(selectedStatus) ? FreezeStatus.FROZEN : FreezeStatus.NOT_FROZEN;

        if (subscriber.getMemberFreezeStatus() != newStatus) {
            subscriber.setMemberFreezeStatus(newStatus); // Update the subscriber's freeze status
            saveSubscriberStatus(); // Save the updated status
        }
    }

    /**
     * Description:
     * Method for loading the borrowed books of the subscriber
     */
    private void loadBooks() {
        int membershipNumber = subscriber.getMembershipNumber(); // Get the subscriber's membership number
        String command = "FetchAllBorrowedBooks:" + membershipNumber; // Construct the command to fetch borrowed books
        ClientUI.chat.accept(command); // Send the request to the server to fetch the books
        List<BorrowedBook> borrowedBooksList = ChatClient.borrowList; // Retrieve the list of borrowed books from the server
        if (borrowedBooksList == null) {
            return;
        }
        borrowedBooks.clear(); // Clear the existing borrowed books list
        borrowedBooks.addAll(borrowedBooksList); // Add the fetched books to the list

        // Set up the table columns with data
        colBookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameOfBook())); // Bind book title to the column
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Create a date formatter for formatting dates
        colBorrowDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowDate().format(formatter))); // Bind borrow date to the column
        colReturnDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnDate().format(formatter))); // Bind return date to the column

        // Add search functionality for filtering the borrowed books
        filterField.textProperty().addListener((observable, oldValue, newValue) -> filterBooks(newValue)); // Listen for text changes in the filter field

        colLibrarianName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLibrarianName())); // Bind librarian name to the column

        colExtentionDate.setCellValueFactory(cellData -> {
            LocalDate extensionDate = cellData.getValue().getExtensionDate(); // Get the extension date
            return new SimpleStringProperty(extensionDate != null ? extensionDate.format(formatter) : "N/A"); // Bind extension date to the column, handle null values
        });

        tblBooks.setItems(borrowedBooks); // Set the borrowed books as the items for the table
        tblBooks.setOnMouseClicked(event -> changeReturnDate()); // Set an event listener for clicking on the table to change the return date
    }

    /**
     * Description:
     * Method for filtering the books displayed in the table based on the search query
     *
     * @param query String.class
     */
    private void filterBooks(String query) {
        if (query == null || query.isEmpty()) {
            tblBooks.setItems(borrowedBooks); // If the search query is empty, display all borrowed books
        } else {
            ObservableList<BorrowedBook> filteredList = FXCollections.observableArrayList(); // Create a new list for filtered books
            for (BorrowedBook book : borrowedBooks) {
                String bookName = book.getNameOfBook().toLowerCase(); // Convert the book name to lowercase for case-insensitive comparison
                if (bookName.contains(query.toLowerCase())) {
                    filteredList.add(book); // Add the book to the filtered list if the name contains the query
                }
            }
            tblBooks.setItems(filteredList); // Set the filtered list as the table items
        }
    }

    /**
     * Description:
     * Method for changing the return date of a selected borrowed book
     */
    public void changeReturnDate() {
        BorrowedBook selectedBook = tblBooks.getSelectionModel().getSelectedItem(); // Get the selected book from the table
        if (selectedBook != null) {
            TextInputDialog dialog = new TextInputDialog(selectedBook.getReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // Create a dialog to input the new return date
            dialog.setTitle("Change Return Date"); // Set the dialog title
            dialog.setHeaderText("Enter the new return date for the book: " + selectedBook.getNameOfBook()); // Set the header text
            dialog.setContentText("Date format: dd/MM/yyyy"); // Set the content text for the date format
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/dialog.css").toExternalForm());
            dialog.getDialogPane().getStylesheets().add("custom-alert");

            Optional<String> result = dialog.showAndWait(); // Show the dialog and wait for the user input
            if (!result.isPresent()) {
                loadBooks(); // Reload the books if the user cancels the dialog
                filterField.setText(filterField.getText()); // Maintain the filter text
                return;
            }
            String newReturnDate = result.get().trim(); // Get the new return date entered by the user
            if (!newReturnDate.isEmpty()) {
                try {
                    LocalDate parsedReturnDate = LocalDate.parse(newReturnDate, formatter); // Parse the entered date
                    if (parsedReturnDate.isEqual(selectedBook.getReturnDate())) {
                        showAlertError("No Changes Detected", "The return date is the same as the current date. No changes were made."); // Show an alert if the date is the same
                        loadBooks(); // Reload the books
                        filterField.setText(filterField.getText()); // Maintain the filter text
                        return;
                    }
                    if (parsedReturnDate.isBefore(selectedBook.getBorrowDate())) {
                        showAlertError("Invalid Return Date", "Return date cannot be earlier than the borrow date."); // Show an alert if the return date is invalid
                        loadBooks(); // Reload the books
                        filterField.setText(filterField.getText()); // Maintain the filter text
                    } else {
                        SaveReturnDate(selectedBook, parsedReturnDate); // Save the new return date if valid
                    }
                } catch (Exception ex) {
                    loadBooks(); // Reload the books if an error occurs
                    filterField.setText(filterField.getText()); // Maintain the filter text
                    showAlertError("Invalid Date", "Please enter a valid date in the format dd/MM/yyyy."); // Show an alert for invalid date format
                }
            } else {
                loadBooks(); // Reload the books if the return date is empty
                filterField.setText(filterField.getText()); // Maintain the filter text
                showAlertError("Empty Return Date", "Please enter a return date."); // Show an alert for empty return date
            }
        }
    }

    /**
     * Description:
     * Method for saving the new return date for a borrowed book
     *
     * @param selectedBook  BorrowedBook.class
     * @param newReturnDate LocalDate.class
     */
    private void SaveReturnDate(BorrowedBook selectedBook, LocalDate newReturnDate) {
        String currentDate = LocalDate.now().toString(); // Get the current date

        String libName = "Librarian"; // Default librarian name
        int libId = 101; // Default librarian ID
        if (ChatClient.librarianLogin != null) {
            libName = ChatClient.librarianLogin.getFullName(); // Get the librarian's full name
            libId = ChatClient.librarianLogin.getLibrarianID(); // Get the librarian's ID
        }

        // Construct the command to change the return date
        String command = "ChangeReturnDate:" + subscriber.getMembershipNumber() + "," + selectedBook.getCopyOfBookId() + "," + newReturnDate + "," + libName + "," + libId + "," + currentDate;
        addActivity(String.format("extendBorrow, %s extended return date to %s by %s ", selectedBook.getNameOfBook(), newReturnDate.format(formatter), libName));
        ClientUI.chat.accept(command); // Send the command to the server to update the return date
        loadBooks(); // Reload the books after the return date is changed
    }

    /**
     * Description:
     * Method for returning to the librarian main page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void getReturnBtn(ActionEvent event) throws Exception {
        ManageMemberController view = new ManageMemberController(); // Create an instance of the ManageMemberController
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow()); // Start the ManageMemberController view
    }

    /**
     * Description:
     * Method for saving the subscriber's freeze status
     */
    private void saveSubscriberStatus() {
        FreezeStatus originalFreezeStatus = subscriber.getMemberFreezeStatus() == FreezeStatus.NOT_FROZEN ? FreezeStatus.FROZEN : FreezeStatus.NOT_FROZEN;
        String command = "SetSubscriberStatus:" + subscriber.getMembershipNumber() + "," + subscriber.getMemberFreezeStatus().getDbValue() + ',' + LocalDate.now(); // Construct the command to set the subscriber's status
        ClientUI.chat.accept(command); // Send the command to the server to update the subscriber's status
        if (ChatClient.sub == null) {
            showAlertError("Error in updating status", "Error in updating Status Please Try Again"); // Show an error alert if the status update fails
            ChatClient.sub = subscriber; // Reset the subscriber object
            return;
        }
        showAlertSuccess("Updating status", "Updating status successful"); // Show a success alert
        addActivity(String.format("freezeStatus,status changed from %s to %s ", originalFreezeStatus.getDbValue(), subscriber.getMemberFreezeStatus().getDbValue()));
    }

    /**
     * Description:
     * Method for adding an activity to the activity_db (borrow book)
     *
     * @param activityDetails String.class
     */
    private void addActivity(String activityDetails) {
        String command = "addActivity:" + subscriber.getMembershipNumber() + "," + activityDetails;
        ClientUI.chat.accept(command);
        LoadActivities();
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
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
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
}