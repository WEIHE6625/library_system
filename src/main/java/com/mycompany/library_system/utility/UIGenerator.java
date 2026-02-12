package com.mycompany.library_system.utility;
import java.io.File;
import java.util.Scanner;

/**
 * 
 * @author all
 */
public class UIGenerator {    

    static Scanner scanner = new Scanner(System.in);

    // Used for build a title
    public static void titleBuilder(String title) {
        int totalWidth = 160; // Standard console width
        int titleLength = title.length();
        
        int paddingNeeded = totalWidth - titleLength - 2;
        int leftPad = paddingNeeded / 2;
        int rightPad = paddingNeeded - leftPad; 
        
        String leftSide = " ".repeat(Math.max(0, leftPad));
        String rightSide = " ".repeat(Math.max(0, rightPad));
        
        System.out.println("=".repeat(totalWidth));
        System.out.println(leftSide + " " + title + " " + rightSide);
        System.out.println("=".repeat(totalWidth));
    }

    // Clear the console screen in NetBeans
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void displayInvalidChoiceMessage() {
        System.out.println("\nInvalid input! Please try again.");
    }
    public static void displayExitMessage() {
        System.out.println("\nExiting system...");
    }

    public static void pressEnterToContinue() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.close();
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    /**  
     * General option menu
     * @author wh
     * @param options Strings list to display (Option 0 is for exit)
     * @param optionInputMessage Prompt message for user input
     * @param action Array of Runnable actions corresponding to each option
     * @return true if user chooses to exit (option 0), otherwise false
     */
    public static boolean optionMenu(String[] options, String optionInputMessage, Runnable[] action) {
        /*
        -----------------
        | Sample result |
        -----------------

        -----------------------------
         [1] Add Book
         [2] Edit Book
         [3] Delete Book
         [0] Back to Main Menu
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

        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                return true;
            } else if (choice >= 1 && choice <= options.length - 1) {
                action[choice - 1].run();
            } else {
                System.out.println("Invalid input! Try again.");
                pressEnterToContinue();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Try again.");
            pressEnterToContinue();
        }
        return false;
    }

}
