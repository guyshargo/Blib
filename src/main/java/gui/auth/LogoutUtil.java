package gui.auth;

import client.SessionManager;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;


public class LogoutUtil {

    public static void handleLogoutAction() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            if (SessionManager.currentLibrarian != null) {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/librarians/" + SessionManager.currentLibrarian.getLibrarianID() + "/login-status?status=false"))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            if (SessionManager.currentSubscriber != null) {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/subscribers/" + SessionManager.currentSubscriber.getMembershipNumber() + "/login-status?status=false"))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            SessionManager.clearSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addWindowCloseListener(Stage stage) {
    stage.setOnCloseRequest(event -> {
        try {
            handleLogoutAction(); 
            System.exit(0); // close the javaFX client application
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}

    public static void handleLogoutButtonAction(ActionEvent event) throws Exception {
        handleLogoutAction();
    }
}