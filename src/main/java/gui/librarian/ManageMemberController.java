package gui.librarian;

import client.SessionManager;
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
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Subscriber;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;



public class ManageMemberController {
    @FXML private TableView<Subscriber> subscriberTable;
    @FXML private TableColumn<Subscriber, Integer> membershipNumberColumn;
    @FXML private TableColumn<Subscriber, String> nameColumn;
    @FXML private TextField membershipNumberSearchField;
    @FXML private Button logoutButton;
    @FXML private Button returnBtn;
    @FXML private Button scanReaderCard;
    @FXML private Label librarianName;

    private final ObservableList<Subscriber> subscribers = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Configure table columns with updated names
        membershipNumberColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMembershipNumber()).asObject());

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFullName())); // Updated to use getMemberFullName

        // Populate the table with initial data
        subscriberTable.setItems(subscribers);

        // Add search functionality
        membershipNumberSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterSubscribers(newValue));

        // Load initial data
        loadSubscribers();

        // Add on-click listener for TableView rows
        subscriberTable.setOnMouseClicked(event -> handleSubscriberClick());
        librarianName.setText(SessionManager.currentLibrarian.getFullName());
    }


    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/librarian/ManageMember.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/librarian/ManageMember.css").toExternalForm());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/images/book_logo.png")));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manage Member");
        LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
    }


    void handleSubscriberClick() {
        // Get the selected subscriber from the table
        Subscriber selectedSubscriber = subscriberTable.getSelectionModel().getSelectedItem();
        if (selectedSubscriber == null) {
            showAlertError("No Selection", "Please select a subscriber to update.");
            return;
        }
        loadSubscriber(selectedSubscriber);
    }


    private void loadSubscriber(Subscriber selectedSubscriber) {
        try {
            // Hide the current window
            Stage currentStage = (Stage) subscriberTable.getScene().getWindow();

            // Load the SubscriberUpdateFrame
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/librarian/ViewMember.fxml"));
            Pane root = loader.load();

            // Pass the selected subscriber to the update frame controller
            ViewMemberController controller = loader.getController();
            controller.loadSubscriber(selectedSubscriber);

            // Set up the new stage
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/librarian/ViewMember.css").toExternalForm());
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/images/book_logo.png")));
            primaryStage.setTitle("View Member Page");
            primaryStage.setScene(scene);
            primaryStage.show();
            LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadSubscribers() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/all"))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<Subscriber> subList = mapper.readValue(res.body(), new TypeReference<List<Subscriber>>(){});
                updateSubscribersList(subList);
            } else {
                showAlertError("No Subscribers Found", "No subscribers were found in the database.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void filterSubscribers(String query) {
        if (query == null || query.isEmpty()) {
            subscriberTable.setItems(subscribers);
        } else {
            ObservableList<Subscriber> filteredList = FXCollections.observableArrayList();
            for (Subscriber subscriber : subscribers) {
                String membershipNum = String.valueOf(subscriber.getMembershipNumber());
                String memberName = subscriber.getFullName().toLowerCase();
                if (membershipNum.contains(query) || memberName.contains(query.toLowerCase())) {
                    filteredList.add(subscriber);
                }
            }
            subscriberTable.setItems(filteredList);
        }
    }


    public void updateSubscribersList(List<Subscriber> newSubscribers) {
        subscribers.clear();
        if (newSubscribers != null)
            subscribers.addAll(newSubscribers);
        subscriberTable.setItems(subscribers);
    }

    public void getReturnBtn(ActionEvent event) throws Exception {
        LibrarianDashboardController view = new LibrarianDashboardController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }


    @FXML
    private void scanReaderCard(ActionEvent event) {
        // Create a dialog box to ask the user to enter a barcode
        TextInputDialog barcodeDialog = new TextInputDialog();
        barcodeDialog.setTitle("Scan ReaderCard Barcode");
        barcodeDialog.setHeaderText("Please enter the barcode to scan:");
        barcodeDialog.setContentText("Barcode:");
        barcodeDialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/common/Dialog.css").toExternalForm());
        barcodeDialog.getDialogPane().getStyleClass().add("custom-alert");

        // Show the dialog and wait for the user input
        Optional<String> result = barcodeDialog.showAndWait();

        // If the user clicks "Cancel," exit without doing anything
        if (!result.isPresent()) {
            return; // Exit the method
        }
        // If the user entered a barcode (not empty)
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String enteredBarcode = result.get().trim();

            // Now use the entered barcode to search for the available copy of the book
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


    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(message);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/Alert.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }

    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}