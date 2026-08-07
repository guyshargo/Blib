package logic.BorrowControl;

import DBControl.mysqlConnection;
import logic.BookControl.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:
 * Class for the borrowing logic of books in the system
 */
public class BorrowLogic {
    /**
     * Connection to the mySQL database
     */
    private mysqlConnection dbConnector;

    /**
     * Description:
     * Method for constructing the given class
     */
    public BorrowLogic() {
        this.dbConnector = mysqlConnection.getInstance(); // Singleton database connector
    }

    /**
     * Description:
     * Method for receiving a borrowed book with the closest return date from all return dates
     *
     * @param bookID int
     * @return BorrowedBook.class (borrowed book that was found)
     */
    public synchronized BorrowedBook getCloserReturnDateBook(int bookID) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM borrowed_book WHERE BookId = ? ORDER BY returnDate ASC LIMIT 1";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, bookID);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                java.sql.Date extensionDateSql = resultSet.getDate("extensionDate");
                LocalDate extensionDate = (extensionDateSql != null) ? extensionDateSql.toLocalDate().plusDays(1) : null;
                // Extract details from the result set
                int membershipNumber = resultSet.getInt("membershipNumber");
                String bookName = resultSet.getString("NameOfBook");
                int copyOfBookId = resultSet.getInt("CopyOfBookId");
                String librarianName = resultSet.getString("librarianName");
                int librarianId = resultSet.getInt("librarianId");
                LocalDate borrowDate = resultSet.getDate("borrowDate").toLocalDate().plusDays(1);
                LocalDate returnDate = resultSet.getDate("returnDate").toLocalDate().plusDays(1);
                int bookId = resultSet.getInt("BookId");

