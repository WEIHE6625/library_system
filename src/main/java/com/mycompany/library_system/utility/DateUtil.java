package com.mycompany.library_system.utility;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author all
 */
public class DateUtil {

    public static String getToday() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }



}
