package com.mycompany.library_system.utility;

import java.time.Year;
import java.util.Scanner;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.control.BookInfoController;

/**
 *
 * @author all
 */
public class UserPrompt {

    static Scanner scanner = new Scanner(System.in);

    public static char inputChar(String promptMessage, char... validChars) {
        boolean isValid = false;
        char value = '\0';
        do {
            System.out.print(promptMessage);
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.length() == 1) {
                value = input.charAt(0);

                if (validChars.length == 0) {
                    isValid = true; // accept any single character
                } else {
                    for (char c : validChars) {
                        if (value == Character.toUpperCase(c)) {
                            isValid = true;
                            break;
                        }
                    }
                }
            }

            if (!isValid) {
                System.out.print("Invalid input! ");
                if (validChars.length > 0) {
                    System.out.println("Enter one of: " + java.util.Arrays.toString(validChars));
                } else {
                    System.out.println("Please enter a single character.");
                }
            }

        } while (!isValid);

        return value;
    }
    public static double inputDouble(String promptMessage, double min, double max) {
        boolean isValid = false;
        double value = -1;
        do {
            System.out.print(promptMessage);
            String input = scanner.nextLine();
            try {
                value = Double.parseDouble(input);
                if (value >= min && value <= max) {
                    isValid = true;
                } else {
                    System.out.println("Invalid input! Value must be between " + min + " and " + max + ". Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        } while (!isValid);
        return value;
    }

    public static int inputInteger(String promptMessage, int min, int max) {
        boolean isValid = false;
        int value = -1;
        do {
            System.out.print(promptMessage);
            String input = scanner.nextLine();
            try {
                value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    isValid = true;
                } else {
                    System.out.println("Invalid input! Value must be between " + min + " and " + max + ". Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        } while (!isValid);
        return value;
    }

    public static String inputString(String promptMessage, int maxLength, boolean allowEmpty) {
        boolean isValid = false;
        String value = "";
        do {
            System.out.print(promptMessage);
            value = scanner.nextLine();
            if (!allowEmpty && value.trim().isEmpty()) {
                System.out.println("Invalid input! Input cannot be empty. Please try again.");
            } else if (value.length() > maxLength) {
                System.out.println("Invalid input! Input exceeds maximum length of " + maxLength + " characters. Please try again.");
            } else {
                isValid = true;
            }
        } while (!isValid);
        return value;
    }

    public static String inputEmail(String promptMessage, int maxLength, boolean allowEmpty) {
        boolean isValid = false;
        String email = "";

        do {
            System.out.print(promptMessage);
            email = scanner.nextLine().trim();

            if (!allowEmpty && email.isEmpty()) {
                System.out.println("Invalid input! Email cannot be empty. Please try again.");
            }
            else if (email.length() > maxLength) {
                System.out.println("Invalid input! Email exceeds maximum length of " + maxLength + " characters.");
            }
            else if (!allowEmpty && !isValidEmail(email)) {
                System.out.println("Invalid email format! Email must contain '@' and end with '.com'.");
            }
            else {
                isValid = true;
            }

        } while (!isValid);

        return email;
    }

    public static <T extends Enum<T>> T inputEnum(String promptMessage,Class<T> enumClass,boolean allowEmpty) {
        while (true) {
            System.out.print(promptMessage);
            String input = scanner.nextLine().trim();

            if (allowEmpty && input.isEmpty())
                return null;

            if (input.isEmpty()) {
                System.out.println("Invalid input! Cannot be empty.");
                continue;
            }

            input = input.toUpperCase().replace(" ", "_");

            try {
                return Enum.valueOf(enumClass, input);
            }
            catch (IllegalArgumentException e) {

                System.out.print("Invalid input! Allowed values: ");

                T[] constants = enumClass.getEnumConstants();

                for (int i = 0; i < constants.length; i++) {
                    System.out.print(constants[i]);
                    if (i < constants.length - 1)
                        System.out.print(", ");
                }

                System.out.println();
            }
        }
    }

    /**
     * @param promptMessage Array of prompt messages for year[0], month[1], day[2] respectively
     * @param yearRequired specify if year input is required
     * @param monthRequired specify if month input is required
     * @param dayRequired specify if day input is required
     * @return int array of [day, month, year]
     */
    public static int[] inputDate(String[] promptMessage, boolean yearRequired, boolean monthRequired, boolean dayRequired) {
        boolean isValid = false;
        int day = 0;
        int month = 0;
        int year = 0;
        int currentYear = Year.now().getValue();

        // Validate Year
        if(yearRequired) {
            do {
                System.out.print(promptMessage[0]);
                String input = scanner.nextLine().trim();

                try {
                    year = Integer.parseInt(input);
                    // Check if year is 4 digits and not in the distant future
                    if (year >= 1000 && year <= currentYear + 1) { 
                        isValid = true;
                    } else {
                        System.out.println("Please enter a valid 4-digit year (1000 - " + (currentYear + 1) + ").");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a year (e.g., 2024).");
                }
            } while (!isValid);
        }

        isValid = false;

        // Validate Month
        if(monthRequired) {
            do {
                System.out.print(promptMessage[1]);
                String input = scanner.nextLine().trim();

                try {
                    month = Integer.parseInt(input);
                    if (month >= 1 && month <= 12) {
                        isValid = true;
                    } else {
                        System.out.println("Invalid input! Month must be between 1 and 12.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number.");
                }
            } while (!isValid);
        }

        isValid = false;

        // Validate Day
        if(dayRequired) {
            do {
                System.out.print(promptMessage[2]);
                String input = scanner.nextLine().trim();

                try {
                    day = Integer.parseInt(input);
                    if(monthRequired == false || month == 2) {
                        // February check (not leap year aware)
                        if (day >= 1 && day <= 28) {
                            isValid = true;
                        } else {
                            System.out.println("Invalid input! Day must be between 1 and 28 for February.");
                        }
                    } else if (monthRequired == false || month == 4 || month == 6 || month == 9 || month == 11) {
                        // April, June, September, November check
                        if (day >= 1 && day <= 30) {
                            isValid = true;
                        } else {
                            System.out.println("Invalid input! Day must be between 1 and 30 for the selected month.");
                        }
                    } else {
                        // All other months
                        if (day >= 1 && day <= 31) {
                            isValid = true;
                        } else {
                            System.out.println("Invalid input! Day must be between 1 and 31 for the selected month.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a number.");
                }
            } while (!isValid);
        }

        return new int[]{day, month, year};
    }

    /**
     * @param bookManager The book manager which store the book list
     * @param categoriesExisted The categories that the book already have, 
     *                          so exclude them to prevent user select them again.
     * @return A list of selected categories
     */
    public static ListInterface<String> inputBookCategory(BookInfoController bookManager, ListInterface<String> categoriesExisted) {        
        // Get the list of all existing categories
        ListInterface<String> categories = new ArrayList<>();
        ListInterface<String> allCategories = bookManager.getAllUniqueCategories(categoriesExisted);
        boolean isExit = false;

        for (String cat : categoriesExisted) { // Show the current categoris the book have
            categories.add(cat);
        }

        do {
            UIGenerator.clearScreen();
            System.out.println("\n---------------- Select Category ----------------");
            System.out.println("\nType a keyword to search (e.g., 'Hist').");
            System.out.print("Search >> ");
            String query = scanner.nextLine().trim().toLowerCase();

            // Filter the list based on user input
            ListInterface<String> matches = new ArrayList<>();
            for (String cat : allCategories) {
                if (cat.toLowerCase().contains(query) && !categories.contains(cat)) {
                    matches.add(cat);
                }
            }
            matches.sort((s1, s2) -> {
                boolean s1Starts = s1.toLowerCase().startsWith(query);
                boolean s2Starts = s2.toLowerCase().startsWith(query);

                // "Starts With" comes first
                if (s1Starts && !s2Starts) return -1; // s1 moves up
                if (!s1Starts && s2Starts) return 1;  // s2 moves up

                // If both/neither start with it, sort Alphabetically
                return s1.compareToIgnoreCase(s2);
            });

            // Display Matches
            if (matches.isEmpty()) {
                System.out.println("No existing categories match '" + query + "'.");
                System.out.print("Would you like to use '" + query + "' as a NEW category? (Y/N) >> ");
                String response = scanner.nextLine().trim();
                if (response.equalsIgnoreCase("y")) {
                    isExit = true;
                    categories.add(query.substring(0, 1).toUpperCase() + query.substring(1));
                }
            } else {
                System.out.println("-".repeat(50));
                System.out.println("Matches found:");
                for (int i = 0; i < matches.size(); i++) {
                    // Print only top 20 to avoid spamming
                    if (i >= 20) {
                        System.out.println("... (" + (matches.size() - 20) + " more)");
                        break;
                    }
                    System.out.printf("[%d] %s\n", (i + 1), matches.get(i));
                }

                // Loop if invalid input or already input a category
                boolean isContinue = true;
                do {
                    System.out.println("-".repeat(50));
                    System.out.println("[0] Search Again");
                    System.out.println("[y] Submit Categories");
                    System.out.println("[c] Clear All Current Categories");
                    System.out.println("[n] Type a New Category Manually");

                    if (categories.size() > 0) {
                        System.out.println("\n[ Current Categories: " + categories.toString() + " ]");
                    }else {
                        System.out.println("\n[ Current Categories: No categories ]");
                    }

                    // User Selection
                    System.out.print("Select option >> ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("n")) {
                        System.out.print("\nEnter new category name: ");
                        isExit = false;
                        String newCategory = scanner.nextLine().trim();
                        categories.add(newCategory);
                        System.out.println("\nCategory : " + newCategory + " added.");
                        UIGenerator.pressEnterToContinue();

                    } else if (input.equalsIgnoreCase("y")) {
                        if(categories.size() == 0) {
                            System.out.println("\nNo categories selected yet. Please select at least one category.");
                            UIGenerator.pressEnterToContinue();
                        } else {
                            isContinue = false;
                            isExit = true;
                        }
                    } else if (input.equalsIgnoreCase("c")) {
                        categories.clear();
                    } else if (input.equals("0")) {
                        isContinue = false;
                    } else {
                        try {
                            int selection = Integer.parseInt(input);
                            if (selection > 0 && selection <= matches.size()) {
                                categories.add(matches.get(selection - 1));
                                System.out.println("\nCategory : " + matches.get(selection - 1) + " added.");

                            } else {
                                UIGenerator.displayInvalidChoiceMessage();
                                UIGenerator.pressEnterToContinue();
                            }
                        } catch (NumberFormatException e) {
                            UIGenerator.displayInvalidChoiceMessage();
                            UIGenerator.pressEnterToContinue();
                        }
                    }
                } while (isContinue);
            }
        } while (!isExit);
        System.out.println("\nSelected Categories : " + categories.toString() +"\n");
        return categories;
    }

    public static ListInterface<String> inputBookCategory(BookInfoController bookManager) {
        return inputBookCategory(bookManager, new ArrayList<>());
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;

        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$");
    }
}
