package logic.BookControl;

import DBControl.mysqlConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Class for the book's logic in the system
 */
public class BookLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public BookLogic() {
        this.dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for increasing the number of books of the given book by one (when returning a book to the library)
     *
     * @param book Book.class
     * @return boolean (true if changed in database)
     */
    public synchronized boolean increaseQuantityOfBook(Book book) {
        Connection connection = null;
        PreparedStatement ps = null;

        // Check if book exists
        Book existingBook = fetchBook(book.getBookID());
        if (existingBook == null) {
            return false; // Book doesn't exist
        }

        String query = "UPDATE BookDB SET NumberOfCopies = NumberOfCopies + 1 WHERE bookID = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, book.getBookID());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Failed to decrease book quantity: " + e.getMessage());
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
     * Method for decreasing the number of books of the given book by one (when borrowing a book to the library)
     *
     * @param book Book.class
     * @return boolean (true if changed in database)
     */
    public synchronized boolean decreaseQuantityOfBook(Book book) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE BookDB SET NumberOfCopies = NumberOfCopies - 1 WHERE bookID = ?";
        // Check if book exists
        Book existingBook = fetchBook(book.getBookID());
        if (existingBook == null) {
            return false; // Book doesn't exist
        }
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, book.getBookID());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Failed to decrease book quantity: " + e.getMessage());
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
     * Method for changing borrow status of a book's copy (when borrowing and when returning)
     *
     * @param copyOfBook CopyOfBook.class
     * @return boolean (true if changed in database)
     */
    public synchronized boolean changeBookCopyBorrowStatus(CopyOfBook copyOfBook) {
        Connection conn = null;
        PreparedStatement ps = null;
        String query = "UPDATE copy_of_book SET borrowStatus = ? WHERE CopyOfBookId = ?";
        try {
            conn = dbConnector.getConnection();  // Ensure connection is open
            ps = conn.prepareStatement(query);
            ps.setString(1, copyOfBook.getBorrowStatus().toString());
            ps.setInt(2, copyOfBook.getCopyOfBookId());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0; // If rows were updated, return true
        } catch (SQLException e) {
            System.out.println("Failed to update borrow status: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Description:
     * Method for fetching a book by its ID
     *
     * @param bookID int
     * @return Book.class (fetched book from the database)
     */
    public synchronized Book fetchBook(int bookID) {
        Connection conn = null;
        PreparedStatement ps = null;

        String query = "SELECT * FROM book_db WHERE bookID = ?";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(query);

            ps.setInt(1, bookID);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                // Handle null values and fetch data
                String bookName = resultSet.getString("BookName");
                String bookSubject = resultSet.getString("BookSubject");
                int numberOfCopies = resultSet.getInt("NumberOfCopies");
                int borrowedCopies = resultSet.getInt("NumberOfBorrowedCopies");
                String keywords = resultSet.getString("Keywords");
                if (keywords == null) {
                    keywords = ""; // Default to empty string if null
                }
                int numberOfOrders = resultSet.getInt("NumberOfOrders");

                // Enum mapping to IsOrdered (ensure this is correct)
                IsOrdered isOrdered = IsOrdered.valueOf(resultSet.getString("IsOrdered").toUpperCase());

                // Create and return a Book object based on the result set
                Book newBook = new Book(bookName, bookSubject, numberOfCopies, borrowedCopies, keywords, isOrdered, bookID, numberOfOrders);

                return newBook;
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch book by ID: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // If the book is not found
    }

    /**
     * Description:
     * Method for borrowing a book from the library, add a borrowed book to the borrowed_book database
     *
     * @param borrowedBook BorrowedBook.class
     * @return boolean (if changed in the database)
     */
    public synchronized boolean addNewBorrowedBook(BorrowedBook borrowedBook) {
        Connection conn = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO borrowed_book (membershipNumber, NameOfBook, borrowDate, returnDate, librarianName, librarianID, CopyOfBookId, extensionDate,bookID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(query);

            // Set the values for the parameters in the prepared statement
            ps.setInt(1, borrowedBook.getMembershipNumber());  // membershipNumber
            ps.setString(2, borrowedBook.getNameOfBook());     // NameOfBook
            ps.setDate(3, java.sql.Date.valueOf(borrowedBook.getBorrowDate()));   // borrowDate
            ps.setDate(4, java.sql.Date.valueOf(borrowedBook.getReturnDate()));   // returnDate
            ps.setString(5, borrowedBook.getLibrarianName());
            ps.setInt(6, borrowedBook.getLibrarianId());
            ps.setInt(7, borrowedBook.getCopyOfBookId());

            // Set extensionDate to null if not available
            if (borrowedBook.getExtensionDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(borrowedBook.getExtensionDate()));  // extensionDate
            } else {
                ps.setNull(8, java.sql.Types.DATE);  // Set null for extensionDate if not available
            }
            ps.setInt(9, borrowedBook.getBookId());

            // Execute the update and check the result
            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0;  // If rows were inserted, return true
        } catch (SQLException e) {
            System.out.println("Failed to add borrowed book: " + e.getMessage());
            return false;
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
     * Method for importing all the borrowed books
     *
     * @param membershipNumber int
     * @return borrowList List.class
     */
    public synchronized List<BorrowedBook> importBorrowedBooks(int membershipNumber) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM borrowed_book WHERE membershipNumber=?";
        List<BorrowedBook> borrowList = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, membershipNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date extensionDateSql = rs.getDate("extensionDate");
                LocalDate extensionDate = (extensionDateSql != null) ? extensionDateSql.toLocalDate().plusDays(1) : null;
                borrowList.add(new BorrowedBook(
                        rs.getInt("membershipNumber"),
                        rs.getString("NameOfBook"),
                        rs.getInt("CopyOfBookId"),
                        rs.getString("librarianName"),
                        rs.getInt("librarianID"),
                        rs.getDate("borrowDate").toLocalDate().plusDays(1),
                        rs.getDate("returnDate").toLocalDate().plusDays(1),
                        extensionDate,
                        rs.getInt("bookID")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return borrowList;
    }

    /**
     * Description:
     * Method for searching books in the book_db based on criteria given
     *
     * @param bookName    String.class
     * @param bookSubject String.class
     * @param freeText    String.class
     * @return searchResults List.class
     */
    public synchronized List<Book> searchBooks(String bookName, String bookSubject, String freeText) {
        Connection connection = null;
        PreparedStatement ps = null;
        List<Book> searchResults = new ArrayList<>();
        String query = "SELECT * FROM book_db WHERE 1=1"; // always true to keep things flexible

        // Add search criteria dynamically
        if (!bookName.equals("is empty") && !bookName.isEmpty()) {
            query += " AND bookName LIKE ?";
        }
        if (!bookSubject.equals("is empty") && !bookSubject.isEmpty()) {
            query += " AND bookSubject LIKE ?";
        }
        if (!freeText.equals("is empty") && !freeText.isEmpty()) {
            String[] keywords = freeText.split(",");
            query += " AND (";
            for (int i = 0; i < keywords.length; i++) {
                query += "keywords LIKE ?";
                if (i < keywords.length - 1) {
                    query += " OR "; // Add OR between the keyword conditions
                }
            }
            query += ")";
        }
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            int paramIndex = 1;
            if (!bookName.equals("is empty") && !bookName.isEmpty()) {
                ps.setString(paramIndex++, "%" + bookName.trim().toLowerCase() + "%");
            }
            if (!bookSubject.equals("is empty") && !bookSubject.isEmpty()) {
                ps.setString(paramIndex++, "%" + bookSubject.trim().toLowerCase() + "%");
            }
            if (freeText != null && !freeText.isEmpty() && !freeText.equals("is empty")) {
                String[] keywords = freeText.split(",");
                for (String keyword : keywords) {
                    ps.setString(paramIndex++, "%" + keyword.trim().toLowerCase() + "%"); // Set each keyword dynamically
                }
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Create a Book object with all the fields from the result set
                String bookSummary = rs.getString("BookSummary");
                if (bookSummary == null) {
                    bookSummary = ""; // Default to an empty string if null
                }
                IsOrdered isOrderedEnum = IsOrdered.valueOf(rs.getString("isOrdered").toUpperCase());
                Book book = new Book(
                        rs.getString("bookName"),
                        rs.getString("bookSubject"),
                        rs.getInt("NumberOfCopies"),
                        rs.getInt("NumberOfBorrowedCopies"),
                        rs.getString("keywords"),
                        isOrderedEnum,
                        rs.getInt("bookID"),
                        rs.getInt("numberOforders")
                );
                // Set BookSummary using the setter
                book.setBookSummary(bookSummary);
                searchResults.add(book);
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch books: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return searchResults;
    }

    /**
     * Description:
     * Method for fetching 1 borrowed book from database
     *
     * @param membershipNumber int
     * @param CopyOfBookId     int
     * @return borrow BorrowedBook.class
     */
    public synchronized BorrowedBook fetchBorrowedBook(int membershipNumber, int CopyOfBookId) {
        // Importing all borrowed books of subscriber
        List<BorrowedBook> borrowedBooks = this.importBorrowedBooks(membershipNumber);
        // Finding specific borrowed book
        for (BorrowedBook borrow : borrowedBooks) {
            if (borrow.getMembershipNumber() == membershipNumber && borrow.getCopyOfBookId() == CopyOfBookId)
                return borrow;
        }
        return null;
    }

    /**
     * Description:
     * Method for importing all the ordered books by given member's id
     *
     * @param membershipNumber int
     * @return orderList List.class
     */
    public synchronized List<OrderedBook> importOrderedBooks(int membershipNumber) {
        Connection con = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM ordered_book WHERE membershipNumber=?";
        List<OrderedBook> orderList = new ArrayList<>();
        try {
            con = dbConnector.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, membershipNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ArrivalStatus arrivalStatus = ArrivalStatus.NOT_ARRIVED;
                if (rs.getString("arrivalStatus").equals("Arrived"))
                    arrivalStatus = ArrivalStatus.ARRIVED;
                Date orderDate = rs.getDate("orderDate");
                LocalDate localCurrentDate = null;
                Date arrivalDate = rs.getDate("arrivalDate");
                if (arrivalDate != null)
                    localCurrentDate = arrivalDate.toLocalDate();
                orderList.add(new OrderedBook(
                        rs.getInt("orderID"),
                        rs.getString("memberName"),
                        rs.getInt("membershipNumber"),
                        rs.getString("memberPhone"),
                        rs.getString("memberEmail"),
                        rs.getInt("BookId"),
                        rs.getString("bookName"),
                        orderDate.toLocalDate(),
                        arrivalStatus,
                        localCurrentDate
                ));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return orderList;
    }

    /**
     * Description:
     * Method for importing all the ordered books by given book's name
     *
     * @param bookName String.class
     * @return found OrderedBook.class
     */
    public synchronized OrderedBook importOrderedBooksByBookName(String bookName) {
        Connection con = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM ordered_book WHERE bookName = ?  AND arrivalStatus = ? ORDER BY orderDate ASC LIMIT 1";
        OrderedBook found = null;
        try {
            con = dbConnector.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, bookName);
            ps.setString(2, "notArrived");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ArrivalStatus arrivalStatus = ArrivalStatus.NOT_ARRIVED;
                if (rs.getString("arrivalStatus").equals("Arrived"))
                    arrivalStatus = ArrivalStatus.ARRIVED;
                LocalDate orderDate = rs.getDate("orderDate").toLocalDate();
                LocalDate arrivalDate = null;
                if (rs.getDate("arrivalDate") != null)
                    arrivalDate = rs.getDate("arrivalDate").toLocalDate();
                found = new OrderedBook(
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
        } catch (SQLException e) {
            System.out.println("Failed to fetch subscribers: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return found;
    }
}