package com.mycompany.library_system.entity;

public class BorrowFineRecord {
    public String borrowId;
    public Fine fine;
    public boolean finePayed;

    public BorrowFineRecord(String borrowId, Fine fine) {
        this.borrowId = borrowId;
        this.fine = fine;
        this.finePayed = false; 
    }

    public String getBookId() {
        return this.borrowId;
    }
    public boolean getFinePayed() {
        return this.finePayed;
    }

    public void receiveFinePayment() {
        this.finePayed = true;
    }

}
