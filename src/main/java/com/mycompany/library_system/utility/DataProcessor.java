package com.mycompany.library_system.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author wh
 */
public class DataProcessor {
    // Count total lines in a file
    public static int lineCounter(String filePath) {
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
}
