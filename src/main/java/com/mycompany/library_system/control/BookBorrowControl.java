package com.mycompany.library_system.control;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookBorrowDAO;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRepo;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.entity.BorrowFineRepo;
import com.mycompany.library_system.entity.Fine;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.adt.ArrayList;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookBorrowControl {
    
    private String userId;
    private BookBorrowRepo borrowRecords = new BookBorrowRepo();
    private BorrowFineRepo borrowFineRecords = new BorrowFineRepo();
    
    private final String DEFAULT_LIBRARY_FILE_PATH = "database/book.csv";
    private TreeMap<String, Book> library = BookDAO.readFile(DEFAULT_LIBRARY_FILE_PATH);

    private static final int BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE = 10;
    private static final int BORROW_DAYS_ALLOWED = 14;
    

    public BookBorrowControl(String userId) {this.userId = userId;}
    public String getUserId(){return this.userId;}

    public boolean isBookExist(String bookId) {return (this.library.get(bookId) != null);}
    public boolean isBookAvailable(String bookId) {return (this.library.get(bookId).getQtyAvailable() > 0);}
    public Book getBook(String bookId) {return this.library.get(bookId);}
    public static int getBooksAllowedToBeBorrowed() {return BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE;}
    public static int getBorrowDaysAllowed() {return BORROW_DAYS_ALLOWED;}
    public BookBorrowRecord getBorrowRecord(String borrowId) {return this.borrowRecords.getBorrowRecord(borrowId);}
    public boolean isRecordExist(String borrowId) {return this.borrowRecords.getBookBorrowRecords().containsKey(borrowId);}
    public boolean isFineRecordExist(String borrowId) {return this.borrowFineRecords.getBorrowFineRecords().containsKey(borrowId);}
    public BorrowFineRecord getBorrowFineRecord(String boorowId) {return this.borrowFineRecords.getBorrowFineRecord(boorowId);}

    public boolean isUserHasOutstandingFine(String userId, ArrayList<BookBorrowRecord> userBorrowRecords) {
        ArrayList<BorrowFineRecord> fineNotPayedRecords = borrowFineRecords.getFinePayedRecords(false);
        for(BookBorrowRecord record: userBorrowRecords) {
            if(fineNotPayedRecords.contains(this.borrowFineRecords.getBorrowFineRecord(record.getBorrowId()))) {
                return true;
            } 
        }

        return false;
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

            if(isLateReturn(borrowId, returnDate)) {
                borrowRecord.setLateReturn(true);
                borrowFineRecords.addNewRecord(borrowId, returnDate, calculateFineAmount(borrowRecord.getBorrowDate(), returnDate));
            }

            saveBorrowRecordsChanges();
        }  

    }

    public double calculateFineAmount(LocalDate borrowDate, LocalDate returnDate) {
        return ((int) ((int) ChronoUnit.DAYS.between(borrowDate, returnDate) / Fine.getDaysToIncrementFine())) * Fine.getWeeklyFineAmount();
    }

    public boolean isLateReturn(String borrowId, LocalDate returnDate) {
        return ((int) ChronoUnit.DAYS.between(this.borrowRecords.getBorrowRecord(borrowId).getBorrowDate(), returnDate)) > BORROW_DAYS_ALLOWED;
    }
    public boolean isLateReturn(String borrowId) {
        return isLateReturn(borrowId, LocalDate.now());
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
            System.out.println(borrowRecord + String.format("%19s", borrowRecord.getBorrowDate().plusDays(BORROW_DAYS_ALLOWED)));
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
        for(MapInterface.Entry<String, BorrowFineRecord> fineRecordEntry : this.borrowFineRecords.getBorrowFineRecords().entrySet()) {
            BorrowFineRecord fineRecord = this.borrowFineRecords.getBorrowFineRecord(fineRecordEntry.getKey());
            BookBorrowRecord borrowRecord = this.borrowRecords.getBookBorrowRecords().get(fineRecord.getBorrowId());
            if(borrowRecord.getUserId().equals(userId) ) {
                System.out.println(fineRecord);
            }
        }
    }

    public void displayPendingOrPayedFineRecords(String userId, boolean finePayed ) {
        for(MapInterface.Entry<String, BorrowFineRecord> fineRecordEntry : this.borrowFineRecords.getBorrowFineRecords().entrySet()) {
            BorrowFineRecord fineRecord = this.borrowFineRecords.getBorrowFineRecord(fineRecordEntry.getKey());
            BookBorrowRecord borrowRecord = this.borrowRecords.getBookBorrowRecords().get(fineRecord.getBorrowId());
            if(borrowRecord.getUserId().equals(userId) && fineRecord.getFinePayed() == finePayed) {
                System.out.println(fineRecord);
            }
        }
    }

    public void saveLibraryChanges(String filePath, TreeMap<String, Book> bookMap) {BookDAO.writeFile(filePath, bookMap);}
    public void saveLibraryChanges() {saveLibraryChanges(DEFAULT_LIBRARY_FILE_PATH, library);}

    
    public void saveBorrowRecordsChanges() {borrowRecords.saveChanges();}
}
