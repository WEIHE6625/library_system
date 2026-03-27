/**
 *
 * @author Isaac Kuan Way Houng
 */
package com.mycompany.library_system.entity;

import java.time.LocalDate;

public class Fine {
    private LocalDate fineDate;
    private double fineAmount;
    private static final double WEEKLY_FINE_AMOUNT = 1.5;
    private static final int DAYS_TO_INCREMENT_FINE = 7;

    public Fine(LocalDate fineDate, double fineAmount) {
        this.fineDate = fineDate;
        this.fineAmount = fineAmount;
    }

    public Fine(LocalDate fineDate) {
        this(fineDate, WEEKLY_FINE_AMOUNT);
    }

    public LocalDate getFineDate() {return this.fineDate;}
    public double getFineAmount() {return this.fineAmount;}
    public static double getWeeklyFineAmount() {return WEEKLY_FINE_AMOUNT;}
    public static double getDaysToIncrementFine() {return DAYS_TO_INCREMENT_FINE;}

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        return String.format("%-21s %10s", String.valueOf(this.fineDate), String.format("%.2f", fineAmount));
    }
}