                // Return the BorrowedBook object
                return new BorrowedBook(membershipNumber, bookName, copyOfBookId, librarianName, librarianId, borrowDate, returnDate, extensionDate, bookId);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching the soonest return date: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // Return null if no borrowed books are found
    }

    /**
     * Description:
     * Method for finding a specific copy of book based on copy of book ID and commit borrow on it
     *
     * @param copyOfBookID int
     * @return CopyOfBook.class (book copy that was found)
     */
    public synchronized CopyOfBook findCopyOfBook(int copyOfBookID) {
        Connection connection = null;
        PreparedStatement ps = null;
        String queryAvailableCopy = "SELECT * FROM copy_of_book WHERE CopyOfBookId = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(queryAvailableCopy);
            ps.setInt(1, copyOfBookID);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                // Retrieve copy of book attributes from the result set
                int copyOfBookId = resultSet.getInt("CopyOfBookId");
                String copOfBookName = resultSet.getString("bookName");
                String shelfLocation = resultSet.getString("ShelfLocation");
                String barcode = resultSet.getString("Barcode");
                // Change borrow status to BORROWED
                String borrowStatusString = resultSet.getString("borrowStatus");
                BorrowStatus borrowStatus = "NotBorrowed".equalsIgnoreCase(borrowStatusString) ? BorrowStatus.NOT_BORROWED : BorrowStatus.BORROWED;
                int bookId = resultSet.getInt("BookId");
                // Create and return a CopyOfBook object
                return new CopyOfBook(copyOfBookId, copOfBookName, borrowStatus, shelfLocation, barcode, bookId);
            }
        } catch (SQLException e) {
            System.out.println("Error while finding book: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // Book not found
    }

    /**
     * Description:
     * Method for checking if a member can borrow a book copy based on the copy's id
     *
     * @param membershipNumber int
     * @param cpbook           CopyOfBook.class
     * @param book             Book.class
     * @return boolean (true if the member can borrow the copy)
     */
    public synchronized boolean checkIfAMemberCanBorrowTheBook(int membershipNumber, CopyOfBook cpbook, Book book) {
        Connection connection = null;
        PreparedStatement ps = null;
        String orderQuery = "SELECT * FROM ordered_book WHERE bookId = ? AND membershipNumber = ? AND arrivalStatus = 'Arrived'";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(orderQuery);
            // Check if the book is ordered and how many orders exist
            ps = connection.prepareStatement(orderQuery);
            ps.setInt(1, cpbook.getBookId()); // First parameter
            ps.setInt(2, membershipNumber);  // Second parameter
            ResultSet ordersResult = ps.executeQuery();

            boolean isBookOrdered = false;
            boolean memberOrderedBook = false;

            // Check if the member has ordered the book
            if (ordersResult.next()) {
                memberOrderedBook = true;
            }
            if (book.getIsOrderedBoolean())
                isBookOrdered = true;
            // Calculate available copies
            int numberOfAvailableCopies = book.getNumberOfCopies() - book.getNumberOfBorrowedCopies();
            // Check borrowing conditions
            if (memberOrderedBook || !isBookOrdered || numberOfAvailableCopies > book.getNumberOforders()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Error while checking if a member can borrow the book: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Description:
     * Method for finding one copy of a book based on barcode and borrow status
     *
     * @param barcode String.class
     * @return CopyOfBook.class (book copy that was found)
     */
    public synchronized CopyOfBook getAvailableCopyOfBookByBarcode(String barcode) {
        Connection connection = null;
        PreparedStatement ps = null;
        // Query to find a book copy with a specific barcode and 'NotBorrowed' status
        String queryAvailableCopy = "SELECT * FROM copy_of_book WHERE Barcode = ? AND borrowStatus = 'NotBorrowed' LIMIT 1";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(queryAvailableCopy);
            ps.setString(1, barcode); // Set the barcode parameter
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                // Retrieve copy of book attributes from the result set
                int copyOfBookId = resultSet.getInt("CopyOfBookId");
                String copyOfBookName = resultSet.getString("bookName");
                String shelfLocation = resultSet.getString("ShelfLocation");
                String barcodeResult = resultSet.getString("Barcode");

                // Convert the string value to BorrowStatus enum
                String borrowStatusString = resultSet.getString("borrowStatus");
                BorrowStatus borrowStatus = "NotBorrowed".equalsIgnoreCase(borrowStatusString) ? BorrowStatus.NOT_BORROWED : BorrowStatus.BORROWED;

                int bookId = resultSet.getInt("BookId");
                CopyOfBook availablecopy = new CopyOfBook(copyOfBookId, copyOfBookName, borrowStatus, shelfLocation, barcodeResult, bookId);
                return availablecopy;
            }
        } catch (SQLException e) {
            System.out.println("Error while finding book by barcode: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // No available copy found
    }

    /**
     * Description:
     * Method for deleting a borrowed book's order
     *
     * @param borrowedBook BorrowedBook.class
     * @return boolean (true if changed in database)
     */
    public synchronized boolean removeOrderForBorrowedBook(BorrowedBook borrowedBook) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "DELETE FROM ordered_book WHERE bookId = ? AND membershipNumber = ? AND arrivalStatus = 'Arrived'";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Set parameters for the query
            ps.setInt(1, borrowedBook.getBookId());
            ps.setInt(2, borrowedBook.getMembershipNumber());

            // Execute the delete query
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Description:
     * Method for decreasing number of order's to a given book
     *
     * @param book Book.class
     */
    public synchronized void decreaseOrdersNumber(Book book) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE book_db SET numberOforders = numberOforders -1 WHERE bookID = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Set parameters for the query
            ps.setInt(1, book.getBookID());

            // Execute the delete query
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("No matching order found.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
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
     * Method for borrowing a book in the library
     *
     * @param memberId      int
     * @param copyOfBookId  int
     * @param librarianId   int
     * @param librarianName String.class
     * @return BorrowedBook.class (borrowed book that was changed)
     */
    public synchronized BorrowedBook borrowBook(int memberId, int copyOfBookId, int librarianId, String librarianName) {
        // Fetch available copy of the book for borrowing
        CopyOfBook availableCopy = findCopyOfBook(copyOfBookId);
        BookLogic bookLogic = new BookLogic();
        if (availableCopy == null) {
            return null; // No available copy to borrow
        }
        if (availableCopy.getBorrowStatus() == BorrowStatus.BORROWED) {
            return null;
        }
        // Update the borrow status of the selected book
        availableCopy.setBorrowStatus(BorrowStatus.BORROWED);
        boolean borrowUpdated = bookLogic.changeBookCopyBorrowStatus(availableCopy);
        if (!borrowUpdated) {
            return null; // Borrow status update failed
        }
        // Else create a new BorrowedBook and store it in the database
        BorrowedBook borrowedBook = new BorrowedBook(
                memberId,
                availableCopy.getCopyOfBookName(),
                availableCopy.getCopyOfBookId(),
                LocalDate.now(),
                librarianName,
                librarianId,
                availableCopy.getBookId()
        );
        bookLogic.addNewBorrowedBook(borrowedBook);
        // Update the book's borrow and order counts
        Book book = bookLogic.fetchBook(availableCopy.getBookId());
        boolean existingOrders = removeOrderForBorrowedBook(borrowedBook);
        if (existingOrders) {
            decreaseOrdersNumber(book);
        }
        increaseBorrowedCopies(book);
        return borrowedBook;
    }

    /**
     * Description:
     * Method for finding one copy of a book based on book ID and borrow status
     *
     * @param bookID int
     * @return CopyOfBook.class (book copy that was found)
     */
    public synchronized CopyOfBook getAvailableCopyOfBook(int bookID) {
        Connection connection = null;
        PreparedStatement ps = null;
        String queryAvailableCopy = "SELECT * FROM copy_of_book WHERE BookId = ? AND borrowStatus = 'NotBorrowed' LIMIT 1";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(queryAvailableCopy);
            ps.setInt(1, bookID);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                // Retrieve copy of book attributes from the result set
                int copyOfBookId = resultSet.getInt("CopyOfBookId");
                String copOfBookName = resultSet.getString("bookName");
                String shelfLocation = resultSet.getString("ShelfLocation");
                String barcode = resultSet.getString("Barcode");

                // Convert the string value to BorrowStatus enum
                String borrowStatusString = resultSet.getString("borrowStatus");
                BorrowStatus borrowStatus = "NotBorrowed".equalsIgnoreCase(borrowStatusString) ? BorrowStatus.NOT_BORROWED : BorrowStatus.BORROWED;

                int bookId = resultSet.getInt("BookId");
                // Create and return a CopyOfBook object
                CopyOfBook availablecopy = new CopyOfBook(copyOfBookId, copOfBookName, borrowStatus, shelfLocation, barcode, bookId);
                return availablecopy;
            }
        } catch (SQLException e) {
            System.out.println("Error while finding book: " + e.getMessage());
        } finally {
            try {
                // Don't close the connection since it's managed by dbConnector
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null; // Book not found
    }

    /**
     * Description:
     * Method for updating the return date of the borrowed book
     *
     * @param membershipNum int
     * @param CopyOfBookId  int
     * @param newReturnDate LocalDate.class
     * @return boolean (true if changed in database)
     */
    public synchronized boolean setReturnDate(int membershipNum, int CopyOfBookId, LocalDate newReturnDate) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE borrowed_book " + "SET returnDate = ? " + "WHERE membershipNumber = ? AND CopyOfBookId = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Set parameters
            ps.setDate(1, java.sql.Date.valueOf(newReturnDate)); // Set the new return date
            ps.setInt(2, membershipNum);         // Membership number
            ps.setInt(3, CopyOfBookId);             // Copy of book ID

            // Execute the update query
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.out.println("Failed to update return date: " + e.getMessage());
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
     * Method for setting the librarian id in the borrowed book
     *
     * @param memberNumber int
     * @param copyOfBookId int
     * @param librarianName String.class
     * @param librarianID int
     * @param extensionDate LocalDate.class
     * @return boolean (true if changed in database)
     */
    public synchronized boolean setLibrarianForReturnDate(int memberNumber, int copyOfBookId, String librarianName,
                                                          int librarianID, LocalDate extensionDate) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "UPDATE borrowed_book SET librarianName = ?,librarianID=?, extensionDate = ? WHERE membershipNumber = ? AND CopyOfBookId = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            // Set parameters
            ps.setString(1, librarianName); // Set the librarian name
            ps.setInt(2, librarianID); // Set the librarian id
            ps.setDate(3, java.sql.Date.valueOf(extensionDate)); // Set the new return date
            ps.setInt(4, memberNumber);         // Membership number
            ps.setInt(5, copyOfBookId);             // Copy of book ID

            // Execute the update query
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            System.out.println("Failed to update librarian: " + e.getMessage());
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
     * Method for returning all the borrowed books
     *
     * @return borrowList List.class
     */
    public synchronized List<BorrowedBook> importAllBorrowedBooks() {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM borrowed_book";
        List<BorrowedBook> borrowList = new ArrayList<>();
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                java.sql.Date extensionDateSql = rs.getDate("extensionDate");
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
     * Method for increasing number of borrowed copies in given book
     *
     * @param book Book.class
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean increaseBorrowedCopies(Book book) {
        Connection conn = null;
        PreparedStatement ps = null;
        // Query to update the number of borrowed copies
        String query = "UPDATE book_db SET NumberOfBorrowedCopies = NumberOfBorrowedCopies + 1 WHERE bookID = ?";
        // Check if book exists
        if (book == null) {
            return false; // Book doesn't exist
        }
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, book.getBookID());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Failed to increase borrowed copies: " + e.getMessage());
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
     * Method for deleting a borrowed book in the database based on the given book's id
     *
     * @param bookid int
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean deleteBorrowedBook(int bookid) {
        Connection connection = null;
        PreparedStatement ps = null;
        String query = "DELETE FROM borrowed_book WHERE CopyOfBookId = ?";
        try {
            connection = dbConnector.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, bookid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to delete borrowed book: " + e.getMessage());
            e.printStackTrace();
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
     * Method for updating the arrival status of an ordered book
     *
     * @param bookName String.class
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean changeArrivalStatus(String bookName) {
        BookLogic bookLogic = new BookLogic();
        OrderedBook found = bookLogic.importOrderedBooksByBookName(bookName);
        if (found != null) {
            Connection connection = null;
            PreparedStatement ps = null;
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
                int rowsUpdated = ps.executeUpdate();
                return rowsUpdated > 0; // Return true if at least one row was updated
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Return false in case of an exception
            } finally {
                try {
                    // Don't close the connection since it's managed by dbConnector
                    if (ps != null) ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false; // Return false if no matching order was found
    }

    /**
     * Description:
     * Method for retrieving a book based on the given book's id
     *
     * @param bookId int
     * @return Book.class (book that was found)
     */
    public synchronized Book findBookById(int bookId) {
        Connection conn = null;
        PreparedStatement ps = null;
        String query = "SELECT * FROM book_db WHERE bookID = ?";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getString("bookName"),
                        rs.getString("bookSubject"),
                        rs.getInt("NumberOfCopies"),
                        rs.getInt("NumberOfBorrowedCopies"),
                        rs.getString("keywords"),
                        IsOrdered.valueOf(rs.getString("isOrdered").toUpperCase()),
                        bookId,
                        rs.getInt("numberOforders")
                );
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
        return null;
    }

    /**
     * Description:
     * Method for decreasing number of borrowed book copies based on given book's id
     *
     * @param bookid int
     * @return boolean (true if changed in the database)
     */
    public synchronized boolean decreaseBorrowedCopies(int bookid) {
        Connection conn = null;
        PreparedStatement ps = null;
        String query = "UPDATE book_db SET NumberOfBorrowedCopies = NumberOfBorrowedCopies - 1 WHERE bookID = ?";
        try {
            conn = dbConnector.getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, bookid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to decrease borrowed copies: " + e.getMessage());
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
}