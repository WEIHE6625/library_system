package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.BookInfoController;
import com.mycompany.library_system.control.BookReportController;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;
import com.mycompany.library_system.utility.Pagination;

/**
 *
 * @author weihe
 */
public class BookUI {
    
    static Scanner scanner = new Scanner(System.in);
    static Pagination<String, Book> pagination = new Pagination<>();

    public static String inputBookID(MapInterface<String, Book> bookTree) {
        boolean isValid = false;
        String id = "";
        do {
            System.out.print("Enter Book ID* (BK000000) >> ");
            id = scanner.nextLine();
            if (!bookTree.containsKey(id)) {
                System.out.println("Book with ID " + id + " does not exist. Please try again.");
            }else{
                isValid = true;
            }
        } while (!isValid);
        return id;
    }

    public static String inputBookTitle() {
        return UserPrompt.inputString("Enter Book Title*   >> ", 100, false);
    }

    public static String inputBookAuthor() {
        return UserPrompt.inputString("Enter Book Author   >> ", 100, true);
    }

    public static String inputBookDescription() {
        return UserPrompt.inputString("Enter Book Description >> ", 255, true);
    }

    public static MapInterface<String, Integer> inputBookCategory(BookInfoController bookManager) {
        return inputBookCategory(bookManager, new TreeMap<>());
    }

    public static MapInterface<String, Integer> inputBookCategory(BookInfoController bookManager, MapInterface<String, Integer> categoriesExcluded) {
        return UserPrompt.inputBookCategory(bookManager, categoriesExcluded);
    }

    public static String inputBookPublisher() {
        return UserPrompt.inputString("Enter Book Publisher  >> ", 100, true);
    }

    public static double inputBookPrice() {
        return UserPrompt.inputDouble("Enter Book Price ($)* >> ", 0, Double.MAX_VALUE);
    }

    public static int[] inputBookPublishDate() {
        return UserPrompt.inputDate(new String[]{
            "Enter Publish Year (YYYY)*  >> ",
            "Enter Publish Month (1-12)* >> ",
            ""
        }, true, true, false);
    }

    public static int inputQty() {
        return UserPrompt.inputInteger("Enter Total Quantity Added* >> ", 1, 999);
    }
    
    public static int inputReduceQty(int qtyAvailable) {
        System.out.println("\n Current Available Quantity : " + qtyAvailable);
        return UserPrompt.inputInteger("Enter Total Quantity Reduced* >> ", 1, qtyAvailable);
    }


