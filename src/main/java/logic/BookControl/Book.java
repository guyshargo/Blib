package logic.BookControl;

import java.io.Serializable;

/**
 * Description:
 * Class for the books in the system
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    // Book's name
    private String bookName;
    // Subject of the book
    private String bookSubject;
    // Number of copies of the book in the library
    private int NumberOfCopies;
    // Number of copies borrowed of the book in the library
    private int NumberOfBorrowedCopies;
    // Highlited keywords for searching the book
    private String keywords;
    // Is the book already ordered
    private IsOrdered isOrdered;
    // Book primary key ID for the DB
    private int bookID;
    // Number of orders for the book by members
    private int numberOforders;
    //Summary of the book
    private String BookSummary;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param BookName               String.class
     * @param bookSubject            Subject.class
     * @param NumberOfCopies         int
     * @param NumberOfBorrowedCopies int
     * @param keywords               String.class
     * @param isOrdered              IsOrdered.class
     * @param bookID                 int
     * @param numberOforders         int
     */
    public Book(String BookName, String bookSubject, int NumberOfCopies, int NumberOfBorrowedCopies, String keywords,
                IsOrdered isOrdered, int bookID, int numberOforders) {
        this.bookName = BookName;
        this.bookSubject = bookSubject;
        this.NumberOfCopies = NumberOfCopies;
        this.NumberOfBorrowedCopies = NumberOfBorrowedCopies;
        this.keywords = keywords;
        this.isOrdered = isOrdered;
        this.bookID = bookID;
        this.numberOforders = numberOforders;
    }

    /**
     * Description:
     * Getter method for the book's name
     *
     * @return bookName String.class
     */
    public String getBookName() {
        return this.bookName;
    }

    /**
     * Description:
     * Setter method for the book's name
     *
     * @param BookName String.class
     */
    public void setBookName(String BookName) {
        this.bookName = BookName;
    }

    /**
     * Description:
     * Getter method for the book's subject
     *
     * @return bookSubject String.class
     */
    public String getBookSubject() {
        return this.bookSubject;
    }

    /**
     * Description:
     * Getter method for the book's number of copies
     *
     * @return NumberOfCopies int
     */
    public int getNumberOfCopies() {
        return this.NumberOfCopies;
    }

    /**
     * Description:
     * Getter method for the book's number of borrowed copies
     *
     * @return NumberOfBorrowedCopies int
     */
    public int getNumberOfBorrowedCopies() {
        return this.NumberOfBorrowedCopies;
    }

    /**
     * Description:
     * Getter method for the book's keywords
     *
     * @return keywords String.class
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * Description:
     * Getter method for the book's ordered status
     *
     * @return boolean (true if the book is ordered)
     */
    public boolean getIsOrderedBoolean() {
        return this.isOrdered.getValue();
    }

    /**
     * Description:
     * Getter method for the book's id
     *
     * @return bookID int
     */
    public int getBookID() {
        return this.bookID;
    }

    /**
     * Description:
     * Getter method for the book's number of orders
     *
     * @return numberOforders int
     */
    public int getNumberOforders() {
        return this.numberOforders;
    }

    /**
     * Description:
     * Getter method for the book summary
     *
     * @return BookSummary String.class
     */
    public String getBookSummary() {
        return this.BookSummary;
    }

    /**
     * Description:
     * Setter method for the book's Summary
     *
     * @param BookSummary String.class
     */
    public void setBookSummary(String BookSummary) {
        this.BookSummary = BookSummary;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    public String toString() {
        return "(Book) " + this.bookName + " (subject: " + this.bookSubject + ", copies: " + this.NumberOfCopies + ", borrowed: "
                + this.NumberOfBorrowedCopies + ", keywords: " + this.keywords + ", ordered?: " + this.isOrdered
                + ", id: " + this.bookID + ", orders: " + this.numberOforders + ", Summary: " + this.BookSummary + ")\n";
    }
}