package com.mycompany.library_system.control;

import java.time.YearMonth;

import java.util.function.Supplier;
import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BookUI;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

/**
 *
 * @author wh
 */
public class BookInfoController {
    private MapInterface<String, Book> bookTree;
    private MapInterface<String, Book> bookTitleTree;
    private MapInterface<String, Book> bookAuthorTree;
    private MapInterface<String, Book> bookPriceTree;
    private MapInterface<String, Book> bookDateTree;
    private MapInterface<String, Book> bookQtyATree;
    private MapInterface<String, Book> bookQtyTTree;
    private MapInterface<String, Book> bookCatTree;
    private MapInterface<String, Book> searchTree = new TreeMap<>();
    
    public static final String BOOK_FILE_PATH = "database/book.csv";

    public BookInfoController() {
        System.out.println("Initializing...");
        this.bookTree = BookDAO.readFile(BOOK_FILE_PATH);
        this.bookTitleTree = new TreeMap<>();
        this.bookAuthorTree = new TreeMap<>();
        this.bookPriceTree = new TreeMap<>();
        this.bookDateTree = new TreeMap<>();
        this.bookQtyATree = new TreeMap<>();
        this.bookQtyTTree = new TreeMap<>();
        this.bookCatTree = new TreeMap<>();

        // Loop through every book in the main bookTree
        System.out.println("Matching...");
        for (Book book : bookTree.values()) {
            // Add the book to each tree
            bookTitleTree.put(IdGenerator.getKeyId(book.getTitle(), book.getId()), book);
            bookAuthorTree.put(IdGenerator.getKeyId(book.getAuthor(), book.getId()), book);
            bookPriceTree.put(IdGenerator.getKeyId(book.getPrice(), book.getId()), book);
            bookDateTree.put(IdGenerator.getKeyId(book.getPublishDate(), book.getId()), book);
            bookQtyATree.put(IdGenerator.getKeyId(book.getQtyAvailable(), book.getId()), book);
            bookQtyTTree.put(IdGenerator.getKeyId(book.getQtyTotal(), book.getId()), book);

            for (MapInterface.Entry<String, Integer> category : book.getCategories()) {
                bookCatTree.put(IdGenerator.getKeyId(category.getKey(), book.getId()), book);
            }
        }
    }

    // Return the Tree that key sort by...
    public MapInterface<String, Book> getbookList() { 
        return bookTree;
    }

    public MapInterface<String, Integer> getAllUniqueCategories(MapInterface<String, Integer> categoriesExcluded) {
        MapInterface<String, Integer> uniqueCats = new TreeMap<>();
        
        // Loop all books in the manager
        for (MapInterface.Entry<String, Book> books : bookTree) {
            Book book = books.getValue();
            for (MapInterface.Entry<String, Integer> cat : book.getCategories()) {
                if (!categoriesExcluded.containsKey(cat.getKey())) {
                    uniqueCats.put(cat.getKey(), cat.getValue());
                }
            }
        }
        return uniqueCats;
    }

    public MapInterface<String, Integer> getAllUniqueCategories() {
        return getAllUniqueCategories(new TreeMap<>());
    }

    public Book insertBook() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Add New Book");
        String id = IdGenerator.generateId(bookTree, "BK", 6);
        String title = BookUI.inputBookTitle();
        String author = BookUI.inputBookAuthor();
        String description = BookUI.inputBookDescription();
        MapInterface<String, Integer> category = BookUI.inputBookCategory(this);
        String publisher = BookUI.inputBookPublisher();
        double price = BookUI.inputBookPrice();
        int[] publishYearMonth = BookUI.inputBookPublishDate();
        int qty = BookUI.inputQty();

        Book newBook = new Book(id, title, author, description, category, publisher, price, publishYearMonth[1], publishYearMonth[2], qty, qty);
        Book book = bookTree.put(id, newBook);
        
        // Add to secondary indexes
        bookTitleTree.put(IdGenerator.getKeyId(newBook.getTitle(), id), newBook);
        bookAuthorTree.put(IdGenerator.getKeyId(newBook.getAuthor(), id), newBook);
        bookPriceTree.put(IdGenerator.getKeyId(newBook.getPrice(), id), newBook);
        bookDateTree.put(IdGenerator.getKeyId(newBook.getPublishDate(), id), newBook);
        bookQtyATree.put(IdGenerator.getKeyId(newBook.getQtyAvailable(), id), newBook);
        bookQtyTTree.put(IdGenerator.getKeyId(newBook.getQtyTotal(), id), newBook);

        BookUI.getPagination().setDisplayTree(bookTree);
        BookUI.getPagination().setCurrentPage(1);

        System.out.println("Added book with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return book;
    }

    public Book editBook() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Edit Existing Book");
        String id = BookUI.inputBookID(bookTree);

