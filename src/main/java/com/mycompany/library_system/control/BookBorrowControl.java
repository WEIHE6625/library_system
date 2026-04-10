/**
 *
 * @author Isaac Kuan Way Houng
 */
package com.mycompany.library_system.control;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.dao.UserDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRepo;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.entity.BorrowFineRepo;
import com.mycompany.library_system.entity.Fine;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.utility.IdGenerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookBorrowControl {
    
    
    private String userId;
    private BookBorrowRepo borrowRecords;
    private BorrowFineRepo borrowFineRecords;
    
    private final String DEFAULT_LIBRARY_FILE_PATH = "database/book.csv";
    private MapInterface<String, User> userMap;
    private final String DEFAULT_USER_FILE_PATH = "database/users.csv";
    private MapInterface<String, Book> library;

    private static final int BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE = 5;
    private static final int BORROW_DAYS_ALLOWED = 14;
    private static final int MAX_LOST_BOOKS_ALLOWED = 10;
    private static final double MAX_FINE_CHARGEABLE = 9999.99;
    private static final LocalDate BORROW_START_DATE = LocalDate.parse("2017-05-01");
    

    public BookBorrowControl(String userId, MapInterface<String, Book> library, MapInterface<String, User> userMap) {
        this.userId = userId; 
        this.userMap = userMap;
        this.library = library;
        this.borrowRecords = new BookBorrowRepo();
        this.borrowFineRecords = new BorrowFineRepo();
        batchCalculateFine();
    }

    public String getUserId(){return this.userId;}

    public BookBorrowRepo getBorrowRepo() {return this.borrowRecords;}
    public BorrowFineRepo getFineRepo() {return this.borrowFineRecords;}

    public boolean isBookExist(String bookId) {return (this.library.get(bookId) != null);}
    public boolean isBookAvailable(String bookId) {return (this.library.get(bookId).getQtyAvailable() > 0);}
    public Book getBook(String bookId) {return this.library.get(bookId);}
    public boolean isUserExist(String userId) {return (this.userMap.get(userId) != null);}
    public static int getBooksAllowedToBeBorrowed() {return BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE;}
    public static int getBorrowDaysAllowed() {return BORROW_DAYS_ALLOWED;}
    public static double getMaxFineChargeable() {return MAX_FINE_CHARGEABLE;}
    public static int getMaxLostBooksAllowed() {return MAX_LOST_BOOKS_ALLOWED;}
    public static LocalDate getBorrowStartDate() {return BORROW_START_DATE;}
    public BookBorrowRecord getBorrowRecord(String borrowId) {return this.borrowRecords.getBorrowRecord(borrowId);}
    public boolean isRecordExist(String borrowId) {return this.borrowRecords.getBookBorrowRecords().containsKey(borrowId);}
    public boolean isFineRecordExist(String borrowId) {return this.borrowFineRecords.getBorrowFineRecords().containsKey(borrowId);}
    public BorrowFineRecord getBorrowFineRecord(String boorowId) {return this.borrowFineRecords.getBorrowFineRecord(boorowId);}
    public MapInterface<String, Book> getBookTree() {return this.library;}
    public MapInterface<String, User> getUserTree() {return this.userMap;}

    public LocalDate calculateReturnDate(String borrowId) {
        BookBorrowRecord record = borrowRecords.getBorrowRecord(borrowId);
        return record.getBorrowDate().plusDays(BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE);
    }

    public boolean isUserHasOutstandingFine(String userId) {
        TreeMap<String, BorrowFineRecord> userFineRecord = borrowFineRecords.getUserFineRecords(userId);
        for(MapInterface.Entry<String, BorrowFineRecord> record: userFineRecord) {
            if(record.getValue().getFinePayed() == false) {
                return true;
            } 
        }

        return false;
    }

    public boolean isUserEligible(String userId) {
        int booksBorrowedCurrently = 0;
        int lostBooks = 0;
        TreeMap<String, BookBorrowRecord> userBorrowRecordsMap = borrowRecords.getUserBorrowRecords(userId);

        for(MapInterface.Entry<String, BookBorrowRecord> record: userBorrowRecordsMap) {
            if(record.getValue().getAlreadyReturned() == false && record.getValue().getIsLostBook() == false) {booksBorrowedCurrently++;}
            if(record.getValue().getIsLostBook()) {lostBooks++;}

            if(isUserHasOutstandingFine(userId) || (lostBooks >= MAX_LOST_BOOKS_ALLOWED)) {
                return false;
            }            
        }
        
        return (booksBorrowedCurrently < BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE);
    }


    public boolean borrow(String userId, String bookId, LocalDate borrowDate) {
        if(isBookAvailable(bookId) && isUserEligible(userId)) {
            borrowRecords.addNewRecord(generateBorrowId(), bookId, userId, borrowDate);
            library.get(bookId).setAvailableQty(library.get(bookId).getQtyAvailable() - 1);
            userMap.get(userId).setNumberOfBooksBorrowing(userMap.get(userId).getNumOfBooksBorrowing() + 1);
            saveLibraryChanges();
            saveUserChanges();
            return true;
        }
        
        if(!isBookAvailable(bookId) && isUserEligible(userId)) {
            System.out.println("The book is currently not available for borrowing. Please try again later. Sorry for the inconvenience caused.\n");
        }
        else {
            System.out.println("You are currently not allowed to borrow any new books. This may be due to the following reasons:\n");
            System.out.println("1. You are have reached the current borrow limit of " + BOOKS_ALLOWED_TO_BE_BORROWED_AT_ONCE + " books at once.");
            System.out.println("2. You have outstanding fines to settle.");
            System.out.println("3. You have lost " + MAX_LOST_BOOKS_ALLOWED + " books in the past.\n");
            System.out.println("Please contact the counter staff for futher inquiries.");
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
        User user = userMap.get(borrowRecord.getUserId());
        if(borrowRecord != null) {
            borrowRecord.setAlreadyReturned(returnDate);
            book.setAvailableQty(book.getQtyAvailable()+1);
            user.setNumberOfBooksBorrowing(user.getNumOfBooksBorrowing() - 1);
            saveLibraryChanges();
            saveUserChanges();
            if(isLateReturn(borrowId, returnDate)) {
                borrowRecord.setLateReturn(true);
                borrowFineRecords.addNewRecord(borrowId, user.getUserId(), returnDate, calculateFineAmount(borrowRecord.getBorrowDate(), returnDate));
            }

            saveBorrowRecordsChanges();
        }  

    }

    public void setBookNotLost(String borrowId) {
        BookBorrowRecord borrowRecord = borrowRecords.getBorrowRecord(borrowId);
        borrowRecord.setIsLostBook(false);

        Book book = getBook(borrowRecord.getBookId());
        BorrowFineRecord fineRecord = borrowFineRecords.getBorrowFineRecord(borrowId);
        fineRecord.getFine().setFineAmount(fineRecord.getFine().getFineAmount() - book.getPrice());

        if(fineRecord.getFine().getFineAmount() == 0) {
            borrowFineRecords.deleteRecord(borrowId);
        }

        saveBorrowFineChanges();
        saveBorrowRecordsChanges();
    }

    public void setBookLost(String borrowId) {
        BookBorrowRecord borrowRecord = borrowRecords.getBorrowRecord(borrowId);
        borrowRecord.setIsLostBook(true);
        if(!isFineRecordExist(borrowId)) {
            borrowFineRecords.addNewRecord(borrowId, borrowRecord.getUserId(), LocalDate.now(), library.get(borrowRecord.getBookId()).getPrice());
        }
        else {
            BorrowFineRecord fineRecord = borrowFineRecords.getBorrowFineRecord(borrowId);
            fineRecord.getFine().setFineAmount(fineRecord.getFine().getFineAmount() + library.get(borrowRecord.getBookId()).getPrice());
        }
        
        saveBorrowRecordsChanges();
        saveBorrowFineChanges();
    }

    public double calculateFineAmount(LocalDate borrowDate, LocalDate currentDate) {
        return ((int) ((int) ChronoUnit.DAYS.between(borrowDate, currentDate) / Fine.getDaysToIncrementFine())) * Fine.getWeeklyFineAmount();
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

    public void batchCalculateFine() {
        for(MapInterface.Entry<String, BookBorrowRecord> borrowRecord: this.borrowRecords.getBookBorrowRecords()) {
            if(borrowRecord.getValue().getAlreadyReturned()) {continue;}
            if(!isLateReturn(borrowRecord.getKey())) {continue;}
            borrowRecord.getValue().setLateReturn(true);
            BorrowFineRecord fineRecord = borrowFineRecords.getBorrowFineRecord(borrowRecord.getKey());
            double fineAmount = calculateFineAmount(borrowRecord.getValue().getBorrowDate(), LocalDate.now());
            if(borrowRecord.getValue().getIsLostBook()) {
                fineAmount += getBook(borrowRecord.getValue().getBookId()).getPrice();
            }
            if(fineAmount > MAX_FINE_CHARGEABLE) {fineAmount = MAX_FINE_CHARGEABLE;}
            if(fineRecord != null) {fineRecord.getFine().setFineAmount(fineAmount); continue;}
            this.borrowFineRecords.addNewRecord(borrowRecord.getKey(), borrowRecord.getValue().getUserId(), LocalDate.now(), fineAmount);
        }
        saveBorrowFineChanges();
    }

    public void receiveFinePayment(String borrowId, LocalDate paymentDate) {
        this.borrowFineRecords.getBorrowFineRecord(borrowId).receiveFinePayment(paymentDate);
        saveBorrowFineChanges();
    }
    

    public void saveLibraryChanges(String filePath, MapInterface<String, Book> bookMap) {BookDAO.writeFile(filePath, bookMap);}
    public void saveLibraryChanges() {saveLibraryChanges(DEFAULT_LIBRARY_FILE_PATH, library);}
    public void saveUserChanges(String filePath, MapInterface<String, User> userMap) {UserDAO.writeFile(filePath, userMap);}
    public void saveUserChanges() {saveUserChanges(DEFAULT_USER_FILE_PATH, userMap);}
    
    public void saveBorrowRecordsChanges() {borrowRecords.saveChanges();}
    public void saveBorrowFineChanges() {borrowFineRecords.saveChanges();}
}
