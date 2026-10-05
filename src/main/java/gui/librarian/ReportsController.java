package gui.librarian;

import client.SessionManager;
import enums.FreezeStatus;
import enums.ReportType;
import gui.common.MainLayoutController;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import model.BorrowHistory;
import model.BorrowTracking;
import model.MemberStatusChange;
import model.Report;
import model.StatusTracking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;


public class ReportsController {
    @FXML private ComboBox<Month> monthComboBox;
    @FXML private ComboBox<String> yearComboBox;
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private TableView<MemberStatusChange> memberStatusTable;
    @FXML private TableView<BorrowHistory> borrowHistoryTable;
    @FXML private TableColumn<MemberStatusChange, Integer> membershipNumberColumn;
    @FXML private TableColumn<MemberStatusChange, String> memberNameColumn;
    @FXML private TableColumn<MemberStatusChange, String> freezeStatusColumn;
    @FXML private TableColumn<MemberStatusChange, String> savedStatusDateColumn;
    @FXML private TableColumn<BorrowHistory, Integer> borrowMembershipNumberColumn;
    @FXML private TableColumn<BorrowHistory, String> borrowMemberNameColumn;
    @FXML private TableColumn<BorrowHistory, String> bookNameColumn;
    @FXML private TableColumn<BorrowHistory, String> borrowDateColumn;
    @FXML private TableColumn<BorrowHistory, String> originalReturnDateColumn;
    @FXML private TableColumn<BorrowHistory, String> actualReturnDateColumn;
    @FXML private TableColumn<BorrowHistory, Integer> lateDaysColumn;
    @FXML private Button generateReportButton;
    @FXML private BarChart<String, Number> freezeStatusBarChart;
    @FXML private BarChart<String, Number> borrowDatesBarChart;
    @FXML private CategoryAxis daysAxis;
    @FXML private Button resizeButton;
    @FXML private Label librarianName;

    private ObservableList<MemberStatusChange> memberStatusData;
    private ObservableList<BorrowHistory> borrowHistoryData;


