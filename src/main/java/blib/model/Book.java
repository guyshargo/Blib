package blib.model;

import java.io.Serializable;

import blib.enums.IsOrdered;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bookName;
    private String bookGenre;
    private int NumberOfCopies;
    private int NumberOfBorrowedCopies;
    private String keywords;
    private IsOrdered isOrdered;
    private int bookID;
    private int numberOforders;
    private String BookSummary;

    public Book(String BookName, String bookGenre, int NumberOfCopies, int NumberOfBorrowedCopies, String keywords,
                IsOrdered isOrdered, int bookID, int numberOforders) {
        this.bookName = BookName;
        this.bookGenre = bookGenre;
        this.NumberOfCopies = NumberOfCopies;
        this.NumberOfBorrowedCopies = NumberOfBorrowedCopies;
        this.keywords = keywords;
        this.isOrdered = isOrdered;
        this.bookID = bookID;
        this.numberOforders = numberOforders;
    }

    public String getBookName() { return this.bookName; }
    public void setBookName(String BookName) { this.bookName = BookName; }
    
    public String getBookGenre() { return this.bookGenre; }
    public void setBookGenre(String bookGenre) { this.bookGenre = bookGenre; }

    public int getNumberOfCopies() { return this.NumberOfCopies; }
    public void setNumberOfCopies(int NumberOfCopies) { this.NumberOfCopies = NumberOfCopies; }

    public int getNumberOfBorrowedCopies() { return this.NumberOfBorrowedCopies; }
    public void setNumberOfBorrowedCopies(int NumberOfBorrowedCopies) { this.NumberOfBorrowedCopies = NumberOfBorrowedCopies; }

    public String getKeywords() { return this.keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public IsOrdered getIsOrdered() { return this.isOrdered; }
    public void setIsOrdered(IsOrdered isOrdered) { this.isOrdered = isOrdered; }
    public boolean getIsOrderedBoolean() { return this.isOrdered.getValue(); }
    
    public int getBookID() { return this.bookID; }
    public void setBookID(int bookID) { this.bookID = bookID; }

    public int getNumberOforders() { return this.numberOforders; }
    public void setNumberOforders(int numberOforders) { this.numberOforders = numberOforders; }

    public String getBookSummary() { return this.BookSummary; }
    public void setBookSummary(String BookSummary) { this.BookSummary = BookSummary; }
    
    public String toString() { return "(Book) " + this.bookName + " (genre: " + this.bookGenre + ", copies: " + this.NumberOfCopies + ", borrowed: " + this.NumberOfBorrowedCopies + ", keywords: " + this.keywords + ", ordered?: " + this.isOrdered + ", id: " + this.bookID + ", orders: " + this.numberOforders + ", Summary: " + this.BookSummary + ")\n"; }
}