package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.BookInfoController;
import com.mycompany.library_system.control.BookReportController;
import com.mycompany.library_system.control.StockOrderController;
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
            System.out.print("Enter Book ID* (BK000000) (0 to exit) >> ");
            id = scanner.nextLine();
            if (id.equals("0")) return "-1";
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

    @SuppressWarnings("unchecked")
    public static void menu() {
        BookInfoController bController = new BookInfoController();
        StockOrderController rController = new StockOrderController(bController.getbookTree(), bController.getbookTree("AvailableQuantity"), bController.getbookTree("TotalQuantity"));
        boolean isExit = false;
        pagination.setDisplayTree(bController.getbookTree());
        do {
            UIGenerator.clearScreen();
            bookListPage("admin");
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Manage Books Information",
                    "[2] Manage Books in Stock",
                    "[3] View Book Details",
                    "[4] Sort Books",
                    "[5] Search Books",
                    "[6] Generate Reports",
                    " ",
                    "[7] <- Previous Page          [8] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { crudMenu(bController); return false; },
                        () -> { StockOrderUI.menu(rController); return false; },
                        () -> { bController.viewBookDetails(); return false; },
                        () -> { bController.sortBooks(); return false; },
                        () -> { bController.searchBooks(); return false; },
                        () -> { BookReportUI.reportMenu(bController); return false; },
                        () -> { pagination.previousPage(); return false; },
                        () -> { pagination.nextPage(); return false; },
                        () -> true
                }
            );
        }while (!isExit);
    }

    @SuppressWarnings("unchecked")
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
                    "[3] Delete Book",
                    "[4] View Book Details",
                    "[5] Sort Books",
                    "[6] Search Books",
                    " ",
                    "[7] <- Previous Page          [8] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { bController.insertBook(); return false; },
                        () -> { bController.editBook(); return false; },
                        () -> { bController.deleteBook(); return false; },
                        () -> { bController.viewBookDetails(); return false; },
                        () -> { bController.sortBooks(); return false; },
                        () -> { bController.searchBooks(); return false; },
                        () -> { pagination.previousPage(); return false; },
                        () -> { pagination.nextPage(); return false; },
                        () -> true
                }
            );
        }while (!isExit);
    }

    public static void bookListPage(String role) {
        UIGenerator.titleBuilder("Book List");
        System.out.println(pagination.getSearchingWord());
        ListInterface<Book> displayList = pagination.getDisplayTree().values();

        if (displayList == null || displayList.isEmpty()) {
            System.out.println("No books found in the library.");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = pagination.getStartIndex();
        int endIndex = pagination.getEndIndex();

        StringBuilder sBuilder = new StringBuilder();
        if (role.equals("member")) {

            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-40s-+-%-30s-+-%-25s-+-%-25s-+-%-13s-+", "-".repeat(8), "-".repeat(40), "-".repeat(30), "-".repeat(25), "-".repeat(25), "-".repeat(13)));
            UIGenerator.appendCenteredLine(sBuilder, String.format("| %-8s | %-40s | %-30s | %-25s | %-25s | %-13s |", "Book ID ", "Book Title", "Category", "Author", "Publisher", "Available Qty"));
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-40s-+-%-30s-+-%-25s-+-%-25s-+-%-13s-+", "-".repeat(8), "-".repeat(40), "-".repeat(30), "-".repeat(25), "-".repeat(25), "-".repeat(13)));

            for (int i = startIndex; i <= endIndex; i++) {
                int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

                Book b = displayList.get(actualIndex);
                String title = DataProcessor.truncate(b.getTitle(), 40);
                String category = DataProcessor.truncate(DataProcessor.getKeysAsString(b.getCategories()), 30);
                String author = DataProcessor.truncate(b.getAuthor(), 25);
                String publisher = DataProcessor.truncate(b.getPublisher(), 25);
                UIGenerator.appendCenteredLine(sBuilder, String.format("| %-8s | %-40s | %-30s | %-25s | %-25s | %13d |", b.getId(), title, category, author, publisher, b.getQtyAvailable()));
            }
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-40s-+-%-30s-+-%-25s-+-%-25s-+-%-13s-+", "-".repeat(8), "-".repeat(40), "-".repeat(30), "-".repeat(25), "-".repeat(25), "-".repeat(13)));
        } else if (role.equals("admin")) {
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-33s-+-%-25s-+-%-25s-+-%-25s-+-%-13s-+-%-9s-+", "-".repeat(8), "-".repeat(33), "-".repeat(25), "-".repeat(25), "-".repeat(25), "-".repeat(13), "-".repeat(9)));
            UIGenerator.appendCenteredLine(sBuilder, String.format("| %-8s | %-33s | %-25s | %-25s | %-25s | %-13s | %-9s |", "Book ID ", "Book Title", "Category", "Author", "Publisher", "Available Qty", "Total Qty"));
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-33s-+-%-25s-+-%-25s-+-%-25s-+-%-13s-+-%-9s-+", "-".repeat(8), "-".repeat(33), "-".repeat(25), "-".repeat(25), "-".repeat(25), "-".repeat(13), "-".repeat(9)));

            for (int i = startIndex; i <= endIndex; i++) {
                int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

                Book b = displayList.get(actualIndex);
                String title = DataProcessor.truncate(b.getTitle(), 33);
                String category = DataProcessor.truncate(DataProcessor.getKeysAsString(b.getCategories()), 25);
                String author = DataProcessor.truncate(b.getAuthor(), 25);
                String publisher = DataProcessor.truncate(b.getPublisher(), 25);
                UIGenerator.appendCenteredLine(sBuilder, String.format("| %-8s | %-33s | %-25s | %-25s | %-25s | %13d | %9d |", b.getId(), title, category, author, publisher, b.getQtyAvailable(), b.getQtyTotal()));
            }
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-33s-+-%-25s-+-%-25s-+-%-25s-+-%-13s-+-%-9s-+", "-".repeat(8), "-".repeat(33), "-".repeat(25), "-".repeat(25), "-".repeat(25), "-".repeat(13), "-".repeat(9)));
        } else {
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-48s-+-%-40s-+-%-10s-+-%-14s-+-%-21s-+", "-".repeat(8), "-".repeat(48), "-".repeat(40), "-".repeat(10), "-".repeat(14), "-".repeat(21)));
            UIGenerator.appendCenteredLine(sBuilder, String.format("| %-8s | %-48s | %-40s | %-10s | %-14s | %-21s |", "Book ID", "Book Title", "Publisher", "Price ($)", "Stock Left (%)", "Qty Available / Total"));
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-48s-+-%-40s-+-%-10s-+-%-14s-+-%-21s-+", "-".repeat(8), "-".repeat(48), "-".repeat(40), "-".repeat(10), "-".repeat(14), "-".repeat(21)));

            for (int i = startIndex; i <= endIndex; i++) {
                int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

                Book b = displayList.get(actualIndex);
                String title = DataProcessor.truncate(b.getTitle(), 48);
                String publisher = DataProcessor.truncate(b.getPublisher(), 40);
                String stockPercentage = b.getQtyTotal() > 0 ? String.format("%.2f", ((double) b.getQtyAvailable() / b.getQtyTotal()) * 100) : "0.00";

                UIGenerator.appendCenteredLine(sBuilder, String.format("| %-8s | %-48s | %-40s | %-10s | %-14s | %13s / %-5s |", b.getId(), title, publisher, b.getPrice(), stockPercentage, String.valueOf(b.getQtyAvailable()), String.valueOf(b.getQtyTotal())));
            }
            UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-8s-+-%-48s-+-%-40s-+-%-10s-+-%-14s-+-%-21s-+", "-".repeat(8), "-".repeat(48), "-".repeat(40), "-".repeat(10), "-".repeat(14), "-".repeat(21)));
        }
        UIGenerator.appendCenteredLine(sBuilder, String.format("Page: %d/%d ------ Total Records: %d\n\n", pagination.getCurrentPage(), pagination.getTotalPages(), displayList.size()));
        System.out.print(sBuilder.toString());
    }

    public static Pagination<String, Book> getPagination() {
        return pagination;
    }
}
