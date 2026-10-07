package gui.subscriber;

import gui.common.MainLayoutController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Description:
 * Controller class for the member's main page
 */
public class MemberHomeController {
    @FXML public Button extendBorrowPageButton;
    @FXML public Button searchBookButton;
    @FXML public Button viewPersonalInfoButton;
    @FXML public Button orderPageButton;

    @FXML private HBox currentlyReadingShelf;
    @FXML private HBox topRatedShelf;
    @FXML private HBox latestArrivalsShelf;

    @FXML
    public void initialize() {
        fetchCatalog();
    }

    private void fetchCatalog() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/books/search?name=is%20empty&subject=is%20empty&freeText=is%20empty"))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::populateShelves)
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateShelves(String jsonBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<Book> catalog = mapper.readValue(jsonBody, new TypeReference<List<Book>>() {});

            // Update UI on the JavaFX main thread
            Platform.runLater(() -> {
                // Clear FXML placeholders
                if (currentlyReadingShelf != null) currentlyReadingShelf.getChildren().clear();
                if (topRatedShelf != null) topRatedShelf.getChildren().clear();
                if (latestArrivalsShelf != null) latestArrivalsShelf.getChildren().clear();

                // Generate cards and distribute them
                for (int i = 0; i < catalog.size(); i++) {
                    VBox bookCard = createBookCard(catalog.get(i));
                    
                    if (i % 2 == 0) {
                        if (topRatedShelf != null) topRatedShelf.getChildren().add(bookCard);
                    } else {
                        if (latestArrivalsShelf != null) latestArrivalsShelf.getChildren().add(bookCard);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchBookButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/catalog/SearchCatalog.fxml");
    }

    @FXML
    private void handleViewPersonalInfoButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/subscriber/PersonalInfo.fxml");
    }

    @FXML
    private void handleExtendBorrowPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/subscriber/ExtendBorrow.fxml");
    }

    @FXML
    private void handleOrderPageButton(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/subscriber/OrderBook.fxml");
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(5);
        card.getStyleClass().add("book-card-placeholder");
        card.setAlignment(Pos.CENTER);

        ImageView coverImage = new ImageView();
        try {
            String imagePath = "/gui/images/covers/" + book.getBookID() + ".jpg";
            var resource = getClass().getResource(imagePath);
            
            if (resource != null) {
                coverImage.setImage(new Image(resource.toExternalForm()));
            } else {
                coverImage.setImage(new Image(getClass().getResourceAsStream("/gui/images/book_logo.png")));
            }
        } catch (Exception e) {
            coverImage.setImage(new Image(getClass().getResourceAsStream("/gui/images/book_logo.png")));
        }
        
        coverImage.setFitHeight(180);
        coverImage.setFitWidth(120);
        coverImage.setPreserveRatio(false);

        Label titleLabel = new Label(book.getBookName());
        titleLabel.setStyle("-fx-font-family: 'Assistant SemiBold'; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);

        Label authorLabel = new Label(book.getAuthor());
        authorLabel.setStyle("-fx-font-family: 'Assistant'; -fx-font-size: 12px; -fx-text-fill: #777;");

        card.getChildren().addAll(coverImage, titleLabel, authorLabel);
        return card;
    }
}