package logic;

import model.Book;
import model.BorrowedBook;
import model.CopyOfBook;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import database.MysqlConnection;
import enums.BorrowStatus;

public class BorrowLogic {

    private final MysqlConnection dbConnector;

    // Singleton database connector
    public BorrowLogic() { dbConnector = MysqlConnection.getInstance(); }

    public synchronized BorrowedBook getCloserReturnDateBook(int bookID) {
        String query = "SELECT * FROM borrowed_book WHERE BookId = ? ORDER BY returnDate ASC LIMIT 1";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, bookID);
            
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    java.sql.Date extensionDateSql = resultSet.getDate("extensionDate");
                    LocalDate extensionDate = (extensionDateSql != null) ? extensionDateSql.toLocalDate().plusDays(1) : null;
                    
                    int membershipNumber = resultSet.getInt("membershipNumber");
                    String bookName = resultSet.getString("NameOfBook");
                    int copyOfBookId = resultSet.getInt("CopyOfBookId");
                    String librarianName = resultSet.getString("librarianName");
                    int librarianId = resultSet.getInt("librarianId");
                    LocalDate borrowDate = resultSet.getDate("borrowDate").toLocalDate().plusDays(1);
                    LocalDate returnDate = resultSet.getDate("returnDate").toLocalDate().plusDays(1);
                    int bookId = resultSet.getInt("BookId");

                    return new BorrowedBook(membershipNumber, bookName, copyOfBookId, librarianName, librarianId, borrowDate, returnDate, extensionDate, bookId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching the soonest return date: " + e.getMessage());
        }
        return null;
    }

    

    public synchronized boolean checkIfAMemberCanBorrowTheBook(int membershipNumber, CopyOfBook cpbook, Book book) {
        String orderQuery = "SELECT * FROM ordered_book WHERE bookId = ? AND membershipNumber = ? AND arrivalStatus = 'Arrived'";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(orderQuery)) {
             
            ps.setInt(1, cpbook.getBookId());
            ps.setInt(2, membershipNumber);
            
            try (ResultSet ordersResult = ps.executeQuery()) {
                boolean memberOrderedBook = ordersResult.next();
                boolean isBookOrdered = book.getIsOrderedBoolean();
                
                int numberOfAvailableCopies = book.getNumberOfCopies() - book.getNumberOfBorrowedCopies();
                
                // Check borrowing conditions
                return memberOrderedBook || !isBookOrdered || numberOfAvailableCopies > book.getNumberOforders();
            }
        } catch (SQLException e) {
            System.out.println("Error while checking if a member can borrow the book: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    

    

    

    public synchronized BorrowedBook borrowBook(int memberId, int copyOfBookId, int librarianId, String librarianName) {
        BookLogic bookLogic = new BookLogic();
        OrderLogic orderLogic = new OrderLogic();

        CopyOfBook availableCopy = bookLogic.findCopyOfBook(copyOfBookId);
        
        if (availableCopy == null || availableCopy.getBorrowStatus() == BorrowStatus.BORROWED) {
            return null; 
        }
        
        availableCopy.setBorrowStatus(BorrowStatus.BORROWED);
        boolean borrowUpdated = bookLogic.changeBookCopyBorrowStatus(availableCopy);
        if (!borrowUpdated) {
            return null; 
        }
        
        BorrowedBook borrowedBook = new BorrowedBook(
                memberId,
                availableCopy.getCopyOfBookName(),
                availableCopy.getCopyOfBookId(),
                LocalDate.now(),
                librarianName,
                librarianId,
                availableCopy.getBookId()
        );
        addNewBorrowedBook(borrowedBook);
        
        Book book = bookLogic.fetchBook(availableCopy.getBookId());
        boolean existingOrders = orderLogic.removeOrderForBorrowedBook(borrowedBook);
        if (existingOrders) {
            bookLogic.decreaseOrdersNumber(book);
        }
        bookLogic.increaseBorrowedCopies(book);
        return borrowedBook;
    }

    

    public synchronized boolean setReturnDate(int membershipNum, int CopyOfBookId, LocalDate newReturnDate) {
        String query = "UPDATE borrowed_book SET returnDate = ? WHERE membershipNumber = ? AND CopyOfBookId = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setDate(1, java.sql.Date.valueOf(newReturnDate));
            ps.setInt(2, membershipNum);
            ps.setInt(3, CopyOfBookId);

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to update return date: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean setLibrarianForReturnDate(int memberNumber, int copyOfBookId, String librarianName,
                                                          int librarianID, LocalDate extensionDate) {
        String query = "UPDATE borrowed_book SET librarianName = ?, librarianID = ?, extensionDate = ? WHERE membershipNumber = ? AND CopyOfBookId = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, librarianName);
            ps.setInt(2, librarianID);
            ps.setDate(3, java.sql.Date.valueOf(extensionDate));
            ps.setInt(4, memberNumber);
            ps.setInt(5, copyOfBookId);

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to update librarian: " + e.getMessage());
            return false;
        }
    }

    public synchronized List<BorrowedBook> importAllBorrowedBooks() {
        String query = "SELECT * FROM borrowed_book";
        List<BorrowedBook> borrowList = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
             
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
            System.out.println("Failed to fetch borrowed books: " + e.getMessage());
        }
        return borrowList;
    }

    

    public synchronized boolean deleteBorrowedBook(int bookid) {
        String query = "DELETE FROM borrowed_book WHERE CopyOfBookId = ?";
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, bookid);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to delete borrowed book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public synchronized boolean addNewBorrowedBook(BorrowedBook borrowedBook) {
        String query = "INSERT INTO borrowed_book (membershipNumber, NameOfBook, borrowDate, returnDate, librarianName, librarianID, CopyOfBookId, extensionDate, bookID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setInt(1, borrowedBook.getMembershipNumber());
            ps.setString(2, borrowedBook.getNameOfBook());
            ps.setDate(3, java.sql.Date.valueOf(borrowedBook.getBorrowDate()));
            ps.setDate(4, java.sql.Date.valueOf(borrowedBook.getReturnDate()));
            ps.setString(5, borrowedBook.getLibrarianName());
            ps.setInt(6, borrowedBook.getLibrarianId());
            ps.setInt(7, borrowedBook.getCopyOfBookId());

            if (borrowedBook.getExtensionDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(borrowedBook.getExtensionDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setInt(9, borrowedBook.getBookId());

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Failed to add borrowed book: " + e.getMessage());
            return false;
        }
    }

    

    public synchronized BorrowedBook fetchBorrowedBook(int membershipNumber, int CopyOfBookId) {
        List<BorrowedBook> borrowedBooks = this.importBorrowedBooks(membershipNumber);
        for (BorrowedBook borrow : borrowedBooks) {
            if (borrow.getMembershipNumber() == membershipNumber && borrow.getCopyOfBookId() == CopyOfBookId) {
                return borrow;
            }
        }
        return null;
    }


    public synchronized List<BorrowedBook> importBorrowedBooks(int membershipNumber) {
        String query = "SELECT * FROM borrowed_book WHERE membershipNumber=?";
        List<BorrowedBook> borrowList = new ArrayList<>();
        
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setInt(1, membershipNumber);
            
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch borrowed books: " + e.getMessage());
        }
        return borrowList;
    }
}