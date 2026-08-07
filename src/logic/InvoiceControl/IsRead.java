package logic.InvoiceControl;

/**
 * Description:
 * Enum class for the invoice message's read status
 */
public enum IsRead {
    /**
     * Attributes of the enum ('Read', 'notRead')
     */
    READ(true), NOT_READ(false);

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
    IsRead(boolean value) {
        this.value = value;
    }

    /**
     * Description:
     * Getter method for the invoice message's string value
     *
     * @return String.class ('Read', 'notRead')
     */
    public String getStringValue() {
        if (value)
            return "Read";
        else
            return "notRead";
    }

    /**
     * Description:
     * Getter method for the invoice message's value
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
            return "Read";
        else
            return "Not Read";
    }
}