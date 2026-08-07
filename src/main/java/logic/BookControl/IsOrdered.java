package logic.BookControl;

/**
 * Description:
 * Enum class for the book's order status
 */
public enum IsOrdered {
    /**
     * Attributes of the enum ('yes', 'no')
     */
    YES(true), NO(false);

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
    IsOrdered(boolean value) {
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
            return "Yes";
        else
            return "No";
    }
}