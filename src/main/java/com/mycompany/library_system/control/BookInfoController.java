package com.mycompany.library_system.control;

import java.time.YearMonth;
import java.util.function.Supplier;
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
    private MapInterface<String, String> searchCriteriaTree; // 0: Title, 1: Author, 2: Category, 3: Available Qty, 4: Total Qty
    
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
        initCriteriaTree();

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
                String[] categoryNames = category.getKey().split(",");
                for (String catName : categoryNames) {
                    bookCatTree.put(IdGenerator.getKeyId(catName.trim(), book.getId()), book);
                }
            }
        }
    }

    private void initCriteriaTree() {
        searchCriteriaTree = new TreeMap<>();
        searchCriteriaTree.put("Title", null);
        searchCriteriaTree.put("Author", null);
        searchCriteriaTree.put("Category", null);
        searchCriteriaTree.put("Available Quantity", null);
        searchCriteriaTree.put("Total Quantity", null);
    }

    // Return the Tree
    public MapInterface<String, Book> getbookTree() { 
        return bookTree;
    }
    public MapInterface<String, Book> getbookTree(String treeType) { 
        switch (treeType) {
            case "Title": return bookTitleTree;
            case "Author": return bookAuthorTree;
            case "Price": return bookPriceTree;
            case "PublishDate": return bookDateTree;
            case "AvailableQuantity": return bookQtyATree;
            case "TotalQuantity": return bookQtyTTree;
            case "Category": return bookCatTree;
            default: return bookTree;
        }
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
        int qty = 0;

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
                        () -> { book.setDescription(BookUI.inputBookDescription()); return false; },
                        () -> { 
                            MapInterface<String, Integer> oldCatsTree = book.getCategories();
                            MapInterface<String, Integer> newCatsTree = BookUI.inputBookCategory(this, book.getCategories()); 
                            book.setCategories(newCatsTree);
                            for (MapInterface.Entry<String, Integer> category : oldCatsTree) {
                                String[] categoryNames = category.getKey().split(",");
                                for (String catName : categoryNames) {
                                    bookCatTree.remove(IdGenerator.getKeyId(catName.trim(), id));
                                }
                            }
                            for (MapInterface.Entry<String, Integer> category : newCatsTree) {
                                String[] categoryNames = category.getKey().split(",");
                                for (String catName : categoryNames) {
                                    bookCatTree.put(IdGenerator.getKeyId(catName.trim(), id), book);
                                }
                            }                            
                            return false; 
                        },
                        () -> { book.setPublisher(BookUI.inputBookPublisher()); return false; },
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
                        () -> { applySort(1); return false; },
                        () -> { applySort(2); return false; },
                        () -> { applySort(3); return false; },
                        () -> { applySort(4); return false; },
                        () -> { applySort(5); return false; },
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

    private void applySort(int sortType) {
        MapInterface<String, Book> currentDisplayTree = BookUI.getPagination().getDisplayTree();

        // If no search is applied, directly set the display tree to the corresponding sorted tree.
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
            
            for (MapInterface.Entry<String, Book> entry : currentDisplayTree) {
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
                        () -> { 
                                initCriteriaTree();
                                BookUI.getPagination().setDisplayTree(bookTree); 
                                BookUI.getPagination().setCurrentPage(1); 
                                BookUI.getPagination().setSearchingWord(""); 
                                return false; 
                            },
                        () -> { applySearch("Title", "string"); return false; },
                        () -> { applySearch("Author", "string"); return false; },
                        () -> { applySearch("Category", "category"); return false; },
                        () -> { applySearch("Available Quantity", "int"); return false; },
                        () -> { applySearch("Total Quantity", "int"); return false; },
                        () -> true
                }
            );

            if(isBack) {
                return;
            }
            
        } while (isBack);
    }

    private void applySearch(String searchCriteria, String searchType) {
        String search = "";

        switch (searchType) {
            case "string": search = UserPrompt.inputString("Enter the word to search : ", 999, false).toLowerCase().replaceAll("\\s+", ""); break;
            case "category": 
                MapInterface<String, Integer> searchCategories = UserPrompt.inputBookCategory(this);
                StringBuilder catBuilder = new StringBuilder();
                
                for (MapInterface.Entry<String, Integer> category : searchCategories) {
                    String[] categoryNames = category.getKey().split(",");
                    for (String catName : categoryNames) {
                        catBuilder.append(IdGenerator.getKeyId(catName.trim(), "")).deleteCharAt(catBuilder.length() - 1).append(",");
                    }
                }
                if (catBuilder.length() > 0) {
                    catBuilder.setLength(catBuilder.length() - 1); 
                }
                search = catBuilder.toString();
                break;
            case "int": int input = UserPrompt.inputInteger("Enter the number to search : ", 0, 999); search = IdGenerator.getKeyId(input, ""); break;
        }

        if (searchCriteriaTree.get(searchCriteria) != null) {
            System.out.println("Search criteria already applied. It will be overridden. Do you want to continue? (Y/N)");
            String choice = UserPrompt.inputString("Enter your choice: ", 1, false);
            if (!"Y".equalsIgnoreCase(choice)) {
                return;
            }
        }

        searchCriteriaTree.put(searchCriteria, search);

        MapInterface<String, Book> finalResults = bookTree; 

        // Loop through only the ACTIVE criteria that have actual values
        for (MapInterface.Entry<String, String> entry : searchCriteriaTree) {
            String activeCriteria = entry.getKey();
            String activeSearchWord = entry.getValue();

            if (activeSearchWord == null) continue;

            MapInterface<String, Book> rawMatches = new TreeMap<>();
            MapInterface<String, Book> targetTree = new TreeMap<>();

            if (activeCriteria.equals("Category")) {
                String[] categories = activeSearchWord.split(",");

                for (int i = 0; i < categories.length; i++) {
                    String cat = categories[i];
                    MapInterface<String, Book> singleCatMatches = bookCatTree.getRange(cat, cat + Character.MAX_VALUE);
                    
                    if (i == 0) {
                        for (MapInterface.Entry<String, Book> match : singleCatMatches) {
                            rawMatches.put(match.getValue().getId(), match.getValue());
                        }
                    } else {
                        MapInterface<String, Book> tempIntersection = new TreeMap<>();
                        // Intersection
                        for (MapInterface.Entry<String, Book> match : singleCatMatches) {
                            if (rawMatches.containsKey(match.getValue().getId())) {
                                tempIntersection.put(match.getValue().getId(), match.getValue());
                            }
                        }
                        rawMatches = tempIntersection; 
                    }
                }
            } else {
                switch (activeCriteria) {
                    case "Title": targetTree = bookTitleTree; break;
                    case "Author": targetTree = bookAuthorTree; break;
                    case "Available Quantity": targetTree = bookQtyATree; break;
                    case "Total Quantity": targetTree = bookQtyTTree; break;
                }
                rawMatches = targetTree.getRange(activeSearchWord, activeSearchWord + Character.MAX_VALUE);
            }
            // Intersection
            MapInterface<String, Book> intersectionTree = new TreeMap<>();
            for (MapInterface.Entry<String, Book> match : rawMatches) {
                if (finalResults.containsKey(match.getValue().getId())) {
                    intersectionTree.put(match.getValue().getId(), match.getValue()); 
                }
            }
            
            finalResults = intersectionTree; 
        }

        StringBuilder displaySearch = new StringBuilder();
        for (MapInterface.Entry<String, String> entry : searchCriteriaTree) {
            if (entry.getValue() != null) displaySearch.append("{").append(entry.getKey()).append(": ").append(entry.getValue()).append("} ");
        }

        BookUI.getPagination().setSearchingWord(displaySearch.toString().trim());
        BookUI.getPagination().setDisplayTree(finalResults);
        BookUI.getPagination().setCurrentPage(1);
    }

    public void saveToFile() {
        BookDAO.writeFile(BOOK_FILE_PATH, bookTree);
    }
}
