package logic;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Description:
 * Class for the representing the subscribers in the system
 */
public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    private int membershipNumber;
    private String memberFullName;
    private String userName;
    private String password;
    private FreezeStatus memberFreezeStatus; // Use enum type for freeze status
    private String emailAddress;
    private String memberPhoneNumber;
    private LocalDate freezeStatusDate;
    private String readerCardBarcode;
    private boolean loginStatus;

    /**
     * Description:
     * Method for constructing the given class (without the freeze date)
     *
     * @param membershipNumber   int
     * @param memberFullName     String.class
     * @param userName           String.class
     * @param password           String.class
     * @param memberFreezeStatus FreezeStatus.class
     * @param emailAddress       String.class
     * @param memberPhoneNumber  String.class
     * @param readerCardBarcode  String.class
     */
    public Subscriber(int membershipNumber, String memberFullName, String userName, String password,
                      FreezeStatus memberFreezeStatus, String emailAddress, String memberPhoneNumber, String readerCardBarcode) {
        this.membershipNumber = membershipNumber;
        this.memberFullName = memberFullName;
        this.userName = userName;
        this.password = password;
        this.memberFreezeStatus = memberFreezeStatus;
        this.emailAddress = emailAddress;
        this.memberPhoneNumber = memberPhoneNumber;
        this.readerCardBarcode = readerCardBarcode;
    }

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param membershipNumber   int
     * @param memberFullName     String.class
     * @param userName           String.class
     * @param password           String.class
     * @param memberFreezeStatus FreezeStatus.class
     * @param emailAddress       String.class
     * @param memberPhoneNumber  String.class
     * @param freezeStatusDate   LocalDate.class
     * @param readerCardBarcode  String.class
     */
    public Subscriber(int membershipNumber, String memberFullName, String userName, String password,
                      FreezeStatus memberFreezeStatus, String emailAddress, String memberPhoneNumber, LocalDate freezeStatusDate, String readerCardBarcode) {
        this.membershipNumber = membershipNumber;
        this.memberFullName = memberFullName;
        this.userName = userName;
        this.password = password;
        this.memberFreezeStatus = memberFreezeStatus;
        this.emailAddress = emailAddress;
        this.memberPhoneNumber = memberPhoneNumber;
        this.freezeStatusDate = freezeStatusDate;
        this.readerCardBarcode = readerCardBarcode;
    }

    /**
     * Description:
     * Setter method for the subscriber's login status
     *
     * @param loginStatus boolean
     */
    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    /**
     * Description:
     * Getter method for the subscriber's login status
     *
     * @return loginStatus boolean
     */
    public boolean getLoginStatus() {
        return loginStatus;
    }

    /**
     * Description:
     * Getter method for the subscriber's barcode
     *
     * @return readerCardBarcode String.class
     */
    public String getreaderCardBarcode() {
        return readerCardBarcode;
    }

    /**
     * Description:
     * Setter method for the subscriber's barcode
     *
     * @param readerCardBarcode String.class
     */
    public void setreaderCardBarcode(String readerCardBarcode) {
        this.readerCardBarcode = readerCardBarcode;
    }

    /**
     * Description:
     * Getter method for the subscriber's freeze date
     *
     * @return freezeStatusDate LocalDate.class
     */
    public LocalDate getFreezeStatusDate() {
        return freezeStatusDate;
    }

    /**
     * Description:
     * Setter method for the subscriber's freeze date
     *
     * @param freezeStatusDate LocalDate.class
     */
    public void setFreezeStatusDate(LocalDate freezeStatusDate) {
        this.freezeStatusDate = freezeStatusDate;
    }

    /**
     * Description:
     * Getter method for the subscriber's membership number
     *
     * @return membershipNumber int
     */
    public int getMembershipNumber() {
        return membershipNumber;
    }

    /**
     * Description:
     * Setter method for the subscriber's membership number
     *
     * @param membershipNumber int
     */
    public void setMembershipNumber(int membershipNumber) {
        this.membershipNumber = membershipNumber;
    }

    /**
     * Description:
     * Getter method for the subscriber's full name
     *
     * @return memberFullName String.class
     */
    public String getMemberFullName() {
        return memberFullName;
    }

    /**
     * Description:
     * Setter method for the subscriber's full name
     *
     * @param memberFullName String.class
     */
    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    /**
     * Description:
     * Getter method for the subscriber's username
     *
     * @return userName String.class
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Description:
     * Setter method for the subscriber's username
     *
     * @param userName String.class
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Description:
     * Getter method for the subscriber's password
     *
     * @return password String.class
     */
    public String getMemberPassword() {
        return password;
    }

    /**
     * Description:
     * Setter method for the subscriber's password
     *
     * @param password String.class
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Description:
     * Getter method for the subscriber's freeze status
     *
     * @return memberFreezeStatus FreezeStatus.class
     */
    public FreezeStatus getMemberFreezeStatus() {
        return memberFreezeStatus;
    }

    /**
     * Description:
     * Setter method for the subscriber's freeze status
     *
     * @param memberFreezeStatus FreezeStatus.class
     */
    public void setMemberFreezeStatus(FreezeStatus memberFreezeStatus) {
        this.memberFreezeStatus = memberFreezeStatus;
    }

    /**
     * Description:
     * Getter method for the subscriber's email
     *
     * @return emailAddress String.class
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Description:
     * Setter method for the subscriber's email
     *
     * @param emailAddress String.class
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Description:
     * Getter method for the subscriber's phone number
     *
     * @return memberPhoneNumber String.class
     */
    public String getMemberPhoneNumber() {
        return memberPhoneNumber;
    }

    /**
     * Description:
     * Setter method for the subscriber's phone number
     *
     * @param memberPhoneNumber String.class
     */
    public void setMemberPhoneNumber(String memberPhoneNumber) {
        this.memberPhoneNumber = memberPhoneNumber;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    @Override
    public String toString() {
        return "Subscriber{" +
                "membershipNumber=" + membershipNumber +
                ", memberFullName='" + memberFullName + '\'' +
                ", userName='" + userName + '\'' +
                ", memberFreezeStatus=" + memberFreezeStatus +
                ", emailAddress='" + emailAddress + '\'' +
                ", memberPhoneNumber='" + memberPhoneNumber + '\'' +
                '}';
    }
}