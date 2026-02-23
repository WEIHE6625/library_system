package com.mycompany.library_system.entity;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookBorrowDAO;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.adt.ArrayList;

import java.time.LocalDate;

public class BookBorrowRepo {
    private TreeMap<String, BookBorrowRecord> bookBorrowRecords;
    private TreeMap<String, BookBorrowRecord> userBorrowRecords;
    private TreeMap<String, BookBorrowRecord> bookRecords;
    private TreeMap<String, BookBorrowRecord> borrowDateRecords;
    private TreeMap<String, BookBorrowRecord> alreadyReturnRecords;
    private TreeMap<String, BookBorrowRecord> returnDateRecords;
    private TreeMap<String, BookBorrowRecord> lateReturnRecords;
    private TreeMap<String, BookBorrowRecord> lostBookRecords;
    private TreeMap<String, BookBorrowRecord> searchTree  = new TreeMap<>();
    
    private static final String DEFAULT_FILE_PATH = "database/borrowBookRecords.csv";

    public BookBorrowRepo(String filePath) {
        this.bookBorrowRecords = BookBorrowDAO.readFile(filePath);
        this.userBorrowRecords = new TreeMap<>();
        this.bookRecords = new TreeMap<>();
        this.borrowDateRecords = new TreeMap<>();
        this.alreadyReturnRecords = new TreeMap<>();
        this.returnDateRecords = new TreeMap<>();
        this.lateReturnRecords = new TreeMap<>();
        this.lostBookRecords = new TreeMap<>();

        for(MapInterface.Entry<String, BookBorrowRecord> record: bookBorrowRecords.entrySet()) {
            this.userBorrowRecords.put(IdGenerator.getKeyId(record.getValue().getUserId(), record.getKey()), record.getValue());
            this.bookRecords.put(IdGenerator.getKeyId(record.getValue().getBookId(), record.getKey()), record.getValue());
            this.borrowDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getBorrowDate()), record.getKey()), record.getValue());
            this.alreadyReturnRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getAlreadyReturned()), record.getKey()), record.getValue());
            this.returnDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getReturnDate()), record.getKey()), record.getValue());
            this.lateReturnRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getLateReturn()), record.getKey()), record.getValue());
            this.lostBookRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getIsLostBook()), record.getKey()), record.getValue());
        }


    }

    public BookBorrowRepo() {
        this(DEFAULT_FILE_PATH);
    }


    public TreeMap<String, BookBorrowRecord> getBookBorrowRecords() {return this.bookBorrowRecords;}
    public TreeMap<String, BookBorrowRecord> getUserBorrowRecords() {return this.userBorrowRecords;}

    public BookBorrowRecord getBorrowRecord(String borrowId) {return this.bookBorrowRecords.get(borrowId);}

    public ArrayList<BookBorrowRecord> getRecords(String id, MapInterface<String, BookBorrowRecord> map) {
        ArrayList<BookBorrowRecord> list = new ArrayList<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: map.entrySet()) {
            if(record.getKey().contains(id.toLowerCase().replaceAll("\\s+", ""))) {
                list.add(record.getValue());
            }
        }
        return list;
    }

    public ArrayList<BookBorrowRecord> getUserBorrowRecords(String userId) {return getRecords(userId, this.userBorrowRecords);}
    public ArrayList<BookBorrowRecord> getBookBorrowRecords(String bookId) {return getRecords(bookId, this.bookRecords);}
    public ArrayList<BookBorrowRecord> getBorrowDateRecords(LocalDate borrowDate) {return getRecords(String.valueOf(borrowDate), this.borrowDateRecords);}
    public ArrayList<BookBorrowRecord> getAlreadyReturnRecords(Boolean alreadyReturn) {return getRecords(String.valueOf(alreadyReturn), this.alreadyReturnRecords);}
    public ArrayList<BookBorrowRecord> getReturnDateRecords(LocalDate returnDate) {return getRecords(String.valueOf(returnDate), this.returnDateRecords);}
    public ArrayList<BookBorrowRecord> getLateReturnRecords(boolean lateReturn) {return getRecords(String.valueOf(lateReturn), this.lateReturnRecords);}
    public ArrayList<BookBorrowRecord> getLostBookRecords(boolean lostBook) {return getRecords(String.valueOf(lostBook), this.lostBookRecords);}

    public void addNewRecord(String borrowId, String bookId, String userId, LocalDate borrowDate) {
        BookBorrowRecord record = new BookBorrowRecord(borrowId, bookId, userId, borrowDate);

        this.bookBorrowRecords.put(borrowId, record);
        this.userBorrowRecords.put(IdGenerator.getKeyId(userId, borrowId), record);
        this.bookRecords.put(IdGenerator.getKeyId(bookId, borrowId), record);
        this.borrowDateRecords.put(IdGenerator.getKeyId(String.valueOf(borrowDate), borrowId), record);
        this.alreadyReturnRecords.put(IdGenerator.getKeyId(String.valueOf(record.getAlreadyReturned()), borrowId), record);
        this.returnDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getReturnDate()), borrowId), record);
        this.lateReturnRecords.put(IdGenerator.getKeyId(String.valueOf(record.getLateReturn()), borrowId), record);
        this.lostBookRecords.put(IdGenerator.getKeyId(String.valueOf(record.getIsLostBook()), borrowId), record);

        saveChanges();
    }

    public boolean deleteRecord(String borrowId) {
        if(isBorrowRecordExist(borrowId)) {
            BookBorrowRecord record = this.bookBorrowRecords.get(borrowId);
            this.bookBorrowRecords.remove(borrowId);
            this.userBorrowRecords.remove(IdGenerator.getKeyId(record.getUserId(), borrowId));
            this.bookRecords.remove(IdGenerator.getKeyId(record.getBookId(), borrowId));
            this.borrowDateRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getBorrowDate()), borrowId));
            this.alreadyReturnRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getAlreadyReturned()), borrowId));
            this.returnDateRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getReturnDate()), borrowId));
            this.lateReturnRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getLateReturn()), borrowId));
            this.lostBookRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getIsLostBook()), borrowId));
            saveChanges();
            return true;
        }
        return false;
    }

    public boolean isBorrowRecordExist(String borrowId) {return this.bookBorrowRecords.containsKey(borrowId);}

    public void saveChanges(String filePath, TreeMap<String, BookBorrowRecord> bookBorrowRecords) {BookBorrowDAO.writeFile(filePath, bookBorrowRecords);}

    public void saveChanges() {saveChanges(DEFAULT_FILE_PATH, this.bookBorrowRecords);}

}
