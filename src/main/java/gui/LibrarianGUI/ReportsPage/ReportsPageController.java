package gui.LibrarianGUI.ReportsPage;

import client.ChatClient;
import client.ClientUI;
import gui.LibrarianGUI.LibrarianMainPageController;
import gui.LogInGUI.LogoutUtil;
import javafx.application.Platform;
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
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logic.FreezeStatus;
import logic.ReportControl.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Description:
 * Controller class for showing reports made by the system for the librarian
 */
public class ReportsPageController {
    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private ComboBox<String> reportTypeComboBox;

    @FXML
    private TableView<MemberStatusChange> memberStatusTable;

    @FXML
    private TableView<BorrowHistory> borrowHistoryTable;

    @FXML
    private TableColumn<MemberStatusChange, Integer> membershipNumberColumn;

    @FXML
    private TableColumn<MemberStatusChange, String> memberNameColumn;

    @FXML
    private TableColumn<MemberStatusChange, String> freezeStatusColumn;

    @FXML
    private TableColumn<MemberStatusChange, String> savedStatusDateColumn;

    @FXML
    private TableColumn<BorrowHistory, Integer> borrowMembershipNumberColumn;

    @FXML
    private TableColumn<BorrowHistory, String> borrowMemberNameColumn;

    @FXML
    private TableColumn<BorrowHistory, String> bookNameColumn;

    @FXML
    private TableColumn<BorrowHistory, String> borrowDateColumn;

    @FXML
    private TableColumn<BorrowHistory, String> originalReturnDateColumn;

    @FXML
    private TableColumn<BorrowHistory, String> actualReturnDateColumn;

    @FXML
    private TableColumn<BorrowHistory, Integer> lateDaysColumn;

    @FXML
    private Button generateReportButton;

    @FXML
    private BarChart<String, Number> freezeStatusBarChart;

    @FXML
    private BarChart<String, Number> borrowDatesBarChart;

    @FXML
    private CategoryAxis daysAxis;

    @FXML
    private Button resizeButton;  // Button to trigger resizing

    @FXML
    private Label librarianName;
    // Sample data for demonstration, replace with real data handling
    private ObservableList<MemberStatusChange> memberStatusData;

    private ObservableList<BorrowHistory> borrowHistoryData;

