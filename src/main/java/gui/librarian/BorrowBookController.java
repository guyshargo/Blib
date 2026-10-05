package gui.librarian;

import client.SessionManager;
import model.Book;
import model.BorrowedBook;
import model.CopyOfBook;
import enums.FreezeStatus;
import gui.common.MainLayoutController;
import model.Subscriber;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class BorrowBookController {
    @FXML private TextField bookIdField;
    @FXML private TextField memberFullNameField;
    @FXML private TextField membershipNumberField;
    @FXML private TextField bookNameField;
    @FXML private TextField copyTxt;
    @FXML private Button scanBookButton;
    @FXML private Button findBookButton;
    @FXML private Button commitBorrowButton;
    @FXML private Label librarianName;

    // State tracking variables
    private boolean isBookFound = false;
    private boolean isMemberFound = false;
    private Subscriber subscriber;
    private CopyOfBook copyOfBook;


    @FXML
    public void initialize() {
        librarianName.setText(SessionManager.currentLibrarian.getFullName());
    }

    @FXML
    private void handleFindBook() {
        String bookID = bookIdField.getText();

        if (bookID == null || bookID.trim().isEmpty()) {
            showAlertError("Error", "Book Name cannot be empty.");  
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            HttpRequest availableReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/book/" + bookID + "/available-copy/subscriber/" + subscriber.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> availableRes = client.send(availableReq, HttpResponse.BodyHandlers.ofString());

            if (availableRes.statusCode() == 200) {
                copyOfBook = mapper.readValue(availableRes.body(), CopyOfBook.class);
                bookNameField.setText(copyOfBook.getCopyOfBookName());  
                isBookFound = true;

                HttpRequest bookReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/books/" + copyOfBook.getBookId()))
                        .GET().build();
                HttpResponse<String> bookRes = client.send(bookReq, HttpResponse.BodyHandlers.ofString());
                
                if(bookRes.statusCode() == 200) {
                    Book foundBook = mapper.readValue(bookRes.body(), Book.class);
                    int availableCopies = foundBook.getNumberOfCopies() - foundBook.getNumberOfBorrowedCopies();
                    copyTxt.setText(String.valueOf(availableCopies));
                }
            } else {
                showAlertError("Error", "No available copy of the book found.");
                isBookFound = false;
            }
            updateCommitBorrowButtonState();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        barcodeDialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        barcodeDialog.getDialogPane().getStyleClass().addAll("custom-alert", "alert-info");

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


    public void getReturnBtn(ActionEvent event) throws Exception {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/LibrarianDashboard.fxml");
    }

    

    private void findAvailableCopyOfBookByBarcode(String barcode) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/barcode/" + barcode + "/available-copy/subscriber/" + subscriber.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                copyOfBook = mapper.readValue(res.body(), CopyOfBook.class);
                bookIdField.setText(String.valueOf(copyOfBook.getBookId()));  
                bookNameField.setText(copyOfBook.getCopyOfBookName());  
                isBookFound = true;
            } else {
                showAlertError("Error", "The copy is not available.");
                bookNameField.setText("");  
                isBookFound = false;
            }
            updateCommitBorrowButtonState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void scanReaderCard(ActionEvent event) {
        TextInputDialog barcodeDialog = new TextInputDialog();  // Create input dialog for barcode
        barcodeDialog.setTitle("Scan ReaderCard Barcode");
        barcodeDialog.setHeaderText("Please enter the barcode to scan:");
        barcodeDialog.setContentText("Barcode:");
        barcodeDialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        barcodeDialog.getDialogPane().getStyleClass().addAll("custom-alert","alert-info");

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


    private void findSubscriberByBarcode(String enteredBarcode) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/barcode/" + enteredBarcode))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                Subscriber sub = mapper.readValue(res.body(), Subscriber.class);
                loadSubscriber(sub);
            } else {
                showAlertError("No Members Found", "No member found with barcode.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 
    @FXML
    private void handleFindMember() {
        String membershipNumberText = membershipNumberField.getText();  
        if (membershipNumberText == null || membershipNumberText.trim().isEmpty()) {
            showAlertError("Error", "Membership Number cannot be empty."); return;
        }
        if (!membershipNumberText.matches("\\d+")) {
            showAlertError("Invalid Id", "The ID must contain only digits."); return;
        } 
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/" + membershipNumberText))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                Subscriber sub = mapper.readValue(res.body(), Subscriber.class);
                loadSubscriber(sub);
            } else {
                loadSubscriber(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void loadSubscriber(Subscriber sub) {
        subscriber = sub;

        // Handle if no subscriber is found
        if (sub == null) {
            showAlertError("Member Not Found", "No member found with the given ID.");
            return;
        }

        // Handle if the subscriber's status is frozen
        else if (sub.getFreezeStatus() == FreezeStatus.FROZEN) {
            showAlertError("Member Is Frozen", "Member Status Is Frozen.");
            return;
        }

        // Display subscriber details in the UI
        membershipNumberField.setText(String.valueOf(subscriber.getMembershipNumber()));
        memberFullNameField.setText(subscriber.getFullName());
        isMemberFound = true;
        findBookButton.setDisable(false);
        scanBookButton.setDisable(false);
        bookIdField.setDisable(false);
    }

 
    private void updateCommitBorrowButtonState() {
        commitBorrowButton.setDisable(!(isBookFound && isMemberFound));
    }

  

    @FXML
    private void handleCommitBorrow() {
        int librarianID = SessionManager.currentLibrarian != null ? SessionManager.currentLibrarian.getLibrarianID() : 101;
        String librarianName = SessionManager.currentLibrarian != null ? SessionManager.currentLibrarian.getFullName() : "Lucy";

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            HttpRequest checkReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/subscriber/" + subscriber.getMembershipNumber()))
                    .GET().build();
            HttpResponse<String> checkRes = client.send(checkReq, HttpResponse.BodyHandlers.ofString());

            if (checkRes.statusCode() == 200) {
                List<BorrowedBook> borrowedBooksList = mapper.readValue(checkRes.body(), new TypeReference<List<BorrowedBook>>(){});
                LocalDate current = LocalDate.now();
                StringBuilder borrowString = new StringBuilder();
                for (BorrowedBook book : borrowedBooksList) {
                    if (book.getReturnDate().isBefore(current)) {
                        borrowString.append(String.format("\"%s\",", book.getNameOfBook()));
                    }
                }
                if (borrowString.length() > 0) {
                    borrowString.deleteCharAt(borrowString.length() - 1);
                    showAlertError("Book Return Date", "The following borrowed books haven't been returned in time:\n" + borrowString + "\nPlease contact the library.");
                    return;
                }
            }

            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("member_id", subscriber.getMembershipNumber());
            reqMap.put("copy_id", copyOfBook.getCopyOfBookId());
            reqMap.put("librarian_id", librarianID);
            reqMap.put("librarian_name", librarianName);

            HttpRequest borrowReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/borrows/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(reqMap)))
                    .build();
            
            HttpResponse<String> borrowRes = client.send(borrowReq, HttpResponse.BodyHandlers.ofString());

            if (borrowRes.statusCode() == 200) {
                BorrowedBook borrowedBook = mapper.readValue(borrowRes.body(), BorrowedBook.class);
                showAlertSuccess("Borrow Successful", "Book Borrowed successfully.");
                addActivity(borrowedBook);
                MainLayoutController.getInstance().loadCenterView("/gui/librarian/BorrowBook.fxml");
            } else {
                showAlertError("Borrow Unsuccessful", borrowRes.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addActivity(BorrowedBook borrowedBook) {
        try {
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("member_id", subscriber.getMembershipNumber());
            reqMap.put("activity_type", "borrow");
            reqMap.put("description", String.format("borrowed %s ", borrowedBook.getNameOfBook()));

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/activities/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(reqMap))).build();
            
            client.send(request, HttpResponse.BodyHandlers.ofString());

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
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-info");

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
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-success");
        alert.showAndWait();
    }

}