package com.mycompany.library_system.boundary;

import java.util.Scanner;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.BookManager;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

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
        return UserPrompt.inputString("Enter Book Title*   >> ", 100, false);
    }

    public static String inputBookAuthor() {
        return UserPrompt.inputString("Enter Book Author   >> ", 100, true);
    }

    public static String inputBookDescription() {
        return UserPrompt.inputString("Enter Book Description >> ", 255, true);
    }

    public static ListInterface<String> inputBookCategory(BookManager bookManager) {
        return inputBookCategory(bookManager, new ArrayList<>());
    }

    public static ListInterface<String> inputBookCategory(BookManager bookManager, ListInterface<String> categoriesExcluded) {
        return UserPrompt.inputBookCategory(bookManager, categoriesExcluded);
    }

    public static String inputBookPublisher() {
        return UserPrompt.inputString("Enter Book Publisher >> ", 100, true);
    }

    public static double inputBookPrice() {
        return UserPrompt.inputDouble("Enter Book Price ($) >> ", 0, Double.MAX_VALUE);
    }

    public static int[] inputBookPublishDate() {
        return UserPrompt.inputDate(new String[]{
            "Enter Publish Year (YYYY)  >> ",
            "Enter Publish Month (1-12) >> ",
            ""
        }, true, true, false);
    }

    public static void menu(BookManager bookManager) {
        boolean isExit = false;
        do {
            UIGenerator.clearScreen();
            bookListPage(bookManager);
            isExit = UIGenerator.optionMenu(
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

        UIGenerator.titleBuilder("Book List");
        System.out.printf("%-8s %-40s %-30s %-30s %-26s %-6s\n", "Book ID ", "Book Title", "Category", "Author", "Publisher", "Price ($)");
        System.out.printf("%-8s %-40s %-30s %-30s %-26s %-6s\n", "--------", "----------", "--------", "------", "---------", "--------");

        for (int i = startIndex; i < endIndex; i++) {
                Book b = allBooks.get(i);
                String title = DataProcessor.truncate(b.getTitle(), 38);
                String category = DataProcessor.truncate(b.categoriesToString(), 28);
                String author = DataProcessor.truncate(b.getAuthor(),28);
                String publisher = DataProcessor.truncate(b.getPublisher(), 24);
                System.out.printf("%-8s %-40s %-30s %-30s %-26s %6.2f\n", b.getId(), title, category, author, publisher, b.getPrice());
        }

        System.out.println("-".repeat(160));
        System.out.printf("Page: %d/%d\n\n", currentPage, totalPages);
    }
}
