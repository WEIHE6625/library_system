package com.mycompany.library_system.entity;

import java.time.LocalDate;

public class BorrowFineRecord {
    private String borrowId;
    private Fine fine;
    private boolean finePayed;
    private LocalDate paymentDate;

    public BorrowFineRecord(String borrowId, Fine fine, boolean finePayed, LocalDate paymentDate) {
        this.borrowId = borrowId;
        this.fine = fine;
        this.finePayed = finePayed; 
        this.paymentDate = paymentDate;
    }

    public BorrowFineRecord(String borrowId, Fine fine) {
        this(borrowId, fine, false, null);
    }

    public String getBorrowId() {return this.borrowId;}
    public Fine getFine() {return this.fine;}
    public boolean getFinePayed() {return this.finePayed;}
    public LocalDate getPaymentDate() {return this.paymentDate;}
    public String finePayedToString(boolean finePayed) {
        if(finePayed) {
            return "Paid";
        }
        return "Pending";
    }

    public void receiveFinePayment(LocalDate paymentDate) {
        this.finePayed = true;
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return String.format("%-10s ", this.borrowId) + this.fine.toString() + String.format(" %-10s %-15s", this.finePayed, String.valueOf(this.paymentDate));
    }

}
