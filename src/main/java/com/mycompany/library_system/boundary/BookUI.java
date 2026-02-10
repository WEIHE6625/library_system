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

    public static String inputBookID() {
        System.out.print("Enter Book ID*      >> ");
        String id = scanner.nextLine();
        return id;
    }

    public static String inputBookTitle() {
        System.out.print("Enter Book Title*   >> ");
        String title = scanner.nextLine();
        return title;
    }

    public static String inputBookAuthor() {
        System.out.print("Enter Book Author   >> ");
        String author = scanner.nextLine();
        return author;
    }

    public static String inputBookDescription() {
        System.out.print("Enter Description   >> ");
        String description = scanner.nextLine();
        return description;
    }

    public static ListInterface<String> inputBookCategory() {
        System.out.print("Enter Book Category >> ");
        String category = scanner.nextLine();
        ListInterface<String> categories = new ArrayList<>();
        categories.add(category);
        return categories;
    }

    public static String inputBookPublisher() {
        System.out.print("Enter Book Publisher >> ");
        String publisher = scanner.nextLine();
        return publisher;
    }

    public static double inputBookPrice() {
        System.out.print("Enter Book Price ($) >> ");
        double price = scanner.nextDouble();
        return price;
    }

    public static int[] inputBookPublishDate() {
        System.out.print("Enter Publish Month (1-12) >> ");
        int month = scanner.nextInt();
        System.out.print("Enter Publish Year  (XXXX) >> ");
        int year = scanner.nextInt();
        return new int[]{month, year};
    }

    public static void menu(BookManager bookManager) {
        boolean isExit = false;
        do {
            Utility.clearScreen();
            bookListPage(bookManager);
            isExit = Utility.optionMenu(
                new String[]{
                    "1. Add or Edit Book",
                    "2. Delete Book",
                    "3. Next Page",
                    "4. Previous Page",
                    "0. Back to Main Menu"
                },
                "Select an option >> ",
                new Runnable[]{
                    () -> bookManager.insertBook(),
                    () -> bookManager.deleteBook(),
                    () -> {currentPage++;},
                    () -> {currentPage = Math.max(1, currentPage - 1);},
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

        System.out.println("-".repeat(85));
        System.out.printf("Page: %d/%d\n", currentPage, totalPages);
    }
}
