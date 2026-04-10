package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;
import com.mycompany.library_system.control.BookBorrowControl;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.ChronoUnit;

public class BorrowReportController {

    private MapInterface<String, BookBorrowRecord> borrowTree;
    private final String REPORT_DIRECTORY = "./database/reports/borrow_reports/";
    private final int COLUMN_WIDTH = 13;
    
    public BorrowReportController(MapInterface<String, BookBorrowRecord> borrowTree) {
        this.borrowTree = borrowTree;
    }

    /*
    Report #1: Most High in Demand Books report
     */
    public MapInterface<String, Integer> calulateBorrowsPerBook(LocalDate startDate, LocalDate endDate) {
        MapInterface<String, Integer> bookBorrowMap = new TreeMap<>();
        MapInterface<String, Integer> sortedBookBorrowMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.borrowTree) {
            if (record.getValue().getBorrowDate().isBefore(startDate) || record.getValue().getBorrowDate().isAfter(endDate)) {continue;}
            String bookId = record.getValue().getBookId();
            
            if(!bookBorrowMap.containsKey(bookId)) {bookBorrowMap.put(bookId, 1);}
            else {
                int currentCount = bookBorrowMap.get(bookId);
                bookBorrowMap.put(bookId, currentCount+1);
            }
        }

        for(MapInterface.Entry<String, Integer> record: bookBorrowMap) {
            sortedBookBorrowMap.put(IdGenerator.getKeyId(record.getValue(), record.getKey()), record.getValue());
        }

