package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;
import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.BookInfoController;
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
    static Pagination<Book> pagination = new Pagination<>();

    public static String inputBookID(TreeMap<String, Book> bookList) {
        boolean isValid = false;
        String id = "";
        do {
            System.out.print("Enter Book ID*      >> ");
            id = scanner.nextLine();
            if (!bookList.containsKey(id)) {
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

    public static ListInterface<String> inputBookCategory(BookInfoController bookManager) {
        return inputBookCategory(bookManager, new ArrayList<>());
    }

    public static ListInterface<String> inputBookCategory(BookInfoController bookManager, ListInterface<String> categoriesExcluded) {
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
        pagination.setDisplayList(bController.getbookList().values());
        do {
            UIGenerator.clearScreen();
            bookListPage();
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Add New Book",
                    "[2] Edit Book Information",
                    "[3] Edit Book in Stock",
                    "[4] Delete Book",
                    "[5] View Book Details",
                    "[6] Sort Books",
                    "[7] Search Books",
                    " ",
                    "[8] <- Previous Page          [9] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { bController.insertBook(); return false; },
                        () -> { bController.editBook(); return false; },
                        () -> { bController.editBookInStock(); return false; },
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

    public static void bookListPage() {
        UIGenerator.titleBuilder("Book List");
        System.out.println(pagination.getSearchingWord());
        ListInterface<Book> displayList = pagination.getDisplayList();

        if (displayList == null || displayList.isEmpty()) {
            System.out.println("No books found in the library.");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = pagination.getStartIndex();
        int endIndex = pagination.getEndIndex();

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

    public static Pagination<Book> getPagination() {
        return pagination;
    }
}
