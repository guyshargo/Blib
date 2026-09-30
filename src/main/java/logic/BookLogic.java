package logic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.MysqlConnection;
import enums.BorrowStatus;
import enums.IsOrdered;
import model.Book;
import model.CopyOfBook;

public class BookLogic {

    private final MysqlConnection dbConnector;

    //// Singleton database connector
    public BookLogic() { dbConnector = MysqlConnection.getInstance(); }

    public synchronized CopyOfBook findCopyOfBook(int copyOfBookID) {
        String queryAvailableCopy = "SELECT * FROM copy_of_book WHERE CopyOfBookId = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(queryAvailableCopy)) {
             
            ps.setInt(1, copyOfBookID);
            
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int copyOfBookId = resultSet.getInt("CopyOfBookId");
                    String copOfBookName = resultSet.getString("bookName");
                    String shelfLocation = resultSet.getString("ShelfLocation");
                    String barcode = resultSet.getString("Barcode");
                    
                    String borrowStatusString = resultSet.getString("borrowStatus");
                    BorrowStatus borrowStatus = "NotBorrowed".equalsIgnoreCase(borrowStatusString) ? BorrowStatus.NOT_BORROWED : BorrowStatus.BORROWED;
                    int bookId = resultSet.getInt("BookId");
                    
                    return new CopyOfBook(copyOfBookId, copOfBookName, borrowStatus, shelfLocation, barcode, bookId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding book: " + e.getMessage());
        }
        return null;
    }

    public synchronized CopyOfBook getAvailableCopyOfBookByBarcode(String barcode) {
        String queryAvailableCopy = "SELECT * FROM copy_of_book WHERE Barcode = ? AND borrowStatus = 'NotBorrowed' LIMIT 1";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(queryAvailableCopy)) {
             
            ps.setString(1, barcode);
            
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int copyOfBookId = resultSet.getInt("CopyOfBookId");
                    String copyOfBookName = resultSet.getString("bookName");
                    String shelfLocation = resultSet.getString("ShelfLocation");
                    String barcodeResult = resultSet.getString("Barcode");

                    String borrowStatusString = resultSet.getString("borrowStatus");
                    BorrowStatus borrowStatus = "NotBorrowed".equalsIgnoreCase(borrowStatusString) ? BorrowStatus.NOT_BORROWED : BorrowStatus.BORROWED;
                    int bookId = resultSet.getInt("BookId");
                    
                    return new CopyOfBook(copyOfBookId, copyOfBookName, borrowStatus, shelfLocation, barcodeResult, bookId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding book by barcode: " + e.getMessage());
        }
        return null;
    }

    public synchronized CopyOfBook getAvailableCopyOfBook(int bookID) {
        String queryAvailableCopy = "SELECT * FROM copy_of_book WHERE BookId = ? AND borrowStatus = 'NotBorrowed' LIMIT 1";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(queryAvailableCopy)) {
             
            ps.setInt(1, bookID);
            
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int copyOfBookId = resultSet.getInt("CopyOfBookId");
                    String copOfBookName = resultSet.getString("bookName");
                    String shelfLocation = resultSet.getString("ShelfLocation");
                    String barcode = resultSet.getString("Barcode");

                    String borrowStatusString = resultSet.getString("borrowStatus");
                    BorrowStatus borrowStatus = "NotBorrowed".equalsIgnoreCase(borrowStatusString) ? BorrowStatus.NOT_BORROWED : BorrowStatus.BORROWED;
                    int bookId = resultSet.getInt("BookId");
                    
                    return new CopyOfBook(copyOfBookId, copOfBookName, borrowStatus, shelfLocation, barcode, bookId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while finding book: " + e.getMessage());
        }
        return null; 
    }