    @FXML
    public void initialize() {
        // Clear combo boxes in case of re-initialization
        monthComboBox.getItems().clear();
        yearComboBox.getItems().clear();
        freezeStatusBarChart.setVisible(false);
        borrowDatesBarChart.setVisible(false);
        librarianName.setText(SessionManager.currentLibrarian.getFullName());

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/reports/available-dates"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                Map<String, List<Integer>> datesData = mapper.readValue(response.body(), new TypeReference<Map<String, List<Integer>>>(){});
                
                List<Integer> yearsList = datesData.get("years");
                List<Integer> monthsList = datesData.get("months");

                if (yearsList != null && !yearsList.isEmpty()) {
                    for (int year : yearsList) {
                        yearComboBox.getItems().add(String.valueOf(year));
                    }
                }
                
                if (monthsList != null && !monthsList.isEmpty()) {
                    for (int month : monthsList) {
                        monthComboBox.getItems().add(Month.of(month));
                    }
                }
            } else {
                showAlertError("Error", "Failed to load available dates from server.");
            }
        } catch (Exception e) {
            showAlertError("Initialization Error", "Failed to load available months and years.");
            e.printStackTrace();
        }

        monthComboBox.setCellFactory(listView -> createMonthCell());
        monthComboBox.setButtonCell(createMonthCell());

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

    private ListCell<Month> createMonthCell() {
        return new ListCell<Month>() {
            @Override
            protected void updateItem(Month month, boolean empty) {
                super.updateItem(month, empty);
                setText(empty || month == null ? null : month.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            }
        };
    }


    @FXML
    private void handleGenerateReport() {
        // Get the selected month, year, and report type from the UI combo boxes
        Month selectedMonth = monthComboBox.getValue();
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

        int monthNumber = selectedMonth.getValue();

        // Hide the charts and tables before the report is loaded
        freezeStatusBarChart.setVisible(false);
        borrowDatesBarChart.setVisible(false);
        memberStatusTable.setVisible(false);
        borrowHistoryTable.setVisible(false);

        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = String.format("http://localhost:8080/api/reports/generate?reportType=%s&monthNumber=%d&year=%s", chosenReport, monthNumber, selectedYear);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                Report report = mapper.readValue(response.body(), Report.class);

                if (report.getReportType() == ReportType.MEMBER_STATUS_REPORT) {
                    memberStatusTable.setVisible(true);
                    LoadMemberStatusReport(report); 
                } else if (report.getReportType() == ReportType.BORROW_REPORT) {
                    borrowHistoryTable.setVisible(true);
                    LoadBorrowReport(report); 
                } else {
                    showAlertError("No Reports Found", "No reports are available for the selected date and type.");
                }
            } else {
                showAlertError("No Data", "Failed to retrieve the report data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertError("Error", "Network connection failed.");
        }
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

        freezeStatusBarChart.getData().clear();

        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = String.format("http://localhost:8080/api/reports/generate?reportType=%s&monthNumber=%d&year=%d", 
                    ReportType.STATUS_TRACKING, report.getReportDate().getMonthValue(), report.getReportDate().getYear());
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                Report trackingreport = mapper.readValue(response.body(), Report.class);
                List<StatusTracking> statusTrackingList = report.parseStatusTrackingCSV(trackingreport.getReportData());

                XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
                frozenSeries.setName("Frozen Members");

                XYChart.Series<String, Number> notFrozenSeries = new XYChart.Series<>();
                notFrozenSeries.setName("Not Frozen Members");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");

                for (StatusTracking statusTracking : statusTrackingList) {
                    String formattedDate = statusTracking.getDate().format(formatter); 
                    frozenSeries.getData().add(new XYChart.Data<>(formattedDate, statusTracking.getFrozenMembers()));
                    notFrozenSeries.getData().add(new XYChart.Data<>(formattedDate, statusTracking.getNotFrozenMembers()));
                }
                
                freezeStatusBarChart.getData().add(frozenSeries);
                freezeStatusBarChart.getData().add(notFrozenSeries);
                Platform.runLater(() -> {
                    freezeStatusBarChart.setCategoryGap(5.5);
                    freezeStatusBarChart.setVisible(true);
                    freezeStatusBarChart.requestLayout(); 
                });
            } else {
                showAlertError("Problem", "Failed to retrieve status tracking chart data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void LoadBorrowReport(Report report) {
        List<BorrowHistory> borrowHistoryList = report.parseBorrowHistoryCSV(report.getReportData());
        borrowHistoryData.clear();
        borrowHistoryData.addAll(borrowHistoryList);

        borrowDatesBarChart.getData().clear();

        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = String.format("http://localhost:8080/api/reports/generate?reportType=%s&monthNumber=%d&year=%d", 
                    ReportType.BORROW_TRACKING, report.getReportDate().getMonthValue(), report.getReportDate().getYear());
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
                Report borrowReport = mapper.readValue(response.body(), Report.class);
                List<BorrowTracking> borrowTrackingListFromServer = Report.parseCSVBorrowTracking(borrowReport.getReportData());
                
                XYChart.Series<String, Number> borrowCountSeries = new XYChart.Series<>();
                borrowCountSeries.setName("Borrowed");
                XYChart.Series<String, Number> returnsCountSeries = new XYChart.Series<>();
                returnsCountSeries.setName("Returns");
                XYChart.Series<String, Number> lateCountSeries = new XYChart.Series<>();
                lateCountSeries.setName("Late");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
                Map<String, Integer> returns = new HashMap<>();
                for (BorrowHistory borrowHistory : borrowHistoryList) {
                    if (borrowHistory.getActualReturnDate() != null) {
                        String formatted = borrowHistory.getActualReturnDate().format(formatter);
                        returns.put(formatted, returns.getOrDefault(formatted, 0) + 1);
                    }
                }
                
                for (BorrowTracking borrowTracking : borrowTrackingListFromServer) {
                    String formattedDate = borrowTracking.getDate().format(formatter); 
                    borrowCountSeries.getData().add(new XYChart.Data<>(formattedDate, borrowTracking.getBorrowCount()));
                    lateCountSeries.getData().add(new XYChart.Data<>(formattedDate, borrowTracking.getLateCount()));
                    returnsCountSeries.getData().add(new XYChart.Data<>(formattedDate, returns.getOrDefault(formattedDate, 0)));
                }
                
                borrowDatesBarChart.getData().add(borrowCountSeries);
                borrowDatesBarChart.getData().add(lateCountSeries);
                borrowDatesBarChart.getData().add(returnsCountSeries);
                Platform.runLater(() -> {
                    borrowDatesBarChart.setCategoryGap(5.5);
                    borrowDatesBarChart.setVisible(true);
                    borrowDatesBarChart.requestLayout();  
                });
            } else {
                showAlertError("Problem", "No data received for borrow tracking");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    @FXML
    public void handleReturn(ActionEvent event) {
        MainLayoutController.getInstance().loadCenterView("/gui/librarian/LibrarianDashboard.fxml");
    }
}