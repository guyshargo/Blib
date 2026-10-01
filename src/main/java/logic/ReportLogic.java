package logic;

import model.BorrowHistory;
import model.BorrowTracking;
import model.BorrowedBook;
import model.MemberStatusChange;
import model.Report;
import model.StatusTracking;
import model.Subscriber;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.MysqlConnection;
import enums.FreezeStatus;
import enums.ReportType;


public class ReportLogic {

    private final MysqlConnection dbConnector;


    public ReportLogic() { dbConnector = MysqlConnection.getInstance(); }

    
    public synchronized void saveMemberStatusChange(MemberStatusChange memberStatusChange) {
        String insertQuery = "INSERT INTO member_status_changes (MemberID, MemberName, MemberStatus, ChangeStatusDate) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(insertQuery)) {
             
            ps.setInt(1, memberStatusChange.getMemberId());
            ps.setString(2, memberStatusChange.getMemberName());
            ps.setString(3, memberStatusChange.getMemberStatus().getDbValue());
            
            if (memberStatusChange.getChangeStatusDate() != null) {
                ps.setDate(4, Date.valueOf(memberStatusChange.getChangeStatusDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error saving status change: " + e.getMessage());
        }
    }

    private synchronized List<BorrowHistory> fetchBorrowHistory() throws SQLException {
        String query = "SELECT * FROM borrowhistory";
        List<BorrowHistory> borrowHistories = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                int memberId = rs.getInt("MemberID");
                String bookName = rs.getString("BookName");
                String memberName = rs.getString("memberName");
                LocalDate borrowDate = rs.getDate("BorrowDate").toLocalDate().plusDays(1);
                LocalDate originalReturnDate = rs.getDate("OriginalReturnDate").toLocalDate().plusDays(1);
                LocalDate actualReturnDate = rs.getDate("ActualReturnDate") != null ? rs.getDate("ActualReturnDate").toLocalDate().plusDays(1) : null;
                int lateDays = rs.getInt("LateDays");
                int copyofBookId = rs.getInt("CopyOfBookID");
                
                BorrowHistory borrowHistory = new BorrowHistory(memberId, memberName, bookName, borrowDate, originalReturnDate, actualReturnDate, copyofBookId);
                borrowHistory.setLateDays(lateDays);
                borrowHistories.add(borrowHistory);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrow history: " + e.getMessage());
        }
        return borrowHistories;
    }

    private synchronized List<MemberStatusChange> fetchMemberStatusChanges() throws SQLException {
        String query = "SELECT * FROM member_status_changes";
        List<MemberStatusChange> statusChanges = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                int memberId = rs.getInt("MemberID");
                String memberName = rs.getString("MemberName");
                FreezeStatus status = FreezeStatus.fromDbValue(rs.getString("MemberStatus"));
                LocalDate changeStatusDate = rs.getDate("ChangeStatusDate") != null ? rs.getDate("ChangeStatusDate").toLocalDate().plusDays(1) : null;
                
                statusChanges.add(new MemberStatusChange(memberId, memberName, status, changeStatusDate));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching member status changes: " + e.getMessage());
        }
        return statusChanges;
    }

