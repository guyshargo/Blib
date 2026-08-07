package logic.InvoiceControl;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 * Class for implementing messages to librarians invoice
 */
public class InvoiceMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private int messageID;
    private int membershipNumber;
    private String username;
    private String name;
    private Subject subject;
    private String content;
    private Date messageDate;
    private IsRead isRead;

    /**
     * Description:
     * Method for constructing the given enum class
     *
     * @param messageID        int
     * @param membershipNumber int
     * @param username         String.class
     * @param name             String.class
     * @param subject          Subject.class
     * @param content          String.class
     * @param messageDate      Date.class
     * @param isRead           IsRead.class
     */
    public InvoiceMessage(int messageID, int membershipNumber, String username, String name, Subject subject,
                          String content, Date messageDate, IsRead isRead) {
        this.messageID = messageID;
        this.membershipNumber = membershipNumber;
        this.username = username;
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.messageDate = messageDate;
        this.isRead = isRead;
    }

    /**
     * Description:
     * Getter method for the invoice message's id
     *
     * @return messageID int
     */
    public int getMessageID() {
        return this.messageID;
    }

    /**
     * Description:
     * Getter method for the invoice message's membership number
     *
     * @return membershipNumber int
     */
    public int getMembershipNumber() {
        return this.membershipNumber;
    }

    /**
     * Description:
     * Getter method for the invoice message's member username
     *
     * @return username String.class
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Description:
     * Getter method for the invoice message's member name
     *
     * @return name String.class
     */
    public String getName() {
        return this.name;
    }

    /**
     * Description:
     * Getter method for the invoice message's subject
     *
     * @return subject Subject.class
     */
    public Subject getSubject() {
        return this.subject;
    }

    /**
     * Description:
     * Getter method for the invoice message's content
     *
     * @return content String.class
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Description:
     * Getter method for the invoice message's date
     *
     * @return messageDate Date.class
     */
    public Date getMessageDate() {
        return this.messageDate;
    }

    /**
     * Description:
     * Getter method for the invoice message's read status
     *
     * @return isRead IsRead.class
     */
    public IsRead getIsRead() {
        return this.isRead;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    public String toString() {
        return "(InvoiceMessage) " + this.getSubject().toString() + "(id: " + this.getMessageID() + ", memberID: " +
                this.getMembershipNumber() + ", username: " + this.getUsername() + ", name: " + this.getName() +
                ", read?: " + this.getIsRead().toString() + ", date: " + this.getMessageDate().toString() + ")";
    }
}