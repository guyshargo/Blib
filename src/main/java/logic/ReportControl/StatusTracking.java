package logic.ReportControl;

import java.time.LocalDate;

/**
 * Description:
 * Class for the status tracking in the reports
 */
public class StatusTracking {
    private LocalDate date;
    private int frozenMembers;
    private int notFrozenMembers;

    /**
     * Description:
     * Method for constructing the given class (data is an undefined list)
     *
     * @param date             LocalDate.class
     * @param frozenMembers    int
     * @param notFrozenMembers int
     */
    public StatusTracking(LocalDate date, int frozenMembers, int notFrozenMembers) {
        this.date = date;
        this.frozenMembers = frozenMembers;
        this.notFrozenMembers = notFrozenMembers;
    }

    /**
     * Description:
     * Getter method for the status tracking date
     *
     * @return date LocalDate.class
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Description:
     * Setter method for the status tracking date
     *
     * @param date LocalDate.class
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Description:
     * Getter method for the status tracking number of frozen members
     *
     * @return frozenMembers int
     */
    public int getFrozenMembers() {
        return frozenMembers;
    }

    /**
     * Description:
     * Getter method for the status tracking number of non-frozen members
     *
     * @return notFrozenMembers int
     */
    public int getNotFrozenMembers() {
        return notFrozenMembers;
    }
}