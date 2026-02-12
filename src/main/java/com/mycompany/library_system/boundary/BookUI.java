package com.mycompany.library_system.boundary;

import java.util.Scanner;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.BookManager;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.Utility;

/**
 *
 * @author wh
 */
public class BookUI {
    
    static Scanner scanner = new Scanner(System.in);
    private static int currentPage = 1;
    private static int totalPages = 99;
    private static final int PAGE_SIZE = 30;

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
        return Utility.inputString("Enter Book Title*   >> ", 100, false);
    }

    public static String inputBookAuthor() {
        return Utility.inputString("Enter Book Author   >> ", 100, true);
    }

    public static String inputBookDescription() {
        return Utility.inputString("Enter Book Description >> ", 255, true);
    }

    public static ListInterface<String> inputBookCategory(BookManager bookManager) {
        return inputBookCategory(bookManager, new ArrayList<>());
    }

    public static ListInterface<String> inputBookCategory(BookManager bookManager, ListInterface<String> categoriesExcluded) {
        return Utility.inputBookCategory(bookManager, categoriesExcluded);
    }

    public static String inputBookPublisher() {
        return Utility.inputString("Enter Book Publisher >> ", 100, true);
    }

    public static double inputBookPrice() {
        return Utility.inputDouble("Enter Book Price ($) >> ", 0, Double.MAX_VALUE);
    }

    public static int[] inputBookPublishDate() {
        return Utility.inputDate(new String[]{
            "Enter Publish Year (YYYY)  >> ",
            "Enter Publish Month (1-12) >> ",
            ""
        }, true, true, false);
    }

    public static void menu(BookManager bookManager) {
        boolean isExit = false;
        do {
            Utility.clearScreen();
            bookListPage(bookManager);
            isExit = Utility.optionMenu(
                new String[]{
                    "[1] Add Book",
                    "[2] Edit Book",
                    "[3] Delete Book",
                    "[4] View Book Details",
                    " ",
                    "[5] <- Previous Page          [6] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Runnable[]{
                    () -> bookManager.insertBook(),
                    () -> bookManager.editBook(),
                    () -> bookManager.deleteBook(),
                    () -> bookManager.viewBookDetails(),
                    () -> {currentPage = Math.max(1, currentPage - 1);},
                    () -> {currentPage = Math.min(totalPages, currentPage + 1);},
                    () -> {}  // Back to Main Menu
                }
            );
        }while (!isExit);
        
    }

    public static void bookListPage(BookManager bookManager) {

        TreeMap<String, Book> bookList = bookManager.getBookList();
        ListInterface<Book> allBooks = bookList.values();
        if (allBooks.isEmpty()) {
            System.out.println("No books found in the library.");
            return;
        }

        int totalRecords = allBooks.size();
        totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalRecords);

        Utility.titleBuilder("Book List");
        System.out.printf("%-8s %-40s %-30s %-30s %-26s %-6s\n", "Book ID ", "Book Title", "Category", "Author", "Publisher", "Price ($)");
        System.out.printf("%-8s %-40s %-30s %-30s %-26s %-6s\n", "--------", "----------", "--------", "------", "---------", "--------");

        for (int i = startIndex; i < endIndex; i++) {
                Book b = allBooks.get(i);
                String title = Utility.truncate(b.getTitle(), 38);
                String category = Utility.truncate(b.categoriesToString(), 28);
                String author = Utility.truncate(b.getAuthor(),28);
                String publisher = Utility.truncate(b.getPublisher(), 24);
                System.out.printf("%-8s %-40s %-30s %-30s %-26s %6.2f\n", b.getId(), title, category, author, publisher, b.getPrice());
        }

        System.out.println("-".repeat(160));
        System.out.printf("Page: %d/%d\n\n", currentPage, totalPages);
    }
}
