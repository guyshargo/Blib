package blib.model;

import java.io.Serializable;

import blib.enums.IsOrdered;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String genre;
    private int copiesNum;
    private int borrowedCopiesNum;
    private String keywords;
    private IsOrdered isOrdered;
    private int bookId;
    private int ordersNum;
    private String summary;

    public Book(String title, String genre, int copiesNum, int borrowedCopiesNum, String keywords,
                IsOrdered isOrdered, int bookId, int ordersNum) {
        this.title = title;
        this.genre = genre;
        this.copiesNum = copiesNum;
        this.borrowedCopiesNum = borrowedCopiesNum;
        this.keywords = keywords;
        this.isOrdered = isOrdered;
        this.bookId = bookId;
        this.ordersNum = ordersNum;
    }

    public String getBookTitle() { return this.title; }
    public void setBookTitle(String title) { this.title = title; }
    
    public String getBookGenre() { return this.genre; }
    public void setBookGenre(String genre) { this.genre = genre; }

    public int getNumberOfCopies() { return this.copiesNum; }
    public void setNumberOfCopies(int copiesNum) { this.copiesNum = copiesNum; }

    public int getNumberOfBorrowedCopies() { return this.borrowedCopiesNum; }
    public void setNumberOfBorrowedCopies(int borrowedCopiesNum) { this.borrowedCopiesNum = borrowedCopiesNum; }

    public String getKeywords() { return this.keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public IsOrdered getIsOrdered() { return this.isOrdered; }
    public void setIsOrdered(IsOrdered isOrdered) { this.isOrdered = isOrdered; }
    public boolean getIsOrderedBoolean() { return this.isOrdered.getValue(); }
    
    public int getBookID() { return this.bookId; }
    public void setBookID(int bookId) { this.bookId = bookId; }

    public int getNumberOforders() { return this.ordersNum; }
    public void setNumberOforders(int ordersNum) { this.ordersNum = ordersNum; }

    public String getBookSummary() { return this.summary; }
    public void setBookSummary(String summary) { this.summary = summary; }
    
    public String toString() { return "(Book) " + this.title + " (genre: " + this.genre + ", copies: " + this.copiesNum + ", borrowed: " + this.borrowedCopiesNum + ", keywords: " + this.keywords + ", ordered?: " + this.isOrdered + ", id: " + this.bookId + ", orders: " + this.ordersNum + ", Summary: " + this.summary + ")\n"; }
}