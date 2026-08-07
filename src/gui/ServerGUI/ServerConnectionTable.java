package gui.ServerGUI;

import Server.ServerUI;
import gui.LogInGUI.LogoutUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.ClientInfo;
import ocsf.server.ConnectionToClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Class for the connections made to the server
 */
public class ServerConnectionTable {
    @FXML
    private TableView<ClientInfo> connectionTable;

    @FXML
    private TableColumn<ClientInfo, String> ipColumn;

    @FXML
    private TableColumn<ClientInfo, String> hostColumn;

    @FXML
    private TableColumn<ClientInfo, String> statusColumn;

    @FXML
    private Button btnRunServer;

    @FXML
    private Button btnShutDownServer;

    private static ObservableList<ClientInfo> clientData = FXCollections.observableArrayList();

    private static final String DEFAULT_PORT = "5555"; // Define port number

    // List to store active client connections
    private static List<ConnectionToClient> clientConnections = new ArrayList<>();

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        btnShutDownServer.setDisable(true);

        // Set up the columns to bind to the String fields in ClientInfo
        ipColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIp()));
        hostColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHost()));
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus()));
        connectionTable.setItems(clientData);
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerGUI/ServerConnectionTable.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerGUI/ServerConnectionTable.css").toExternalForm());
        primaryStage.setTitle("Server Connection");
        primaryStage.setScene(scene);
        primaryStage.show();
        LogoutUtil.addWindowCloseListenerAndExitServer(primaryStage, clientConnections);
    }

    /**
     * Description:
     * Method for running the server when pressing the 'Run Server' button
     *
     * @param actionEvent ActionEvent.class
     */
    @FXML
    public void runServer(ActionEvent actionEvent) {
        ServerUI.runServer(DEFAULT_PORT);
        btnRunServer.setDisable(true);
        btnShutDownServer.setDisable(false);
    }

    /**
     * Description:
     * Method for shutting down the server when pressing the 'Shut Down Server' button
     *
     * @param actionEvent ActionEvent.class
     */
    @FXML
    public void shutDownServer(ActionEvent actionEvent) {
        LogoutUtil.exitServer(clientConnections);
    }

    /**
     * Description:
     * Method for adding a client to the table
     *
     * @param clientInfo ClientInfo.class
     * @param client     ConnectionToClient.class
     */
    public static void addClientToTable(ClientInfo clientInfo, ConnectionToClient client) {
        // Using Platform.runLater to ensure the update is done on the JavaFX application thread
        Platform.runLater(() -> {
            try {
                clientData.add(clientInfo);
                clientConnections.add(client);
            } catch (Exception e) {
                System.out.println("Error adding client: " + e);
            }
        });
    }

    /**
     * Description:
     * Method for removing a client from the table
     *
     * @param clientInfo ClientInfo.class
     * @param client     ConnectionToClientConnectionToClient.class
     */
    public static void removeClientFromTable(ClientInfo clientInfo, ConnectionToClient client) {
        Platform.runLater(() -> {
            clientData.remove(clientInfo);
            clientConnections.remove(client);
        });
    }
}