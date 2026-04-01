package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.utility.DataProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;



public class ReportService {


    //1
    public static String buildUserSummaryReport(TreeMap<String, User> userMap, LocalDate fromDate, LocalDate toDate) {

        ListInterface<User> allUsers = userMap.values();
        int filteredTotal = 0;
        int adminCount = 0 ;
        int memberCount = 0;

        LocalDate actualToDate = (toDate==null)?LocalDate.now():toDate;

        for(int i = 0; i < allUsers.size(); i++) {
            User u = allUsers.get(i);
            LocalDate userDate = DataProcessor.parseAnyDate(u.getCreatedAt());

            if(userDate!=null) {
                boolean isAfterFrom = (fromDate == null) || !userDate.isBefore(fromDate);
                boolean isBeforeTo = !userDate.isAfter(actualToDate);

                if (isAfterFrom && isBeforeTo) {
                    filteredTotal++;
                    if (u.getRole() == User.UserRole.ADMIN) adminCount++;
                    else memberCount++;
                }
            }
        }


        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("         USER ACCOUNT SUMMARY             \n");
        sb.append("Period: ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("==========================================\n");
        sb.append("Total Users in Period: ").append(filteredTotal).append("\n");
        sb.append("Admins:                ").append(adminCount).append("\n");
        sb.append("Members:               ").append(memberCount).append("\n");
        sb.append("==========================================\n");

        return sb.toString();
    }

    //2
    public static String buildBorrowSummaryReport(TreeMap<String, BookBorrowRecord> borrowMap, LocalDate fromDate, LocalDate toDate) {
        ListInterface<BookBorrowRecord> allBorrows = borrowMap.values();

        // Counter Variables
        int totalInPeriod = 0;
        int returnedCount = 0;
        int pendingCount = 0;
        int lostCount = 0;

        // Default to "Now" if To Date is blank
        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        for (int i = 0; i < allBorrows.size(); i++) {
            BookBorrowRecord record = allBorrows.get(i);

            // Use the "Any Date" parser we built for DataProcessor
            LocalDate borrowDate = record.getBorrowDate();

            if (borrowDate != null) {
                // Range Filter Logic
                boolean isAfterFrom = (fromDate == null) || !borrowDate.isBefore(fromDate);
                boolean isBeforeTo = !borrowDate.isAfter(actualToDate);

                if (isAfterFrom && isBeforeTo) {
                    totalInPeriod++;

                    // Tallying the Status based on your Entity methods
                    if (record.getIsLostBook()) {
                        lostCount++;
                    } else if (record.getAlreadyReturned()) {
                        returnedCount++;
                    } else {
                        pendingCount++;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       LIBRARY BORROW STATUS SUMMARY      \n");
        sb.append("==========================================\n");

        // Display the filter period for clarity
        sb.append("Report Period: ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("------------------------------------------\n");

        sb.append(String.format("%-25s : %d\n", "Total Transactions", totalInPeriod));
        sb.append(String.format("%-25s : %d\n", "Successfully Returned", returnedCount));
        sb.append(String.format("%-25s : %d\n", "Currently Borrowed", pendingCount));
        sb.append(String.format("%-25s : %d\n", "Declared Lost", lostCount));
        sb.append("------------------------------------------\n");

        if (totalInPeriod > 0) {
            double lossRate = ((double) lostCount / totalInPeriod) * 100;
            sb.append(String.format("Current Loss Rate     : %.1f%%\n", lossRate));
        }

        sb.append("==========================================\n");
        return sb.toString();
    }

    //3
    public static String buildGlobalFineSummaryReport(TreeMap<String, BorrowFineRecord> fineMap, LocalDate fromDate, LocalDate toDate) {
        ListInterface<BorrowFineRecord> allFines = fineMap.values();

        int totalInPeriod = 0;
        int paidCount = 0;
        int pendingCount = 0;
        double totalCollected = 0.0;
        double totalOutstanding = 0.0;

        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        for (int i = 0; i < allFines.size(); i++) {
            BorrowFineRecord record = allFines.get(i);

            // 1. Use the Issued Date for the filter
            LocalDate issuedDate = record.getFine().getFineDate();

            if (issuedDate != null) {
                // 2. The Range Filter Logic
                boolean isAfterFrom = (fromDate == null) || !issuedDate.isBefore(fromDate);
                boolean isBeforeTo = !issuedDate.isAfter(actualToDate);

                if (isAfterFrom && isBeforeTo) {
                    totalInPeriod++;
                    double amount = record.getFine().getFineAmount();

                    // 3. Tally based on payment status
                    if (record.getFinePayed()) {
                        paidCount++;
                        totalCollected += amount;
                    } else {
                        pendingCount++;
                        totalOutstanding += amount;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("        LIBRARY FINANCIAL SUMMARY         \n");
        sb.append("==========================================\n");

        // We update the label to "Issued Period" to be precise
        sb.append("Issued Period: ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("------------------------------------------\n");

        sb.append(String.format("%-22s : %d\n", "Total Fines Issued", totalInPeriod));
        sb.append(String.format("%-22s : %d (RM%.2f)\n", "Collected (Paid)", paidCount, totalCollected));
        sb.append(String.format("%-22s : %d (RM%.2f)\n", "Outstanding (Unpaid)", pendingCount, totalOutstanding));
        sb.append("------------------------------------------\n");

        double totalRevenue = totalCollected + totalOutstanding;
        sb.append(String.format("TOTAL PROJECTED REVENUE: RM%.2f\n", totalRevenue));

        if (totalRevenue > 0) {
            double collectionRate = (totalCollected / totalRevenue) * 100;
            sb.append(String.format("Collection Rate        : %.1f%%\n", collectionRate));
        }

        sb.append("==========================================\n");
        return sb.toString();
    }


    //4 Reads all the data
    /*public static String buildBookCategoriesReport(TreeMap<String, Book> bookMap){
        int totalBooks = bookMap.size();

        TreeMap<String, Integer> categoryCounts = new TreeMap<>();
        ListInterface<Book> allBooks = bookMap.values();
        // System.out.println("DEBUG: List size from values " +   allBooks.size());
        if(allBooks.isEmpty()){
            return "NO BOOKS";
        }
        for(int i = 0 ; i < allBooks.size(); i++){
            Book currentBook = allBooks.get(i);
            ListInterface<String> bookCategories = currentBook.getCategories();

            if(bookCategories!= null){
                for(int j = 0 ; j<bookCategories.size(); j++){
                    String catName = bookCategories.get(j).trim();

                    if(categoryCounts.containsKey(catName)){
                        categoryCounts.put(catName, categoryCounts.get(catName) + 1);
                    }else{
                        categoryCounts.put(catName, 1);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("        BOOK CATEGORY DISTRIBUTION        \n");
        sb.append("==========================================\n");
        sb.append("Total Books Analyzed: ").append(totalBooks).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-25s %-10s\n", "Category", "Quantity"));
        sb.append("------------------------------------------\n");

        ListInterface<String> uniqueCategories = categoryCounts.keys();
        for (int k = 0; k < uniqueCategories.size(); k++) {
            String name = uniqueCategories.get(k);
            int count = categoryCounts.get(name);
            sb.append(String.format("%-25s %-10d\n", name, count));
        }

        sb.append("==========================================\n");
        return sb.toString();
    }*/

    //4 Reads top 5 set by limit on controller
    public static String buildCategoryExtremeReport(TreeMap<String, Book> bookMap, int limit,
                                                    int startYear, int startMonth,
                                                    int endYear, int endMonth) {

        TreeMap<String, Integer> categoryCounts = new TreeMap<>();
        ListInterface<Book> allBooks = bookMap.values();

        int startValue = (startYear * 12) + startMonth;
        int endValue = (endYear * 12) + endMonth;

        for (int i = 0; i < allBooks.size(); i++) {
            Book currentBook = allBooks.get(i);

            int bookYear = currentBook.getPublishYear();
            int bookMonth = currentBook.getPublishMonth();
            int bookValue = (bookYear * 12) + bookMonth;

            if (bookValue >= startValue && bookValue <= endValue) {
                ListInterface<String> cats = currentBook.getCategories();
                if (cats != null) {
                    for (int j = 0; j < cats.size(); j++) {
                        String catName = cats.get(j).trim();
                        if (categoryCounts.containsKey(catName)) {
                            int currentCount = categoryCounts.get(catName);
                            categoryCounts.put(catName, currentCount + 1);
                        } else {
                            categoryCounts.put(catName, 1);
                        }
                    }
                }
            }
        }

        int uniqueCount = categoryCounts.size();
        if (uniqueCount == 0) return "No category data found for the selected period.";

        CategoryEntry[] sortedArray = new CategoryEntry[uniqueCount];
        ListInterface<String> keys = categoryCounts.keys();
        for (int i = 0; i < uniqueCount; i++) {
            String key = keys.get(i);
            sortedArray[i] = new CategoryEntry(key, categoryCounts.get(key));
        }

        for (int i = 0; i < uniqueCount - 1; i++) {
            for (int j = 0; j < uniqueCount - i - 1; j++) {
                if (sortedArray[j].count < sortedArray[j + 1].count) {
                    CategoryEntry temp = sortedArray[j];
                    sortedArray[j] = sortedArray[j + 1];
                    sortedArray[j + 1] = temp;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       CATEGORY POPULARITY ANALYSIS       \n");
        sb.append(String.format("   PERIOD: %02d/%d to %02d/%d\n", startMonth, startYear, endMonth, endYear));
        sb.append("==========================================\n\n");

        sb.append("--- HIGHEST QUANTITY (TOP ").append(limit).append(") ---\n");
        int topLimit = Math.min(limit, uniqueCount);
        for (int i = 0; i < topLimit; i++) {
            sb.append(String.format("%-25s : %d books\n", sortedArray[i].name, sortedArray[i].count));
        }

        sb.append("\n--- LOWEST QUANTITY (BOTTOM ").append(limit).append(") ---\n");
        int shown = 0;
        for (int i = uniqueCount - 1; i >= 0 && shown < limit; i--) {
            sb.append(String.format("%-25s : %d books\n", sortedArray[i].name, sortedArray[i].count));
            shown++;
        }

        sb.append("==========================================\n");
        return sb.toString();
    }

    private static class CategoryEntry {
        String name;
        int count;
        CategoryEntry(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    //12
    public static String buildSingleUserBorrowReport(String targetId, TreeMap<String, User> userMap,
                                                     TreeMap<String, BookBorrowRecord> borrowMap,
                                                     LocalDate fromDate, LocalDate toDate) {
        User user = userMap.get(targetId);

        if (user == null) {
            return "Error: User ID [" + targetId + "] not found.";
        }

        // Default to "Now" if To Date is blank
        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        StringBuilder sb = new StringBuilder();
        sb.append("==============================================================\n");
        sb.append("               PERSONAL BORROWING STATEMENT                   \n");
        sb.append("==============================================================\n");
        sb.append("User   : ").append(user.getName()).append(" (").append(user.getUserId()).append(")\n");
        sb.append("Period : ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("--------------------------------------------------------------\n");

        // Header for the table
        sb.append(String.format("%-10s %-10s %-12s %-12s %-10s\n",
                "ID", "Book ID", "Borrow Date", "Return Date", "Status"));
        sb.append("--------------------------------------------------------------\n");

        ListInterface<BookBorrowRecord> allBorrows = borrowMap.values();
        boolean found = false;
        int recordCount = 0;

        for (int i = 0; i < allBorrows.size(); i++) {
            BookBorrowRecord record = allBorrows.get(i);

            // 1. Check User ID Match
            if (record.getUserId().equalsIgnoreCase(targetId)) {

                // 2. Check Date Range
                LocalDate borrowDate = record.getBorrowDate();
                if (borrowDate != null) {
                    boolean isAfterFrom = (fromDate == null) || !borrowDate.isBefore(fromDate);
                    boolean isBeforeTo = !borrowDate.isAfter(actualToDate);

                    if (isAfterFrom && isBeforeTo) {
                        String status = record.alreadyReturnToString(record.getAlreadyReturned());
                        String returnDate = record.dateToString(record.getReturnDate());

                        sb.append(String.format("%-10s %-10s %-12s %-12s %-10s\n",
                                record.getBorrowId(),
                                record.getBookId(),
                                record.getBorrowDate(),
                                returnDate,
                                status));
                        found = true;
                        recordCount++;
                    }
                }
            }
        }

        if (!found) {
            sb.append("No borrowing records found for this user within the selected period.\n");
        } else {
            sb.append("--------------------------------------------------------------\n");
            sb.append("Total Records Found: ").append(recordCount).append("\n");
        }

        sb.append("==============================================================\n");
        return sb.toString();
    }

    //13
    public static String buildUserFineReport(String targetId, TreeMap<String, User> userMap,
                                             TreeMap<String, BorrowFineRecord> fineMap,
                                             LocalDate fromDate, LocalDate toDate) {
        User user = userMap.get(targetId);
        if (user == null) {
            return "Error: User ID [" + targetId + "] not found.";
        }

        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("             USER FINE & PENALTY STATEMENT          \n");
        sb.append("============================================================\n");
        sb.append("User   : ").append(user.getName()).append(" (").append(user.getUserId()).append(")\n");
        sb.append("Period : ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("------------------------------------------------------------\n");

        sb.append(String.format("%-15s %-12s %-12s %-12s\n", "Fine ID", "Amount", "Status", "Issued Date"));
        sb.append("------------------------------------------------------------\n");

        ListInterface<BorrowFineRecord> allFines = fineMap.values();
        double totalUnpaidInPeriod = 0.0;
        boolean foundFines = false;

        for (int i = 0; i < allFines.size(); i++) {
            BorrowFineRecord fineRecord = allFines.get(i);

            if (fineRecord.getUserId().equalsIgnoreCase(targetId)) {

                LocalDate issuedDate = fineRecord.getFine().getFineDate();
                if (issuedDate != null) {
                    boolean isAfterFrom = (fromDate == null) || !issuedDate.isBefore(fromDate);
                    boolean isBeforeTo = !issuedDate.isAfter(actualToDate);

                    if (isAfterFrom && isBeforeTo) {
                        String status = fineRecord.finePayedToString(fineRecord.getFinePayed());
                        double amt = fineRecord.getFine().getFineAmount();

                        sb.append(String.format("%-15s RM%-10.2f %-12s %-12s\n",
                                fineRecord.getBorrowId(),
                                amt,
                                status,
                                issuedDate.toString()));

                        if (!fineRecord.getFinePayed()) {
                            totalUnpaidInPeriod += amt;
                        }
                        foundFines = true;
                    }
                }
            }
        }

        if (!foundFines) {
            sb.append("No fines found for this user within the selected period.\n");
        } else {
            sb.append("------------------------------------------------------------\n");
            sb.append(String.format("TOTAL PENDING FINES (IN PERIOD): RM%.2f\n", totalUnpaidInPeriod));
        }

        sb.append("============================================================\n");
        return sb.toString();
    }

    //14
    public static String buildUserInterestReport(String targetId, TreeMap<String, User> userMap,
                                                 TreeMap<String, BookBorrowRecord> borrowMap,
                                                 TreeMap<String, Book> bookMap,
                                                 LocalDate fromDate, LocalDate toDate) {
        User user = userMap.get(targetId);
        if (user == null) return "User not found.";

        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        TreeMap<String, Integer> userInterests = new TreeMap<>();
        ListInterface<BookBorrowRecord> allBorrows = borrowMap.values();
        int totalCategoryHits = 0;

        for (int i = 0; i < allBorrows.size(); i++) {
            BookBorrowRecord record = allBorrows.get(i);

            // 1. Filter by User ID
            if (record.getUserId().equalsIgnoreCase(targetId)) {

                // 2. Filter by Borrow Date
                LocalDate borrowDate = record.getBorrowDate();
                if (borrowDate != null) {
                    boolean isAfterFrom = (fromDate == null) || !borrowDate.isBefore(fromDate);
                    boolean isBeforeTo = !borrowDate.isAfter(actualToDate);

                    if (isAfterFrom && isBeforeTo) {
                        // 3. Link to Book Map to get Categories
                        Book book = bookMap.get(record.getBookId());
                        if (book != null) {
                            ListInterface<String> cats = book.getCategories();
                            for (int j = 0; j < cats.size(); j++) {
                                String cat = cats.get(j).trim();
                                // Manual tallying (for custom TreeMap)
                                if (userInterests.containsKey(cat)) {
                                    userInterests.put(cat, userInterests.get(cat) + 1);
                                } else {
                                    userInterests.put(cat, 1);
                                }
                                totalCategoryHits++;
                            }
                        }
                    }
                }
            }
        }

        // --- Sorting Logic (ArrayList + Friend's Sort) ---
        ArrayList<CategoryEntry> sortedInterests = new ArrayList<>();
        ListInterface<String> keys = userInterests.keys();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            sortedInterests.add(new CategoryEntry(k, userInterests.get(k)));
        }
        sortedInterests.sort((a, b) -> b.count - a.count);

        // --- Report Building ---
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("           USER READING INTEREST PROFILE            \n");
        sb.append("====================================================\n");
        sb.append("Member Name : ").append(user.getName()).append("\n");
        sb.append("Period      : ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("----------------------------------------------------\n");

        if (sortedInterests.isEmpty()) {
            sb.append("Result: No borrowing history found in this period.\n");
        } else {
            CategoryEntry top = sortedInterests.get(0);
            double topPercent = ((double) top.count / totalCategoryHits) * 100;

            sb.append(String.format("PRIMARY INTEREST   : %s (%.1f%% of period)\n", top.name, topPercent));
            sb.append("----------------------------------------------------\n");
            sb.append(String.format("%-25s %-10s %-10s\n", "Genre", "Count", "Weight"));
            sb.append("----------------------------------------------------\n");

            int displayLimit = Math.min(5, sortedInterests.size());
            for (int i = 0; i < displayLimit; i++) {
                CategoryEntry e = sortedInterests.get(i);
                double percent = ((double) e.count / totalCategoryHits) * 100;
                sb.append(String.format("%-25s %-10d %-10s\n", e.name, e.count, String.format("%.1f%%", percent)));
            }
        }

        sb.append("====================================================\n");
        return sb.toString();
    }

    //23
    public static String buildOperationsHealthReport(TreeMap<String, BookBorrowRecord> borrowMap,
                                                     TreeMap<String, BorrowFineRecord> fineMap,
                                                     LocalDate fromDate, LocalDate toDate) {

        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        // --- PART 1: Borrowing Stats (Filtered by Borrow Date) ---
        ListInterface<BookBorrowRecord> allBorrows = borrowMap.values();
        int totalBorrows = 0;
        int lostBooks = 0;

        for (int i = 0; i < allBorrows.size(); i++) {
            BookBorrowRecord b = allBorrows.get(i);
            LocalDate bDate = b.getBorrowDate(); // Assuming Entity returns LocalDate

            if (bDate != null) {
                if (((fromDate == null) || !bDate.isBefore(fromDate)) && !bDate.isAfter(actualToDate)) {
                    totalBorrows++;
                    if (b.getIsLostBook()) lostBooks++;
                }
            }
        }

        // --- PART 2: Fine Stats (Filtered by Fine Issued Date) ---
        ListInterface<BorrowFineRecord> allFines = fineMap.values();
        int totalFines = 0;
        double totalOwed = 0.0;
        double totalPaid = 0.0;

        for (int i = 0; i < allFines.size(); i++) {
            BorrowFineRecord f = allFines.get(i);
            LocalDate fDate = f.getFine().getFineDate(); // Using Issued Date

            if (fDate != null) {
                if (((fromDate == null) || !fDate.isBefore(fromDate)) && !fDate.isAfter(actualToDate)) {
                    totalFines++;
                    double amount = f.getFine().getFineAmount();
                    if (f.getFinePayed()) totalPaid += amount;
                    else totalOwed += amount;
                }
            }
        }

        // --- PART 3: Build the Combined Report ---
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("          LIBRARY OPERATIONS & HEALTH REPORT        \n");
        sb.append("====================================================\n");
        sb.append("Period: ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("----------------------------------------------------\n");

        sb.append(String.format("%-25s : %d\n", "Total Borrowing Events", totalBorrows));
        sb.append(String.format("%-25s : %d\n", "Total Fine Incidents", totalFines));

        if (totalBorrows > 0) {
            double penaltyRate = ((double) totalFines / totalBorrows) * 100;
            sb.append(String.format("%-25s : %.1f%%\n", "User Penalty Rate", penaltyRate));
        }

        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Collected Fines    : RM%.2f\n", totalPaid));
        sb.append(String.format("Outstanding Fines : RM%.2f\n", totalOwed));

        sb.append("----------------------------------------------------\n");
        if (lostBooks > 0) {
            sb.append(lostBooks).append(" books were marked as LOST in this period.\n");
        } else {
            sb.append("No books marked as lost in this period.\n");
        }

        sb.append("====================================================\n");
        return sb.toString();
    }

    //24
    public static String buildPopularCategoryReport(TreeMap<String, BookBorrowRecord> borrowMap,
                                                    TreeMap<String, Book> bookMap,
                                                    LocalDate fromDate, LocalDate toDate) {

        TreeMap<String, Integer> borrowCounts = new TreeMap<>();
        ListInterface<BookBorrowRecord> allBorrows = borrowMap.values();
        int totalBorrowsInPeriod = 0;

        LocalDate actualToDate = (toDate == null) ? LocalDate.now() : toDate;

        for (int i = 0; i < allBorrows.size(); i++) {
            BookBorrowRecord record = allBorrows.get(i);

            // 1. Filter by Borrow Date
            LocalDate bDate = record.getBorrowDate();
            if (bDate != null) {
                boolean isAfterFrom = (fromDate == null) || !bDate.isBefore(fromDate);
                boolean isBeforeTo = !bDate.isAfter(actualToDate);

                if (isAfterFrom && isBeforeTo) {
                    // 2. Link to Book Map to get Categories
                    Book book = bookMap.get(record.getBookId());
                    if (book != null) {
                        ListInterface<String> cats = book.getCategories();
                        if (cats != null) {
                            for (int j = 0; j < cats.size(); j++) {
                                String catName = cats.get(j).trim();
                                // Manual tally for custom TreeMap
                                if (borrowCounts.containsKey(catName)) {
                                    borrowCounts.put(catName, borrowCounts.get(catName) + 1);
                                } else {
                                    borrowCounts.put(catName, 1);
                                }
                            }
                        }
                        totalBorrowsInPeriod++;
                    }
                }
            }
        }

        // --- Sorting Logic ---
        ArrayList<CategoryEntry> sortedList = new ArrayList<>();
        ListInterface<String> keys = borrowCounts.keys();
        for (int k = 0; k < keys.size(); k++) {
            String name = keys.get(k);
            sortedList.add(new CategoryEntry(name, borrowCounts.get(name)));
        }
        sortedList.sort((a, b) -> b.count - a.count);

        // --- Report Building ---
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("           MOST BORROWED BOOK CATEGORIES            \n");
        sb.append("====================================================\n");
        sb.append("Period: ").append(fromDate == null ? "Beginning" : fromDate)
                .append(" to ").append(actualToDate).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("%-30s %-15s\n", "Category", "Borrow Count"));
        sb.append("----------------------------------------------------\n");

        for (int idx = 0; idx < sortedList.size(); idx++) {
            CategoryEntry entry = sortedList.get(idx);
            sb.append(String.format("%-30s %-15d\n", entry.name, entry.count));
        }

        sb.append("----------------------------------------------------\n");
        sb.append("Total Borrows in Period: ").append(totalBorrowsInPeriod).append("\n");
        sb.append("====================================================\n");

        return sb.toString();
    }
}