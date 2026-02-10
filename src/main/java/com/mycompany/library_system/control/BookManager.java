package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BookUI;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.Utility;

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

    public Book insertBook() {
        Utility.titleBuilder("Insert New Book");
        String id = BookUI.inputBookID();
        String title = BookUI.inputBookTitle();
        String author = BookUI.inputBookAuthor();
        String description = BookUI.inputBookDescription();
        ListInterface<String> category = BookUI.inputBookCategory();
        String publisher = BookUI.inputBookPublisher();
        double price = BookUI.inputBookPrice();
        int[] publishYearMonth = BookUI.inputBookPublishDate();

        Book newBook = new Book(id, title, author, description, category, publisher, price, publishYearMonth[0], publishYearMonth[1]);

        if (bookList.containsKey(id)) {
            System.out.println("Book ID already exists. Updating existing book.");
        } else {
            System.out.println("Inserting a new book.");
        }
        
        Book book = bookList.put(id, newBook);
        return book;
    }

    public Book deleteBook() {
        Utility.titleBuilder("Delete Book");
        String id = BookUI.inputBookID();
        Book book = bookList.remove(id);
        if(book != null) {
            System.out.println("Book with ID " + id + " has been deleted.");
        } else {
            System.out.println("Book with ID " + id + " not found.");
        }
        return book;
    }
}
