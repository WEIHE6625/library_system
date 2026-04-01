package com.mycompany.library_system.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author all
 */
public class DataProcessor {
    // Count total lines in a file
    public static final DateTimeFormatter CSV_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");    public static int lineCounter(String filePath) {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            count = reader.lines().count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ((int) count);
    }

    // Truncate long text with "..." in display
    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    // Clean the text from CSV, remove extra spaces and surrounding quotes
    public static String clean(String input) {
        if (input == null) return "";
        String result = input.trim();
        if (result.length() >= 2 && result.startsWith("\"") && result.endsWith("\"")) {
            // Remove the first and last character
            result = result.substring(1, result.length() - 1);
        }
        return result.trim();
    }

    public static LocalDate parseAnyDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        try {
            // 1. Check if it contains a space (User format: "2026-03-01 08:00:00")
            if (dateStr.contains(" ")) {
                return LocalDateTime.parse(dateStr, CSV_FORMATTER).toLocalDate();
            }
            // 2. Otherwise, assume simple format (Borrow format: "2026-03-01")
            else {
                return LocalDate.parse(dateStr);
            }
        } catch (Exception e) {
            return null; // Return null if the date is corrupted
        }
    }

}