    public synchronized boolean increaseBorrowedCopies(Book book) {
        if (book == null) {
            return false;
        }
        
        String query = "UPDATE book_db SET NumberOfBorrowedCopies = NumberOfBorrowedCopies + 1 WHERE bookID = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setInt(1, book.getBookID());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to increase borrowed copies: " + e.getMessage());
            return false;
        }
    }


    public synchronized boolean decreaseBorrowedCopies(int bookid) {
        String query = "UPDATE book_db SET NumberOfBorrowedCopies = NumberOfBorrowedCopies - 1 WHERE bookID = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setInt(1, bookid);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to decrease borrowed copies: " + e.getMessage());
            return false;
        }
    }

    public synchronized void decreaseOrdersNumber(Book book) {
        String query = "UPDATE book_db SET numberOforders = numberOforders - 1 WHERE bookID = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, book.getBookID());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("No matching order found.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    public synchronized boolean changeBookOrderStatus(int bookID, int numberOfOrders, boolean increase) {
        String query = "UPDATE book_db SET isOrdered = ?, numberOforders = ? WHERE bookID = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            if (increase) {
                ps.setString(1, "yes");
                ps.setInt(2, numberOfOrders + 1);
            } else {
                int newNumberOfOrders = numberOfOrders - 1;
                ps.setInt(2, newNumberOfOrders);
                if (newNumberOfOrders == 0) {
                    ps.setString(1, "no");
                } else {
                    ps.setString(1, "yes");
                }
            }
            ps.setInt(3, bookID);
            ps.executeUpdate(); 
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean increaseQuantityOfBook(Book book) {
        // Check if book exists
        Book existingBook = fetchBook(book.getBookID());
        if (existingBook == null) {
            return false; // Book doesn't exist
        }

        String query = "UPDATE book_db SET NumberOfCopies = NumberOfCopies + 1 WHERE bookID = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, book.getBookID());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to increase book quantity: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean decreaseQuantityOfBook(Book book) {
        // Check if book exists
        Book existingBook = fetchBook(book.getBookID());
        if (existingBook == null) {
            return false; // Book doesn't exist
        }
        
        String query = "UPDATE book_db SET NumberOfCopies = NumberOfCopies - 1 WHERE bookID = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, book.getBookID());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to decrease book quantity: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean changeBookCopyBorrowStatus(CopyOfBook copyOfBook) {
        String query = "UPDATE copy_of_book SET borrowStatus = ? WHERE CopyOfBookId = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setString(1, copyOfBook.getBorrowStatus().toString());
            ps.setInt(2, copyOfBook.getCopyOfBookId());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to update borrow status: " + e.getMessage());
            return false;
        }
    }

    public synchronized Book fetchBook(int bookID) {
        String query = "SELECT * FROM book_db WHERE bookID = ?";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setInt(1, bookID);
            
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    String bookName = resultSet.getString("BookName");
                    String bookSubject = resultSet.getString("BookSubject");
                    int numberOfCopies = resultSet.getInt("NumberOfCopies");
                    int borrowedCopies = resultSet.getInt("NumberOfBorrowedCopies");
                    String keywords = resultSet.getString("Keywords");
                    if (keywords == null) {
                        keywords = ""; 
                    }
                    int numberOfOrders = resultSet.getInt("NumberOfOrders");
                    IsOrdered isOrdered = IsOrdered.valueOf(resultSet.getString("IsOrdered").toUpperCase());

                    return new Book(bookName, bookSubject, numberOfCopies, borrowedCopies, keywords, isOrdered, bookID, numberOfOrders);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch book by ID: " + e.getMessage());
        }
        return null;
    }    

    public synchronized List<Book> searchBooks(String bookName, String bookSubject, String freeText) {
        List<Book> searchResults = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM book_db WHERE 1=1");

        // add search criteria dynamically
        boolean hasName = bookName != null && !bookName.trim().isEmpty() && !"is empty".equals(bookName);
        boolean hasSubject = bookSubject != null && !bookSubject.trim().isEmpty() && !"is empty".equals(bookSubject);
        boolean hasFreeText = freeText != null && !freeText.trim().isEmpty() && !"is empty".equals(freeText);

        if (hasName) queryBuilder.append(" AND bookName LIKE ?");
        if (hasSubject) queryBuilder.append(" AND bookSubject LIKE ?");
        
        String[] keywords = null;
        if (hasFreeText) {
            keywords = freeText.split(",");
            queryBuilder.append(" AND (");
            for (int i = 0; i < keywords.length; i++) {
                queryBuilder.append("keywords LIKE ?");
                if (i < keywords.length - 1) queryBuilder.append(" OR ");
            }
            queryBuilder.append(")");
        }

        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
             
            int paramIndex = 1;
            if (hasName) ps.setString(paramIndex++, "%" + bookName.trim().toLowerCase() + "%");
            if (hasSubject) ps.setString(paramIndex++, "%" + bookSubject.trim().toLowerCase() + "%");
            if (hasFreeText && keywords != null) {
                for (String keyword : keywords) {
                    ps.setString(paramIndex++, "%" + keyword.trim().toLowerCase() + "%");
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bookSummary = rs.getString("BookSummary");
                    if (bookSummary == null) bookSummary = "";
                    
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
                    book.setBookSummary(bookSummary);
                    searchResults.add(book);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to search books: " + e.getMessage());
        }
        return searchResults;
    }


    
}