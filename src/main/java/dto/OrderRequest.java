package dto;

public class OrderRequest {
    private String bookName;
    private int bookId;
    private int memberId;
    private String memberName;
    private String memberPhone;
    private String memberEmail;

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberPhone() { return memberPhone; }
    public void setMemberPhone(String memberPhone) { this.memberPhone = memberPhone; }
    public String getMemberEmail() { return memberEmail; }
    public void setMemberEmail(String memberEmail) { this.memberEmail = memberEmail; }
}
