package com.mycompany.library_system.entity;
import java.time.LocalDate;

public class BookBorrowRecord {
    private String borrowId;
    private String bookId;
    private String userId;
    private LocalDate borrowDate;
    private boolean alreadyReturned;
    private LocalDate returnDate;
    private boolean lateReturn;
    private boolean isLostBook;

    public BookBorrowRecord(String borrowId, String bookId, String userId, LocalDate borrowDate, boolean alreadyReturn, LocalDate returnDate, boolean lateReturn, boolean isLostBook) {
        this.borrowId = borrowId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        if(isLostBook) {
            this.alreadyReturned = false;
            this.returnDate = null;
            this.lateReturn = false;
            this.isLostBook = isLostBook;
        }
        else {
            this.alreadyReturned = alreadyReturn;
            this.returnDate = returnDate;
            this.lateReturn = lateReturn;
            this.isLostBook = isLostBook;
        }
        
    }

    public BookBorrowRecord(String borrowId, String bookId, String userId, LocalDate borrowDate) {
        this(borrowId, bookId, userId, borrowDate, false, null, false, false);
    }

    //Setters and getters
    public String getBorrowId() {return this.borrowId;}
    public String getBookId() {return this.bookId;}
    public String getUserId() {return this.userId;}
    public LocalDate getBorrowDate() {return this.borrowDate;}
    public LocalDate getReturnDate() {return this.returnDate;}
    public boolean getAlreadyReturned() {return this.alreadyReturned;}
    public boolean getLateReturn() {return this.lateReturn;}
    public boolean getIsLostBook() {return this.isLostBook;}

    public void setAlreadyReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.alreadyReturned = true;
    }
    public String alreadyReturnToString(boolean alreadyReturn) {
        if(alreadyReturn) {
            return "Returned";
        }
        else if(!alreadyReturn && this.isLostBook) {
            return "Lost";
        }
        return "Pending";
    }

    public String dateToString(LocalDate date) {
        if(date == null) {
            return "-";
        }
        return date.toString();
    }

    public String isLostBookToString(boolean isLostBook) {
        if(isLostBook) {return "Yes";}
        return "No";
    }

    public void setLateReturn(boolean lateReturn) {this.lateReturn = lateReturn;}
    public void setIsLostBook(boolean lostBook) {this.isLostBook = lostBook;}

    @Override
    public String toString() {
        return String.format("%-13s %-12s %-9s %-16s %10s %14s %14s", this.getBorrowId(), this.getBookId(), this.getUserId(), this.getBorrowDate(), alreadyReturnToString(this.getAlreadyReturned()), dateToString(this.getReturnDate()), isLostBookToString(this.getIsLostBook()));
    }
    

}
