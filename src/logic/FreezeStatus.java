package logic;

/**
 * Description:
 * Enum class for the subscriber's freeze status
 */
public enum FreezeStatus {
    /**
     * Attributes of the enum (Frozen, notFrozen)
     */
    FROZEN("Frozen"), NOT_FROZEN("NotFrozen");

    /**
     * String value of the enum in the database
     */
    private final String dbValue;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param dbValue String.class
     */
    FreezeStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Description:
     * Getter method for the enum's database string value
     *
     * @return dbValue String.class
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Description:
     * Method for converting a database string to a FreezeStatus enum
     *
     * @param dbValue String.class
     * @return FreezeStatus.class
     */
    public static FreezeStatus fromDbValue(String dbValue) {
        for (FreezeStatus status : FreezeStatus.values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown database value for FreezeStatus: " + dbValue);
    }
}