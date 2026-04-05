//package com.mycompany.library_system.boundary;
//
//import java.util.Scanner;
//import java.util.function.Supplier;
//import com.mycompany.library_system.adt.ListInterface;
//import com.mycompany.library_system.adt.TreeMap;
//import com.mycompany.library_system.entity.Report;
//import com.mycompany.library_system.utility.DataProcessor;
//import com.mycompany.library_system.utility.UIGenerator;
//import com.mycompany.library_system.utility.Pagination;
//import java.time.LocalDate;
//import java.time.format.DateTimeParseException;
//public class ReportUI {
//
//    static Scanner scanner = new Scanner(System.in);
//    static Pagination<String, Report> pagination = new Pagination<>();
//
//
//    public static String choseRequiredReports() {
//        String input;
//        boolean isValid = false;
//
//        do {
//            System.out.println("\n============================================================");
//            System.out.println("                LIBRARY SYSTEM REPORT MODULE                ");
//            System.out.println("============================================================");
//            System.out.println("  [1]  User Account Summary (Total users & roles)          ");
//            System.out.println("  [2]  Library Borrow Status (Returned vs. Lost vs. Pending)");
//            System.out.println("  [3]  Financial Fine Summary (Collected vs. Outstanding)   ");
//            System.out.println("  [4]  Book Category Distribution (Inventory counts)        ");
//            System.out.println("----------------------- Combinations -----------------------");
//            System.out.println("  [12] User Borrow Audit (Specific user's history)          ");
//            System.out.println("  [13] User Fine Statement (Specific user's debt)           ");
//            System.out.println("  [14] User Interest Profile (Specific user's favorite genres)");
//            System.out.println("  [23] Operations Health (Borrow vs. Fine Analysis)          ");
//            System.out.println("  [24] Category Popularity (Most borrowed subjects)         ");
//            System.out.println("============================================================");
//            System.out.print("Select Report ID >> ");
//
//            input = scanner.nextLine().trim();
//
//            switch (input) {
//                case "1": case "2": case "3": case "4":
//                case "12": case "13": case "14": case "24": case "23":
//                    isValid = true;
//                    break;
//                default:
//                    System.out.println("\n[!] Invalid selection! Please choose an ID from the list (e.g., 14).");
//            }
//
//        } while (!isValid);
//
//        return input;
//    }
//
//    public static void menu(ReportInfoController rController) {
//        boolean isExit = false;
//        // Sync pagination with the data from the controller
//        pagination.setDisplayTree(rController.getReportTree());
//
//        do {
//            UIGenerator.clearScreen();
//            reportListPage();
//            isExit = UIGenerator.optionMenu(
//                    new String[]{
//                            "[1] Generate New Report",
//                            "[2] View Report",
//                            "[3] Delete Report",
//                            " ",
//                            "[8] <- Previous Page",
//                            "[9] Next Page ->",
//                            "[0] Back to Main Menu"
//                    },
//                    "Select an option >> ",
//                    new Supplier[] {
//                            () -> { rController.generateNewReport(); return false; },
//                            () -> { rController.viewReportDetails(); return false; },
//                            () -> { rController.deleteReport(); return false; },
//                            () -> { pagination.nextPage(); return false; },
//                            () -> true
//                    }
//            );
//        } while (!isExit);
//    }
//
//    public static void reportListPage() {
//        UIGenerator.titleBuilder("Report Archive");
//        ListInterface<Report> displayList = pagination.getDisplayTree().values();
//
//        if (displayList == null || displayList.isEmpty()) {
//            System.out.println("No reports found in the archive.");
//            return;
//        }
//
//        int totalRecords = displayList.size();
//        int startIndex = pagination.getStartIndex();
//        int endIndex = pagination.getEndIndex();
//
//        // Header Design
//        System.out.printf("%-12s %-60s %-15s\n", "Report ID", "Report Title", "Generated Date");
//        System.out.printf("%-12s %-60s %-15s\n", "---------", "------------", "--------------");
//
//        for (int i = startIndex; i <= endIndex; i++) {
//            int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);
//
//            Report r = displayList.get(actualIndex);
//            String title = DataProcessor.truncate(r.getReportTitle(), 58);
//
//            System.out.printf("%-12s %-60s %-15s\n",
//                    r.getReportId(),
//                    title,
//                    r.getGeneratedDate().toString());
//        }
//
//        System.out.println("-".repeat(90));
//        System.out.printf("Page: %d/%d ------ Total Reports: %d\n\n",
//                pagination.getCurrentPage(),
//                pagination.getTotalPages(),
//                displayList.size());
//    }
//
//    public static String inputReportID(TreeMap<String, Report> reportList) {
//        String id;
//        boolean isValid = false;
//        do {
//            System.out.print("Enter Report ID (or '0' to cancel) >> ");
//            id = scanner.nextLine().trim().toUpperCase();
//
//            if (id.equals("0")) return ""; // Cancel
//
//            if (!reportList.containsKey(id)) {
//                System.out.println("Report ID " + id + " not found. Please try again.");
//            } else {
//                isValid = true;
//            }
//        } while (!isValid);
//        return id;
//    }
//
//    public static LocalDate[] inputDateRange() {
//        LocalDate from = null;
//        LocalDate to = null;
//        boolean isValid = false;
//
//        System.out.println("\n--- Date Range Filter (Format: YYYY-MM-DD) ---");
//        System.out.println("--- Leave blank to skip (Beginning / Until Now) ---");
//
//        do {
//            try {
//
//                System.out.print("From Date >> ");
//                String fromInput = scanner.nextLine().trim();
//                from = fromInput.isEmpty() ? null : LocalDate.parse(fromInput);
//
//                System.out.print("To Date   >> ");
//                String toInput = scanner.nextLine().trim();
//                to = toInput.isEmpty() ? null : LocalDate.parse(toInput);
//
//                // Logic Check: "From" cannot be after "To"
//                if (from != null && to != null && from.isAfter(to)) {
//                    System.out.println("[!] Error: 'From' date cannot be later than 'To' date. Please re-enter.");
//                } else {
//                    isValid = true;
//                }
//            } catch (DateTimeParseException e) {
//                System.out.println("[!] Invalid Format! Please use YYYY-MM-DD (e.g., 2026-04-02).");
//            }
//        } while (!isValid);
//
//        // Return as an array: index 0 is From, index 1 is To
//        return new LocalDate[]{from, to};
//    }
//
//    public static int[] inputCategoryExtremeParams() {
//        int sy, sm, ey, em, limit;
//
//        System.out.println("\n--- Filter by Publication Date (Enter '0' for all-time) ---");
//
//        System.out.print("Start Year (e.g. 2020) >> ");
//        sy = scanner.nextInt();
//        if (sy == 0) {
//            scanner.nextLine(); // clear buffer
//            System.out.print("How many Top/Bottom items to show? >> ");
//            limit = scanner.nextInt();
//            scanner.nextLine();
//            return new int[]{1900, 1, 2099, 12, limit};
//        }
//
//        System.out.print("Start Month (1-12)     >> ");
//        sm = scanner.nextInt();
//
//        System.out.print("End Year (e.g. 2026)   >> ");
//        ey = scanner.nextInt();
//        System.out.print("End Month (1-12)       >> ");
//        em = scanner.nextInt();
//
//        System.out.print("How many Top/Bottom items to show? (e.g. 5) >> ");
//        limit = scanner.nextInt();
//        scanner.nextLine();
//
//
//        return new int[]{sy, sm, ey, em, limit};
//    }
//
//
//
//}