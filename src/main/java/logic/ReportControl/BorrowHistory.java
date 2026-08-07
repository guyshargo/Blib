package logic.ReportControl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Description:
 * Class for the member's borrow history report in the system
 */
public class BorrowHistory {
    private int copyOfBookId;
    private int memberId;
    private String memberName;
    private String bookName;
    private LocalDate borrowDate;
    private LocalDate originalReturnDate;
    private LocalDate actualReturnDate;
    private int lateDays;  // Number of late days

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param memberId           int
     * @param memberName         String.class
     * @param bookName           String.class
     * @param borrowDate         LocalDate.class
     * @param originalReturnDate LocalDate.class
     * @param actualReturnDate   LocalDate.class
     * @param copyOfBookId       int
     */
    public BorrowHistory(int memberId, String memberName, String bookName, LocalDate borrowDate,
                         LocalDate originalReturnDate, LocalDate actualReturnDate, int copyOfBookId) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookName = bookName;
        this.borrowDate = borrowDate;
        this.originalReturnDate = originalReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.lateDays = calculateLateDays(originalReturnDate, actualReturnDate);
        this.copyOfBookId = copyOfBookId;
    }

    /**
     * Description:
     * Method for constructing the given class (with no actual return date)
     *
     * @param memberId           int
     * @param memberName         String.class
     * @param bookName           String.class
     * @param borrowDate         LocalDate.class
     * @param originalReturnDate LocalDate.class
     * @param CopyOfBookId       int
     */
    public BorrowHistory(int memberId, String memberName, String bookName, LocalDate borrowDate,
                         LocalDate originalReturnDate, int CopyOfBookId) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookName = bookName;
        this.borrowDate = borrowDate;
        this.originalReturnDate = originalReturnDate;
        this.actualReturnDate = null;
        this.copyOfBookId = CopyOfBookId;
        this.lateDays = 0;
    }

    /**
     * Description:
     * Method for calculating late days
     *
     * @param originalReturnDate LocalDate.class
     * @param actualReturnDate   LocalDate.class
     * @return int (number of days late)
     */
    public int calculateLateDays(LocalDate originalReturnDate, LocalDate actualReturnDate) {
        if (actualReturnDate != null && actualReturnDate.isAfter(originalReturnDate)) {
            return (int) ChronoUnit.DAYS.between(originalReturnDate, actualReturnDate);
        }
        return 0;  // If not late or the book is not yet returned
    }

    /**
     * Description:
     * Getter method for the borrow history report's member name
     *
     * @return memberName String.class
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * Description:
     * Getter method for the borrow history report's member id
     *
     * @return memberId int
     */
    public int getMemberId() {
        return memberId;
    }

    /**
     * Description:
     * Getter method for the borrow history report's member copy of book id
     *
     * @return copyOfBookId int
     */
    public int getCopyOfBookId() {
        return copyOfBookId;
    }

    /**
     * Description:
     * Setter method for the borrow history report's member copy of book id
     *
     * @param copyOfBookId int
     */
    public void setCopyOfBookId(int copyOfBookId) {
        this.copyOfBookId = copyOfBookId;
    }

    /**
     * Description:
     * Setter method for the borrow history report's member id
     *
     * @param memberId int
     */
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    /**
     * Description:
     * Getter method for the borrow history report's book name
     *
     * @return bookName String.class
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Description:
     * Setter method for the borrow history report's book name
     *
     * @param bookName String.class
     */
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    /**
     * Description:
     * Getter method for the borrow history report's date
     *
     * @return borrowDate LocalDate.class
     */
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    /**
     * Description:
     * Setter method for the borrow history report's date
     *
     * @param borrowDate LocalDate.class
     */
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Description:
     * Getter method for the borrow history report's original return date
     *
     * @return originalReturnDate LocalDate.class
     */
    public LocalDate getOriginalReturnDate() {
        return originalReturnDate;
    }

    /**
     * Description:
     * Setter method for the borrow history report's original return date
     *
     * @param originalReturnDate LocalDate.class
     */
    public void setOriginalReturnDate(LocalDate originalReturnDate) {
        this.originalReturnDate = originalReturnDate;
    }

    /**
     * Description:
     * Getter method for the borrow history report's actual return date
     *
     * @return actualReturnDate LocalDate.class
     */
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    /**
     * Description:
     * Setter method for the borrow history report's actual return date
     *
     * @param actualReturnDate LocalDate.class
     */
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
        this.lateDays = calculateLateDays(originalReturnDate, actualReturnDate);  // Recalculate late days
    }

    /**
     * Description:
     * Getter method for the borrow history report's late days
     *
     * @return lateDays int
     */
    public int getLateDays() {
        return lateDays;
    }

    /**
     * Description:
     * Setter method for the borrow history report's late days
     *
     * @param lateDays int
     */
    public void setLateDays(int lateDays) {
        this.lateDays = lateDays;
    }
}