package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BookUI;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;

/**
 *
 * @author wh
 */
public class BookManager {
    private TreeMap<String, Book> bookList;
    public static final String BOOK_FILE_PATH = "database/book.csv";

    public BookManager() {
        this.bookList = BookDAO.readFile(BOOK_FILE_PATH);
    }

    public TreeMap<String, Book> getBookList() {
        return bookList;
    }

    public ListInterface<String> getAllUniqueCategories(ListInterface<String> categoriesExcluded) {
        ListInterface<String> uniqueCats = new ArrayList<>();
        
        // Loop all books in the manager
        for (Book b : bookList.values()) {
            for (String cat : b.getCategories()) {
                if (!categoriesExcluded.contains(cat)) {
                    uniqueCats.add(cat);
                }
            }
        }
        return uniqueCats.unique();
    }

    public ListInterface<String> getAllUniqueCategories() {
        return getAllUniqueCategories(new ArrayList<>());
    }

    public Book insertBook() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Add New Book");
        String id = IdGenerator.generateId(bookList, "BK", 6);
        String title = BookUI.inputBookTitle();
        String author = BookUI.inputBookAuthor();
        String description = BookUI.inputBookDescription();
        ListInterface<String> category = BookUI.inputBookCategory(this);
        String publisher = BookUI.inputBookPublisher();
        double price = BookUI.inputBookPrice();
        int[] publishYearMonth = BookUI.inputBookPublishDate();

        Book newBook = new Book(id, title, author, description, category, publisher, price, publishYearMonth[1], publishYearMonth[2]);
        
        Book book = bookList.put(id, newBook);
        System.out.println("Added book with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return book;
    }

    public Book editBook() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Edit Existing Book");
        String id = BookUI.inputBookID(bookList);

        System.out.println("\nEditing Book with ID: " + id);
        System.out.println(bookList.get(id).toString());

        Book oldBook = bookList.get(id);
        boolean isExit = false;
        do {
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Book Title",
                    "[2] Book Author",
                    "[3] Book Description",
                    "[4] Book Category",
                    "[5] Book Publisher",
                    "[6] Book Price",
                    "[7] Book Publish Date",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Runnable[]{
                    () -> bookList.get(id).setTitle(BookUI.inputBookTitle()),
                    () -> bookList.get(id).setAuthor(BookUI.inputBookAuthor()),
                    () -> bookList.get(id).setDescription(BookUI.inputBookDescription()),
                    () -> bookList.get(id).setCategories(BookUI.inputBookCategory(this, bookList.get(id).getCategories())),
                    () -> bookList.get(id).setPublisher(BookUI.inputBookPublisher()),
                    () -> bookList.get(id).setPrice(BookUI.inputBookPrice()),
                    () -> {
                        int[] publishYearMonth = BookUI.inputBookPublishDate();
                        bookList.get(id).setPublishDate(publishYearMonth[2], publishYearMonth[1]);
                    },
                    () -> {}  // Back to Main Menu
                }
            );
        }while (!isExit);

        System.out.println("Edited book with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return oldBook;
    }

    public Book deleteBook() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete Existing Book");
        String id = BookUI.inputBookID(bookList);
        Book book = bookList.remove(id);
        if(book != null) {
            System.out.println("Book with ID " + id + " has been deleted.");
        } else {
            System.out.println("Book with ID " + id + " not found.");
        }
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return book;
    }

    public void viewBookDetails() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("View Book Details");
        String id = BookUI.inputBookID(bookList);
        Book book = bookList.get(id);
        System.out.println(book.toString());
        UIGenerator.pressEnterToContinue();
    }

    public void saveToFile() {
        BookDAO.writeFile(BOOK_FILE_PATH, bookList);
    }
}
