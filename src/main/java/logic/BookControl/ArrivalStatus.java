package logic.BookControl;

/**
 * Description:
 * Enum class for the book's arrival status
 */
public enum ArrivalStatus {
    /**
     * Attributes of the enum ('Arrived', 'notArrived')
     */
    ARRIVED(true), NOT_ARRIVED(false);

    /**
     * Boolean value of the enum
     */
    private final boolean value;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param value boolean
     */
    ArrivalStatus(boolean value) {
        this.value = value;
    }

    /**
     * Description:
     * Getter method for the enum's value
     *
     * @return value boolean
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    public String toString() {
        if (value)
            return "Arrived";
        else
            return "Not Arrived";
    }
}