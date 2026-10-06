package gui.subscriber;

import client.SessionManager;
import enums.FreezeStatus;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    @FXML private TableColumn<BorrowedBook, Void> actionColumn;
    @FXML private TextField bookNameTxt;
    @FXML private TextField copyOfBookIDTxt;
    @FXML private RadioButton overdueRadioBtn;

    private Subscriber sub = null;

    @FXML
    public void initialize() {
        this.sub = SessionManager.currentSubscriber;

        borrowedIDColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCopyOfBookId()).asObject());
        bookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNameOfBook()));
        returnDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getReturnDate().toString()));

        borrowedTable.setItems(borrowedBooks);

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

        overdueRadioBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                filterOverdueBooks();
            } else {
                borrowedTable.setItems(borrowedBooks);
            }
            copyOfBookIDTxt.setText("");
        });

        bookNameTxt.textProperty().addListener((observable, oldValue, newValue) -> filterBorrowedBooks(newValue));
        loadBorrowedBooks();
        borrowedTable.setOnMouseClicked(event -> handleBorrowedBookClick());
    }

    void handleBorrowedBookClick() {
        BorrowedBook selectedBorrowedBook = borrowedTable.getSelectionModel().getSelectedItem();
        if (selectedBorrowedBook == null)
            showAlertError("No Selection", "Please select a book to extend.");
        else
            copyOfBookIDTxt.setText("" + selectedBorrowedBook.getCopyOfBookId());
    }

    private void filterOverdueBooks() {
        ObservableList<BorrowedBook> overdueBooks = FXCollections.observableArrayList();
        for (BorrowedBook book : borrowedBooks)
            if (book.getReturnDate().isBefore(LocalDate.now()))
                overdueBooks.add(book);
        borrowedTable.setItems(overdueBooks);
    }

    private void filterBorrowedBooks(String query) {
        if (query == null || query.isEmpty())
            borrowedTable.setItems(borrowedBooks);
        else {
            ObservableList<BorrowedBook> filteredList = FXCollections.observableArrayList();
            for (BorrowedBook borrow : borrowedBooks) {
                if (borrow.getNameOfBook().toLowerCase().contains(query.toLowerCase()))
                    filteredList.add(borrow);
            }
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
        if (subBorrowedBooks != null)
            borrowedBooks.addAll(subBorrowedBooks);
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
        LocalDate currentDate = LocalDate.now();
        LocalDate oneWeekBefore = returnDate.minusDays(7);
        return !currentDate.isBefore(oneWeekBefore) && currentDate.minusDays(1).isBefore(returnDate);
    }

    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-error");
        alert.showAndWait();
    }

    private void showAlertSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Success");
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-success");
        alert.showAndWait();
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
}