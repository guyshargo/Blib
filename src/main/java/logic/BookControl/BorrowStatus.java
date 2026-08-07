package logic.BookControl;

/**
 * Description:
 * Enum class for the book's borrow status
 */
public enum BorrowStatus {
    /**
     * Attributes of the enum ('Borrowed', 'notBorrowed')
     */
    BORROWED(true), NOT_BORROWED(false);

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
    BorrowStatus(boolean value) {
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
            return "Borrowed";
        else
            return "NotBorrowed";
    }
}