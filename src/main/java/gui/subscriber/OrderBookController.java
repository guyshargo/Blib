package gui.subscriber;

import client.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Book;
import model.OrderedBook;
import model.Subscriber;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

public class OrderBookController {
    private final ObservableList<OrderedBook> orderedBooks = FXCollections.observableArrayList();
    private Book foundBook = null;
    private OrderedBook cancelOrder = null;
    private Book cancelBook = null;
    private Subscriber sub = null;

    @FXML private TextField bookIDTxt;
    @FXML private TextField bookNameTxt;
    @FXML private TableView<OrderedBook> orderTable;
    @FXML private TableColumn<OrderedBook, Integer> bookIDColumn;
    @FXML private TableColumn<OrderedBook, String> bookNameColumn;
    @FXML private TableColumn<OrderedBook, String> arrivedStatusColumn;
    @FXML private Button orderBtn;
    @FXML private Label foundLabel;
    @FXML private Label foundLabel2;

    @FXML
    public void initialize() {
        this.sub = SessionManager.currentSubscriber;

        bookIDColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getBookID()).asObject());
        bookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookName()));
        arrivedStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getArrivalStatus().toString()));

        orderTable.setItems(orderedBooks);
        loadOrderedBooks();

        foundLabel.setText("");
        foundLabel2.setText("");
        orderBtn.setDisable(true);

        orderTable.setOnMouseClicked(event -> handleOrderedBookClick());
    }

    void handleOrderedBookClick() {
        OrderedBook selectedOrderedBook = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrderedBook == null) {
            showAlertError("No Selection", "Please select an order.");
            return;
        }

        this.cancelOrder = selectedOrderedBook;
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/orders/search-to-cancel?bookId=" + cancelOrder.getBookID()))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<Book> bookList = mapper.readValue(res.body(), new TypeReference<List<Book>>(){});
                if (!bookList.isEmpty()) {
                    this.cancelBook = bookList.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrderedBooks() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/orders/subscriber/" + sub.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<OrderedBook> orderList = mapper.readValue(res.body(), new TypeReference<List<OrderedBook>>(){});
                updateOrderedBookList(orderList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateOrderedBookList(List<OrderedBook> subOrderedBooks) {
        orderedBooks.clear();
        if (subOrderedBooks != null)
            orderedBooks.addAll(subOrderedBooks);
        orderTable.setItems(orderedBooks);
    }

    @FXML
    void handleReorderClick(ActionEvent event) throws Exception {
        if (cancelOrder == null) {
            showAlertError("Cancel order request", "Error: must select an order to cancel");
            return;
        }

        OrderedBook selectedOrderedBook = orderTable.getSelectionModel().getSelectedItem();
        try {
            String encodedName = URLEncoder.encode(selectedOrderedBook.getBookName(), StandardCharsets.UTF_8);
            String url = String.format("http://localhost:8080/api/orders/search-to-order?bookName=%s&bookId=%d", encodedName, selectedOrderedBook.getBookID());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<Book> bookList = mapper.readValue(res.body(), new TypeReference<List<Book>>(){});
                
                if (bookList != null && !bookList.isEmpty()) {
                    this.foundBook = this.cancelBook;
                    orderBook(event);
                    this.cancelBook = null;
                } else {
                    showAlertError("Search Request", "No books found for the given search criteria");
                }
            } else {
                showAlertError("Search Request", "No books found for the given search criteria");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void orderBook(ActionEvent event) {
        if (foundBook == null) {
            showAlertError("Order request", "Error: must search a book to order");
            return;
        }
        
        if (sub.getFreezeStatus().getDbValue().equals("Frozen")) {
            showAlertError("Order request", "Member is Frozen");
            return;
        }

        if (foundBook.getNumberOfCopies() == foundBook.getNumberOfBorrowedCopies()) {
            showAlertError("Order request", foundBook.getBookName() + " reached maximum ordering to the book");
            return;
        }

        try {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("title", foundBook.getBookName());
            reqMap.put("book_id", foundBook.getBookID());
            reqMap.put("member_id", sub.getMembershipNumber());
            reqMap.put("full_name", sub.getFullName());
            reqMap.put("memberPhone", sub.getPhoneNumber());
            reqMap.put("memberEmail", sub.getEmailAddress());

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String jsonBody = mapper.writeValueAsString(reqMap);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/orders/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body().equals("approve")) {
                addActivity(foundBook, true);

                String statusUrl = String.format("http://localhost:8080/api/orders/status?bookId=%d&numberOfOrders=%d&increase=true", 
                        foundBook.getBookID(), foundBook.getNumberOforders());
                HttpRequest statusReq = HttpRequest.newBuilder().uri(URI.create(statusUrl)).PUT(HttpRequest.BodyPublishers.noBody()).build();
                client.send(statusReq, HttpResponse.BodyHandlers.ofString());

                showAlertSuccess("Order request", foundBook.getBookName() + " has been ordered");
                foundBook = null;
                foundLabel.setText("");
                foundLabel2.setText("");
                bookIDTxt.setText("");
                bookNameTxt.setText("");
                orderBtn.setDisable(true);
                loadOrderedBooks();
            } else {
                showAlertError("Order request", "Error in ordering " + foundBook.getBookName() + " because of " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "Network error occurred.");
        }
    }
    
    public void cancelOrderedBook(ActionEvent event) {
        if (cancelOrder == null) {
            showAlertError("Cancel order request", "Error: must select an order to cancel");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            String encodedName = URLEncoder.encode(cancelOrder.getBookName(), StandardCharsets.UTF_8);
            String url = String.format("http://localhost:8080/api/orders/%d?bookName=%s&arrivalStatus=%s", 
                    cancelOrder.getOrderID(), encodedName, cancelOrder.getArrivalStatus().toString());

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                addActivity(cancelBook, false);
                
                String statusUrl = String.format("http://localhost:8080/api/orders/status?bookId=%d&numberOfOrders=%d&increase=false", 
                        cancelBook.getBookID(), cancelBook.getNumberOforders());
                HttpRequest statusReq = HttpRequest.newBuilder().uri(URI.create(statusUrl)).PUT(HttpRequest.BodyPublishers.noBody()).build();
                client.send(statusReq, HttpResponse.BodyHandlers.ofString());
                
                showAlertSuccess("Cancel order request", cancelOrder.getBookName() + " has been canceled from list");
                cancelOrder = null;
                cancelBook = null;
                loadOrderedBooks();
            } else {
                showAlertSuccess("Cancel order request", cancelOrder.getBookName() + " has been canceled from list");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlertError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(content);
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

    private void initiateBookSearch(String bookName, String bookID) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String encodedName = URLEncoder.encode(bookName, StandardCharsets.UTF_8);
            int id = bookID.equals("null") ? -1 : Integer.parseInt(bookID);
            
            String url = String.format("http://localhost:8080/api/orders/search-to-order?bookName=%s&bookId=%d", encodedName, id);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<Book> bookList = mapper.readValue(res.body(), new TypeReference<List<Book>>(){});
                
                if (bookList != null && !bookList.isEmpty()) {
                    displaySearchResults(bookList);
                } else {
                    foundBook = null;
                    foundLabel.setText("");
                    foundLabel2.setText("");
                    showAlertError("Search Request", "No books found for the given search criteria");
                }
            } else {
                foundBook = null;
                foundLabel.setText("");
                foundLabel2.setText("");
                showAlertError("Search Request", "No books found for the given search criteria");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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

    private void addActivity(Book book, boolean order) {
        try {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("membershipNumber", sub.getMembershipNumber());
            
            if (order) {
                reqMap.put("activityType", "order");
                reqMap.put("description", String.format("order %s ", book.getBookName()));
            } else {
                reqMap.put("activityType", "cancelOrder");
                reqMap.put("description", String.format("cancel order %s ", book.getBookName()));
            }

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