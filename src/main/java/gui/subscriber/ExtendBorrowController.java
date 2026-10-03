package gui.subscriber;

import client.SessionManager;
import enums.FreezeStatus;
import gui.auth.LogoutUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.BorrowedBook;
import model.Subscriber;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;


public class ExtendBorrowController {
    private final ObservableList<BorrowedBook> borrowedBooks = FXCollections.observableArrayList();

    @FXML private TableView<BorrowedBook> borrowedTable;
    @FXML private TableColumn<BorrowedBook, Integer> borrowedIDColumn;
    @FXML private TableColumn<BorrowedBook, String> bookNameColumn;
    @FXML private TableColumn<BorrowedBook, String> returnDateColumn;
    @FXML private TextField bookNameTxt;
    @FXML private TextField copyOfBookIDTxt;
    @FXML private RadioButton overdueRadioBtn;
    @FXML private Label subscriberName;
    @FXML private Button logoutButton;

    private Subscriber sub = null;

    @FXML
    public void initialize() {
        // Fetch Subscriber's borrowed books from borrowed_book database
        this.sub = SessionManager.currentSubscriber;
        subscriberName.setText(sub.getFullName());

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


    private void filterOverdueBooks() {
        ObservableList<BorrowedBook> overdueBooks = FXCollections.observableArrayList();
        for (BorrowedBook book : borrowedBooks)
            // filter books by overdue date
            if (book.getReturnDate().isBefore(LocalDate.now()))
                overdueBooks.add(book);
        borrowedTable.setItems(overdueBooks);
    }


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


    private void loadBorrowedBooks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/subscriber/" + sub.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<BorrowedBook> subBorrowedBooks = mapper.readValue(response.body(), new TypeReference<List<BorrowedBook>>(){});
                updateBorrowedBookList(subBorrowedBooks);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateBorrowedBookList(List<BorrowedBook> subBorrowedBooks) {
        borrowedBooks.clear();
        // Case the subscriber has borrowed books
        if (subBorrowedBooks != null)
            borrowedBooks.addAll(subBorrowedBooks);
        // Add the borrowed books to the table
        borrowedTable.setItems(borrowedBooks);
    }


    public void extendBorrow(ActionEvent event) {
        if (copyOfBookIDTxt.getText().isEmpty()) {
            showAlertError("Extension Request", "Must insert the ID of the book you want to extend the return date");
            return;
        }
        BorrowedBook selectedBorrowedBook = borrowedTable.getSelectionModel().getSelectedItem();

        if (selectedBorrowedBook == null) {
            for (BorrowedBook borrow : borrowedBooks) {
                if (String.valueOf(borrow.getCopyOfBookId()).equals(copyOfBookIDTxt.getText())) {
                    selectedBorrowedBook = borrow;
                    break;
                }
            }
        }
        
        if (selectedBorrowedBook == null) {
            showAlertError("Extension Request", "There is no book with the inserted ID");
        } else {
            LocalDate returnDate = selectedBorrowedBook.getReturnDate();
            if (LocalDate.now().isAfter(returnDate)) {
                showAlertError("Extension Request", "Must return book, please contact a librarian from the library");
            } else if (!checkWeekTillReturnDate(returnDate)) {
                showAlertError("Extension Request", "Borrowed book must be at least one week till return date");
            } else if (this.sub.getFreezeStatus() == FreezeStatus.FROZEN) {
                showAlertError("Extension Request", "Member must not be in frozen status. Please contact the library");
            } else {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    String url = String.format("http://localhost:8080/api/borrows/subscriber/%d/book/%d/extend", 
                            selectedBorrowedBook.getMembershipNumber(), selectedBorrowedBook.getCopyOfBookId());
                    
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).PUT(HttpRequest.BodyPublishers.noBody()).build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        showAlertSuccess("Extension Request", "Approved");
                        addActivity(selectedBorrowedBook);
                        loadBorrowedBooks();
                    } else {
                        showAlertError("Extension Request", "Not Approved there are orders for the book");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlertError("Error", "Network error occurred processing extension request.");
                }
            }
        }
    }


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

    
    public void getReturnBtn(ActionEvent event) throws Exception {
        MemberDashboardController view = new MemberDashboardController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }



    private void addActivity(BorrowedBook selectedBorrowedBook) {
        try {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("membershipNumber", sub.getMembershipNumber());
            reqMap.put("activityType", "extendBorrow");
            reqMap.put("description", String.format("Extending borrowed book %s ", selectedBorrowedBook.getNameOfBook()));

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String jsonBody = mapper.writeValueAsString(reqMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/activities/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}