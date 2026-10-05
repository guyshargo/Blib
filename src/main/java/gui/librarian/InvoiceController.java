package gui.librarian;

import client.SessionManager;
import gui.common.MainLayoutController;
import model.InvoiceMessage;
import model.Librarian;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

import java.util.List;

/**
 * Description:
 * Controller class for the librarian's invoice messages
 */
public class InvoiceController {
    // Changeable list to change the table of invoice messages
    private final ObservableList<InvoiceMessage> invoice = FXCollections.observableArrayList();

    @FXML private TextField fromTxt;
    @FXML private TextArea descriptionTxt;
    @FXML private TextField subjectTxt;
    @FXML private TextField dateTxt;
    @FXML private RadioButton unreadRadioBtn;
    @FXML private TableView<InvoiceMessage> invoiceTable;
    @FXML private TableColumn<InvoiceMessage, String> subjectColumn;
    @FXML private TableColumn<InvoiceMessage, String> usernameColumn;
    @FXML private TableColumn<InvoiceMessage, String> nameColumn;
    @FXML private TableColumn<InvoiceMessage, String> dateColumn;
    @FXML private Label librarianName;

    private Librarian librarian = null;


    @FXML
    public void initialize() {

        this.librarian = SessionManager.currentLibrarian;

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

 
    void handleInvoiceClick() {
        InvoiceMessage selectedMessage = invoiceTable.getSelectionModel().getSelectedItem();

        if (selectedMessage == null) {
            showAlertError("Invoice Error", "Must click on a message in the invoice table");
        } else {
            fromTxt.setText(selectedMessage.getName());
            descriptionTxt.setText(selectedMessage.getContent());
            subjectTxt.setText(selectedMessage.getSubject().toString());
            dateTxt.setText(selectedMessage.getMessageDate().toString());

            if (selectedMessage.getIsRead().getValue() == false) {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/api/invoices/" + selectedMessage.getMessageID() + "/read-status"))
                            .PUT(HttpRequest.BodyPublishers.noBody()).build();
                    HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

                    if (res.statusCode() == 200) {
                        loadMessages();
                        if (unreadRadioBtn.isSelected()) filterUnreadMessages();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void loadMessages() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/invoices/"))
                    .GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                List<InvoiceMessage> invoiceList = mapper.readValue(res.body(), new TypeReference<List<InvoiceMessage>>(){});
                updateInvoiceList(invoiceList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void updateInvoiceList(List<InvoiceMessage> invoiceList) {
        invoice.clear();

        // Case the invoice is not empty
        if (invoiceList != null)
            invoice.addAll(invoiceList);

        // Add the messages to the invoice table
        invoiceTable.setItems(invoice);
    }


    private void filterUnreadMessages() {
        ObservableList<InvoiceMessage> unreadMessages = FXCollections.observableArrayList();

        for (InvoiceMessage message : invoice)
            // filter messages that are unread
            if (message.getIsRead().getValue() == false)
                unreadMessages.add(message);
        invoiceTable.setItems(unreadMessages);
    }


    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error!");
        alert.setContentText(message);

        // Get the DialogPane of the alert
        DialogPane dialogPane = alert.getDialogPane();

        // Apply custom CSS file
        dialogPane.getStylesheets().add(getClass().getResource("/gui/common/SharedAlerts.css").toExternalForm());
        dialogPane.getStyleClass().addAll("custom-alert", "alert-error");

        alert.showAndWait();
    }


    public void getReturnBtn(ActionEvent event) throws Exception {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/LibrarianDashboard.fxml");
    }

}