    /**
     * Description:
     * Method for initializing the given window before it starts
     */
    @FXML
    public void initialize() {
        // Clear combo boxes in case of re-initialization
        monthComboBox.getItems().clear();
        yearComboBox.getItems().clear();
        freezeStatusBarChart.setVisible(false);
        borrowDatesBarChart.setVisible(false);
        librarianName.setText(ChatClient.librarianLogin.getFullName());

        // Fetch available months and years from the database
        try {
            String command = ("fetchAvailableYearsAndMonths:" + "demoParameter");
            ClientUI.chat.accept(command);

            if (ChatClient.yearsList != null && !ChatClient.yearsList.isEmpty()) {
                // Populate the yearComboBox with available years
                for (int year : ChatClient.yearsList) {
                    yearComboBox.getItems().add(String.valueOf(year));
                }
            } else {
                showAlertError("Error", "Failed to load available years.");
            }

            if (ChatClient.monthsList != null && !ChatClient.monthsList.isEmpty()) {
                // Populate the monthComboBox with available months
                for (int month : ChatClient.monthsList) {
                    monthComboBox.getItems().add(monthNameFromNumber(month));
                }
            } else {
                showAlertError("Error", "Failed to load available months.");
            }
        } catch (Exception e) {
            showAlertError("Initialization Error", "Failed to load available months and years.");
            e.printStackTrace();
        }
        reportTypeComboBox.getItems().addAll("Borrowed Books Report", "members Status Report");
        monthComboBox.setValue(null);
        yearComboBox.setValue(null);

        // Initialize TableViews with empty data
        memberStatusData = FXCollections.observableArrayList();
        borrowHistoryData = FXCollections.observableArrayList();

        memberStatusTable.setItems(memberStatusData);
        borrowHistoryTable.setItems(borrowHistoryData);

        // Initially hide both tables
        memberStatusTable.setVisible(false);
        borrowHistoryTable.setVisible(false);

        // Set up columns for Member Status TableView
        membershipNumberColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMemberId()).asObject());

        memberNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMemberName())); // Updated to use getMemberFullName

        freezeStatusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMemberStatus().toString())); // Updated to use getMemberFullName

        savedStatusDateColumn.setCellValueFactory(cellData -> {
            LocalDate actualReturnDate = cellData.getValue().getChangeStatusDate();
            // Check if the actualReturnDate is null and handle it accordingly
            String returnDateStr = (actualReturnDate == null) ? "----" : actualReturnDate.toString();
            return new SimpleStringProperty(returnDateStr);
        });

        borrowMembershipNumberColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMemberId()).asObject());
        borrowMemberNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMemberName()));
        bookNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBookName()));
        borrowDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBorrowDate().toString()));
        originalReturnDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOriginalReturnDate().toString()));
        actualReturnDateColumn.setCellValueFactory(cellData -> {
            LocalDate actualReturnDate = cellData.getValue().getActualReturnDate();
            // Check if the actualReturnDate is null and handle it accordingly
            String returnDateStr = (actualReturnDate == null) ? "----" : actualReturnDate.toString();
            return new SimpleStringProperty(returnDateStr);
        });

        lateDaysColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getLateDays()).asObject());
        memberStatusTable.setRowFactory(tv -> new TableRow<MemberStatusChange>() {
            @Override
            protected void updateItem(MemberStatusChange item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setStyle("");
                } else if (item.getMemberStatus() == FreezeStatus.FROZEN) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
        borrowHistoryTable.setRowFactory(tv -> new TableRow<BorrowHistory>() {
            @Override
            protected void updateItem(BorrowHistory item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getLateDays() > 0) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    /**
     * Description:
     * Method for loading the given window
     *
     * @param primaryStage Stage.class
     * @throws Exception (when loading the scene)
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUI/ReportsPage/ReportsPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUI/ReportsPage/ReportsPage.css").toExternalForm());
        primaryStage.setTitle("Reports Management Tool");
        primaryStage.setScene(scene);
        primaryStage.show();
        LogoutUtil.addWindowCloseListener(primaryStage); // Register window close listener for logout
    }

    /**
     * Description:
     * Method for replacing a month integer to its string name
     *
     * @param month int
     * @return String.class (month's name)
     */
    private String monthNameFromNumber(int month) {
        return java.time.Month.of(month).name().charAt(0) + java.time.Month.of(month).name().substring(1).toLowerCase();
    }

    /**
     * Description:
     * Method for retrieving a report to show
     */
    @FXML
    private void handleGenerateReport() {
        // Get the selected month, year, and report type from the UI combo boxes
        String selectedMonth = monthComboBox.getValue();
        String selectedYear = yearComboBox.getValue();
        String selectedReportType = reportTypeComboBox.getValue();

        // Check if any field is not selected and show an alert if so
        if (selectedMonth == null || selectedYear == null || selectedReportType == null) {
            showAlertError("Input Error", "Please select all fields.");
            return; // Exit the method if inputs are incomplete
        }

        // Set the chosen report type based on user selection
        String chosenReport;
        if (selectedReportType.equals("Borrowed Books Report"))
            chosenReport = ReportType.BORROW_REPORT.getValue(); // For borrowed books
        else
            chosenReport = ReportType.MEMBER_STATUS_REPORT.getValue(); // For member status

        // Convert the selected month to its numeric representation
        int monthNumber = getMonthNumber(selectedMonth);

        // Check if the month number is valid (1 to 12)
        if (monthNumber < 1 || monthNumber > 12) {
            showAlertError("Input Error", "Invalid month selected.");
            return; // Exit if the month is invalid
        }

        // Hide the charts and tables before the report is loaded
        freezeStatusBarChart.setVisible(false);
        borrowDatesBarChart.setVisible(false);
        memberStatusTable.setVisible(false);
        borrowHistoryTable.setVisible(false);

        // Prepare the command for generating the report with selected month, year, and report type
        String command = String.format("generateReport:%s,%d,%s", chosenReport, monthNumber, selectedYear);

        // Send the command to the server to fetch the report data
        ClientUI.chat.accept(command);

        // Retrieve the generated report from the server response
        Report report = ChatClient.report;

        // Check if a valid report was received
        if (report != null) {
            // If the report is of type MEMBER_STATUS_REPORT, display it in the member status table
            if (report.getReportType() == ReportType.MEMBER_STATUS_REPORT) {
                memberStatusTable.setVisible(true);
                LoadMemberStatusReport(report); // Load and display the member status report
            }
            // If the report is of type BORROW_REPORT, display it in the borrow history table
            else if (report.getReportType() == ReportType.BORROW_REPORT) {
                borrowHistoryTable.setVisible(true);
                LoadBorrowReport(report); // Load and display the borrow report
            }
            // If the report type is not recognized, show an alert
            else {
                showAlertError("No Reports Found", "No reports are available for the selected date and type.");
            }
        } else {
            // If no report data is received, show a failure alert
            showAlertError("No Data", "Failed to retrieve the report data.");
        }
    }

    /**
     * Description:
     * Method for creating a map for selected month name with its corresponding integer
     *
     * @param selectedMonth String.class
     * @return int (month's integer)
     */
    private int getMonthNumber(String selectedMonth) {
        Map<String, Integer> monthMap = new HashMap<>();
        monthMap.put("January", 1);
        monthMap.put("February", 2);
        monthMap.put("March", 3);
        monthMap.put("April", 4);
        monthMap.put("May", 5);
        monthMap.put("June", 6);
        monthMap.put("July", 7);
        monthMap.put("August", 8);
        monthMap.put("September", 9);
        monthMap.put("October", 10);
        monthMap.put("November", 11);
        monthMap.put("December", 12);
        return monthMap.getOrDefault(selectedMonth, -1);
    }

    /**
     * Description:
     * Method for loading the member status report
     *
     * @param report Report.class
     */
    private void LoadMemberStatusReport(Report report) {
        List<MemberStatusChange> memberStatusChangeList = report.parseMemberStatusCSV(report.getReportData());
        memberStatusData.clear();
        memberStatusData.addAll(memberStatusChangeList);
        // Prepare the command for generating the report with selected month, year, and report type
        String command = String.format("generateReport:%s,%d,%s", ReportType.STATUS_TRACKING, report.getReportDate().getMonthValue(), report.getReportDate().getYear());
        // Send the command to the server to fetch the report data
        ClientUI.chat.accept(command);
        freezeStatusBarChart.getData().clear();

        // Retrieve the generated report from the server response
        Report trackingreport = ChatClient.report;
        if (trackingreport == null) {
            showAlertError("Problem", "None");
            return;
        }
        List<StatusTracking> statusTrackingList = report.parseStatusTrackingCSV(trackingreport.getReportData());

        XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
        frozenSeries.setName("Frozen Members");

        XYChart.Series<String, Number> notFrozenSeries = new XYChart.Series<>();
        notFrozenSeries.setName("Not Frozen Members");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");

        // Add the data for each date in the report
        for (StatusTracking statusTracking : statusTrackingList) {
            String formattedDate = statusTracking.getDate().format(formatter);  // Format LocalDate
            frozenSeries.getData().add(new XYChart.Data<>(formattedDate, statusTracking.getFrozenMembers()));
            notFrozenSeries.getData().add(new XYChart.Data<>(formattedDate, statusTracking.getNotFrozenMembers()));
        }
        // Add series to the chart
        freezeStatusBarChart.getData().addAll(frozenSeries, notFrozenSeries);
        // update the chart and force layout adjustment
        Platform.runLater(() -> {
            freezeStatusBarChart.setCategoryGap(5.5);
            freezeStatusBarChart.setVisible(true);
            freezeStatusBarChart.requestLayout();  // This forces the layout to update
        });
    }

    /**
     * Description:
     * Method for loading the borrow status report
     *
     * @param report Report.class
     */
    private void LoadBorrowReport(Report report) {
        // Parse the borrow tracking data
        List<BorrowHistory> borrowHistoryList = report.parseBorrowHistoryCSV(report.getReportData());
        borrowHistoryData.clear();
        borrowHistoryData.addAll(borrowHistoryList);

        // Prepare the command for generating the report with selected month, year, and report type
        String command = String.format("generateReport:%s,%d,%s", ReportType.BORROW_TRACKING, report.getReportDate().getMonthValue(), report.getReportDate().getYear());
        // Send the command to the server to fetch the borrow tracking report data
        ClientUI.chat.accept(command);
        borrowDatesBarChart.getData().clear();

        // Retrieve the generated report from the server response
        Report borrowReport = ChatClient.report;
        if (borrowReport == null) {
            showAlertError("Problem", "No data received for borrow tracking");
            return;
        }

        // Parse the CSV data from the server response into BorrowTracking objects
        List<BorrowTracking> borrowTrackingListFromServer = Report.parseCSVBorrowTracking(borrowReport.getReportData());
        // Create series for borrow count and late count
        XYChart.Series<String, Number> borrowCountSeries = new XYChart.Series<>();
        borrowCountSeries.setName("Borrowed");
        XYChart.Series<String, Number> returnsCountSeries = new XYChart.Series<>();
        returnsCountSeries.setName("Returns");
        XYChart.Series<String, Number> lateCountSeries = new XYChart.Series<>();
        lateCountSeries.setName("Late");

        // Date formatter to format the date as dd-MM
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
        Map<String, Integer> returns = new HashMap<>();
        for (BorrowHistory borrowHistory : borrowHistoryList) {
            if (borrowHistory.getActualReturnDate() != null) {
                String formatted = borrowHistory.getActualReturnDate().format(formatter);
                returns.put(formatted, returns.getOrDefault(formatted, 0) + 1);
            }
        }
        // Loop through the borrow tracking list and add data to the chart
        for (BorrowTracking borrowTracking : borrowTrackingListFromServer) {
            String formattedDate = borrowTracking.getDate().format(formatter); // Format LocalDate as "dd-MM"
            borrowCountSeries.getData().add(new XYChart.Data<>(formattedDate, borrowTracking.getBorrowCount()));
            lateCountSeries.getData().add(new XYChart.Data<>(formattedDate, borrowTracking.getLateCount()));
            returnsCountSeries.getData().add(new XYChart.Data<>(formattedDate, returns.getOrDefault(formattedDate, 0)));
        }
        // Add the series to the chart
        borrowDatesBarChart.getData().addAll(borrowCountSeries, lateCountSeries, returnsCountSeries);

        // Update the chart visibility and layout
        Platform.runLater(() -> {
            borrowDatesBarChart.setCategoryGap(5.5);
            borrowDatesBarChart.setVisible(true);
            borrowDatesBarChart.requestLayout();  // Forces layout to update
        });
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
     */
    @FXML
    public void handleReturn(ActionEvent event) {
        try {
            LibrarianMainPageController view = new LibrarianMainPageController();
            view.start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Description:
     * Method to logout from librarian's account
     *
     * @param event ActionEvent.class
     * @throws Exception (when loading the scene)
     */
    @FXML
    void getLogoutButton(ActionEvent event) throws Exception {
        LogoutUtil.handleLogoutButtonAction(event);
        // Close the current stage (i.e., the current window)
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();  // Closes the current window
    }
}