    public static void menu() {
        BookInfoController bController = new BookInfoController();
        boolean isExit = false;
        pagination.setDisplayTree(bController.getbookTree());
        do {
            UIGenerator.clearScreen();
            bookListPage("admin");
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Manage Books",
                    "[2] View Book Details",
                    "[3] Sort Books",
                    "[4] Search Books",
                    "[5] Generate Reports",
                    " ",
                    "[6] <- Previous Page          [7] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { crudMenu(bController); return false; },
                        () -> { bController.viewBookDetails(); return false; },
                        () -> { bController.sortBooks(); return false; },
                        () -> { bController.searchBooks(); return false; },
                        () -> { reportMenu(bController); return false; },
                        () -> { pagination.previousPage(); return false; },
                        () -> { pagination.nextPage(); return false; },
                        () -> true
                }
            );
        }while (!isExit);
    }

    public static void reportMenu(BookInfoController bController) {
        boolean isExit = false;
        do {
            UIGenerator.clearScreen();
            System.out.println(UIGenerator.titleBuilder("Admin Dashboard --- Book Reports"));
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
        BookReportController reportController = new BookReportController(
            bController.getbookTree(), 
            bController.getbookTree("Category"),
            bController.getbookTree("AvailableQuantity")
        );
        UIGenerator.clearScreen();
        System.out.println(UIGenerator.titleBuilder("Book Inventory Reports"));
        String report = "";
        String fileName = "";
        switch (reportType) {
            case 1: report = reportController.generateAdminReport1(); fileName = "Low_Stock_Report"; break;
            // case 2: report = reportController.generateAdminReport2(); fileName = "Inventory_Cost_Report"; break;
        }

        System.out.println(report);

        System.out.println("\nWould you like to export this report to a text file? (Y/N)");
        String choice = UserPrompt.inputString("Enter your choice >> ", 1, false);

        if ("Y".equalsIgnoreCase(choice)) {
            reportController.exportToFile(report, fileName);
        } else {
            UIGenerator.pressEnterToContinue();
        }
    }

    public static void crudMenu(BookInfoController bController) {
        boolean isExit = false;
        pagination.setDisplayTree(bController.getbookTree());
        do {
            UIGenerator.clearScreen();
            bookListPage("admin");
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Add New Book",
                    "[2] Edit Book Information",
                    "[3] Edit Book in Stock",
                    "[4] Delete Book",
                    " ",
                    "[5] <- Previous Page          [6] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { bController.insertBook(); return false; },
                        () -> { bController.editBook(); return false; },
                        () -> { bController.editBookInStock(); return false; },
                        () -> { bController.deleteBook(); return false; },
                        () -> { pagination.previousPage(); return false; },
                        () -> { pagination.nextPage(); return false; },
                        () -> true
                }
            );
        }while (!isExit);
    }

    public static void bookListPage(String role) {
        System.out.println(UIGenerator.titleBuilder("Book List"));
        System.out.println(pagination.getSearchingWord());
        ListInterface<Book> displayList = pagination.getDisplayTree().values();

        if (displayList == null || displayList.isEmpty()) {
            System.out.println("No books found in the library.");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = pagination.getStartIndex();
        int endIndex = pagination.getEndIndex();

        if (role.equals("member")) {
            System.out.printf("%-8s %-45s %-30s %-30s %-29s %-13s\n", "Book ID ", "Book Title", "Category", "Author", "Publisher", "Available Qty");
            System.out.printf("%-8s %-45s %-30s %-30s %-29s %-13s\n", "--------", "----------", "--------", "------", "---------", "-------------");

            for (int i = startIndex; i <= endIndex; i++) {
                int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

                Book b = displayList.get(actualIndex);
                String title = DataProcessor.truncate(b.getTitle(), 43);
                String category = DataProcessor.truncate(b.getCategories().toString(), 28);
                String author = DataProcessor.truncate(b.getAuthor(), 28);
                String publisher = DataProcessor.truncate(b.getPublisher(), 27);
                System.out.printf("%-8s %-45s %-30s %-30s %-29s %13d\n", b.getId(), title, category, author, publisher, b.getQtyAvailable());
            }
        } else {
            System.out.printf("%-8s %-40s %-30s %-28s %-26s %-13s %-9s\n", "Book ID ", "Book Title", "Category", "Author", "Publisher", "Available Qty", "Total Qty");
            System.out.printf("%-8s %-40s %-30s %-28s %-26s %-13s %-9s\n", "--------", "----------", "--------", "------", "---------", "-------------", "---------");

            for (int i = startIndex; i <= endIndex; i++) {
                int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

                Book b = displayList.get(actualIndex);
                String title = DataProcessor.truncate(b.getTitle(), 38);
                String category = DataProcessor.truncate(b.getCategories().toString(), 28);
                String author = DataProcessor.truncate(b.getAuthor(), 26);
                String publisher = DataProcessor.truncate(b.getPublisher(), 24);
                System.out.printf("%-8s %-40s %-30s %-28s %-26s %13d %9d\n", b.getId(), title, category, author, publisher, b.getQtyAvailable(), b.getQtyTotal());
            }

            System.out.println("-".repeat(160));
            System.out.printf("Page: %d/%d ------ Total Records: %d\n\n", pagination.getCurrentPage(), pagination.getTotalPages(), displayList.size());
        }
    }

    public static Pagination<String, Book> getPagination() {
        return pagination;
    }
}
