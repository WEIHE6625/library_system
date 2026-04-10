package com.mycompany.library_system.boundary;

import java.util.function.Supplier;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.control.BookInfoController;
import com.mycompany.library_system.control.BookReportController;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

/**
 *
 * @author weihe
 */
public class BookReportUI {
    final static int COLUMN_WIDTH = 13;  

    @SuppressWarnings("unchecked")
    public static void reportMenu(BookInfoController bController) {
        boolean isExit = false;
        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Admin Dashboard --- Book Reports");
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Low Stock Alert Report",
                    "[2] Total Inventory Cost Report",
                    " ",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { reportMainPage(bController, 1); return false; },
                        () -> { reportMainPage(bController, 2); return false; },
                        () -> true
                }
            );
        }while (!isExit);
    }

    private static void reportMainPage(BookInfoController bController, int reportType) {
        BookReportController reportController = new BookReportController(bController.getbookTree());
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Book Inventory Reports");
        String report = "";
        String fileName = "";
        switch (reportType) {
            case 1: report = reportController.generateAdminReport1(); fileName = "Low_Stock_Report"; break;
            case 2: report = reportController.generateAdminReport2(); fileName = "Inventory_Cost_Report"; break;
        }

        String choice = UserPrompt.inputString("\nWould you like to export this report to a text file? (Y/N) >> ", 1, false);

        if ("Y".equalsIgnoreCase(choice)) {
            reportController.exportToFile(report, fileName);
        } else {
            UIGenerator.pressEnterToContinue();
        }
    }

    public static String displayLowStockGraph(String title, StringBuilder barBuilder, MapInterface<String, Book> lowStockInfo, int tableSize) {
        StringBuilder reportBuilder = new StringBuilder();
        UIGenerator.appendHeader(reportBuilder, "BOOK INVENTORY ANALYSIS", title);

        reportBuilder.append(barBuilder);
        
        UIGenerator.appendCenteredLine(reportBuilder, "--- BOOKS REQUIRING RESTOCKING ---");

        // Table Header
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-8s | %-60s | %-10s | %-21s |", "BOOK ID", "TITLE", "STOCK (%)", "QTY AVAILABLE / TOTAL"));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));

        double totalLowStockPercentage = 0.0; 
        int totalLowStockBooks = 0;
        for (MapInterface.Entry<String, Book> entry : lowStockInfo) {
            Book book = entry.getValue();
            double stockPercentage = Double.parseDouble(entry.getKey().split("-")[0]);
            UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-8s | %-60s | %9.2f%% | %13s / %-5s |", 
                book.getId(), 
                DataProcessor.truncate(book.getTitle(), 60), 
                stockPercentage, 
                book.getQtyAvailable(),
                book.getQtyTotal())
            );
            totalLowStockPercentage += stockPercentage;
            totalLowStockBooks++;
        }
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));
        double avgLowStockPercentage = (totalLowStockBooks == 0) ? 0.0 : (totalLowStockPercentage / totalLowStockBooks);
        UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-8s | %-60s | %-3s %5.2f%% | %-21s |", " ", "Total Low Stock Books : " + totalLowStockBooks, "Avg", avgLowStockPercentage, " "));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));
        reportBuilder.append("\n");
        UIGenerator.appendFooter(reportBuilder);

        System.out.println(reportBuilder.toString());
        return reportBuilder.toString();
    }

    public static String displayCostByCategoryGraph(String title, String[] labels, int[] scaledValues, 
        String[] tableRows, int chartSize, int chartHeight, int tableSize, double standMaxCost, double totalTopNCost, int totalTopNCount) {

        StringBuilder reportBuilder = new StringBuilder();
        UIGenerator.appendHeader(reportBuilder, "BOOK INVENTORY ANALYSIS", title);

        // Vertical bar chart
        UIGenerator.appendCenteredLine(reportBuilder, String.format("--- TOP %d CATEGORIES BY COST ---", chartSize));
        reportBuilder.append("\n");

        UIGenerator.appendVerticalBarChart(reportBuilder, labels, scaledValues, chartSize, chartHeight, COLUMN_WIDTH, standMaxCost, false);
        reportBuilder.append("\n\n");

        // Table
        UIGenerator.appendCenteredLine(reportBuilder, String.format("--- TOP %d CATEGORIES BY COST ---", tableSize));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-40s-+-%-20s-+-%-11s-+", "-".repeat(40), "-".repeat(20), "-".repeat(11)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-40s | %-20s | %-11s |", "CATEGORY", "TOTAL COST", "TOTAL BOOKS"));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-40s-+-%-20s-+-%-11s-+", "-".repeat(40), "-".repeat(20), "-".repeat(11)));

        // Print the perfectly sorted rows
        for (int i = 0; i < tableRows.length; i++) {
            UIGenerator.appendCenteredLine(reportBuilder, tableRows[i]);
        }
        
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-40s-+-%-20s-+-%-11s-+", "-".repeat(40), "-".repeat(20), "-".repeat(11)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-40s | %-20s | %-11s |", "Total (Top " + tableSize + ")", String.format("$ %,18.2f", totalTopNCost), String.format("%11d", totalTopNCount)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-40s-+-%-20s-+-%-11s-+", "-".repeat(40), "-".repeat(20), "-".repeat(11)));

        reportBuilder.append("\n");

        System.out.println(reportBuilder);
        return reportBuilder.toString();
    }
}