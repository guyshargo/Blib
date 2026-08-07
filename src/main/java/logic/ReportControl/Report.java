package logic.ReportControl;

import logic.FreezeStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Class for the reports in the system
 */
public class Report implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    private LocalDate reportDate;
    private ReportType reportType;
    private String reportData;  // Store report data as a CSV string

    /**
     * Description:
     * Method for constructing the given class (data is an undefined list)
     *
     * @param reportDate LocalDate.class
     * @param reportType ReportType.class
     * @param reportData reportData.class
     */
    public Report(LocalDate reportDate, ReportType reportType, List<?> reportData) {
        this.reportDate = reportDate;
        this.reportType = reportType;

        // Check if it's a list of MemberStatusChange objects or BorrowHistory objects
        if (reportData != null && !reportData.isEmpty()) {
            if (reportData.get(0) instanceof MemberStatusChange) {
                this.reportData = convertToCSVMemberStatus((List<MemberStatusChange>) reportData);
            } else if (reportData.get(0) instanceof BorrowHistory) {
                this.reportData = convertToCSVBorrow((List<BorrowHistory>) reportData);
            } else if (reportData.get(0) instanceof StatusTracking) {
                this.reportData = convertToCSVStatusTracking((List<StatusTracking>) reportData);
            } else if (reportData.get(0) instanceof BorrowTracking) {
                this.reportData = convertToCSVBorrowTracking((List<BorrowTracking>) reportData);
            } else {
                throw new IllegalArgumentException("Unsupported data type in report data");
            }
        }
    }

    /**
     * Description:
     * Method for constructing the given class (data is a string)
     *
     * @param reportDate LocalDate.class
     * @param reportType ReportType.class
     * @param reportData String.class
     */
    public Report(LocalDate reportDate, ReportType reportType, String reportData) {
        this.reportDate = reportDate;
        this.reportType = reportType;
        this.reportData = reportData;
    }

    /**
     * Description:
     * Getter method for the report's date
     *
     * @return reportDate LocalDate.class
     */
    public LocalDate getReportDate() {
        return reportDate;
    }

    /**
     * Description:
     * Setter method for the report's date
     *
     * @param reportDate LocalDate.class
     */
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    /**
     * Description:
     * Getter method for the report's type
     *
     * @return reportType ReportType.class
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Description:
     * Setter method for the report's type
     *
     * @param reportType ReportType.class
     */
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    /**
     * Description:
     * Getter method for the report's data
     *
     * @return reportData String.class
     */
    public String getReportData() {
        return reportData;
    }

    /**
     * Description:
     * Setter method for the report's data
     *
     * @param reportData String.class
     */
    public void setReportData(String reportData) {
        this.reportData = reportData;
    }

    /**
     * Description:
     * Method for converting a status tarcking to CSV format string
     *
     * @param statusTrackings List<StatusTracking>.class
     * @return sb String.class
     */
    private String convertToCSVStatusTracking(List<StatusTracking> statusTrackings) {
        StringBuilder sb = new StringBuilder();
        // CSV header
        sb.append("Date,FrozenMembers,NotFrozenMembers\n");
        // Loop through the status tracking list and format them into CSV rows
        for (StatusTracking tracking : statusTrackings) {
            sb.append(tracking.getDate())  // Date
                    .append(", ")
                    .append(tracking.getFrozenMembers())  // FrozenMembers
                    .append(", ")
                    .append(tracking.getNotFrozenMembers())  // NotFrozenMembers
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Description:
     * Method for parsing the status tracking CSV data to a list
     *
     * @param csvData String.class
     * @return statusTrackings List<statusTrackings>.class
     */
    public List<StatusTracking> parseStatusTrackingCSV(String csvData) {
        List<StatusTracking> statusTrackings = new ArrayList<>();
        String[] lines = csvData.split("\n");
        // Skip the header
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(", ");
            if (values.length == 3) {
                // Parse the date
                LocalDate date = LocalDate.parse(values[0].trim());
                // Parse frozen members count
                int frozenMembers = Integer.parseInt(values[1].trim());
                // Parse not frozen members count
                int notFrozenMembers = Integer.parseInt(values[2].trim());
                // Create and add a StatusTracking object to the list
                statusTrackings.add(new StatusTracking(date, frozenMembers, notFrozenMembers));
            }
        }
        return statusTrackings;
    }

    /**
     * Description:
     * Method for converting a borrow tarcking to CSV format string
     *
     * @param borrowTrackings List<BorrowTracking>.class
     * @return sb String.class
     */
    private static String convertToCSVBorrowTracking(List<BorrowTracking> borrowTrackings) {
        StringBuilder sb = new StringBuilder();
        // CSV header
        sb.append("Date,BorrowCount,LateCount\n");
        // Loop through the borrow tracking list and format them into CSV rows
        for (BorrowTracking tracking : borrowTrackings) {
            sb.append(tracking.getDate())  // Date
                    .append(", ")
                    .append(tracking.getBorrowCount())  // BorrowCount
                    .append(", ")
                    .append(tracking.getLateCount())  // LateCount
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Description:
     * // Parse CSV data and convert it into a list of BorrowTracking objects
     *
     * @param csvData String.class
     * @return borrowTrackings List<BorrowTracking>.class
     */
    public static List<BorrowTracking> parseCSVBorrowTracking(String csvData) {
        List<BorrowTracking> borrowTrackings = new ArrayList<>();
        String[] lines = csvData.split("\n");
        // Skip the header
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(", ");
            if (values.length == 3) {
                // Parse the date
                LocalDate date = LocalDate.parse(values[0].trim());

                // Parse borrow count
                int borrowCount = Integer.parseInt(values[1].trim());

                // Parse late count
                int lateCount = Integer.parseInt(values[2].trim());

                // Create and add a BorrowTracking object to the list
                borrowTrackings.add(new BorrowTracking(date, borrowCount, lateCount));
            }
        }
        return borrowTrackings;
    }


    /**
     * Description:
     * Method for converting a member status changes to CSV format string
     *
     * @param statusChanges List<MemberStatusChange>.class
     * @return sb String.class
     */
    private String convertToCSVMemberStatus(List<MemberStatusChange> statusChanges) {
        StringBuilder sb = new StringBuilder();
        // CSV header
        sb.append("MemberID,memberName,MemberStatus,ChangeStatusDate\n");
        // Loop through status changes and format them into CSV rows
        for (MemberStatusChange change : statusChanges) {
            sb.append(change.getMemberId())  // MemberID
                    .append(", ")
                    .append(change.getMemberName())  // MemberName
                    .append(", ")
                    .append(change.getMemberStatus().getDbValue())  // MemberStatus (Frozen/NotFrozen)
                    .append(", ")
                    .append(change.getChangeStatusDate().plusDays(1))  // ChangeStatusDate
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Description:
     * Method for converting a borrow history to CSV format string
     *
     * @param borrowHistoryList List<BorrowHistory>.class
     * @return sb String.class
     */
    private String convertToCSVBorrow(List<BorrowHistory> borrowHistoryList) {
        StringBuilder sb = new StringBuilder();
        // CSV header
        sb.append("MemberID,memberName,BookName,BorrowDate,OriginalReturnDate,ActualReturnDate,LateDays,CopyOfBookId\n");
        // Loop through borrow history and format them into CSV rows
        for (BorrowHistory borrow : borrowHistoryList) {
            sb.append(borrow.getMemberId())  // MemberID
                    .append(", ")
                    .append(borrow.getMemberName())//memberName
                    .append(", ")
                    .append(borrow.getBookName())  // BookName
                    .append(", ")
                    .append(borrow.getBorrowDate())  // BorrowDate
                    .append(", ")
                    .append(borrow.getOriginalReturnDate())  // OriginalReturnDate
                    .append(", ")
                    .append(borrow.getActualReturnDate())  // ActualReturnDate
                    .append(", ")
                    .append(borrow.getLateDays())  // LateDays
                    .append(", ")
                    .append(borrow.getCopyOfBookId())  // copyOfBookId
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * Description:
     * Method for parsing the member status CSV data to a list
     *
     * @param csvData String.class
     * @return memberStatusChanges List<MemberStatusChange>.class
     */
    public List<MemberStatusChange> parseMemberStatusCSV(String csvData) {
        List<MemberStatusChange> memberStatusChanges = new ArrayList<>();
        String[] lines = csvData.split("\n");
        // Skip the header
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(", ");
            if (values.length == 4) {
                // Parse the member ID
                int memberId = Integer.parseInt(values[0].trim());
                // Parse the member name
                String memberName = values[1].trim();
                // Parse the FreezeStatus enum
                FreezeStatus memberStatus = FreezeStatus.fromDbValue(values[2].trim());
                // Parse the changeStatusDate, handling potential null or empty values
                LocalDate changeStatusDate = (values[3] == null || values[3].equals("null") || values[3].isEmpty())
                        ? null : LocalDate.parse(values[3].trim());
                memberStatusChanges.add(new MemberStatusChange(memberId, memberName, memberStatus, changeStatusDate));
            }
        }
        return memberStatusChanges;
    }

    /**
     * Description:
     * Method for parsing the borrow history CSV data to a list
     *
     * @param csvData String.class
     * @return borrowHistoryList List<BorrowHistory>.class
     */
    public List<BorrowHistory> parseBorrowHistoryCSV(String csvData) {
        List<BorrowHistory> borrowHistoryList = new ArrayList<>();
        String[] lines = csvData.split("\n");
        // Skip the header
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(", ");
            // Parse the member ID
            int memberId = Integer.parseInt(values[0].trim());
            // Parse the member name
            String memberName = values[1].trim();
            // Parse the book name
            String bookName = values[2].trim();
            // Parse the borrow date
            LocalDate borrowDate = LocalDate.parse(values[3].trim());
            // Parse the original return date
            LocalDate originalReturnDate = LocalDate.parse(values[4].trim());
            // Parse the actual return date (handle null or empty values)
            LocalDate actualReturnDate = (values[5] == null || values[5].equals("null") || values[5].isEmpty())
                    ? null : LocalDate.parse(values[5].trim());
            // Parse late days
            int lateDays = Integer.parseInt(values[6].trim());
            int copyofBookId = Integer.parseInt(values[7].trim());

            // Create BorrowHistory object and add it to the list
            BorrowHistory borrowHistory = new BorrowHistory(memberId, memberName, bookName, borrowDate, originalReturnDate, actualReturnDate, copyofBookId);
            lateDays = lateDays == 0 && actualReturnDate == null ? borrowHistory.calculateLateDays(originalReturnDate, reportDate) : borrowHistory.calculateLateDays(originalReturnDate, actualReturnDate);
            borrowHistory.setLateDays(lateDays);  // Set late days, if calculated externally
            borrowHistoryList.add(borrowHistory);
        }
        return borrowHistoryList;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Report Date: ").append(reportDate).append("\n")
                .append("Report Type: ").append(reportType).append("\n")
                .append("Report Data:\n");
        // Split reportData (CSV) into rows and display it as a table
        String[] rows = reportData.split("\n");
        for (String row : rows) {
            sb.append("| ").append(row.replace(",", " | ")).append(" |\n");
        }
        return sb.toString();
    }
}