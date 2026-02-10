package com.mycompany.library_system.utility;

import java.util.Scanner;

/**
 *
 * @author all
 */
public class Utility {

    static Scanner scanner = new Scanner(System.in);

    // Clear the console screen in NetBeans
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Used for build a title
    public static void titleBuilder(String title) {
        int totalWidth = 160; // Standard console width
        int titleLength = title.length();
        
        int paddingNeeded = totalWidth - titleLength - 2;
        int leftPad = paddingNeeded / 2;
        int rightPad = paddingNeeded - leftPad; 
        
        String leftSide = "=".repeat(Math.max(0, leftPad));
        String rightSide = "=".repeat(Math.max(0, rightPad));
        
        System.out.println(leftSide + " " + title + " " + rightSide);
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


    // General option menu
    public static boolean optionMenu(String[] options, String optionInputMessage, Runnable[] action) {
        /*
        -----------------
        | Sample result |
        -----------------

        -----------------------------
         1. Add or Edit Book
         2. Delete Book
         3. Next Page
         4. Previous Page
         0. Back to Main Menu
        -----------------------------
         Select an option >>  

        */

        System.out.println("-".repeat(30));
        for (String option : options) {
            System.out.println(option);
        }
        System.out.println("-".repeat(30));

        System.out.print(optionInputMessage);
        String input = scanner.nextLine().trim();

        int choice = 0;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Try again.");
        }

        if (choice == 0) {
            return true;
        } else if (choice >= 1 && choice <= options.length - 1) {
            action[choice - 1].run();
        } else {
            System.out.println("Invalid input! Try again.");
        }
        return false;
    }
}
