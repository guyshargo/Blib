package logic.ReportControl;

/**
 * Description:
 * Enum class for the report's type
 */
public enum ReportType {
    /**
     * Attributes of the enum (Member status report, Borrow report,status tracking,borrow tracking)
     */
    MEMBER_STATUS_REPORT("memberStatusReport"), BORROW_REPORT("borrowReport"),
    STATUS_TRACKING("statusTracking"),
    BORROW_TRACKING("borrowTracking");

    /**
     * String value of the enum
     */
    private final String value;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param value String.class
     */
    ReportType(String value) {
        this.value = value;
    }

    /**
     * Description:
     * Getter method for the enum's string value
     *
     * @return value String.class
     */
    public String getValue() {
        return value;
    }

    /**
     * Description:
     * Method for generating a string representing the given enum class object
     *
     * @return value String.class
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Description:
     * Method for getting the enum from string value
     *
     * @param value String.class
     * @return type ReportType.class
     */
    public static ReportType fromValue(String value) {
        for (ReportType type : ReportType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown report type: " + value);
    }
}