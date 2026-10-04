package logic;

import model.BorrowedBook;

import java.sql.*;
import java.time.LocalDate;

import database.MysqlConnection;


public class ExtensionLogic {

    private final MysqlConnection dbConnector;

    // Singleton database connector
    public ExtensionLogic() { dbConnector = MysqlConnection.getInstance(); }

    public synchronized boolean existingOrders(BorrowedBook borrowedBook) {
        String query = "SELECT * FROM orders WHERE book_title = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setString(1, borrowedBook.getNameOfBook());
            
            try (ResultSet resultSet = ps.executeQuery()) {
                return resultSet.next();
            }
            
        } catch (SQLException ex) {
            System.out.println("Error checking existing orders: " + ex.getMessage());
            return false;
        }
    }

    public synchronized boolean borrowExtensionRequest(BorrowedBook borrowedBook) {
        // Check if there are any existing orders for the book copy
        if (existingOrders(borrowedBook)) {
            return false;
        }
        
        BorrowLogic borrowLogic = new BorrowLogic();
        // Default adding extension 2 weeks
        LocalDate newReturnDate = borrowedBook.getReturnDate().plusDays(14);
        
        // Directly return the boolean result of the database update
        return borrowLogic.setReturnDate(borrowedBook.getMembershipNumber(), borrowedBook.getCopyOfBookId(), newReturnDate);
    }
}