        return sortedBookBorrowMap;
    }

    private String generateMostBorrowedBookReport(String title, MapInterface<String, Integer> bookBorrowMap, MapInterface<String, Book> bookTree, LocalDate startDate, LocalDate endDate, int topN) {
    
        if(bookBorrowMap.size() == 0) {
            return title + "\nNo data available to generate report";
        }

        StringBuilder reportBuilder = new StringBuilder();
        UIGenerator.appendHeader(reportBuilder, "BOOK BORROWING ANALYSIS", title);

        int totalUniqueBooksBorrowed = bookBorrowMap.size();

        int chartLimit = Math.min(topN, 10);
        int chartSize = Math.min(chartLimit, totalUniqueBooksBorrowed);
        int tableSize = Math.min(topN, totalUniqueBooksBorrowed);

        int skipChartCount = totalUniqueBooksBorrowed - chartSize;
        int skipTableCount = totalUniqueBooksBorrowed - tableSize;

        int maxBorrowCount = Integer.parseInt(bookBorrowMap.max().getKey().split("-")[0]);
        double standardMaxBorrowCount = DataProcessor.getStandardScale(maxBorrowCount, true);
        int chartHeight = Math.min(10, (int) Math.round(standardMaxBorrowCount));
        int totalNumOfBorrows = 0;

        String[] labels = new String[chartSize];
        int[] scaledValues = new int[chartSize];
        String[] tableRows = new String[tableSize];
        int currentIndex = 0;
        int chartIndex = 0;
        
        for(MapInterface.Entry<String, Integer> entry: bookBorrowMap) {

            
            String[] keyParts = entry.getKey().split("-");
            int numOfBorrows = entry.getValue();
            String bookId = keyParts[1];

            if (currentIndex >= skipChartCount) {
                int targetChartIndex = (chartSize - 1) - chartIndex;
                
                labels[targetChartIndex] = bookId;
                scaledValues[targetChartIndex] = (standardMaxBorrowCount == 0) ? 0 : (int) Math.round((numOfBorrows / standardMaxBorrowCount) * (chartHeight * 2));
                
                chartIndex++;
            }

            if(currentIndex >= skipTableCount) {
                totalNumOfBorrows += numOfBorrows;
                int targetIndex = (totalUniqueBooksBorrowed - 1) - currentIndex;
                double totalPercentageBorrowed = ((double) numOfBorrows / bookTree.get(bookId).getQtyTotal()) * 100;
                tableRows[targetIndex] = String.format("| %-11s | %-40s | %-15d | %-11d | %-11s |",
                    bookId,
                    DataProcessor.truncate(bookTree.get(bookId).getTitle(),40),
                    numOfBorrows,
                    bookTree.get(bookId).getQtyTotal(),
                    String.format("%.2f %%", totalPercentageBorrowed));
            }
            currentIndex++;
        }
        
        UIGenerator.appendCenteredLine(reportBuilder, String.format("--- Top %d Books Borrowed between %s to %s ---", chartSize, startDate.toString(), endDate.toString()));
        reportBuilder.append("\n");
        UIGenerator.appendVerticalBarChart(reportBuilder, labels, scaledValues, chartSize, chartHeight, COLUMN_WIDTH, standardMaxBorrowCount,true);
        reportBuilder.append("\n\n\n\n");

        UIGenerator.appendCenteredLine(reportBuilder, String.format("--- TOP %d MOST IN-DEMAND BOOKS BORROWED", tableSize));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-11s-+-%-40s-+-%-15s-+-%-11s-+-%-11s-+", "-".repeat(11), "-".repeat(40), "-".repeat(15), "-".repeat(11), "-".repeat(11)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-11s | %-40s | %-15s | %-11s | %-11s |", "BOOK ID", "BOOK TITLE", "NO. BORROWED", "TOTAL QTY", "% BORROWED"));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-11s-+-%-40s-+-%-15s-+-%-11s-+-%-11s-+", "-".repeat(11), "-".repeat(40), "-".repeat(15), "-".repeat(11), "-".repeat(11)));

        for(int i = 0; i < tableRows.length; i++) {
            UIGenerator.appendCenteredLine(reportBuilder, tableRows[i]);
        }
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-11s-+-%-40s-+-%-15s-+-%-11s-+-%-11s-+", "-".repeat(11), "-".repeat(40), "-".repeat(15), "-".repeat(11), "-".repeat(11)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("\n\n %-28s : %d\n", "Total No. of Books Borrowed", totalNumOfBorrows));

        return reportBuilder.toString();

    }

    public String generateMostBorrowedBooksReport(MapInterface<String, Book> bookTree) {
        LocalDate[] startAndEndDates = promptStartDateandEndDates(BookBorrowControl.getBorrowStartDate());
        int topN = UserPrompt.inputInteger("Enter the number of top books borrowed to display >>> ", 1, 9999);
        MapInterface<String, Integer> bookBorrowMap = calulateBorrowsPerBook(startAndEndDates[0], startAndEndDates[1]);
        String report = generateMostBorrowedBookReport(
            String.format("Most Borrowed Books from %s to %s", startAndEndDates[0].toString(), startAndEndDates[1].toString()),
            bookBorrowMap,
            bookTree,
            startAndEndDates[0],
            startAndEndDates[1],
            topN
        );
        return report;
    }

    public LocalDate[] promptStartDateandEndDates(LocalDate earliestDate) {
        boolean validDates = false;
        boolean alreadyInputStartDate = false;
        LocalDate[] startAndEndDates = new LocalDate[2];
        while(!validDates) {

            try {
                if(!alreadyInputStartDate) {
                    System.out.printf("Enter a date starting from (" + earliestDate + "):\n");
                    int[] startDateArray = UserPrompt.inputDate(new String[] {
                        "Enter year >>> ",
                        "Enter month >>> ",
                        "Enter day >>> ",
                    },
                    true, 
                    true, 
                    true);

                    String startDateString = String.format("%04d-%02d-%02d", startDateArray[2], startDateArray[1], startDateArray[0]);
                    startAndEndDates[0] = LocalDate.parse(startDateString);
                    alreadyInputStartDate = true;
                }

                boolean afterStartDate = false;

                while(!afterStartDate) {
                    System.out.println("Enter an end date to filter:");
                    int[] endDateArray = UserPrompt.inputDate(new String[] {
                    "Enter year >>> ",
                    "Enter month >>> ",
                    "Enter day >>> ",
                    },
                    true, 
                    true, 
                    true);

                    String endDateString = String.format("%04d-%02d-%02d", endDateArray[2], endDateArray[1], endDateArray[0]);
                    startAndEndDates[1] = LocalDate.parse(endDateString);

                    if(startAndEndDates[1].isBefore(startAndEndDates[0])) {
                        System.out.println("Enter a date after or equal to : " + startAndEndDates[0]);
                        continue;
                    }
                    afterStartDate = true;
                }
                validDates = true;
            }
            catch(DateTimeParseException e) {
                System.out.println("Please enter according to the valid format");
            }
        }

        return startAndEndDates;

        
    }
    


    /*
    Report #2: Average Borrow Duration by Users
     */

    public MapInterface<String, Integer> calculateUserBorrowDays() {
        MapInterface<String, Integer> userBorrowDaysMap = new TreeMap<>();
        MapInterface<String, Integer> sortedUserBorrowDaysMap = new TreeMap<>();

        for(MapInterface.Entry<String, BookBorrowRecord> entry: this.borrowTree) {
            int borrowedDays;
            BookBorrowRecord record = entry.getValue();
            if(record.getAlreadyReturned()) {borrowedDays = (int) ChronoUnit.DAYS.between( record.getBorrowDate(), record.getReturnDate());}
            else {borrowedDays = BookBorrowControl.getBorrowDaysAllowed();}
            userBorrowDaysMap.put(IdGenerator.getKeyId(record.getUserId(), record.getBorrowId()), borrowedDays);
        }
        String currentUser = "";
        int totalNumOfBorrowings = 0;
        int totalBorrowDays = 0;
        for(MapInterface.Entry<String, Integer> entry: userBorrowDaysMap) {
            String user = entry.getKey().split("-")[0];
            if(currentUser.isEmpty()) {
                currentUser = user;
            }

            if(!currentUser.equals(user)) {
                double averageBorrowDays = (double) totalBorrowDays / (double) totalNumOfBorrowings;
                sortedUserBorrowDaysMap.put(IdGenerator.getKeyId(averageBorrowDays, currentUser), totalNumOfBorrowings);
                currentUser = user;
                totalNumOfBorrowings = 0;
                totalBorrowDays = 0;
            }
            totalBorrowDays += entry.getValue();
            totalNumOfBorrowings++;
        }
        double averageBorrowDays = (double) totalBorrowDays / (double) totalNumOfBorrowings;
        sortedUserBorrowDaysMap.put(IdGenerator.getKeyId(averageBorrowDays, currentUser), totalNumOfBorrowings);

        return sortedUserBorrowDaysMap;
    }

    public String generateUserAverageBorrowTimeReport(String title, MapInterface<String, Integer> userBorrowDurationMap, MapInterface<String, User> userTree, int topN) {
        if (userBorrowDurationMap.isEmpty()) {
            return title + "\nNo data available to generate report.\n";
        }

        StringBuilder reportBuilder = new StringBuilder();
        UIGenerator.appendHeader(reportBuilder, "USER BORROW DURATION AND ACTIVITY ANALYSIS", title);

        int totalBorrowingUsers = userBorrowDurationMap.size();

        int chartLimit = Math.min(topN, 10);
        int chartSize = Math.min(chartLimit, totalBorrowingUsers);
        int tableSize = Math.min(topN, totalBorrowingUsers);

        int skipChartCount = totalBorrowingUsers - chartSize;
        int skipTableCount = totalBorrowingUsers - tableSize;

        double maxBorrowDays = Double.parseDouble(userBorrowDurationMap.max().getKey().split("-")[0]);
        double standardMaxBorrowDays = DataProcessor.getStandardScale(maxBorrowDays, false);
        int chartHeight = Math.min(10, (int) Math.round(standardMaxBorrowDays));
        int totalNumOfBooksBorrowed = 0;

        String[] labels = new String[chartSize];
        int[] scaledValues = new int[chartSize];

        String[] tableRows = new String[tableSize];
        int currentIndex = 0;
        int chartIndex = 0;

        for (MapInterface.Entry<String, Integer> entry: userBorrowDurationMap) {
            String[] keyParts = entry.getKey().split("-");
            double averageBorrowDays = Double.parseDouble(keyParts[0]);
            String userId = capitalize(keyParts[1]);
            
            int numOfBorrows = entry.getValue();

            if(currentIndex >= skipChartCount) {
                int targetChartIndex = (chartSize - 1) - chartIndex;
                labels[targetChartIndex] = DataProcessor.truncate(userId, COLUMN_WIDTH);
                scaledValues[targetChartIndex] = (standardMaxBorrowDays == 0) ? 0 : (int) Math.round((averageBorrowDays / standardMaxBorrowDays) * (chartHeight * 2));
                chartIndex++;
            }

            if(currentIndex >= skipTableCount) {
                totalNumOfBooksBorrowed += numOfBorrows;
                
                int targetIndex = (totalBorrowingUsers - 1) - currentIndex;

                tableRows[targetIndex] = String.format("| %-11s | %-40s | %-18.2f | %-11d |",
                    userId,
                    userTree.get(userId).getName(),
                    averageBorrowDays,
                    numOfBorrows
                );
            }
            currentIndex++;
        }
        UIGenerator.appendCenteredLine(reportBuilder, String.format("--- TOP %d USERS BY AVERAGE BORROW DURATION (DAYS) ---", chartSize));
        reportBuilder.append("\n");

        UIGenerator.appendVerticalBarChart(reportBuilder, labels, scaledValues, chartSize, chartHeight, COLUMN_WIDTH, standardMaxBorrowDays, false);
        reportBuilder.append("\n\n\n\n");

        UIGenerator.appendCenteredLine(reportBuilder, String.format("--- TOP %d USERS BY AVERAGE BORROW DURATION (DAYS)", tableSize));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-11s-+-%-40s-+-%-18s-+-%-11s-+", "-".repeat(11), "-".repeat(40), "-".repeat(18), "-".repeat(11)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("| %-11s | %-40s | %-18s | %-11s |", "USER ID", "NAME", "AVERAGE DURATION", "NO. BORROWED"));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-11s-+-%-40s-+-%-18s-+-%-11s-+", "-".repeat(11), "-".repeat(40), "-".repeat(18), "-".repeat(11)));
        
        for(int i = 0 ; i < tableRows.length ; i++) {
            UIGenerator.appendCenteredLine(reportBuilder, tableRows[i]);
        }
        UIGenerator.appendCenteredLine(reportBuilder, String.format("+-%-11s-+-%-40s-+-%-18s-+-%-11s-+", "-".repeat(11), "-".repeat(40), "-".repeat(18), "-".repeat(11)));
        UIGenerator.appendCenteredLine(reportBuilder, String.format("\n\n %-28s : %d\n", "Total No. of Books Borrowed", totalNumOfBooksBorrowed));
        
        return reportBuilder.toString();
    }
    

    public String generateUserAverageBorrowTimeReport(MapInterface<String, User> userTree) {
        int topN = UserPrompt.inputInteger("Enter the number of top users to display >>> ", 1, 9999);
        MapInterface<String, Integer> userBorrowDurationMap = calculateUserBorrowDays();
        String report = generateUserAverageBorrowTimeReport(
            "AVERAGE USER BORROW DURATION", 
            userBorrowDurationMap, 
            userTree, 
            topN);

        return report;
    }

    public void exportToFile(String reportContent, String fileName) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        String timestamp = now.format(formatter);
        String finalFileName = fileName + "_" + timestamp + ".txt";

        File directory = new File(REPORT_DIRECTORY);
        if(!directory.exists()) {
            directory.mkdirs();
        }

        try(FileWriter writer = new FileWriter(REPORT_DIRECTORY + finalFileName)) {
            writer.write(reportContent);
            System.out.println("\nReport successfully exported to: " + REPORT_DIRECTORY + finalFileName);
        }
        catch(IOException e) {
            System.out.println("\nError: Failed to save the report: " + e.getMessage());
        }

        UIGenerator.pressEnterToContinue();
    }

    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
