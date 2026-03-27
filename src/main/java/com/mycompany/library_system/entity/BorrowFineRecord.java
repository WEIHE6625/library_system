package com.mycompany.library_system.entity;

import java.time.LocalDate;

public class BorrowFineRecord {
    private String borrowId;
    private String userId;
    private Fine fine;
    private boolean finePayed;
    private LocalDate paymentDate;

    public BorrowFineRecord(String borrowId, String userId, Fine fine, boolean finePayed, LocalDate paymentDate) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.fine = fine;
        this.finePayed = finePayed; 
        this.paymentDate = paymentDate;
    }

    public BorrowFineRecord(String borrowId, String userId, Fine fine) {
        this(borrowId, userId, fine, false, null);
    }

    public String getBorrowId() {return this.borrowId;}
    public String getUserId() {return this.userId;}
    public Fine getFine() {return this.fine;}
    public boolean getFinePayed() {return this.finePayed;}
    public LocalDate getPaymentDate() {return this.paymentDate;}
    public String finePayedToString(boolean finePayed) {
        if(finePayed) {
            return "Paid";
        }
        return "Pending";
    }

    public String paymentDateToString(LocalDate paymentDate) {
        if(paymentDate == null) {
            return "-";
        }
        return String.valueOf(paymentDate);
    }

    public void receiveFinePayment(LocalDate paymentDate) {
        this.finePayed = true;
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return String.format("%-15s ", this.borrowId) + String.format("%-15s ", this.userId) + this.fine.toString() + String.format(" %22s %18s", finePayedToString(this.finePayed), paymentDateToString(this.paymentDate));
    }

}
