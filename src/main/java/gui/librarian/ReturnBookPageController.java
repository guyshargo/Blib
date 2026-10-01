package gui.librarian;

import client.SessionManager;
import enums.ActivityType;
import enums.FreezeStatus;
import gui.auth.LogoutUtil;
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
import model.BorrowedBook;
import model.Subscriber;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Description:
 * Controller class for the librarian to return a member's borrowed book
 */
public class ReturnBookPageController {
    @FXML private TextField memberNameTxt;
    @FXML private Label extendLabel1;
    @FXML private Label extendLabel11;
    @FXML private TextField MemberNumTextField;
    @FXML private Button searchBtn;
    @FXML private Button ReturnButton;
    @FXML private Label librarianName;
    @FXML private Button LogoutBtn;
    @FXML private TableView<BorrowedBook> BorrowedBooksTable;
    @FXML private TableColumn<BorrowedBook, Integer> BookIdColumn;
    @FXML private TableColumn<BorrowedBook, String> BookNameColumn;

    private final ObservableList<BorrowedBook> borrowedBooks = FXCollections.observableArrayList();


    @FXML
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/librarian/returnbook/ReturnBookPage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Return Borrowed Books");
        primaryStage.show();
        LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
    }


    @FXML
    public void initialize() {
        librarianName.setText(SessionManager.currentLibrarian.getFullName());
        // Set up the columns in the table
        BookIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCopyOfBookId()).asObject());
        BookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNameOfBook()));

        // Set the table's items to the observable list
        BorrowedBooksTable.setItems(borrowedBooks);
        BorrowedBooksTable.setOnMouseClicked(event -> handleReturnConfirmation());
    }


    @FXML
    public void handleSearchBorrowedBooksForMember() {
        String membershipNumberStr = MemberNumTextField.getText().trim();
        if (membershipNumberStr.isEmpty()) {
            BorrowedBooksTable.getItems().clear();
            memberNameTxt.setText("");
            return;
        }

        try {
            int membershipNumber = Integer.parseInt(membershipNumberStr);
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            HttpRequest checkSubReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/" + membershipNumber))
                    .GET().build();
            HttpResponse<String> checkSubRes = client.send(checkSubReq, HttpResponse.BodyHandlers.ofString());

            if (checkSubRes.statusCode() != 200) {
                memberNameTxt.setText("");
                showAlertError("Member Not Found", "The member does not exist. Please enter a valid membership number.");
                return;
            }

            Subscriber sub = mapper.readValue(checkSubRes.body(), Subscriber.class);
            memberNameTxt.setText(sub.getFullName());

            HttpRequest borrowsReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/subscriber/" + membershipNumber))
                    .GET().build();
            HttpResponse<String> borrowsRes = client.send(borrowsReq, HttpResponse.BodyHandlers.ofString());

            if (borrowsRes.statusCode() == 200) {
                List<BorrowedBook> books = mapper.readValue(borrowsRes.body(), new TypeReference<List<BorrowedBook>>(){});
                if (!books.isEmpty()) {
                    BorrowedBooksTable.setItems(FXCollections.observableArrayList(books));
                } else {
                    showAlertError("There is no borrowed books", "This member has no borrowed books.");
                }
            }
        } catch (NumberFormatException e) {
            memberNameTxt.setText("");
            showAlertError("Input Error", "Membership number must be a valid integer.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "Failed to retrieve data.");
        }
    }


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


    private void processReturn(BorrowedBook book) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            LocalDate returnDate = LocalDate.now();
            LocalDate dueDate = book.getReturnDate();
            
            if (returnDate.isAfter(dueDate.plusWeeks(1))) {
                String url = String.format("http://localhost:8080/api/subscribers/%d/freeze-status?status=Frozen&date=%s",
                    book.getMembershipNumber(), returnDate.toString());
                HttpRequest freezeReq = HttpRequest.newBuilder()
                        .uri(URI.create(url)).PUT(HttpRequest.BodyPublishers.noBody()).build();
                client.send(freezeReq, HttpResponse.BodyHandlers.ofString());
                addActivity(book, String.format("freeze_status,status changed from %s to %s ", FreezeStatus.NOT_FROZEN.getDbValue(), FreezeStatus.FROZEN.getDbValue()));
            }

            HttpRequest deleteReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/copy/" + book.getCopyOfBookId() + "/subscriber/" + book.getMembershipNumber()))
                    .DELETE().build();
            HttpResponse<String> deleteRes = client.send(deleteReq, HttpResponse.BodyHandlers.ofString());

            if (deleteRes.statusCode() == 200) {
                String activityDetails;
                if(returnDate.isAfter(dueDate)) {
                    activityDetails = String.format("%s,returned %s late by %s days", ActivityType.LATE_BOOK_RETURN.getDBValue(), book.getNameOfBook(), ChronoUnit.DAYS.between(dueDate, returnDate));
                } else {
                    activityDetails = String.format("returning,return %s ", book.getNameOfBook());
                }
                addActivity(book, activityDetails);
                showAlertSuccess("Return Successful", "The book has been successfully returned.");
                BorrowedBooksTable.getItems().remove(book);
            } else {
                showAlertError("Return Error", "The book has been deleted or could not be returned.");
                BorrowedBooksTable.getItems().remove(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public void handleReturnAction(ActionEvent event) throws Exception {
        LibrarianMainPageController view = new LibrarianMainPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }


    public void handleLogoutAction(ActionEvent event) throws Exception {
        // Handle logout logic
        LogoutUtil.handleLogoutButtonAction(event);

        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }


    private void addActivity(BorrowedBook book, String activityDetails) {
        try {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("membershipNumber", book.getMembershipNumber());
            reqMap.put("activityType", activityDetails.split(",")[0]);
            reqMap.put("description", activityDetails);

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/activities/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(reqMap))).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}