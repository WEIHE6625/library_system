package com.mycompany.library_system.entity;
import com.mycompany.library_system.adt.TreeMap;

public class BookBorrowRepo {
    private TreeMap<String, BookBorrowRecord> bookBorrowRecords;

    public BookBorrowRepo() {
        this.bookBorrowRecords = new TreeMap<String, BookBorrowRecord>();
    }


    public TreeMap<String, BookBorrowRecord> getBookBorrowRecords() {
        return this.bookBorrowRecords;
    }

    public boolean checkBookBorrowRecord(String borrowId) {
        return this.bookBorrowRecords.containsKey(borrowId);
    }

}
