package logic.BookControl;

import java.io.Serializable;

/**
 * Description:
 * Class for the book copies in the system
 */
public class CopyOfBook implements Serializable {
    private static final long serialVersionUID = 1L;
    private int CopyOfBookId;
    private String CopyOfBookName;
    private BorrowStatus borrowStatus;
    private String shelfLocation;
    private String barcode;
    // Attributes for the book (only the ones that are relevant for CopyOfBook)
    private int bookId;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param CopyOfBookId   int
     * @param CopyOfBookName String.class
     * @param borrowStatus   BorrowStatus.class
     * @param shelfLocation  String.class
     * @param barcode        String.class
     * @param bookId         int
     */
    public CopyOfBook(int CopyOfBookId, String CopyOfBookName, BorrowStatus borrowStatus, String shelfLocation,
                      String barcode, int bookId) {
        this.CopyOfBookId = CopyOfBookId;
        this.CopyOfBookName = CopyOfBookName;
        this.borrowStatus = borrowStatus;
        this.shelfLocation = shelfLocation;
        this.barcode = barcode;
        this.bookId = bookId;
    }

    /**
     * Description:
     * Getter method for the book copy's book name
     *
     * @return CopyOfBookName String.class
     */
    public String getCopyOfBookName() {
        return CopyOfBookName;
    }

    /**
     * Description:
     * Getter method for the book copy's id
     *
     * @return CopyOfBookId int
     */
    public int getCopyOfBookId() {
        return CopyOfBookId;
    }

    /**
     * Description:
     * Getter method for the book copy's borrow status
     *
     * @return borrowStatus BorrowStatus.class
     */
    public BorrowStatus getBorrowStatus() {
        return borrowStatus;
    }

    /**
     * Description:
     * Setter method for the book copy's borrow status
     *
     * @param borrowStatus BorrowStatus.class
     */
    public void setBorrowStatus(BorrowStatus borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    /**
     * Description:
     * Getter method for the book copy's shelf location
     *
     * @return shelfLocation String.class
     */
    public String getShelfLocation() {
        return shelfLocation;
    }

    /**
     * Description:
     * Getter method for the book copy's barcode
     *
     * @return barcode String.class
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Description:
     * Getter method for the book copy's book id
     *
     * @return bookId int
     */
    public int getBookId() {
        return bookId;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    @Override
    public String toString() {
        return "(CopyOfBook) CopyID: " + this.getCopyOfBookId() + ", borrowed: " + this.getBorrowStatus()
                + ", location: " + this.getShelfLocation() + ", barcode: " + this.getBarcode() + ", BookID: " + this.getBookId();
    }
}