package logic.ExtensionControl;

import DBControl.mysqlConnection;
import logic.BookControl.*;
import logic.BorrowControl.BorrowLogic;
import java.sql.*;
import java.time.LocalDate;

/**
 * Description:
 * Class for the book extension logic in the system
 */
public class ExtensionLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public ExtensionLogic() {
        this.dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for checking if the copy of book has orders in ordered_book database
     *
     * @param borrowedBook BorrowedBook.class
     * @return boolean (true if there is orders of the book in database)
     */
    public synchronized boolean existingOrders(BorrowedBook borrowedBook) {
        Connection conn = null;
        PreparedStatement ps = null;
        // Query for importing all orders of borrowedBook
        String query = "SELECT * FROM ordered_book WHERE bookName = ?";
        try {
            //
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, borrowedBook.getNameOfBook());
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Description:
     * Method for extending copy of book return date
     *
     * @param borrowedBook BorrowedBook.class
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean borrowExtensionRequest(BorrowedBook borrowedBook) {
        // Check if there is any existing orders for the book copy
        if (existingOrders(borrowedBook)) {
            return false;
        }
        BorrowLogic borrowLogic = new BorrowLogic();
        // Default adding extension 2 week
        LocalDate newReturnDate = borrowedBook.getReturnDate().plusDays(14);
        try {
            // Setting return date of borrowed book
            borrowLogic.setReturnDate(borrowedBook.getMembershipNumber(), borrowedBook.getCopyOfBookId(), newReturnDate);
            return true;
        } catch (Exception e) {
            System.out.println("Error in setting return date to the Extension Request");
            return false;
        }
    }
}