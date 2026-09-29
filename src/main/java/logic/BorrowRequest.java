package logic;

public class BorrowRequest {
    private int memberId;
    private int copyOfBookId;
    private int librarianId;
    private String librarianName;

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getCopyOfBookId() { return copyOfBookId; }
    public void setCopyOfBookId(int copyOfBookId) { this.copyOfBookId = copyOfBookId; }
    public int getLibrarianId() { return librarianId; }
    public void setLibrarianId(int librarianId) { this.librarianId = librarianId; }
    public String getLibrarianName() { return librarianName; }
    public void setLibrarianName(String librarianName) { this.librarianName = librarianName; }
}
