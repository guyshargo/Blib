package gui.auth;

import gui.search.SearchPageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Description:
 * Controller class for the login window
 */
public class LoginController {

    @FXML private Button btnLibrarian;
    @FXML private Button btnMember;
    @FXML private Button guestBtn;

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/login/MainLogin.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/login/MainLogin.css").toExternalForm());
        primaryStage.setTitle("Log In");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/images/book_logo.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
        LogoutUtil.addWindowCloseListener(primaryStage);
    }

    /**
     * Description:
     * Method for showing the member login window
     *
     * @param event ActionEvent.class
     * @return return_param
     * @throws Exception (when loading the scene)
     */
    public void getMemberBtn(ActionEvent event) throws Exception {
        // Load the FXML for the Member Login window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/auth/MemberLogin.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/auth/MainLogin.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Member Login");
        stage.show();
    }

    /**
     * Description:
     * Method for showing the librarian login window
     *
     * @param event ActionEvent.class
     * @return return_param
     * @throws Exception (when loading the scene)
     */
    public void getLibrarianBtn(ActionEvent event) throws Exception {
        // Load the FXML for the Librarian Login window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/auth/LibrarianLogin.fxml"));
        Parent root = loader.load();

        // Set up the scene and stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/auth/MainLogin.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Librarian Login");
        stage.show();
    }

    /**
     * Description:
     * Method for returning to the search page
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    public void getGuestBtn(ActionEvent event) throws Exception {
        SearchPageController view = new SearchPageController();
        view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
    }
}