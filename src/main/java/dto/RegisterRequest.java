package dto;

public class RegisterRequest {
    private int membershipNumber;
    private String fullName;
    private String userName;
    private String password;
    private String phone;
    private String email;

    public int getMembershipNumber(){ return membershipNumber; }
    public void setMembershipNumber(int membershipNumber){ this.membershipNumber = membershipNumber; }
    
    public String getFullName(){ return fullName; }
    public void setFullName(String fullName){ this.fullName = fullName; }

    public String getUserName(){ return userName; }
    public void setUserName(String userName){ this.userName = userName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String toString() {
        return "RegisterRequest{" +
                "membershipNumber=" + membershipNumber +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
