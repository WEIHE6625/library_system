package com.mycompany.library_system.control_classes;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRepo;
import com.mycompany.library_system.entity.BookBorrowRecord;

import java.time.LocalDate;

public class BookBorrowControl {
    private String bookId;
    private TreeMap<String, Book> library = BookDAO.readFile("/database/book.csv");
    private String userId;
    private BookBorrowRepo borrowRecords = new BookBorrowRepo();
    
    
    public BookBorrowControl(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return this.bookId;
    }

    public String getUserId(){
        return this.userId;
    }


    
    public boolean checkBookAvailability(String bookId) {
        
        return false;
    }

    public void borrow(String borrowId, BookBorrowRecord record) {
        borrowRecords.getBookBorrowRecords().put(borrowId, record);
    }

    public void delete(String borrowId) {
        borrowRecords.getBookBorrowRecords().remove(borrowId);
    }

    public void returnBook(String borrowId, LocalDate returnDate) {
        borrowRecords.getBookBorrowRecords().get(borrowId).setAlreadyReturned(returnDate);
    }

    public String generateBorrowId() {
        if (borrowRecords.getBookBorrowRecords().size() == 0) {
            return "BR000000";
        }
        MapInterface.Entry<String, BookBorrowRecord>[] borrowRecordsIds = borrowRecords.getBookBorrowRecords().entrySet();
        
        int idNum = Integer.parseInt(borrowRecordsIds[borrowRecordsIds.length - 1].getKey().substring(2));

        if (idNum != borrowRecordsIds.length) {
            idNum++; 
        };

        return String.format("BR%06d", idNum);
    }

    public void displayRecords() {
        for(MapInterface.Entry<String, BookBorrowRecord> borrowRecord : borrowRecords.getBookBorrowRecords().entrySet()) {
            System.out.printf("%-10s %-10s %-12s %-15s %-10s %-10s\n", borrowRecord.getKey(), borrowRecord.getValue().getBookId(), borrowRecord.getValue().getUserId(), borrowRecord.getValue().getBorrowDate(), borrowRecord.getValue().getAlreadyReturned(), borrowRecord.getValue().getReturnDate());
        }
    }

}
