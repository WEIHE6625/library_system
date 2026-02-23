package com.mycompany.library_system.control_classes;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRepo;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.entity.BorrowFineRepo;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.adt.ArrayList;

import java.time.LocalDate;

public class BookBorrowControl {
    private String bookId;
    private String userId;
    private BookBorrowRepo borrowRecords = new BookBorrowRepo();
    private BorrowFineRepo borrowFineRecords = new BorrowFineRepo();
    
    private final String DEFAULT_LIBRARY_FILE_PATH = "database/book.csv";
    private TreeMap<String, Book> library = BookDAO.readFile(DEFAULT_LIBRARY_FILE_PATH);

    private final int BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE = 2;
    
    public BookBorrowControl(String userId) {this.userId = userId;}
    public String getBookId() {return this.bookId;}
    public String getUserId(){return this.userId;}


    public boolean isBookAvailable(String bookId) {return (this.library.get(bookId).getQtyAvailable() > 0);}

    public boolean isUserHasOutstandingFine(String userId, ArrayList<BookBorrowRecord> userBorrowRecords) {
        ArrayList<BorrowFineRecord> fineNotPayedRecords = borrowFineRecords.getFinePayedRecords(false);
        for(BookBorrowRecord record: userBorrowRecords) {
            if(fineNotPayedRecords.contains(this.borrowFineRecords.getBorrowFineRecord(record.getBorrowId()))) {
                return false;
            } 
        }

        return true;
    }

    public boolean isUserEligible(String userId) {
        int booksBorrowedCurrently = 0;
        ArrayList<BookBorrowRecord> userBorrowRecords = borrowRecords.getUserBorrowRecords(userId);

        for(BookBorrowRecord record: userBorrowRecords) {
            if(record.getAlreadyReturned() == false) {
                booksBorrowedCurrently++;
            }

            if(record.getIsLostBook() == true || isUserHasOutstandingFine(userId, userBorrowRecords)) {
                return false;
            }
        }
        return (booksBorrowedCurrently < BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE);
    }

    public boolean borrow(String userId, String bookId, LocalDate borrowDate) {
        if(isBookAvailable(bookId) && isUserEligible(userId)) {
            borrowRecords.addNewRecord(generateBorrowId(), bookId, userId, borrowDate);
            library.get(bookId).setAvailableQty(library.get(bookId).getQtyAvailable() - 1);
            saveLibraryChanges();
            return true;
        }
        
        if(!isBookAvailable(bookId) && isUserEligible(userId)) {
            System.out.println("The book is currently not available for borrowing. Please try again later. Sorry for the inconvenience caused.\n");
        }
        else {
            System.out.println("You are currently not allowed to borrow any new books until you have return unborrowed books, or settle your outstanding fines.\n");
        }
        
        
        return false;
        
    }

    public void delete(String borrowId) {
        Book book = this.library.get(this.borrowRecords.getBorrowRecord(borrowId).getBookId());
        if (borrowRecords.deleteRecord(borrowId)) {
            System.out.println("Record " + borrowId + " successfully deleted");
            book.setAvailableQty(book.getQtyAvailable()+1);
            saveLibraryChanges();
        }
        else {
            System.out.println("Failed to delete record " + borrowId);
        }
    }

    public void returnBook(String borrowId, LocalDate returnDate) {
        Book book = this.library.get(this.borrowRecords.getBorrowRecord(borrowId).getBookId());
        BookBorrowRecord borrowRecord = borrowRecords.getBookBorrowRecords().get(borrowId);
        if(borrowRecord != null) {
            borrowRecord.setAlreadyReturned(returnDate);
            book.setAvailableQty(book.getQtyAvailable()+1);
            saveLibraryChanges();
        }
        
        
    }

    public String generateBorrowId() {
        return IdGenerator.generateId(borrowRecords.getBookBorrowRecords(), "BR", 6);
    }

    public void displayAllBorrowRecords() {
        for(BookBorrowRecord borrowRecord : this.borrowRecords.getBookBorrowRecords().values()) {
            System.out.println(borrowRecord);
        }
    }

    public void displayUserBorrowRecords(String userId) {
        for(BookBorrowRecord borrowRecord: this.borrowRecords.getUserBorrowRecords(userId)) {
            System.out.println(borrowRecord);
        }
    }
    
    //alreadyReturn == false, means not yet return book (current record)
    public void displayCurrentOrPastRecords(String userId, boolean alreadyReturn) {
        for(BookBorrowRecord borrowRecord: this.borrowRecords.getUserBorrowRecords(userId)) {
            if(borrowRecord.getAlreadyReturned() == alreadyReturn) {
                System.out.println(borrowRecord);
            }
        }
    }

    public void displayAllFineRecords(String userId) {
        for(MapInterface.Entry<String, BorrowFineRecord> fineRecord : this.borrowFineRecords.getBorrowFineRecords().entrySet()) {
            if(this.borrowRecords.getBookBorrowRecords().get(fineRecord.getKey()).getUserId().equals(userId)) {
                System.out.println(fineRecord);
            }
        }
    }

    public void saveLibraryChanges(String filePath, TreeMap<String, Book> bookMap) {BookDAO.writeFile(filePath, bookMap);}
    public void saveLibraryChanges() {saveLibraryChanges(DEFAULT_LIBRARY_FILE_PATH, library);}

}