    public synchronized void saveStatusTracking(StatusTracking statusTracking) {
        String insertQuery = "INSERT INTO status_tracking (Date, FrozenMembers, NotFrozenMembers) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
             
            ps.setDate(1, Date.valueOf(statusTracking.getDate()));
            ps.setInt(2, statusTracking.getFrozenMembers());
            ps.setInt(3, statusTracking.getNotFrozenMembers());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error saving status tracking: " + e.getMessage());
        }
    }

    public synchronized void updateStatusTracking(StatusTracking statusTracking) {
        String updateQuery = "UPDATE status_tracking SET FrozenMembers = FrozenMembers + ?, NotFrozenMembers = NotFrozenMembers + ? WHERE Date = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
             
            ps.setInt(1, statusTracking.getFrozenMembers());
            ps.setInt(2, statusTracking.getNotFrozenMembers());
            ps.setDate(3, Date.valueOf(statusTracking.getDate()));
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error updating status tracking: " + e.getMessage());
        }
    }

    public synchronized List<StatusTracking> fetchAllStatusTrackingOrderedByDate() {
        String selectQuery = "SELECT Date, FrozenMembers, NotFrozenMembers FROM status_tracking ORDER BY Date ASC";
        List<StatusTracking> statusTrackingList = new ArrayList<>();
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectQuery);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                LocalDate date = rs.getDate("Date").toLocalDate().plusDays(1);
                int frozenMembers = rs.getInt("FrozenMembers");
                int notFrozenMembers = rs.getInt("NotFrozenMembers");
                statusTrackingList.add(new StatusTracking(date, frozenMembers, notFrozenMembers));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching status tracking: " + e.getMessage());
        }
        return statusTrackingList;
    }

    public synchronized void saveBorrowTracking(BorrowTracking borrowTracking) {
        String insertQuery = "INSERT INTO borrow_tracking (Date, borrowCount, lateCount) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
             
            ps.setDate(1, Date.valueOf(borrowTracking.getDate()));
            ps.setInt(2, borrowTracking.getBorrowCount());
            ps.setInt(3, borrowTracking.getLateCount());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error saving borrow tracking: " + e.getMessage());
        }
    }

    public synchronized List<BorrowTracking> fetchAllBorrowTrackingOrderedByDate() {
        String selectQuery = "SELECT Date, borrowCount, lateCount FROM borrow_tracking ORDER BY Date ASC";
        List<BorrowTracking> borrowTrackingList = new ArrayList<>();
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectQuery);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                LocalDate date = rs.getDate("Date").toLocalDate().plusDays(1);
                int borrowCount = rs.getInt("borrowCount");
                int lateCount = rs.getInt("lateCount");
                borrowTrackingList.add(new BorrowTracking(date, borrowCount, lateCount));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching borrow tracking: " + e.getMessage());
        }
        return borrowTrackingList;
    }

    public synchronized void updateBorrowTracking(BorrowTracking borrowTracking) {
        String updateQuery = "UPDATE borrow_tracking SET borrowCount = borrowCount + ?, lateCount = lateCount + ? WHERE Date = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
             
            ps.setInt(1, borrowTracking.getBorrowCount());
            ps.setInt(2, borrowTracking.getLateCount());
            ps.setDate(3, Date.valueOf(borrowTracking.getDate()));
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error updating borrow tracking: " + e.getMessage());
        }
    }

    public synchronized void saveBorrowHistory(BorrowHistory borrowHistory) {
        String insertQuery = "INSERT INTO borrowhistory (MemberID, memberName, BookName, BorrowDate, OriginalReturnDate, ActualReturnDate, LateDays, CopyOfBookID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertQuery)) {
             
            ps.setInt(1, borrowHistory.getMemberId());
            ps.setString(2, borrowHistory.getMemberName());
            ps.setString(3, borrowHistory.getBookName());
            ps.setDate(4, Date.valueOf(borrowHistory.getBorrowDate()));
            ps.setDate(5, Date.valueOf(borrowHistory.getOriginalReturnDate()));
            
            if (borrowHistory.getActualReturnDate() != null) {
                ps.setDate(6, Date.valueOf(borrowHistory.getActualReturnDate()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            ps.setInt(7, borrowHistory.getLateDays());
            ps.setInt(8, borrowHistory.getCopyOfBookId());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error saving borrow history: " + e.getMessage());
        }
    }

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

    public synchronized void clearTableData(String tableName) {
        String query = "DELETE FROM " + tableName;
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.executeUpdate(); // FIXED: Removed parameter passing to executeUpdate()
            
        } catch (SQLException e) {
            System.err.println("Error deleting data from table: " + tableName + " - " + e.getMessage());
        }
    }

    public synchronized Report fetchReportByDate(String reportType, LocalDate reportDate) {
        String query = "SELECT ReportDate, ReportType, ReportData FROM reports_db WHERE ReportDate = ? AND ReportType = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setDate(1, java.sql.Date.valueOf(reportDate));
            ps.setString(2, reportType);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate retrievedReportDate = rs.getDate("ReportDate").toLocalDate().plusDays(1);
                    String retrievedReportType = rs.getString("ReportType");
                    String reportData = rs.getString("ReportData");
                    return new Report(retrievedReportDate, ReportType.fromValue(retrievedReportType), reportData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching report: " + e.getMessage());
        }
        return null;
    }

    public synchronized void saveReportToDatabase(Report report) {
        String query = "INSERT INTO reports_db (ReportDate, ReportType, ReportData) VALUES (?, ?, ?)";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setDate(1, Date.valueOf(report.getReportDate()));
            ps.setString(2, report.getReportType().toString());
            ps.setString(3, report.getReportData());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Integer> getAvailableReportYears() throws SQLException {
        String query = "SELECT DISTINCT YEAR(ReportDate) AS reportYear FROM reports_db";
        List<Integer> years = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                years.add(rs.getInt("reportYear"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting years with reports: " + e.getMessage());
        }
        return years;
    }

    public synchronized List<Integer> getAvailableReportMonths() throws SQLException {
        String query = "SELECT DISTINCT MONTH(ReportDate) AS reportMonth FROM reports_db";
        List<Integer> months = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                months.add(rs.getInt("reportMonth"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting months with reports: " + e.getMessage());
        }
        return months;
    }

    public synchronized void updateActualReturnDate(int memberId, int copyOfBookId, LocalDate borrowDate, LocalDate actualReturnDate) {
        String updateQuery = "UPDATE borrowhistory SET ActualReturnDate = ? WHERE MemberID = ? AND CopyOfBookID = ? AND BorrowDate = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
             
            ps.setDate(1, Date.valueOf(actualReturnDate));
            ps.setInt(2, memberId);
            ps.setInt(3, copyOfBookId);
            ps.setDate(4, Date.valueOf(borrowDate));
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error updating actual return date: " + e.getMessage());
        }
    }

    public synchronized void updateOriginalReturnDate(int memberId, int copyOfBookId, LocalDate borrowDate, LocalDate originalReturnDate) {
        String updateQuery = "UPDATE borrowhistory SET OriginalReturnDate = ? WHERE MemberID = ? AND CopyOfBookID = ? AND BorrowDate = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(updateQuery)) {
             
            ps.setDate(1, Date.valueOf(originalReturnDate));
            ps.setInt(2, memberId);
            ps.setInt(3, copyOfBookId);
            ps.setDate(4, Date.valueOf(borrowDate));
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Error updating original return date: " + e.getMessage());
        }
    }

    public synchronized static void CreateReport() {
        LocalDate today = LocalDate.now().minusDays(1);
        ReportLogic reportLogic = new ReportLogic();
        
        Report memberStatus = reportLogic.generateReport(today, ReportType.MEMBER_STATUS_REPORT);
        Report borrowHistory = reportLogic.generateReport(today, ReportType.BORROW_REPORT);
        Report statusTracking = reportLogic.generateReport(today, ReportType.STATUS_TRACKING);
        Report borrowTracking = reportLogic.generateReport(today, ReportType.BORROW_TRACKING);

        reportLogic.saveReportToDatabase(memberStatus);
        reportLogic.saveReportToDatabase(borrowHistory);
        reportLogic.saveReportToDatabase(statusTracking);
        reportLogic.saveReportToDatabase(borrowTracking);

        reportLogic.clearTableData("member_status_changes");
        reportLogic.clearTableData("borrowhistory");
        reportLogic.clearTableData("status_tracking");
        reportLogic.clearTableData("borrow_tracking");

        addAllSubscribersStatus();
        addAllBorrowedBooks();
        addAllStatusTracking();
        addAllBorrowTracking();
    }

    private synchronized static void addAllSubscribersStatus() {
        ReportLogic reportLogic = new ReportLogic();
        SubscriberLogic subLogic = new SubscriberLogic();
        List<Subscriber> subscriberList = subLogic.fetchAllSubscribers();
        for (Subscriber subscriber : subscriberList) {
            reportLogic.saveMemberStatusChange(new MemberStatusChange(
                subscriber.getMembershipNumber(),
                subscriber.getFullName(),
                subscriber.getFreezeStatus(),
                subscriber.getFreezeStatusDate()
            ));
        }
    }

    public synchronized static void addAllStatusTracking() {
        ReportLogic reportLogic = new ReportLogic();
        SubscriberLogic subLogic = new SubscriberLogic();
        int frozen = 0;
        int notFrozen = 0;
        for (Subscriber sub : subLogic.fetchAllSubscribers()) {
            if (sub.getFreezeStatus() == FreezeStatus.FROZEN)
                frozen++;
            else
                notFrozen++;
        }
        reportLogic.saveStatusTracking(new StatusTracking(LocalDate.now(), frozen, notFrozen));
    }

    private synchronized static void addAllBorrowedBooks() {
        ReportLogic reportLogic = new ReportLogic();
        BorrowLogic borrowLogic = new BorrowLogic();
        SubscriberLogic subLogic = new SubscriberLogic();
        List<BorrowedBook> borrowedBooks = borrowLogic.importAllBorrowedBooks();
        for (BorrowedBook borrowedBook : borrowedBooks) {
            Subscriber subscriber = subLogic.fetchSubscriberById(borrowedBook.getMembershipNumber());
            reportLogic.saveBorrowHistory(new BorrowHistory(borrowedBook.getMembershipNumber(), subscriber.getFullName(), borrowedBook.getNameOfBook(), borrowedBook.getBorrowDate(), borrowedBook.getReturnDate(), borrowedBook.getCopyOfBookId()));
        }
    }

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