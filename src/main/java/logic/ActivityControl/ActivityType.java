package logic.ActivityControl;

/**
 * Description:
 * Enum class of the activitie's type
 */
public enum ActivityType {
    /**
     * Attributes of the enum (Member status report, Borrow report,status tracking,borrow tracking)
     */
    BORROW("Borrowing A Book", "borrow"),
    DEFAULT("Default", "default"),
    RETURN("Returning A Book", "returning"),
    FREEZE_STATUS("Freeze Status Changed", "freezeStatus"),
    EXTEND("Extending Borrowed Book", "extendBorrow"),
    ORDER("Ordering a Book", "order"),
    CANCEL_ORDER("Cancel order of Book", "cancelOrder"),
    REGISTRATION("Registration", "registerMember"),
    LATE_BOOK_RETURN("Returned Book Late", "lateBookReturn");

    /**
     * Description:
     * String attribute representing description of activity type
     */
    private String value;

    /**
     * Description:
     * String attribute representing the database value of the object
     */
    private String DBvalue;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param value   String.class
     * @param DBvalue String.class
     */
    ActivityType(String value, String DBvalue) {
        this.value = value;
        this.DBvalue = DBvalue;
    }

    /**
     * Description:
     * Getter method for the enum's string value
     *
     * @return value String.class
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Description:
     * Getter method for the enum's string DBvalue
     *
     * @return DBvalue String.class
     */
    public String getDBValue() {
        return this.DBvalue;
    }
}