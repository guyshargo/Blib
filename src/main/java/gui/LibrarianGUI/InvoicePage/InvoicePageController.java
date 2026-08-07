package gui.LibrarianGUI.InvoicePage;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogoutUtil;
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
import logic.InvoiceControl.InvoiceMessage;
import logic.Librarian;

import java.util.List;

/**
 * Description:
 * Controller class for the librarian's invoice messages
 */
public class InvoicePageController {
    // Changeable list to change the table of invoice messages
    private final ObservableList<InvoiceMessage> invoice = FXCollections.observableArrayList();

    // Sender name of the selected message
    @FXML
    private TextField fromTxt;

    // Description of the selected message
    @FXML
    private TextArea descriptionTxt;

    // Subject of the selected message
    @FXML
    private TextField subjectTxt;

    // Date of the selected message
    @FXML
    private TextField dateTxt;

    // Radio button to filter unread messages
    @FXML
    private RadioButton unreadRadioBtn;

    // Table of invoice messages send to the librarian
    @FXML
    private TableView<InvoiceMessage> invoiceTable;

    // Subject colum of each message in invoice
    @FXML
    private TableColumn<InvoiceMessage, String> subjectColumn;

    // Username colum of each mesaage in invoice
    @FXML
    private TableColumn<InvoiceMessage, String> usernameColumn;

    // Name colum of each message in invoice
    @FXML
    private TableColumn<InvoiceMessage, String> nameColumn;

    // Date colum of each message in invoice
    @FXML
    private TableColumn<InvoiceMessage, String> dateColumn;

    @FXML
    private Button logoutButton;

    @FXML
    private Label librarianName;

    private Librarian librarian = null;

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {

        this.librarian = ChatClient.librarianLogin;

        librarianName.setText(librarian.getFullName());

        // Colum of message subject -> initialized based on subjectColumn
        subjectColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSubject().toString()));

        // Colum of message username -> initialized based on usernameColumn
        usernameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));

        // Colum of message name -> initialized based on nameColumn
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        // Colum of message date -> initialized based on dateColumn
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMessageDate().toString()));

        // Table of all invoice messages
        invoiceTable.setItems(invoice);

        // Listener for the unread messages filter
        unreadRadioBtn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                filterUnreadMessages();
            } else {
                invoiceTable.setItems(invoice); // Reset table to show all messages
            }
        });

        // Overdue borrowed books marked red
        invoiceTable.setRowFactory(tv -> new TableRow<InvoiceMessage>() {
            @Override
            protected void updateItem(InvoiceMessage item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || item.getIsRead() == null) {
                    setStyle("");
                } else if (item.getIsRead().getValue() == false) {
                    setStyle("-fx-background-color: #ADD8E6;");
                } else {
                    setStyle("");
                }
            }
        });

        // Load the invoice messages
        loadMessages();
        invoiceTable.setOnMouseClicked(event -> handleInvoiceClick());
    }

    /**
     * Description:
     * Method for saving a row from the messages table, when clicked on
     */
    void handleInvoiceClick() {
        // Selected row in the table
        InvoiceMessage selectedMessage = invoiceTable.getSelectionModel().getSelectedItem();

        if (selectedMessage == null)
            showAlertError("Invoice Error", "Must click on a message in the invoice table");
        else {
            fromTxt.setText(selectedMessage.getName());
            descriptionTxt.setText(selectedMessage.getContent());
            subjectTxt.setText(selectedMessage.getSubject().toString());
            dateTxt.setText(selectedMessage.getMessageDate().toString());

            if (selectedMessage.getIsRead().getValue() == false) {
                String command = "ChangeReadStatus:" + selectedMessage.getMessageID();
                ClientUI.chat.accept(command);
                loadMessages();
                if (unreadRadioBtn.isSelected())
                    filterUnreadMessages();

            }
        }
    }

    /**
     * Description:
     * Method for loading all invoice messages into the invoice table
     */
    private void loadMessages() {
        String command = "ImportInvoiceMessages:" + "null";
        ClientUI.chat.accept(command);
        if (ChatClient.invoiceList != null)
            updateInvoiceList(ChatClient.invoiceList);
    }

    /**
     * Description:
     * Method for changing the invoiceTable based on messages fetched invoice_db
     *
     * @param invoiceList List<InvoiceMessage>.class
     */
    public void updateInvoiceList(List<InvoiceMessage> invoiceList) {
        invoice.clear();

        // Case the invoice is not empty
        if (invoiceList != null)
            invoice.addAll(invoiceList);

        // Add the messages to the invoice table
        invoiceTable.setItems(invoice);
    }

    /**
     * Description:
     * Method for filtering overdue borrowed books when clicking overdueRadioBtn
     */
    private void filterUnreadMessages() {
        ObservableList<InvoiceMessage> unreadMessages = FXCollections.observableArrayList();

        for (InvoiceMessage message : invoice)
            // filter messages that are unread
            if (message.getIsRead().getValue() == false)
                unreadMessages.add(message);
        invoiceTable.setItems(unreadMessages);
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
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianGUI/InvoicePage/InvoicePage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/InvoicePage/InvoicePage.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("My messages");

        // Register window close listener for logout
        LogoutUtil.addWindowCloseListener(primaryStage);
    }
    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}
