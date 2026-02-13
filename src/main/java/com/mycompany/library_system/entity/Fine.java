package com.mycompany.library_system.entity;

public class Fine {
    private String fineId;
    private String fineDate;
    private double fineAmount;
    private static final double weeklyFineAmount = 1.5;

    public Fine(String fineId, String fineDate, double fineAmount) {
        this.fineId = fineId;
        this.fineDate = fineDate;
        this.fineAmount = fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public void incrementWeeklyFine() {
        this.fineAmount += weeklyFineAmount;
    }
}
