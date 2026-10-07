package blib.model;

import java.io.Serializable;
import java.time.LocalDate;

import blib.enums.FreezeStatus;

/**
 * Description:
 * Class for the representing the subscribers in the system
 */
public class Member implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    private int memberId;
    private String fullName;
    private String userName;
    private String password;
    private FreezeStatus freezeStatus;
    private String email;
    private String phoneNum;
    private LocalDate freezeStatusDate;
    private String readerCardBarcode;
    private boolean loginStatus;

    public Member(){}

    
    public Member(int memberId, String fullName, String userName, String password,
                      FreezeStatus freezeStatus, String email, String phoneNum, String readerCardBarcode) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.freezeStatus = freezeStatus;
        this.email = email;
        this.phoneNum = phoneNum;
        this.readerCardBarcode = readerCardBarcode;
    }

 
    public Member(int memberId, String fullName, String userName, String password,
                      FreezeStatus freezeStatus, String email, String phoneNum, LocalDate freezeStatusDate, String readerCardBarcode) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.freezeStatus = freezeStatus;
        this.email = email;
        this.phoneNum = phoneNum;
        this.freezeStatusDate = freezeStatusDate;
        this.readerCardBarcode = readerCardBarcode;
    }


    public void setLoginStatus(boolean loginStatus) {
        this.loginStatus = loginStatus;
    }

    public boolean getLoginStatus() {
        return loginStatus;
    }


    public String getreaderCardBarcode() {
        return readerCardBarcode;
    }


    public void setreaderCardBarcode(String readerCardBarcode) {
        this.readerCardBarcode = readerCardBarcode;
    }


    public LocalDate getFreezeStatusDate() {
        return freezeStatusDate;
    }

    public void setFreezeStatusDate(LocalDate freezeStatusDate) {
        this.freezeStatusDate = freezeStatusDate;
    }

    public int getMembershipNumber() {
        return memberId;
    }


    public void setMembershipNumber(int memberId) {
        this.memberId = memberId;
    }


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public FreezeStatus getFreezeStatus() {
        return freezeStatus;
    }

    public void setFreezeStatus(FreezeStatus freezeStatus) {
        this.freezeStatus = freezeStatus;
    }


    public String getEmailAddress() {
        return email;
    }

    public void setEmailAddress(String email) {
        this.email = email;
    }


    public String getPhoneNumber() {
        return phoneNum;
    }

    public void setPhoneNumber(String phoneNum) {
        this.phoneNum = phoneNum;
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
                "membershipNumber=" + memberId +
                ", memberFullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", memberFreezeStatus=" + freezeStatus +
                ", emailAddress='" + email + '\'' +
                ", memberPhoneNumber='" + phoneNum + '\'' +
                '}';
    }
}