package logic.ReportControl;

import java.time.LocalDate;

/**
 * Description:
 * Class for the borrow tracking in the reports
 */
public class BorrowTracking {
    /**
     * Date of the borrowing event
     */
    private LocalDate date;

    /**
     * Number of borrowed books on this date
     */
    private int borrowCount;

    /**
     * Number of late returns on this date
     */
    private int lateCount;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param date        LocalDate.class
     * @param borrowCount int
     * @param lateCount   int
     */
    public BorrowTracking(LocalDate date, int borrowCount, int lateCount) {
        this.date = date;
        this.borrowCount = borrowCount;
        this.lateCount = lateCount;
    }

    /**
     * Description:
     * Getter method for the borrow tracking date
     *
     * @return date LocalDate.class
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Description:
     * Setter method for the borrow tracking date
     *
     * @param date LocalDate.class
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Description:
     * Getter method for the borrow tracking borrowing counts
     *
     * @return borrowCount int
     */
    public int getBorrowCount() {
        return borrowCount;
    }

    /**
     * Description:
     * Getter method for the borrow tracking late borrowing counts
     *
     * @return lateCount int
     */
    public int getLateCount() {
        return lateCount;
    }
}