        System.out.println("\nEditing Book with ID: " + id);
        Book book = bookTree.get(id);
        System.out.println(book.toString());
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
                new Supplier[] {
                        () -> {
                            String oldTitle = book.getTitle();
                            String newTitle = BookUI.inputBookTitle();
                            book.setTitle(newTitle);
                            bookTitleTree.remove(IdGenerator.getKeyId(oldTitle, id));
                            bookTitleTree.put(IdGenerator.getKeyId(newTitle, id), book);
                            return false;
                        },
                        () -> {
                            String oldAuthor = book.getAuthor();
                            String newAuthor = BookUI.inputBookAuthor();
                            book.setAuthor(newAuthor);
                            bookAuthorTree.remove(IdGenerator.getKeyId(oldAuthor, id));
                            bookAuthorTree.put(IdGenerator.getKeyId(newAuthor, id), book);
                            return false;
                        },
                        () -> { bookTree.get(id).setDescription(BookUI.inputBookDescription()); return false; },
                        () -> { bookTree.get(id).setCategories(BookUI.inputBookCategory(this, bookTree.get(id).getCategories())); return false; },
                        () -> { bookTree.get(id).setPublisher(BookUI.inputBookPublisher()); return false; },
                        () -> {
                            double oldPrice = book.getPrice();
                            double newPrice = BookUI.inputBookPrice();
                            book.setPrice(newPrice);
                            bookPriceTree.remove(IdGenerator.getKeyId(oldPrice, id));
                            bookPriceTree.put(IdGenerator.getKeyId(newPrice, id), book);
                            return false;
                        },
                        () -> {
                            YearMonth oldDate = book.getPublishDate();
                            int[] newDate = BookUI.inputBookPublishDate();
                            book.setPublishDate(newDate[2], newDate[1]);
                            bookDateTree.remove(IdGenerator.getKeyId(oldDate, id));
                            bookDateTree.put(IdGenerator.getKeyId(book.getPublishDate(), id), book);
                            return false;
                        },
                        () -> { return true; } // Back to Main Menu
                }
            );
        }while (!isExit);

        System.out.println("Edited book with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return book;
    }

    public Book editBookInStock() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Edit Book In Stock");
        String id = BookUI.inputBookID(bookTree);

        System.out.println("\nEditing Book with ID: " + id);
        Book book = bookTree.get(id);
        System.out.println(book.toString());
        boolean isExit = false;
        do {
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Order More Book",
                    "[2] Reduce Book In Stock",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> {
                            int oldQtyA = book.getQtyAvailable();
                            int oldQtyT = book.getQtyTotal();
                            int newQty = BookUI.inputQty();
                            book.addQty(newQty);
                            bookQtyATree.remove(IdGenerator.getKeyId(oldQtyA, id));
                            bookQtyTTree.remove(IdGenerator.getKeyId(oldQtyT, id));
                            bookQtyATree.put(IdGenerator.getKeyId(book.getQtyAvailable(), id), book);
                            bookQtyTTree.put(IdGenerator.getKeyId(book.getQtyTotal(), id), book);
                            return false;
                        },
                        () -> {
                            int oldQtyA = book.getQtyAvailable();
                            int oldQtyT = book.getQtyTotal();
                            if (oldQtyA <= 0) {
                                System.out.println("The available book in stock is empty. Cannot reduce anymore.");
                            } else {
                                int qty = BookUI.inputReduceQty(oldQtyA);
                                book.reduceQty(qty);
                                bookQtyATree.remove(IdGenerator.getKeyId(oldQtyA, id));
                                bookQtyTTree.remove(IdGenerator.getKeyId(oldQtyT, id));
                                bookQtyATree.put(IdGenerator.getKeyId(book.getQtyAvailable(), id), book);
                                bookQtyTTree.put(IdGenerator.getKeyId(book.getQtyTotal(), id), book);
                            }
                            return false;
                        },
                        () -> true
                }
            );
        }while (!isExit);

        System.out.println("Edited book with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return book;
    }

    public Book deleteBook() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete Existing Book");
        String id = BookUI.inputBookID(bookTree);
        Book book = bookTree.get(id);
        if(book != null) {
            if(book.getQtyAvailable() == book.getQtyTotal()) {
                // Remove from secondary indexes
                bookTitleTree.remove(IdGenerator.getKeyId(book.getTitle(), id));
                bookAuthorTree.remove(IdGenerator.getKeyId(book.getAuthor(), id));
                bookPriceTree.remove(IdGenerator.getKeyId(book.getPrice(), id));
                bookDateTree.remove(IdGenerator.getKeyId(book.getPublishDate(), id));
                bookQtyATree.remove(IdGenerator.getKeyId(book.getQtyAvailable(), id));
                bookQtyTTree.remove(IdGenerator.getKeyId(book.getQtyTotal(), id));
                bookTree.remove(id);

                saveToFile();
                UIGenerator.pressEnterToContinue();
            }
            else{
                System.out.println("Book with ID " + id + " cannot be removed because user are borrowing.");    
            }
            System.out.println("Book with ID " + id + " has been deleted.");
        } else {
            System.out.println("Book with ID " + id + " not found.");
        }
        return book;
    }

    public void viewBookDetails() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("View Book Details");
        String id = BookUI.inputBookID(bookTree);
        Book book = bookTree.get(id);
        System.out.println(book.toString());
        UIGenerator.pressEnterToContinue();
    }

    public void sortBooks() {
        boolean isBack = false;

        do {
            isBack = false;
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("View Sorted Books"); 

            isBack = UIGenerator.optionMenu(
                new String[]{
                    "[1] Sort by Book ID",
                    "[2] Sort by Book Title",
                    "[3] Sort by Author",
                    "[4] Sort by Available Quantity",
                    "[5] Sort by Total Quantity",
                    " ",
                    "[0] Back to Main Menu"
                },
                "Select a sorting option >> ",
                new Supplier[] {
                        () -> { sort(1); return false; },
                        () -> { sort(2); return false; },
                        () -> { sort(3); return false; },
                        () -> { sort(4); return false; },
                        () -> { sort(5); return false; },
                        () -> true
                }
            );

            if(isBack) return;

            isBack = UIGenerator.optionMenu(
                new String[]{
                    "\n[1] Ascending Order",
                    "[2] Descending Order",
                    " ",
                    "[0] Back"
                },
                "Select a sorting option >> ",
                new Supplier[] {
                        () -> { BookUI.getPagination().setIsAscending(true); return false; },
                        () -> { BookUI.getPagination().setIsAscending(false); return false; },
                        () -> true
                }
            );

        } while (isBack);
        BookUI.getPagination().setCurrentPage(1);
    }

    private void sort(int sortType) {
        MapInterface<String, Book> currentDisplayTree = BookUI.getPagination().getDisplayTree();

        if (currentDisplayTree.size() == bookTree.size()) {
            switch (sortType) {
                case 1: BookUI.getPagination().setDisplayTree(bookTree); break;
                case 2: BookUI.getPagination().setDisplayTree(bookTitleTree); break;
                case 3: BookUI.getPagination().setDisplayTree(bookAuthorTree); break;
                case 4: BookUI.getPagination().setDisplayTree(bookQtyATree); break;
                case 5: BookUI.getPagination().setDisplayTree(bookQtyTTree); break;
            }
            
        } else {
            TreeMap<String, Book> tempTree = new TreeMap<>();
            
            for (MapInterface.Entry<String, Book> entry : currentDisplayTree.entrySet()) {
                Book b = entry.getValue(); 
                String sortKey = "";
                
                switch (sortType) {
                    case 1: sortKey = b.getId(); break;
                    case 2: sortKey = IdGenerator.getKeyId(b.getTitle(), b.getId()); break;
                    case 3: sortKey = IdGenerator.getKeyId(b.getAuthor(), b.getId()); break;
                    case 4: sortKey = IdGenerator.getKeyId(b.getQtyAvailable(), b.getId()); break; 
                    case 5: sortKey = IdGenerator.getKeyId(b.getQtyTotal(), b.getId()); break;
                }
                
                tempTree.put(sortKey, b);
            }
            BookUI.getPagination().setDisplayTree(tempTree);
        }
    }

    public void searchBooks() {
        boolean isBack = false;
        final int searchOption[] = {0};

        do {
            isBack = false;

            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("View Sorted Books"); 
            isBack = UIGenerator.optionMenu(
                new String[]{
                    "[1] Show All Books",
                    "[2] Search by Book Title",
                    "[3] Search by Author",
                    "[4] Search by Category",
                    "[5] Search by Available Quantity",
                    "[6] Search by Total Quantity",
                    " ",
                    "[0] Back to Main Menu"
                },
                "Select a searching option >> ",
                new Supplier[] {
                        () -> { searchOption[0] = -1; searchTree = bookTree; return false; },
                        () -> { searchOption[0] = 0; searchTree = bookTitleTree; return false; },
                        () -> { searchOption[0] = 0; searchTree = bookAuthorTree; return false; },
                        () -> { searchOption[0] = 0; searchTree = bookCatTree; return false; },
                        () -> { searchOption[0] = 1; searchTree = bookQtyATree; return false; },
                        () -> { searchOption[0] = 1; searchTree = bookQtyTTree; return false; },
                        () -> true
                }
            );

            if(isBack) {
                return;
            }
            
        } while (isBack);

        String search = new String();
        // If user search String
        if(searchOption[0] >= 0) {
            if(searchOption[0] == 0 ) {
                search = UserPrompt.inputString("Enter the word seaching : ", 999, false).toLowerCase().replaceAll("\\s+", "");
                BookUI.getPagination().setSearchingWord(search);    
            }
            else {
                int input = UserPrompt.inputInteger("Enter the number seaching : ", 0, 999);
                search = IdGenerator.getKeyId(input, "");
                BookUI.getPagination().setSearchingWord(String.valueOf(input));
            }
        }

        BookUI.getPagination().setDisplayTree(searchTree.getRange(search, search+Character.MAX_VALUE));
        BookUI.getPagination().setCurrentPage(1);
    }

    public void saveToFile() {
        BookDAO.writeFile(BOOK_FILE_PATH, bookTree);
    }
}
