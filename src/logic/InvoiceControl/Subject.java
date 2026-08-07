package logic.InvoiceControl;

/**
 * Description:
 * Enum class for the invoice message's subject
 */
public enum Subject {
    /**
     * Attributes of the enum ('General', 'Extension')
     */
    GENERAL_MESSAGE("General"), EXTENSION("Extension");

    /**
     * String value of the enum
     */
    private String value;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param value String.class
     */
    Subject(String value) {
        this.value = value;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    public String toString() {
        return this.value;
    }
}