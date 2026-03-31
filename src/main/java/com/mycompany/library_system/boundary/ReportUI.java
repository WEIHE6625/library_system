package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;

import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.entity.Report;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.utility.UserPrompt;
import com.mycompany.library_system.utility.Pagination;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.control.UserInfoController;

public class ReportUI{
    static Scanner sc = new Scanner(System.in);
    static Pagination<String, User> pagination = new Pagination<>();
    
    public static String choseRequiredReports(){
        do {
                System.out.println("\n--- Report Design Selection ---");
                System.out.println("Select the fields to include in your report (e.g., enter '124' for User ID, Borrow Status, and Book Category):");
                System.out.println("1. User ID");
                System.out.println("2. Borrow Status");
                System.out.println("3. Fine Status");
                System.out.println("4. Book Category");
                System.out.print("Enter your choices: ");
                
                input = scanner.nextLine().trim();

                // Validation: Check if input is not empty and only contains digits 1-4
                // The regex "^[1-4]+$" ensures the string has at least one character and only 1,2,3, or 4
                isValid = input.matches("^[1-4]+$") && input.length() == input.chars().distinct().count();

                if (!isValid) {
                    System.out.println("Invalid! Use digits 1-4 without repeating them (e.g., 124).");
                }

            } while (!isValid);

            return input;

    }
}
