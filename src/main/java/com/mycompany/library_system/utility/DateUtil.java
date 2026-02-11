package com.mycompany.library_system.utility;
import java.time.LocalDate;

public class DateUtil {
    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }
}
