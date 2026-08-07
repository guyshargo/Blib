package logic;

import java.io.Serializable;

/**
 * Description:
 * Class for the representing the librarians in the system
 */
public class Librarian implements Serializable {
    /**
     * Unique identifier for serialization
     */
    private static final long serialVersionUID = 1L;
    private String fullName;
    private String phoneNumber;
    private int librarianID; // Primary Key
    private String userName;
    private String password;
    private String emailAddress;
    private boolean loginStatus;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param fullName     String.class
     * @param phoneNumber  String.class
     * @param librarianID  int
     * @param emailAddress String.class
     * @param userName     String.class
     * @param password     String.class
     */
    public Librarian(String fullName, String phoneNumber, int librarianID, String emailAddress,
                     String userName, String password) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.librarianID = librarianID;
        this.userName = userName;
        this.password = password;
        this.emailAddress = emailAddress;
    }

    /**
     * Description:
     * Setter method for the librarian's login status
     *
     * @param loginStatus boolean
     */
    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    /**
     * Description:
     * Getter method for the librarian's login status
     *
     * @return loginStatus boolean
     */
    public boolean getLoginStatus() {
        return loginStatus;
    }

    /**
     * Description:
     * Getter method for the librarian's full name
     *
     * @return fullName String.class
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Description:
     * Setter method for the librarian's full name
     *
     * @param fullName String.class
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Description:
     * Getter method for the librarian's phone number
     *
     * @return phoneNumber String.class
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Description:
     * Setter method for the librarian's phone number
     *
     * @param phoneNumber String.class
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Description:
     * Getter method for the librarian's id
     *
     * @return librarianID int
     */
    public int getLibrarianID() {
        return librarianID;
    }

    /**
     * Description:
     * Setter method for the librarian's id
     *
     * @param librarianID int
     */
    public void setLibrarianID(int librarianID) {
        this.librarianID = librarianID;
    }

    /**
     * Description:
     * Getter method for the librarian's username
     *
     * @return userName String.class
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Description:
     * Setter method for the librarian's username
     *
     * @param userName String.class
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Description:
     * Getter method for the librarian's password
     *
     * @return password String.class
     */
    public String getPassword() {
        return password;
    }

    /**
     * Description:
     * Setter method for the librarian's password
     *
     * @param password String.class
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Description:
     * Getter method for the librarian's email
     *
     * @return emailAddress String.class
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Description:
     * Setter method for the librarian's email
     *
     * @param emailAddress String.class
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Description:
     * Method for generating a string representing the given class object
     *
     * @return String.class
     */
    @Override
    public String toString() {
        return "Librarian{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", librarianID=" + librarianID +
                ", userName='" + userName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}