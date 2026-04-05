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
    private final String REPORT_DIRECTORY = "./database/reports/book_reports/";
    final int COLUMN_WIDTH = 13;  

    public BookReportController(MapInterface<String, Book> bookTree) {
        this.bookTree = bookTree;    
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
        UIGenerator.appendHeader(reportBuilder, "BOOK INVENTORY ANALYSIS", title);

        int lowStockCount = lowStockInfo.size();
        int healthyCount = totalBooksInLibrary - lowStockCount;
        
        // The scale of the graph depends on whichever group has MORE books
        int maxScale = Math.max(lowStockCount, healthyCount);
        int MAX_BAR_LENGTH = 40;

        UIGenerator.appendCenteredLine(reportBuilder, "--- INVENTORY HEALTH SUMMARY ---\n");

        // Draw Healthy Books Bar
        int healthyBarLen = (maxScale == 0) ? 0 : (int) Math.round(((double) healthyCount / maxScale) * MAX_BAR_LENGTH);
        StringBuilder healthyBar = new StringBuilder();
        for (int i = 0; i < healthyBarLen; i++) healthyBar.append("#");
        reportBuilder.append(String.format("%-20s | %-40s | %d Books\n", "Healthy Stock", healthyBar.toString(), healthyCount));

        // Draw Low Stock Books Bar
        int lowBarLen = (maxScale == 0) ? 0 : (int) Math.round(((double) lowStockCount / maxScale) * MAX_BAR_LENGTH);
        StringBuilder lowBar = new StringBuilder();
        for (int i = 0; i < lowBarLen; i++) lowBar.append("#");
        reportBuilder.append(String.format("%-20s | %-40s | %d Books\n\n\n", "Low Stock", lowBar.toString(), lowStockCount));
        
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

        return reportBuilder.toString();
    }

    public String generateAdminReport1() {
        int lowStockThreshold = UserPrompt.inputInteger("Enter low stock threshold (%) >> ", 1, 999);
        MapInterface<String, Book> lowStockData = getLowStockByBooks(lowStockThreshold);
        
        String report = generateLowStockInfoGraph(
            "Stock Level Analysis ( Qty <= " + lowStockThreshold + "% of stock )", 
            lowStockData, 
            bookTree.size()
        );

        return report;
    }

    
    /**
     * REPORT 2 Total Inventory Cost by Category
     */
    public MapInterface<String, Integer> getInventoryCostByCategory(boolean ignoreUnknown) {
        MapInterface<String, Integer> costInfo = new TreeMap<>();  
        MapInterface<String, Double> costCount = new TreeMap<>(); 
        MapInterface<String, Integer> categoryBooksCount = new TreeMap<>();

        for (MapInterface.Entry<String, Book> entry : bookTree) {
            Book book = entry.getValue();
            double totalBookValue = book.getPrice() * book.getQtyTotal();

            int categoryCount = book.getCategories().size();
            double splitValue = (categoryCount > 0) ? (totalBookValue / categoryCount) : totalBookValue;

            for (MapInterface.Entry<String, Integer> catEntry : book.getCategories()) {
                if(ignoreUnknown && catEntry.getKey().equalsIgnoreCase("unknown")) {
                    continue;
                }
                String category = catEntry.getKey();
                Double currentTotal = costCount.get(category);
                int totalBoosksInCategory = categoryBooksCount.get(category) != null ? categoryBooksCount.get(category) : 0;
                
                if (currentTotal == null) {
                    costCount.put(category, splitValue);
                    categoryBooksCount.put(category, 1);
                } else {
                    costCount.put(category, currentTotal + splitValue);
                    categoryBooksCount.put(category, totalBoosksInCategory + 1);
                }
            }
        }

        for (MapInterface.Entry<String, Double> cost : costCount) {
            String category = cost.getKey();
            Double totalValue = cost.getValue(); 
            int totalBooks = categoryBooksCount.get(category);

            String sortedKey = IdGenerator.getKeyId(totalValue, category);
            
            costInfo.put(sortedKey, totalBooks);
        }
        
        return costInfo;
    }

    private String generateCostByCategoryGraph(String title, MapInterface<String, Integer> costInfo, int topN) {
        StringBuilder reportBuilder = new StringBuilder();
        UIGenerator.appendHeader(reportBuilder, "BOOK INVENTORY ANALYSIS", title);
        
        // Use the CLEAN map size for all math!
        int totalCategories = costInfo.size();
    
        int chartLimit = Math.min(topN, 10); 
        int chartSize = Math.min(chartLimit, totalCategories);
        int tableSize = Math.min(topN, totalCategories);
        
        int skipChartCount = totalCategories - chartSize;
        int skipTableCount = totalCategories - tableSize;

        // "032.50-Science Fiction" -> ["032.50", "Science Fiction"]
        double maxCost = Double.parseDouble(costInfo.max().getKey().split("-")[0]);
        double standMaxCost = DataProcessor.getStandardScale(maxCost, false);
        int chartHeight = Math.min(10, (int) Math.round(standMaxCost));
        double totalTopNCost = 0.0;
        int totalTopNCount = 0;

        String[] labels = new String[chartSize];
        int[] scaledValues = new int[chartSize];

        String[] tableRows = new String[tableSize];
        int currentIndex = 0;
        int chartIndex = 0;

        // Loop over the map
        for (MapInterface.Entry<String, Integer> entry : costInfo) {
            String[] keyParts = entry.getKey().split("-");
            double cost = keyParts.length > 0 ? Double.parseDouble(keyParts[0]) : 0.0;
            String categoryName = (keyParts.length > 1) ? keyParts[1] : entry.getKey();
            int bookCount = entry.getValue();

            // Gather Data for the Vertical Bar Chart
            if (currentIndex >= skipChartCount) {
                int targetChartIndex = (chartSize - 1) - chartIndex;
                
                labels[targetChartIndex] = DataProcessor.truncate(categoryName, COLUMN_WIDTH);
                scaledValues[targetChartIndex] = (standMaxCost == 0) ? 0 : (int) Math.round((cost / standMaxCost) * (chartHeight * 2));
                
                chartIndex++;
            }

            // Gather Data for the Table
            if (currentIndex >= skipTableCount) {
                totalTopNCost += cost;
                totalTopNCount += bookCount;

                // (Your table reversal logic remains perfectly the same)
                int targetIndex = (totalCategories - 1) - currentIndex;
                
                tableRows[targetIndex] = String.format("| %-40s | %-20s | %-11s |", 
                    DataProcessor.truncate(categoryName, 40), 
                    String.format("$ %,18.2f", cost),
                    String.format("%11d", bookCount));
            }
            currentIndex++;
        }

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

    public String generateAdminReport2() {
        int topN = UserPrompt.inputInteger("Enter the number of top categories to display >> ", 1, 9999);
        boolean ignoreUnknown = UserPrompt.inputString("Ignore unknown categories? (y/n) >> ", 1, false).equalsIgnoreCase("y");

        MapInterface<String, Integer> costData = getInventoryCostByCategory(ignoreUnknown);

        String report = generateCostByCategoryGraph(
            "Total Cost and Book Count by Category", 
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
        File directory = new File(REPORT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (FileWriter writer = new FileWriter(REPORT_DIRECTORY + finalFileName)) {
            writer.write(reportContent);
            
            System.out.println("\nReport successfully exported to: " + REPORT_DIRECTORY + finalFileName);
        } catch (IOException e) {
            System.out.println("\nError: Failed to save the report: " + e.getMessage());
        }
        
        UIGenerator.pressEnterToContinue();
    }
}