package logic.OrderControl;

import DBControl.mysqlConnection;
import logic.BookControl.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static logic.NotificationControl.notification.sendArrivedBookOrderReminderByEmail;

/**
 * Description:
 * Class for the ordering logic in the system
 */
public class OrderLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public OrderLogic() {
        this.dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for canceling an ordered book of subscriber
     *
     * @param orderID int
     * @return boolean (true if changed in database)
     */
    public synchronized boolean cancelOrder(int orderID) {
        Connection connection = null;
        PreparedStatement ps = null;
        // String to delete an existing order
        String query = "DELETE FROM ordered_book WHERE orderID = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, orderID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
     * Method for adding a row in ordered_book db by subscriber
     *
     * @param bookName         String.class
     * @param bookID           int
     * @param membershipNumber int
     * @param memberName       String.class
     * @param memberPhone      String.class
     * @param memberEmail      String.class
     * @return String.class ('approve' - successful order, else different message representing error)
     */
    public synchronized String orderBook(String bookName, int bookID, int membershipNumber, String memberName,
                                         String memberPhone, String memberEmail) {
        PreparedStatement ps = null;
        Connection connection = null;

        // Initialize the current date as
        Date tempDate = new Date();
        java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
        BookLogic bookLogic = new BookLogic();
        Book book = bookLogic.fetchBook(bookID);
        if (book.getNumberOfCopies() == book.getNumberOforders())
            return "reached maximum ordering to the book";
        // String to insert a new row
        String query = "INSERT INTO ordered_book (bookName, BookId, orderDate, membershipNumber, memberName, memberPhone, memberEmail, arrivalStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, bookName);
            ps.setInt(2, bookID);
            ps.setDate(3, currentDate);
            ps.setInt(4, membershipNumber);
            ps.setString(5, memberName);
            ps.setString(6, memberPhone);
            ps.setString(7, memberEmail);
            ps.setString(8, "notArrived");
            ps.execute();
            return "approve";
        } catch (SQLException e) {
            e.printStackTrace();
            return "inserting problem";
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
     * Method for changing isOrdered status of a book (when ordering and not)
     *
     * @param bookID         int
     * @param numberOfOrders int
     * @param increase       boolean
     * @return boolean (true if changed in database)
     */
    public synchronized boolean changeBookOrderStatus(int bookID, int numberOfOrders, boolean increase) {
        Connection connection = null;
        PreparedStatement ps = null;
        // String to update the book's ordering status
        String query = "UPDATE book_db SET isOrdered = ?, numberOforders = ? WHERE bookID = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);

            // Change isOrdered status when increasing the number of orders in the book
            if (increase) {
                ps.setString(1, "yes");
                int newNumberOfOrders = numberOfOrders + 1;
                ps.setInt(2, newNumberOfOrders);
            }
            // Change isOrdered status when decreasing the number of orders in the book
            if (increase == false) {
                int newNumberOfOrders = numberOfOrders - 1;
                ps.setInt(2, newNumberOfOrders);
                // Check if there are no more orders of the book
                if (newNumberOfOrders == 0) {
                    ps.setString(1, "no");
                } else {
                    ps.setString(1, "yes");
                }
            }
            ps.setInt(3, bookID);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Description:
     * Method for searching book for the subscriber to order (book's name or id)
     *
     * @param bookName String.class
     * @param bookID   int
     * @return bookList List.class
     */
    public synchronized List<Book> searchToOrderBook(String bookName, int bookID) {
        List<Book> bookList = new ArrayList<>();
        // Instances of Book based on the search criteria
        Book bookByName = null;
        Book bookByID = null;
        // Search books that have the same name
        if (bookName != "null")
            bookByName = criteriaSearchBook("SELECT * FROM book_db WHERE BookName = ?", bookName);
        // Search books that have the same id
        if (bookID != -1)
            bookByID = criteriaSearchBook("SELECT * FROM book_db WHERE bookID = ?", bookID);
        // Add the books to the book list, default top is bookByName
        if (bookByName != null) bookList.add(bookByName);
        if (bookByID != null) bookList.add(bookByID);
        return bookList;
    }

    /**
     * Description:
     * Method for launching search based on given insert (String -> book's name, Int -> book's id)
     *
     * @param query String.class
     * @param queryInsert Object.class
     * @return book Book.class
     */
    private synchronized Book criteriaSearchBook(String query, Object queryInsert) {
        Connection connection = null;
        PreparedStatement ps = null;
        Book book = null;
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Inserting queryInsert to the query given
            if (queryInsert.getClass() == String.class)
                // queryInsert is the book's name
                ps.setString(1, queryInsert.toString());
            else
                // queryInsert is the book's id
                ps.setInt(1, Integer.valueOf(queryInsert.toString()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                IsOrdered isOrderedEnum = IsOrdered.valueOf(rs.getString("isOrdered").toUpperCase());
                book = new Book(
                        rs.getString("bookName"),
                        rs.getString("bookSubject"),
                        rs.getInt("NumberOfCopies"),
                        rs.getInt("NumberOfBorrowedCopies"),
                        rs.getString("keywords"),
                        isOrderedEnum,
                        rs.getInt("bookID"),
                        rs.getInt("numberOforders")
                );
            }
            return book;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Description:
     * Method for changing arrival status and arrival date for an ordered book in ordered_db
     *
     * @param bookName String.class
     */
    public synchronized void changeArrivalStatus(String bookName) {
        BookLogic bookLogic = new BookLogic();
        OrderedBook found = bookLogic.importOrderedBooksByBookName(bookName);
        if (found != null) {
            Connection connection = null;
            PreparedStatement ps = null;
            sendArrivedBookOrderReminderByEmail(found, LocalDate.now().plusDays(2));
            // Initialize the current date as
            Date tempDate = new Date();
            java.sql.Date currentDate = new java.sql.Date(tempDate.getTime());
            // Query for changing book order status in book_db
            String query = "UPDATE ordered_book SET arrivalStatus = ?, arrivalDate = ? WHERE orderID = ?";
            try {
                connection = dbConnector.getConnection();
                ps = connection.prepareStatement(query);
                ps.setString(1, "Arrived");
                ps.setDate(2, currentDate);
                ps.setInt(3, found.getOrderID());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    // Don't close the connection since it's managed by dbConnector
                    if (ps != null) ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    /**
     * Description:
     * Method for returning all the ordered books where arrivalDate is 3 days before the given date.
     *
     * @param givenDate LocalDate
     * @return orderList List.class
     */
    public synchronized List<OrderedBook> importAllLateOrderedBooks(LocalDate givenDate) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM ordered_book WHERE arrivalDate = ?";
        List<OrderedBook> orderList = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(givenDate.minusDays(3))); // Calculate the target date
            ResultSet rs = ps.executeQuery();
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
                        ArrivalStatus.valueOf(rs.getString("arrivalStatus").toUpperCase()), // Enum conversion
                        arrivalDate
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch ordered books: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return orderList;
    }

}