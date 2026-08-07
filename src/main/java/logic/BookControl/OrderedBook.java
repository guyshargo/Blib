package logic.BookControl;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Description:
 * Class for the ordered books in the system
 */
public class OrderedBook implements Serializable {
    private static final long serialVersionUID = 1L;
    // Order ID
    private int orderID;
    // Member's name that wants to order the book
    private String memberName;
    // Member's phone number that wants to order the book
    private String memberPhone;
    // Member's email that wants to order the book
    private String memberEmail;
    // Member ID that wants to order the book
    private int membershipNumber;
    // LocalDate of the ordering date of the book
    private LocalDate orderDate;
    // Enum if the ordered book arrived or not
    private ArrivalStatus arrivalStatus;
    // Book ID of the ordered book
    private int BookId;
    // Name of the book
    private String bookName;
    // Arrival date of the book
    private LocalDate arrivalDate;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param orderID          int
     * @param memberName       String.class
     * @param membershipNumber int
     * @param memberPhone      String.class
     * @param memberEmail      String.class
     * @param BookId           int
     * @param bookName         String.class
     * @param orderDate        LocalDate.class
     * @param arrivalStatus    ArrivalStatus.class
     * @param arrivalDate      LocalDate.class
     */
    public OrderedBook(int orderID, String memberName, int membershipNumber, String memberPhone, String memberEmail,
                       int BookId, String bookName, LocalDate orderDate, ArrivalStatus arrivalStatus,
                       LocalDate arrivalDate) {
        this.orderID = orderID;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.memberEmail = memberEmail;
        this.membershipNumber = membershipNumber;
        this.orderDate = orderDate;
        this.arrivalStatus = arrivalStatus;
        this.BookId = BookId;
        this.bookName = bookName;
        this.arrivalDate = arrivalDate;
    }

    /**
     * Description:
     * Getter method for the ordered book's id
     *
     * @return orderID int
     */
    public int getOrderID() {
        return this.orderID;
    }

    /**
     * Description:
     * Getter method for the ordered book's member's name
     *
     * @return memberName String.class
     */
    public String getMemberName() {
        return this.memberName;
    }

    /**
     * Description:
     * Getter method for the ordered book's member's email
     *
     * @return memberEmail String.class
     */
    public String getMemberEmail() {
        return this.memberEmail;
    }

    /**
     * Description:
     * Getter method for the ordered book's member's id
     *
     * @return membershipNumber int
     */
    public int getMembershipNumber() {
        return this.membershipNumber;
    }

    /**
     * Description:
     * Getter method for the ordered book's date
     *
     * @return orderDate LocalDate.class
     */
    public LocalDate getOrderDate() {
        return this.orderDate;
    }

    /**
     * Description:
     * Getter method for the ordered book's arrival status
     *
     * @return arrivalStatus ArrivalStatus.class
     */
    public ArrivalStatus getArrivalStatus() {
        return this.arrivalStatus;
    }

    /**
     * Description:
     * Getter method for the ordered book's name
     *
     * @return bookName String.class
     */
    public String getBookName() {
        return this.bookName;
    }

    /**
     * Description:
     * Getter method for the ordered book's book id
     *
     * @return BookId int
     */
    public int getBookID() {
        return this.BookId;
    }

    /**
     * Description:
     * Getter method for the ordered book's arrival date
     *
     * @return arrivalDate LocalDate.class
     */
    public LocalDate getArrivalDate() {
        return this.arrivalDate;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    public String toString() {
        return "(OrderBook) " + this.getBookName() + "(id: " + this.getOrderID() + ", name: " + this.getMemberName() +
                ", memberID: " + this.membershipNumber + ", bookID: " + this.getBookID() + ", email" +
                this.getMemberEmail() + ", ordered on: " + this.getOrderDate() + ", arrived?:" +
                this.getArrivalStatus() + ", date of arrival" + this.getArrivalDate() + ")\n";
    }
}