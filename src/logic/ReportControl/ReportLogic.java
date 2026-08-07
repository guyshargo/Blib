package logic.ReportControl;

import DBControl.mysqlConnection;
import logic.BookControl.BorrowedBook;
import logic.BorrowControl.BorrowLogic;
import logic.FreezeStatus;
import logic.Subscriber;
import logic.subscriberLogic;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Class for the report logic in the system
 */
public class ReportLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public ReportLogic() {
        dbConnector = mysqlConnection.getInstance();
    }

    /**
     * Description:
     * Method for changing the member status in the report
     *
     * @param memberStatusChange MemberStatusChange.class
     */
    public synchronized void saveMemberStatusChange(MemberStatusChange memberStatusChange) {
        Connection connection = null;
        PreparedStatement ps = null;
        String insertQuery = "INSERT INTO member_status_changes (MemberID, MemberName, MemberStatus, ChangeStatusDate) " +
                "VALUES (?, ?, ?, ?)";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(insertQuery);
            // Set parameters for the SQL query
            ps.setInt(1, memberStatusChange.getMemberId()); // MemberID
            ps.setString(2, memberStatusChange.getMemberName()); // MemberName
            ps.setString(3, memberStatusChange.getMemberStatus().getDbValue()); // MemberStatus
            // Check if ChangeStatusDate is null and set it accordingly
            if (memberStatusChange.getChangeStatusDate() != null) {
                ps.setDate(4, Date.valueOf(memberStatusChange.getChangeStatusDate())); // ChangeStatusDate
            } else {
                ps.setNull(4, Types.DATE); // Set null for ChangeStatusDate if it's null
            }
            // Execute the insert query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving status change: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for fetching the borrow history for the given date range (for report generation)
     *
     * @return borrowHistories List<BorrowHistory>.class
     * @throws SQLException (when fetching the borrow history)
     */
    private synchronized List<BorrowHistory> fetchBorrowHistory() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        List<BorrowHistory> borrowHistories = new ArrayList<>();
        // SQL query to fetch borrow history based on BorrowDate or ActualReturnDate
        String query = "SELECT * FROM borrowhistory";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            // Process the results
            while (rs.next()) {
                int memberId = rs.getInt("MemberID");
                String bookName = rs.getString("BookName");
                String memberName = rs.getString("memberName");
                LocalDate borrowDate = rs.getDate("BorrowDate").toLocalDate().plusDays(1);
                LocalDate originalReturnDate = rs.getDate("OriginalReturnDate").toLocalDate().plusDays(1);
                LocalDate actualReturnDate = rs.getDate("ActualReturnDate") != null ? rs.getDate("ActualReturnDate").toLocalDate().plusDays(1) : null;
                int lateDays = rs.getInt("LateDays");
                int copyofBookId = rs.getInt("CopyOfBookID");
                // Create a BorrowHistory object and add it to the list
                BorrowHistory borrowHistory = new BorrowHistory(memberId, memberName, bookName, borrowDate, originalReturnDate, actualReturnDate, copyofBookId);
                borrowHistory.setLateDays(lateDays);  // Set the number of late days
                borrowHistories.add(borrowHistory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching borrow history: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return borrowHistories;
    }

    /**
     * Description:
     * Method for fetching the member status changes for the given date range (for report generation)
     *
     * @return statusChanges List<MemberStatusChange>.class
     * @throws SQLException (when fetching the member status changes)
     */
    private synchronized List<MemberStatusChange> fetchMemberStatusChanges() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        List<MemberStatusChange> statusChanges = new ArrayList<>();
        String query = "SELECT * FROM member_status_changes";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int memberId = rs.getInt("MemberID");
                String memberName = rs.getString("MemberName");
                FreezeStatus status = FreezeStatus.fromDbValue(rs.getString("MemberStatus"));
                LocalDate changeStatusDate = rs.getDate("ChangeStatusDate") != null ? rs.getDate("ChangeStatusDate").toLocalDate().plusDays(1) : null;
                MemberStatusChange statuschange = new MemberStatusChange(memberId, memberName, status, changeStatusDate);
                statusChanges.add(statuschange);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching member status changes: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statusChanges;
    }

    /**
     * Description:
     * Method for inserting a new status tracking into the status_tracking database
     *
     * @param statusTracking StatusTracking.class
     */
    public synchronized void saveStatusTracking(StatusTracking statusTracking) {
        Connection conn = null;
        PreparedStatement ps = null;
        String insertQuery = "INSERT INTO status_tracking (Date, FrozenMembers, NotFrozenMembers) VALUES (?, ?, ?)";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(insertQuery);
            // Set parameters for the SQL query
            ps.setDate(1, Date.valueOf(statusTracking.getDate())); // Date
            ps.setInt(2, statusTracking.getFrozenMembers()); // FrozenMembers count
            ps.setInt(3, statusTracking.getNotFrozenMembers()); // NotFrozenMembers count
            // Execute the insert query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving status tracking: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for updating a status tracking from the status_tracking database
     *
     * @param statusTracking StatusTracking.class
     */
    public synchronized void updateStatusTracking(StatusTracking statusTracking) {
        Connection conn = null;
        PreparedStatement ps = null;
        String updateQuery = "UPDATE status_tracking " +
                "SET FrozenMembers = FrozenMembers + ?, NotFrozenMembers = NotFrozenMembers + ? " +
                "WHERE Date = ?";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(updateQuery);
            // Set parameters for the SQL query
            ps.setInt(1, statusTracking.getFrozenMembers()); // Increment for FrozenMembers
            ps.setInt(2, statusTracking.getNotFrozenMembers()); // Increment for NotFrozenMembers
            ps.setDate(3, Date.valueOf(statusTracking.getDate())); // Date for the record to be updated
            // Execute the update query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating status tracking: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for fetching all status tracking from the database, ascending order by their dates
     *
     * @return statusTrackingList List<StatusTracking>.class
     */
    public synchronized List<StatusTracking> fetchAllStatusTrackingOrderedByDate() {
        List<StatusTracking> statusTrackingList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String selectQuery = "SELECT Date, FrozenMembers, NotFrozenMembers FROM status_tracking ORDER BY Date ASC";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(selectQuery);
            rs = ps.executeQuery();
            // Loop through the result set and populate the list
            while (rs.next()) {
                LocalDate date = rs.getDate("Date").toLocalDate().plusDays(1);
                int frozenMembers = rs.getInt("FrozenMembers");
                int notFrozenMembers = rs.getInt("NotFrozenMembers");
                // Create StatusTracking object and add it to the list
                StatusTracking statusTracking = new StatusTracking(date, frozenMembers, notFrozenMembers);
                statusTrackingList.add(statusTracking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching status tracking: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statusTrackingList;
    }

    /**
     * Description:
     * Method for inserting a new borrow tracking into the borrow_tracking database
     *
     * @param borrowTracking BorrowTracking.class
     */
    public synchronized void saveBorrowTracking(BorrowTracking borrowTracking) {
        Connection conn = null;
        PreparedStatement ps = null;
        String insertQuery = "INSERT INTO borrow_tracking (Date, borrowCount, lateCount) VALUES (?, ?, ?)";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(insertQuery);
            // Set parameters for the SQL query
            ps.setDate(1, Date.valueOf(borrowTracking.getDate())); // Date
            ps.setInt(2, borrowTracking.getBorrowCount()); // BorrowCount
            ps.setInt(3, borrowTracking.getLateCount()); // LateCount
            // Execute the insert query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving borrow tracking: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for fetching all borrow tracking from the database, ascending order by their date
     *
     * @return borrowTrackingList List<BorrowTracking>.class
     */
    public synchronized List<BorrowTracking> fetchAllBorrowTrackingOrderedByDate() {
        List<BorrowTracking> borrowTrackingList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String selectQuery = "SELECT Date, borrowCount, lateCount FROM borrow_tracking ORDER BY Date ASC";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(selectQuery);
            rs = ps.executeQuery();
            // Loop through the result set and populate the list
            while (rs.next()) {
                LocalDate date = rs.getDate("Date").toLocalDate().plusDays(1);
                int borrowCount = rs.getInt("borrowCount");
                int lateCount = rs.getInt("lateCount");
                // Create BorrowTracking object and add it to the list
                BorrowTracking borrowTracking = new BorrowTracking(date, borrowCount, lateCount);
                borrowTrackingList.add(borrowTracking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching borrow tracking: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return borrowTrackingList;
    }

    /**
     * Description:
     * Method for updating a borrow tracking from the borrow_tracking databas
     *
     * @param borrowTracking BorrowTracking.class
     */
    public synchronized void updateBorrowTracking(BorrowTracking borrowTracking) {
        Connection conn = null;
        PreparedStatement ps = null;
        String updateQuery = "UPDATE borrow_tracking " +
                "SET borrowCount = borrowCount + ?, lateCount = lateCount + ? " +
                "WHERE Date = ?";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(updateQuery);
            // Set parameters for the SQL query
            ps.setInt(1, borrowTracking.getBorrowCount()); // Increment for BorrowCount
            ps.setInt(2, borrowTracking.getLateCount());   // Increment for LateCount
            ps.setDate(3, Date.valueOf(borrowTracking.getDate())); // Date for the record to be updated
            // Execute the update query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating borrow tracking: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for inserting the borrow history in the history_db
     *
     * @param borrowHistory BorrowHistory.class
     */
    public synchronized void saveBorrowHistory(BorrowHistory borrowHistory) {
        Connection conn = null;
        PreparedStatement ps = null;
        String insertQuery = "INSERT INTO borrowhistory (MemberID, memberName, BookName, BorrowDate, OriginalReturnDate, " +
                "ActualReturnDate, LateDays,CopyOfBookID) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(insertQuery);
            // Set parameters for the SQL query
            ps.setInt(1, borrowHistory.getMemberId()); // MemberID
            ps.setString(2, borrowHistory.getMemberName()); // MemberName
            ps.setString(3, borrowHistory.getBookName()); // BookName
            ps.setDate(4, Date.valueOf(borrowHistory.getBorrowDate())); // BorrowDate
            ps.setDate(5, Date.valueOf(borrowHistory.getOriginalReturnDate())); // OriginalReturnDate
            // Check if ActualReturnDate is null
            if (borrowHistory.getActualReturnDate() != null) {
                ps.setDate(6, Date.valueOf(borrowHistory.getActualReturnDate())); // ActualReturnDate
            } else {
                ps.setNull(6, Types.DATE); // Set null for ActualReturnDate
            }
            ps.setInt(7, borrowHistory.getLateDays()); // LateDays
            ps.setInt(8, borrowHistory.getCopyOfBookId()); // LateDays
            // Execute the insert query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving borrow history: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for generating a report (the data is fetched and converted to CSV)
     *
     * @param reportDate LocalDate.class
     * @param reportType ReportType.class
     * @return Report.class (based on its type)
     */
    public synchronized Report generateReport(LocalDate reportDate, ReportType reportType) {
        try {
            if (reportType == ReportType.BORROW_REPORT) {
                List<BorrowHistory> borrowHistoryList = fetchBorrowHistory();
                return new Report(reportDate, reportType, borrowHistoryList);
            } else if (reportType == ReportType.MEMBER_STATUS_REPORT) {
                List<MemberStatusChange> statusChanges = fetchMemberStatusChanges();
                return new Report(reportDate, reportType, statusChanges);
            } else if (reportType == ReportType.STATUS_TRACKING) {
                List<StatusTracking> statusTrackingList = fetchAllStatusTrackingOrderedByDate();
                return new Report(reportDate, reportType, statusTrackingList);
            } else {
                List<BorrowTracking> borrowTrackingList = fetchAllBorrowTrackingOrderedByDate();
                return new Report(reportDate, reportType, borrowTrackingList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Description:
     * Method for deleting all rows in the given table
     *
     * @param tableName String.class
     */
    public synchronized void clearTableData(String tableName) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "DELETE FROM " + tableName;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Execute the delete query
            ps.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error deleting data from table: " + tableName + " - " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for fetching a report from the report_db based on criteria given
     *
     * @param reportType String.class
     * @param reportDate LocalDate.class
     * @return Report.class (based on its type)
     */
    public synchronized Report fetchReportByDate(String reportType, LocalDate reportDate) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT ReportDate, ReportType, ReportData FROM reports_db WHERE ReportDate = ? AND ReportType = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Set query parameters
            ps.setDate(1, java.sql.Date.valueOf(reportDate)); // Convert LocalDate to SQL Date
            ps.setString(2, reportType); // Report Type
            // Execute the query
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Retrieve values from the result set
                LocalDate retrievedReportDate = rs.getDate("ReportDate").toLocalDate().plusDays(1);
                String retrievedReportType = rs.getString("ReportType");
                String reportData = rs.getString("ReportData");
                // Reconstruct the Report object
                return new Report(retrievedReportDate, ReportType.fromValue(retrievedReportType), reportData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching report: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Description:
     * Method for saving a report in the database
     *
     * @param report Report.class
     */
    public synchronized void saveReportToDatabase(Report report) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "INSERT INTO reports_db (ReportDate, ReportType, ReportData) VALUES (?, ?, ?)";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setDate(1, Date.valueOf(report.getReportDate())); // ReportDate
            ps.setString(2, report.getReportType().toString()); // ReportType (e.g., MEMBER_STATUS_REPORT)
            ps.setString(3, report.getReportData()); // ReportData (CSV format)
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for retrieving the years with reports from the reports_db table
     *
     * @return years List<Integer>.class
     * @throws SQLException (when fetching reports)
     */
    public synchronized List<Integer> getAvailableReportYears() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT DISTINCT YEAR(ReportDate) AS reportYear FROM reports_db";
        List<Integer> years = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                years.add(rs.getInt("reportYear"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error getting years with reports from the reports_db table: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return years;
    }

    /**
     * Description:
     * Method fetching the months with reports from the reports_db table
     *
     * @return months List<Integer>.class
     * @throws SQLException (when fetching reports)
     */
    public synchronized List<Integer> getAvailableReportMonths() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT DISTINCT MONTH(ReportDate) AS reportMonth FROM reports_db";
        List<Integer> months = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                months.add(rs.getInt("reportMonth"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error getting months with reports from the reports_db table: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return months;
    }

    /**
     * Description:
     * Method for updating the actual return date value of a borrowed book in the borrowhistory database
     *
     * @param memberId         int
     * @param copyOfBookId     int
     * @param borrowDate       LocalDate.class
     * @param actualReturnDate LocalDate.class
     */
    public synchronized void updateActualReturnDate(int memberId, int copyOfBookId, LocalDate borrowDate, LocalDate actualReturnDate) {
        Connection conn = null;
        PreparedStatement ps = null;
        String updateQuery = "UPDATE borrowhistory SET ActualReturnDate = ? WHERE MemberID = ? AND CopyOfBookID = ? AND BorrowDate = ?";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(updateQuery);
            // Set parameters for the SQL query
            ps.setDate(1, Date.valueOf(actualReturnDate)); // ActualReturnDate
            ps.setInt(2, memberId); // MemberID
            ps.setInt(3, copyOfBookId); // CopyOfBookID
            ps.setDate(4, Date.valueOf(borrowDate)); // BorrowDate
            // Execute the update query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating actual return date: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for updating the original return date value of a borrowed book in the borrowhistory database
     *
     * @param memberId           int
     * @param copyOfBookId       int
     * @param borrowDate         LocalDate.class
     * @param originalReturnDate LocalDate.class
     */
    public synchronized void updateOriginalReturnDate(int memberId, int copyOfBookId, LocalDate borrowDate, LocalDate originalReturnDate) {
        Connection connection = null;
        PreparedStatement ps = null;
        String updateQuery = "UPDATE borrowhistory SET OriginalReturnDate = ? WHERE MemberID = ? AND CopyOfBookID = ? AND BorrowDate = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(updateQuery);
            // Set parameters for the SQL query
            ps.setDate(1, Date.valueOf(originalReturnDate)); // OriginalReturnDate
            ps.setInt(2, memberId); // MemberID
            ps.setInt(3, copyOfBookId); // CopyOfBookID
            ps.setDate(4, Date.valueOf(borrowDate)); // BorrowDate
            // Execute the update query
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating original return date: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Description:
     * Method for creating a new report
     */
    public synchronized static void CreateReport() {
        LocalDate today = LocalDate.now().minusDays(1);
        ReportLogic reportLogic = new ReportLogic();
        // Generate and save the reports
        Report memberStatus = reportLogic.generateReport(today, ReportType.MEMBER_STATUS_REPORT);
        Report borrowHistory = reportLogic.generateReport(today, ReportType.BORROW_REPORT);
        // Generate and save the reports
        Report statusTracking = reportLogic.generateReport(today, ReportType.STATUS_TRACKING);
        Report borrowTracking = reportLogic.generateReport(today, ReportType.BORROW_TRACKING);

        reportLogic.saveReportToDatabase(memberStatus);
        reportLogic.saveReportToDatabase(borrowHistory);
        reportLogic.saveReportToDatabase(statusTracking);
        reportLogic.saveReportToDatabase(borrowTracking);

        // Delete data from both tables
        reportLogic.clearTableData("member_status_changes");
        reportLogic.clearTableData("borrowhistory");
        reportLogic.clearTableData("status_tracking");
        reportLogic.clearTableData("borrow_tracking");

        addAllSubscribersStatus();
        addAllBorrowedBooks();
        addAllStatusTracking();
        addAllBorrowTracking();
    }

    /**
     * Description:
     * Method for add all subscriber statuses from the database to the report
     */
    private synchronized static void addAllSubscribersStatus() {
        ReportLogic reportLogic = new ReportLogic();
        subscriberLogic subLogic = new subscriberLogic();
        List<Subscriber> subscriberList = subLogic.fetchAllSubscribers();
        for (Subscriber subscriber : subscriberList) {
            reportLogic.saveMemberStatusChange(new MemberStatusChange(subscriber.getMembershipNumber(), subscriber.getMemberFullName(), subscriber.getMemberFreezeStatus(), subscriber.getFreezeStatusDate()));
        }
    }

    /**
     * Description:
     * Method for add all status tracking from the database to the report
     */
    public synchronized static void addAllStatusTracking() {
        ReportLogic reportLogic = new ReportLogic();
        subscriberLogic subLogic = new subscriberLogic();
        int frozen = 0;
        int notFrozen = 0;
        for (Subscriber sub : subLogic.fetchAllSubscribers()) {
            if (sub.getMemberFreezeStatus() == FreezeStatus.FROZEN)
                frozen++;
            else
                notFrozen++;
        }
        reportLogic.saveStatusTracking(new StatusTracking(LocalDate.now(), frozen, notFrozen));
    }

    /**
     * Description:
     * Method for add all borrowed books from the database to the report
     */
    private synchronized static void addAllBorrowedBooks() {
        ReportLogic reportLogic = new ReportLogic();
        BorrowLogic borrowLogic = new BorrowLogic();
        subscriberLogic subLogic = new subscriberLogic();
        List<BorrowedBook> borrowedBooks = borrowLogic.importAllBorrowedBooks();
        for (BorrowedBook borrowedBook : borrowedBooks) {
            Subscriber subscriber = subLogic.fetchSubscriberById(borrowedBook.getMembershipNumber());
            reportLogic.saveBorrowHistory(new BorrowHistory(borrowedBook.getMembershipNumber(), subscriber.getMemberFullName(), borrowedBook.getNameOfBook(), borrowedBook.getBorrowDate(), borrowedBook.getReturnDate(), borrowedBook.getCopyOfBookId()));
        }
    }

    /**
     * Description:
     * Method for add al borrow tracking from the database to the report
     */
    public synchronized static void addAllBorrowTracking() {
        ReportLogic reportLogic = new ReportLogic();
        BorrowLogic borrowLogic = new BorrowLogic();
        List<BorrowedBook> borrowedBooks = borrowLogic.importAllBorrowedBooks();
        int lates = 0;
        int borrows = 0;
        for (BorrowedBook borrowedBook : borrowedBooks) {
            if (borrowedBook.getReturnDate().isBefore(LocalDate.now())) {
                lates++;
            }
            borrows++;
        }
        reportLogic.saveBorrowTracking(new BorrowTracking(LocalDate.now(), borrows, lates));
    }
}