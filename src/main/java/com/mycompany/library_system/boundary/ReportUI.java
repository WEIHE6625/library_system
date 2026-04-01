package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.ReportInfoController;
import com.mycompany.library_system.entity.Report;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.Pagination;

public class ReportUI {

    static Scanner scanner = new Scanner(System.in);
    static Pagination<String, Report> pagination = new Pagination<>();


    public static String choseRequiredReports() {
        String input;
        boolean isValid;
        do {
            System.out.println("\nSelect fields to include (e.g., '12' for User & Borrow):");
            System.out.println("1. User List");
            System.out.println("2. Borrow Status");
            System.out.println("3. Fine Status");
            System.out.println("4. Book Category");
            System.out.print("Enter choices (1-4) >> ");

            input = scanner.nextLine().trim();
            // Validates that input only contains digits 1-4
            isValid = input.matches("^[1-4]+$");

            if (!isValid) {
                System.out.println("Invalid selection! Please use digits 1-4 only.");
            }
        } while (!isValid);

        return input;
    }

    public static void menu(ReportInfoController rController) {
        boolean isExit = false;
        // Sync pagination with the data from the controller
        pagination.setDisplayTree(rController.getReportTree());

        do {
            UIGenerator.clearScreen();
            reportListPage();
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Generate New Report",
                            "[2] View Report",
                            "[3] Delete Report",
                            " ",
                            "[8] <- Previous Page",
                            "[9] Next Page ->",
                            "[0] Back to Main Menu"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { rController.generateNewReport(); return false; },
                            () -> { rController.viewReportDetails(); return false; },
                            () -> { rController.deleteReport(); return false; },
                            () -> { pagination.nextPage(); return false; },
                            () -> true
                    }
            );
        } while (!isExit);
    }

    public static void reportListPage() {
        UIGenerator.titleBuilder("Report Archive");
        ListInterface<Report> displayList = pagination.getDisplayTree().values();

        if (displayList == null || displayList.isEmpty()) {
            System.out.println("No reports found in the archive.");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = pagination.getStartIndex();
        int endIndex = pagination.getEndIndex();

        // Header Design
        System.out.printf("%-12s %-60s %-15s\n", "Report ID", "Report Title", "Generated Date");
        System.out.printf("%-12s %-60s %-15s\n", "---------", "------------", "--------------");

        for (int i = startIndex; i <= endIndex; i++) {
            int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

            Report r = displayList.get(actualIndex);
            String title = DataProcessor.truncate(r.getReportTitle(), 58);

            System.out.printf("%-12s %-60s %-15s\n",
                    r.getReportId(),
                    title,
                    r.getGeneratedDate().toString());
        }

        System.out.println("-".repeat(90));
        System.out.printf("Page: %d/%d ------ Total Reports: %d\n\n",
                pagination.getCurrentPage(),
                pagination.getTotalPages(),
                displayList.size());
    }

    public static String inputReportID(TreeMap<String, Report> reportList) {
        String id;
        boolean isValid = false;
        do {
            System.out.print("Enter Report ID to delete (or '0' to cancel) >> ");
            id = scanner.nextLine().trim().toUpperCase();

            if (id.equals("0")) return ""; // Cancel

            if (!reportList.containsKey(id)) {
                System.out.println("Report ID " + id + " not found. Please try again.");
            } else {
                isValid = true;
            }
        } while (!isValid);
        return id;
    }





}