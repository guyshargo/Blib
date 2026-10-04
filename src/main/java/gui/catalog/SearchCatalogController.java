package gui.catalog;

import gui.auth.LogoutUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Book;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

public class SearchCatalogController {
    @FXML private TextField SearchBookByName;
    @FXML private TextField SearchBookBySub;
    @FXML private TextField SearchBookByText;
    @FXML private Button searchButton;
    @FXML private Text resultText;
    @FXML private TableView<Book> searchBookTable;
    @FXML private TableColumn<Book, String> bookName;
    @FXML private TableColumn<Book, String> bookSub;
    @FXML private TableColumn<Book, String> keywords;
    @FXML private TableColumn<Book, String> BookSummary;

    private final ObservableList<Book> bookData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        bookName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookName()));

        bookSub.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookSubject()));

        keywords.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKeywords()));

        BookSummary.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookSummary()));

        // Set the table's items to the observable list
        searchBookTable.setItems(bookData);
        searchBookTable.setOnMouseClicked(event -> handleBookClick());
    }

    public void start(Stage window) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/catalog/SearchCatalog.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            window.setTitle("Search Book Page");
            window.setScene(scene);
            window.show();
            LogoutUtil.addWindowCloseListener(window);
        } catch (IOException e) {
            e.printStackTrace();
            showAlertError("Loading Error", "Failed to load the search page.");
        }
    }

    private void handleBookClick() {
        Book selectedBook = searchBookTable.getSelectionModel().getSelectedItem();
        if(selectedBook == null) {
            showAlertError("No Selection", "Please select a book.");
            return;
        }
        try {
            Stage currentStage = (Stage) searchBookTable.getScene().getWindow();
            currentStage.hide();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/catalog/BookDetails.fxml"));
            Pane root = loader.load();

            BookDetailsController controller = loader.getController();
            controller.loadBooks(selectedBook);

            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/catalog/BookDetails.css").toExternalForm());

            primaryStage.setTitle("Book Details Page");
            primaryStage.setScene(scene);
            primaryStage.show();
            LogoutUtil.addWindowCloseListener(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchAction(ActionEvent event) throws Exception {
        String bookName = SearchBookByName.getText().trim();
        String bookSubject = SearchBookBySub.getText().trim();
        String freeText = SearchBookByText.getText().trim();
        if(bookName.isEmpty() || bookName==null) {
            bookName = "is empty";
        }
        if(bookSubject.isEmpty() || bookSubject==null) {
            bookSubject = "is empty";
        }
        if(freeText.isEmpty() || freeText==null) {
            freeText = "is empty";
        }
        initiateBookSearch(bookName, bookSubject, freeText);
    }

    private void initiateBookSearch(String bookName, String bookSubject, String freeText) {
        try {
            String url = String.format("http://localhost:8080/api/books/search?name=%s&subject=%s&freeText=%s", 
                    URLEncoder.encode(bookName, StandardCharsets.UTF_8),
                    URLEncoder.encode(bookSubject, StandardCharsets.UTF_8),
                    URLEncoder.encode(freeText, StandardCharsets.UTF_8));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                List<Book> books = mapper.readValue(response.body(), new TypeReference<List<Book>>(){});
                displaySearchResults(books);
            } else {
                displaySearchResults(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Network Error", "Could not connect to server.");
        }
    }

    private void displaySearchResults(List<Book> books) {
        bookData.clear();
        if (books != null && !books.isEmpty()) {
            bookData.addAll(books);
        } else {
            showAlertError("No Results", "No books match the search criteria.");
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
}