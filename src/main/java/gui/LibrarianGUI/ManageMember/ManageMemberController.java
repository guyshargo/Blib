package gui.LibrarianGUI.ManageMember;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogoutUtil;
import gui.LibrarianGUI.ViewAndEditMember.ViewMemberPageController;
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
import logic.Subscriber;

import java.util.List;
import java.util.Optional;

/**
 * Description:
 * Controller class for the librarian to view and update members in the library
 */
public class ManageMemberController {
    @FXML
    private TableView<Subscriber> subscriberTable;

    @FXML
    private TableColumn<Subscriber, Integer> membershipNumberColumn;

    @FXML
    private TableColumn<Subscriber, String> nameColumn;

    @FXML
    private TextField membershipNumberSearchField;

    @FXML
    private Button logoutButton;

    @FXML
    private Button returnBtn;
    @FXML
    private Button scanReaderCard;

    @FXML
    private Label librarianName;

    private final ObservableList<Subscriber> subscribers = FXCollections.observableArrayList();

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        // Configure table columns with updated names
        membershipNumberColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMembershipNumber()).asObject());

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMemberFullName())); // Updated to use getMemberFullName

        // Populate the table with initial data
        subscriberTable.setItems(subscribers);

        // Add search functionality
        membershipNumberSearchField.textProperty().addListener((observable, oldValue, newValue) -> filterSubscribers(newValue));

        // Load initial data
        loadSubscribers();

        // Add on-click listener for TableView rows
        subscriberTable.setOnMouseClicked(event -> handleSubscriberClick());
        librarianName.setText(ChatClient.librarianLogin.getFullName());
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianGUI/ManageMember/ManageMember.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/ManageMember/ManageMember.css").toExternalForm());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/HeaderImage/book_logo.png")));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Manage Member");
        LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
    }

    /**
     * Description:
     * Method for checking if the librarian clicked on a subscriber in the subscribers table
     */
    void handleSubscriberClick() {
        // Get the selected subscriber from the table
        Subscriber selectedSubscriber = subscriberTable.getSelectionModel().getSelectedItem();
        if (selectedSubscriber == null) {
            showAlertError("No Selection", "Please select a subscriber to update.");
            return;
        }
        loadSubscriber(selectedSubscriber);
    }

    /**
     * Description:
     * Method for loading a subscriber into the text fields based on the selected subscriber
     *
     * @param selectedSubscriber Subscriber.class
     */
    private void loadSubscriber(Subscriber selectedSubscriber) {
        try {
            // Hide the current window
            Stage currentStage = (Stage) subscriberTable.getScene().getWindow();

            // Load the SubscriberUpdateFrame
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianGUI/ViewAndEditMember/ViewMemberPage.fxml"));
            Pane root = loader.load();

            // Pass the selected subscriber to the update frame controller
            ViewMemberPageController controller = loader.getController();
            controller.loadSubscriber(selectedSubscriber);

            // Set up the new stage
            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/ViewAndEditMember/ViewMemberPage.css").toExternalForm());
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/HeaderImage/book_logo.png")));
            primaryStage.setTitle("View Member Page");
            primaryStage.setScene(scene);
            primaryStage.show();
            LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
            currentStage.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method for loading all subscribers in the library into the subscribers table
     */
    private void loadSubscribers() {
        // Fetch data from the server
        String command = "FetchAllSubscribers:Program";
        ClientUI.chat.accept(command); // Send the request to the server

        if (ChatClient.subList != null) {
            updateSubscribersList(ChatClient.subList);
        } else {
            showAlertError("No Subscribers Found", "No subscribers were found in the database.");
        }
    }

    /**
     * Description:
     * Method for filtering subscribers based on given criteria
     *
     * @param query String.class
     */
    private void filterSubscribers(String query) {
        if (query == null || query.isEmpty()) {
            subscriberTable.setItems(subscribers);
        } else {
            ObservableList<Subscriber> filteredList = FXCollections.observableArrayList();
            for (Subscriber subscriber : subscribers) {
                String membershipNum = String.valueOf(subscriber.getMembershipNumber());
                String memberName = subscriber.getMemberFullName().toLowerCase();
                if (membershipNum.contains(query) || memberName.contains(query.toLowerCase())) {
                    filteredList.add(subscriber);
                }
            }
            subscriberTable.setItems(filteredList);
        }
    }

    /**
     * Description:
     * Method for updating the subscribers list with given list
     *
     * @param newSubscribers List<Subscriber>.class
     */
    public void updateSubscribersList(List<Subscriber> newSubscribers) {
        subscribers.clear();
        if (newSubscribers != null)
            subscribers.addAll(newSubscribers);
        subscriberTable.setItems(subscribers);
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
     * Method for scanning a barcode to show the member's reader card
     *
     * @param event ActionEvent.class
     */
    @FXML
    private void scanReaderCard(ActionEvent event) {
        // Create a dialog box to ask the user to enter a barcode
        TextInputDialog barcodeDialog = new TextInputDialog();
        barcodeDialog.setTitle("Scan ReaderCard Barcode");
        barcodeDialog.setHeaderText("Please enter the barcode to scan:");
        barcodeDialog.setContentText("Barcode:");
        barcodeDialog.getDialogPane().getStylesheets().add(getClass().getResource("/gui/dialog.css").toExternalForm());
        barcodeDialog.getDialogPane().getStylesheets().add("custom-alert");

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

    /**
     * Description:
     * Method for showing a subscriber's information based on given barcode
     *
     * @param enteredBarcode String.class
     */
    private void findSubscriberByBarcode(String enteredBarcode) {
        String command = "ScanReaderCard:" + enteredBarcode;
        ClientUI.chat.accept(command); // Send the request to the server
        if (ChatClient.sub != null) {
            loadSubscriber(ChatClient.sub);
        } else {
            showAlertError("No Members Found", "No member found with barcode.");
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

    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}