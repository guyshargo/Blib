package logic.BookControl;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Description:
 * Class for the borrowed books in the system
 */
public class BorrowedBook implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate borrowDate; // Borrowed date of the book's copy
    private LocalDate returnDate; // Return date of the book's copy
    private int membershipNumber; // Member ID that borrowed the book
    private String nameOfBook; // Name of the book
    private String librarianName; // Librarian name that made the borrow
    private int copyOfBookId; // Copy of book ID of the borrowed book
    private LocalDate extensionDate; // Extension date
    public int librarianId;
    public int bookId;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param membershipNumber int
     * @param nameOfBook       String.class
     * @param copyOfBookId     int
     * @param librarianName    String.class
     * @param librarianId      int
     * @param borrowDate       LocalDate.class
     * @param returnDate       LocalDate.class
     * @param extensionDate    LocalDate.class
     * @param bookId           int
     */
    public BorrowedBook(int membershipNumber, String nameOfBook, int copyOfBookId, String librarianName, int librarianId,
                        LocalDate borrowDate, LocalDate returnDate, LocalDate extensionDate, int bookId) {
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.bookId = bookId;
        this.membershipNumber = membershipNumber;
        this.nameOfBook = nameOfBook;
        this.librarianName = librarianName;
        this.librarianId = librarianId;
        this.copyOfBookId = copyOfBookId;
        this.extensionDate = extensionDate;
    }

    /**
     * Description:
     * Method for constructing the given class (borrow date to current)
     *
     * @param membershipNumber int
     * @param nameOfBook       String.class
     * @param copyOfBookId     int
     * @param librarianName    String.class
     */
    public BorrowedBook(int membershipNumber, String nameOfBook, int copyOfBookId, String librarianName) {
        this.borrowDate = LocalDate.now(); // Set current date as borrow date
        this.returnDate = this.borrowDate.plusWeeks(2); // Default return date to 2 weeks later
        this.copyOfBookId = copyOfBookId;
        this.membershipNumber = membershipNumber;
        this.nameOfBook = nameOfBook;
        this.librarianName = librarianName;
    }

    /**
     * Description:
     * Method for constructing the given class (return date to 2 weeks from borrowed date)
     *
     * @param membershipNumber int
     * @param nameOfBook       String.class
     * @param copyOfBookId     int
     * @param borrowDate       LocalDate.class
     * @param librarianName    String.class
     * @param librarianId      int
     * @param bookId           int
     */
    public BorrowedBook(int membershipNumber, String nameOfBook, int copyOfBookId, LocalDate borrowDate,
                        String librarianName, int librarianId, int bookId) {
        this.borrowDate = borrowDate;
        this.librarianId = librarianId;
        this.librarianName = librarianName;
        this.returnDate = borrowDate.plusWeeks(2); // Default return date to 2 weeks later
        this.bookId = bookId;
        this.membershipNumber = membershipNumber;
        this.nameOfBook = nameOfBook;
        this.copyOfBookId = copyOfBookId;
    }

    /**
     * Description:
     * Getter method for the borrowed book's borrow date
     *
     * @return borrowDate LocalDate.class
     */
    public LocalDate getBorrowDate() {
        return this.borrowDate;
    }

    /**
     * Description:
     * Getter method for the borrowed book's return date
     *
     * @return returnDate LocalDate.class
     */
    public LocalDate getReturnDate() {
        return this.returnDate;
    }

    /**
     * Description:
     * Getter method for the borrowed book's member id
     *
     * @return membershipNumber int
     */
    public int getMembershipNumber() {
        return this.membershipNumber;
    }

    /**
     * Description:
     * Getter method for the borrowed book's book name
     *
     * @return nameOfBook String.class
     */
    public String getNameOfBook() {
        return this.nameOfBook;
    }

    /**
     * Description:
     * Getter method for the borrowed book's librarian name
     *
     * @return librarianName String.class
     */
    public String getLibrarianName() {
        return this.librarianName;
    }

    /**
     * Description:
     * Getter method for the borrowed book's copy id
     *
     * @return copyOfBookId int
     */
    public int getCopyOfBookId() {
        return this.copyOfBookId;
    }

    /**
     * Description:
     * Getter method for the borrowed book's extension date
     *
     * @return extensionDate LocalDate.class
     */
    public LocalDate getExtensionDate() {
        return this.extensionDate;
    }

    /**
     * Description:
     * Getter method for the borrowed book's librarian id
     *
     * @return librarianId int
     */
    public int getLibrarianId() {
        return this.librarianId;
    }

    /**
     * Description:
     * Getter method for the borrowed book's book id
     *
     * @return bookId int
     */
    public int getBookId() {
        return this.bookId;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "(BorrowedBook) " + this.getNameOfBook()
                + " (memberID: " + this.membershipNumber
                + ", copyOfBookID: " + this.getCopyOfBookId()
                + ", librarian name: " + this.getLibrarianName()
                + ", borrowed on: " + this.getBorrowDate().format(formatter)
                + ", return on: " + this.getReturnDate().format(formatter)
                + (this.extensionDate != null ? ", extension date: " + this.getExtensionDate().format(formatter) : "")
                + ")";
    }
}