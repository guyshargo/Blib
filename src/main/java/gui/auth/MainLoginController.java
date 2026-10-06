package gui.auth;

import gui.common.MainLayoutController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class MainLoginController {

    @FXML private Button btnLibrarian;
    @FXML private Button btnMember;
    @FXML private Button guestBtn;


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


    public void getGuestBtn(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/common/MainLayout.fxml"));
        Parent root = loader.load();

        MainLayoutController controller = loader.getController();
        controller.loadCenterView("/gui/subscriber/MemberHome.fxml");;

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Guest - Browse Catalog");
        stage.show();
    }
}