package gui.librarian;

import client.SessionManager;

import enums.FreezeStatus;
import gui.auth.LogoutUtil;
import model.Activity;
import model.BorrowedBook;
import model.Subscriber;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class ViewMemberController {
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtMembershipNumber;
    @FXML private ChoiceBox<String> btnToggleFreeze;
    @FXML private ListView<String> listViewActivities;
    @FXML private TextField filterField;
    @FXML private TableView<BorrowedBook> tblBooks;
    @FXML private TableColumn<BorrowedBook, String> colBookTitle;
    @FXML private TableColumn<BorrowedBook, String> colBorrowDate;
    @FXML private TableColumn<BorrowedBook, String> colReturnDate;
    @FXML private TableColumn<BorrowedBook, String> colLibrarianName;
    @FXML private TableColumn<BorrowedBook, String> colExtentionDate;
    @FXML private Button logoutButton;
    @FXML private Label librarianName;

    private ObservableList<BorrowedBook> borrowedBooks = FXCollections.observableArrayList();
    private Subscriber subscriber;
    private boolean isFirstLoad = true;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        librarianName.setText(SessionManager.currentLibrarian.getFullName());
    }


    public void loadSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
        txtFullName.setText(subscriber.getFullName());
        txtEmail.setText(subscriber.getEmailAddress());
        txtMembershipNumber.setText(String.valueOf(subscriber.getMembershipNumber()));

        // Set the freeze status in the ChoiceBox based on the subscriber's freeze status
        if (subscriber.getFreezeStatus() == FreezeStatus.FROZEN) {
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


    public void LoadActivities() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/activities/subscriber/" + subscriber.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            listViewActivities.getItems().clear();
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<Activity> activities = mapper.readValue(response.body(), new TypeReference<List<Activity>>(){});
                
                if (activities != null && !activities.isEmpty()) {
                    int activitiesCnt = 1;
                    for (Activity activity : activities) {
                        listViewActivities.getItems().add(activitiesCnt + ") " + activity.toString());
                        activitiesCnt++;
                    }
                } else {
                    listViewActivities.getItems().add("No activities found for this subscriber.");
                }
            } else {
                listViewActivities.getItems().add("No activities found for this subscriber.");
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        if (subscriber.getFreezeStatus() != newStatus) {
            subscriber.setFreezeStatus(newStatus); // Update the subscriber's freeze status
            saveSubscriberStatus(); // Save the updated status
        }
    }

    /**
     * Description:
     * Method for loading the borrowed books of the subscriber
     */
    private void loadBooks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/subscriber/" + subscriber.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<BorrowedBook> borrowedBooksList = mapper.readValue(response.body(), new TypeReference<List<BorrowedBook>>(){});
                
                borrowedBooks.clear(); 
                if (borrowedBooksList != null) borrowedBooks.addAll(borrowedBooksList); 

                colBookTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameOfBook())); 
                colBorrowDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBorrowDate().format(formatter))); 
                colReturnDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnDate().format(formatter))); 
                filterField.textProperty().addListener((observable, oldValue, newValue) -> filterBooks(newValue)); 
                colLibrarianName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLibrarianName())); 

                colExtentionDate.setCellValueFactory(cellData -> {
                    LocalDate extensionDate = cellData.getValue().getExtensionDate(); 
                    return new SimpleStringProperty(extensionDate != null ? extensionDate.format(formatter) : "N/A"); 
                });

                tblBooks.setItems(borrowedBooks); 
                tblBooks.setOnMouseClicked(event -> changeReturnDate()); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/common/dialog.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("custom-alert");

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
        try {
            String libName = SessionManager.currentLibrarian != null ? SessionManager.currentLibrarian.getFullName() : "Librarian"; 
            int libId = SessionManager.currentLibrarian != null ? SessionManager.currentLibrarian.getLibrarianID() : 101; 

            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("memberId", subscriber.getMembershipNumber());
            reqMap.put("copyOfBookId", selectedBook.getCopyOfBookId());
            reqMap.put("newReturnDate", newReturnDate.toString());
            reqMap.put("librarianName", libName);
            reqMap.put("librarianId", libId);
            reqMap.put("extensionDate", LocalDate.now().toString());

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String jsonBody = mapper.writeValueAsString(reqMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/change-return-date"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                addActivity(String.format("extendBorrow, %s extended return date to %s by %s ", selectedBook.getNameOfBook(), newReturnDate.format(formatter), libName));
            } else {
                showAlertError("Error", "Failed to update return date on server.");
            }
            loadBooks(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getReturnBtn(ActionEvent event) throws Exception {
        ManageMemberController view = new ManageMemberController(); // Create an instance of the ManageMemberController
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow()); // Start the ManageMemberController view
    }


    private void saveSubscriberStatus() {
        try {
            FreezeStatus originalFreezeStatus = subscriber.getFreezeStatus() == FreezeStatus.NOT_FROZEN ? FreezeStatus.FROZEN : FreezeStatus.NOT_FROZEN;
            
            String url = String.format("http://localhost:8080/api/subscribers/%d/freeze-status?status=%s&date=%s", 
                subscriber.getMembershipNumber(), subscriber.getFreezeStatus().getDbValue(), LocalDate.now().toString());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlertSuccess("Updating status", "Updating status successful"); 
                addActivity(String.format("freezeStatus,status changed from %s to %s ", originalFreezeStatus.getDbValue(), subscriber.getFreezeStatus().getDbValue()));
            } else {
                showAlertError("Error in updating status", "Error in updating Status Please Try Again"); 
                subscriber.setFreezeStatus(originalFreezeStatus); // Revert UI
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void addActivity(String activityDetails) {
        try {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("membershipNumber", subscriber.getMembershipNumber());
            reqMap.put("activityType", activityDetails.split(",")[0]);
            reqMap.put("description", activityDetails);

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String jsonBody = mapper.writeValueAsString(reqMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/activities/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            LoadActivities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }


    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(message);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/alert.css").toExternalForm());
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
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/success.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        alert.showAndWait();
    }
}