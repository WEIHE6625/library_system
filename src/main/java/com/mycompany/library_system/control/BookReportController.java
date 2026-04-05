package com.mycompany.library_system.control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

public class BookReportController {
    
    private MapInterface<String, Book> bookTree;
    private MapInterface<String, Book> bookCatTree;
    private MapInterface<String, Book> bookAQtyTree;

    public BookReportController(
            MapInterface<String, Book> bookTree, 
            MapInterface<String, Book> bookCatTree,
            MapInterface<String, Book> bookAQtyTree
        ) {
        this.bookTree = bookTree;
        this.bookCatTree = bookCatTree;
        this.bookAQtyTree = bookAQtyTree;
    }

    /**
     * REPORT 1 Low Stock Alert by Books
     */
    public MapInterface<String, Book> getLowStockByBooks(double lowStockThreshold) {
        MapInterface<String, Book> lowStockInfo = new TreeMap<>();

        for (MapInterface.Entry<String, Book> entry : bookTree) {
            Book book = entry.getValue();
            double currentStockPercentage = ((double) book.getQtyAvailable() / book.getQtyTotal() * 100);
            if (currentStockPercentage <= lowStockThreshold) {
                lowStockInfo.put(IdGenerator.getKeyId(currentStockPercentage, book.getId()), book);
            }
        }
        return lowStockInfo;
    }

    private String generateLowStockInfoGraph(String title, MapInterface<String, Book> lowStockInfo, int totalBooksInLibrary) {
        if (lowStockInfo.isEmpty() && totalBooksInLibrary == 0) {
            return title + "\nNo data available to generate report.\n";
        }

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append(UIGenerator.titleBuilder(title.toUpperCase()));

        int lowStockCount = lowStockInfo.size();
        int healthyCount = totalBooksInLibrary - lowStockCount;
        
        // The scale of the graph depends on whichever group has MORE books
        int maxScale = Math.max(lowStockCount, healthyCount);
        int MAX_BAR_LENGTH = 40;

        reportBuilder.append("\n--- INVENTORY HEALTH SUMMARY ---\n");

        // Draw Healthy Books Bar
        int healthyBarLen = (maxScale == 0) ? 0 : (int) Math.round(((double) healthyCount / maxScale) * MAX_BAR_LENGTH);
        StringBuilder healthyBar = new StringBuilder();
        for (int i = 0; i < healthyBarLen; i++) healthyBar.append("\u2588");
        reportBuilder.append(String.format("%-20s | %-40s | %d Books\n", "Healthy Stock", healthyBar.toString(), healthyCount));

        // Draw Low Stock Books Bar
        int lowBarLen = (maxScale == 0) ? 0 : (int) Math.round(((double) lowStockCount / maxScale) * MAX_BAR_LENGTH);
        StringBuilder lowBar = new StringBuilder();
        for (int i = 0; i < lowBarLen; i++) lowBar.append("\u2588");
        reportBuilder.append(String.format("%-20s | %-40s | %d Books\n", "Low Stock", lowBar.toString(), lowStockCount));


        reportBuilder.append("\n" + UIGenerator.titleBuilder("BOOKS REQUIRING RESTOCKING"));
        
        // Table Header
        reportBuilder.append(String.format("\n+-%-8s-+-%-60s-+-%-10s-+-%-21s-+\n", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));
        reportBuilder.append(String.format("| %-8s | %-60s | %-10s | %-21s |\n", "BOOK ID", "TITLE", "STOCK (%)", "QTY AVAILABLE / TOTAL"));
        reportBuilder.append(String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+\n", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));

        double totalLowStockPercentage = 0.0; 
        int totalLowStockBooks = 0;
        for (MapInterface.Entry<String, Book> entry : lowStockInfo) {
            Book book = entry.getValue();
            double stockPercentage = Double.parseDouble(entry.getKey().split("-")[0]);
            reportBuilder.append(String.format("| %-8s | %-60s | %9.2f%% | %13s / %-5s |\n", 
                book.getId(), 
                DataProcessor.truncate(book.getTitle(), 58), 
                stockPercentage, 
                book.getQtyAvailable(),
                book.getQtyTotal())
            );
            totalLowStockPercentage += stockPercentage;
            totalLowStockBooks++;
        }
        reportBuilder.append(String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+\n", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));
        double avgLowStockPercentage = (totalLowStockBooks == 0) ? 0.0 : (totalLowStockPercentage / totalLowStockBooks);
        reportBuilder.append(String.format("| %-8s | %-60s | %-3s %5.2f%% | %-21s |\n", " ", "Total Low Stock Books : " + totalLowStockBooks, "Avg", avgLowStockPercentage, " "));
        reportBuilder.append(String.format("+-%-8s-+-%-60s-+-%-10s-+-%-21s-+\n", "-".repeat(8), "-".repeat(60), "-".repeat(10), "-".repeat(21)));

        return reportBuilder.toString();
    }

    public String generateAdminReport1() {
        int lowStockThreshold = UserPrompt.inputInteger("Enter low stock threshold (%) >> ", 1, 999);
        MapInterface<String, Book> lowStockData = getLowStockByBooks(lowStockThreshold);
        
        String report = generateLowStockInfoGraph(
            "Stock Level Analysis ( Qty <=" + lowStockThreshold + "% of stock )", 
            lowStockData, 
            bookTree.size()
        );

        return report;
    }

    
    /**
     * REPORT 2 Total Inventory Cost by Category
     */
    public MapInterface<String, Double> getInventoryCostByCategory() {
        MapInterface<String, Double> costInfo = new TreeMap<>();

        for (MapInterface.Entry<String, Book> entry : bookTree) {
            Book book = entry.getValue();
            double totalBookValue = book.getPrice() * book.getQtyTotal();

            int categoryCount = book.getCategories().size();
            double splitValue = (categoryCount > 0) ? (totalBookValue / categoryCount) : totalBookValue;

            for (MapInterface.Entry<String, Integer> catEntry : book.getCategories()) {
                String category = catEntry.getKey();
                Double currentTotal = costInfo.get(category);
                
                if (currentTotal == null) {
                    costInfo.put(category, splitValue);
                } else {
                    costInfo.put(category, currentTotal + splitValue);
                }
            }
        }
        
        return costInfo;
    }

    private String generateCostByCategoryGraph(String title, MapInterface<String, Double> costInfo, int topN) {
        return "Cost by Category Graph - To be implemented";
    }

    public String generateAdminReport2(int topN) {
        MapInterface<String, Double> costData = getInventoryCostByCategory();

        String report = generateCostByCategoryGraph(
            "Total Asset Value by Category", 
            costData, 
            topN
        );

        return report;
    }

    public void exportToFile(String reportContent, String fileName) {        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        String timestamp = now.format(formatter);
        String finalFileName = fileName + "_" + timestamp + ".txt";
        String directoryPath = "./dataset/book_reports/";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (FileWriter writer = new FileWriter(directoryPath + finalFileName)) {
            writer.write(reportContent);
            
            System.out.println("\nReport successfully exported to: " + directoryPath + finalFileName);
        } catch (IOException e) {
            System.out.println("\nError: Failed to save the report: " + e.getMessage());
        }
        
        UIGenerator.pressEnterToContinue();
    }
}