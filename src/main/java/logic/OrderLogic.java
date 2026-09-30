package logic;

import model.Book;
import model.BorrowedBook;
import model.OrderedBook;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.MysqlConnection;
import enums.ArrivalStatus;

public class OrderLogic {

    private final MysqlConnection dbConnector;

    // Singleton database connector
    public OrderLogic() { dbConnector = MysqlConnection.getInstance(); }


    public synchronized boolean changeArrivalStatus(String bookName) {
        OrderedBook found = importOrderedBooksByBookName(bookName);
        
        if (found != null) {
            // trigger email notification
            Notification.sendArrivedBookOrderReminderByEmail(found, LocalDate.now().plusDays(2));

            Date tempDate = new Date();
            java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
            
            String query = "UPDATE ordered_book SET arrivalStatus = ?, arrivalDate = ? WHERE orderID = ?";
            
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                 
                ps.setString(1, "Arrived");
                ps.setDate(2, currentDate);
                ps.setInt(3, found.getOrderID());
                return ps.executeUpdate() > 0;
                
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public synchronized List<OrderedBook> importOrderedBooks(int membershipNumber) {
        String query = "SELECT * FROM ordered_book WHERE membershipNumber=?";
        List<OrderedBook> orderList = new ArrayList<>();
        
        try (Connection con = dbConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setInt(1, membershipNumber);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ArrivalStatus arrivalStatus = "Arrived".equals(rs.getString("arrivalStatus")) ? ArrivalStatus.ARRIVED : ArrivalStatus.NOT_ARRIVED;
                    
                    java.sql.Date orderDateSql = rs.getDate("orderDate");
                    java.sql.Date arrivalDateSql = rs.getDate("arrivalDate");
                    
                    LocalDate orderDate = (orderDateSql != null) ? orderDateSql.toLocalDate() : null;
                    LocalDate localCurrentDate = (arrivalDateSql != null) ? arrivalDateSql.toLocalDate() : null;
                    
                    orderList.add(new OrderedBook(
                            rs.getInt("orderID"),
                            rs.getString("memberName"),
                            rs.getInt("membershipNumber"),
                            rs.getString("memberPhone"),
                            rs.getString("memberEmail"),
                            rs.getInt("BookId"),
                            rs.getString("bookName"),
                            orderDate,
                            arrivalStatus,
                            localCurrentDate
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch ordered books: " + e.getMessage());
        }
        return orderList;
    }

    public synchronized OrderedBook importOrderedBooksByBookName(String bookName) {
        String query = "SELECT * FROM ordered_book WHERE bookName = ? AND arrivalStatus = ? ORDER BY orderDate ASC LIMIT 1";
        
        try (Connection con = dbConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
             
            ps.setString(1, bookName);
            ps.setString(2, "notArrived");
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ArrivalStatus arrivalStatus = "Arrived".equals(rs.getString("arrivalStatus")) ? ArrivalStatus.ARRIVED : ArrivalStatus.NOT_ARRIVED;
                    LocalDate orderDate = rs.getDate("orderDate").toLocalDate();
                    LocalDate arrivalDate = (rs.getDate("arrivalDate") != null) ? rs.getDate("arrivalDate").toLocalDate() : null;
                    
                    return new OrderedBook(
                            rs.getInt("orderID"),
                            rs.getString("memberName"),
                            rs.getInt("membershipNumber"),
                            rs.getString("memberPhone"),
                            rs.getString("memberEmail"),
                            rs.getInt("BookId"),
                            rs.getString("bookName"),
                            orderDate,
                            arrivalStatus,
                            arrivalDate
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch ordered book by name: " + e.getMessage());
        }
        return null;
    }

    public synchronized boolean removeOrderForBorrowedBook(BorrowedBook borrowedBook) {
        String query = "DELETE FROM ordered_book WHERE bookId = ? AND membershipNumber = ? AND arrivalStatus = 'Arrived'";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, borrowedBook.getBookId());
            ps.setInt(2, borrowedBook.getMembershipNumber());

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return false;
    }


    public synchronized boolean cancelOrder(int orderID) {
        String query = "DELETE FROM ordered_book WHERE orderID = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, orderID);
            ps.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public synchronized String orderBook(String bookName, int bookID, int membershipNumber, String memberName,
                                         String memberPhone, String memberEmail) {
        BookLogic bookLogic = new BookLogic();
        Book book = bookLogic.fetchBook(bookID);
        
        if (book.getNumberOfCopies() == book.getNumberOforders()) {
            return "reached maximum ordering to the book";
        }
            
        Date tempDate = new Date();
        java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
        
        String query = "INSERT INTO ordered_book (bookName, BookId, orderDate, membershipNumber, memberName, memberPhone, memberEmail, arrivalStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, bookName);
            ps.setInt(2, bookID);
            ps.setDate(3, currentDate);
            ps.setInt(4, membershipNumber);
            ps.setString(5, memberName);
            ps.setString(6, memberPhone);
            ps.setString(7, memberEmail);
            ps.setString(8, "notArrived");
            ps.executeUpdate(); 
            
            return "approve";
        } catch (SQLException e) {
            e.printStackTrace();
            return "inserting problem";
        }
    }


    public synchronized List<OrderedBook> importAllLateOrderedBooks(LocalDate givenDate) {
        String query = "SELECT * FROM ordered_book WHERE arrivalDate = ?";
        List<OrderedBook> orderList = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setDate(1, java.sql.Date.valueOf(givenDate.minusDays(3))); 
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date arrivalDateSql = rs.getDate("arrivalDate");
                    LocalDate arrivalDate = (arrivalDateSql != null) ? arrivalDateSql.toLocalDate() : null;

                    orderList.add(new OrderedBook(
                            rs.getInt("orderID"),
                            rs.getString("memberName"),
                            rs.getInt("membershipNumber"),
                            rs.getString("memberPhone"),
                            rs.getString("memberEmail"),
                            rs.getInt("BookId"),
                            rs.getString("bookName"),
                            rs.getDate("orderDate").toLocalDate(),
                            ArrivalStatus.valueOf(rs.getString("arrivalStatus").toUpperCase()),
                            arrivalDate
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch ordered books: " + e.getMessage());
        }
        return orderList;
    }

}