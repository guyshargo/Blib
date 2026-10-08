package blib.enums;

public enum ActivityType {
    BORROW("Borrowing A Book", "borrow"),
    DEFAULT("Default", "default"),
    RETURN("Returning A Book", "returning"),
    FREEZE_STATUS("Freeze Status Changed", "freezeStatus"),
    EXTEND("Extending Borrowed Book", "extendBorrow"),
    ORDER("Ordering a Book", "order"),
    CANCEL_ORDER("Cancel order of Book", "cancelOrder"),
    REGISTRATION("Registration", "registerMember"),
    LATE_BOOK_RETURN("Returned Book Late", "lateBookReturn");

    private String value;
    private String DBvalue;

    ActivityType(String value, String DBvalue) {
        this.value = value;
        this.DBvalue = DBvalue;
    }

    public String getValue() { return this.value; }
    public String getDBValue() { return this.DBvalue; }
}