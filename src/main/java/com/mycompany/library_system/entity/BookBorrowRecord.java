package com.mycompany.library_system.entity;
import java.time.LocalDate;

public class BookBorrowRecord {
    private String bookId;
    private String userId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean alreadyReturned;
    private boolean lateReturn;

    public BookBorrowRecord(String bookId, String userId, LocalDate borrowDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = null;
        this.alreadyReturned = false;
        this.lateReturn = false;
    }

    //Setters and getters
    public String getBookId() {
        return this.bookId;
    }
    public String getUserId() {
        return this.userId;
    }
    public LocalDate getBorrowDate() {
        return this.borrowDate;
    }
    public LocalDate getReturnDate() {
        return this.returnDate;
    }
    public boolean getAlreadyReturned() {
        return this.alreadyReturned;
    }
    public boolean getLateReturn() {
        return this.lateReturn;
    }

    public void setAlreadyReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.alreadyReturned = true;
    }

    

}
