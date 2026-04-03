/**
 *
 * @author Isaac Kuan Way Houng
 */
package com.mycompany.library_system.entity;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookBorrowDAO;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.utility.IdGenerator;

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
    
    private static final String DEFAULT_FILE_PATH = "database/borrowBookRecords.csv";

    public BookBorrowRepo(String filePath) {
        this.bookBorrowRecords = BookBorrowDAO.readFile(filePath);
        reloadMaps();
    }

    public BookBorrowRepo() {
        this(DEFAULT_FILE_PATH);
    }

    //General trees (not tied to specific user)

    public TreeMap<String, BookBorrowRecord> getBookBorrowRecords() {return this.bookBorrowRecords;}
    public TreeMap<String, BookBorrowRecord> getUserBorrowRecords() {return this.userBorrowRecords;}
    public BookBorrowRecord getBorrowRecord(String borrowId) {return this.bookBorrowRecords.get(borrowId);}
    public TreeMap<String, BookBorrowRecord> getBorrowDateRecords(int year, int month, int day) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.bookBorrowRecords) {
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getBorrowDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getBorrowDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getBorrowDate().getDayOfMonth()) {continue;}

            newMap.put(record.getKey(), record.getValue());
        }

        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getBorrowDateRecords(LocalDate borrowDate) {return getRecords(String.valueOf(borrowDate), this.borrowDateRecords);}
    public TreeMap<String, BookBorrowRecord> getAlreadyReturnRecords(boolean alreadyReturn) {return getRecords(String.valueOf(alreadyReturn), this.alreadyReturnRecords);}
    public TreeMap<String, BookBorrowRecord> getReturnDateRecords(LocalDate returnDate) {return getRecords(String.valueOf(returnDate), this.returnDateRecords);}
    public TreeMap<String, BookBorrowRecord> getLateReturnRecords(boolean lateReturn) {return getRecords(String.valueOf(lateReturn), this.lateReturnRecords);}
    public TreeMap<String, BookBorrowRecord> getLostBookRecords(boolean lostBook) {return getRecords(String.valueOf(lostBook), this.lostBookRecords);}
    public TreeMap<String, BookBorrowRecord> getReturnDateRecords(int year, int month, int day) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {

            if(!record.getValue().getAlreadyReturned()) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getReturnDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getReturnDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getReturnDate().getDayOfMonth()) {continue;}

            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }

        //Already returned filtered records
    public TreeMap<String, BookBorrowRecord> getAlreadyReturnedBorrowDateRecords(int year, int month, int day, boolean alreadyReturn) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.bookBorrowRecords) {
            if(record.getValue().getIsLostBook()) {continue;}
            if(record.getValue().getAlreadyReturned() != alreadyReturn) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getBorrowDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getBorrowDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getBorrowDate().getDayOfMonth()) {continue;}

            newMap.put(record.getKey(), record.getValue());
        }

        return newMap;
    }



    //User/Book specific trees
    public TreeMap<String, BookBorrowRecord> getUserBorrowRecords(String userId) {return getRecords(userId, this.userBorrowRecords);}
    public TreeMap<String, BookBorrowRecord> getBookBorrowRecords(String bookId) {return getRecords(bookId, this.bookRecords);} 
    public TreeMap<String, BookBorrowRecord> getUserBookRecords(String userId, String bookId) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {
            if(record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", "")) && record.getValue().getBookId().equals(bookId)) {
                newMap.put(record.getKey(), record.getValue());
            }
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getUserBorrowDateRecords(String userId, int year, int month, int day) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {

            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getBorrowDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getBorrowDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getBorrowDate().getDayOfMonth()) {continue;}

            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getUserReturnDateRecords(String userId, int year, int month, int day) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {

            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", "")) || !record.getValue().getAlreadyReturned()) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getReturnDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getReturnDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getReturnDate().getDayOfMonth()) {continue;}

            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getUserIsLostBookRecords(String userId) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {
            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(!record.getValue().getIsLostBook()) {continue;}
            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getBookIsLostBookRecords(String bookId) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.bookRecords) {
            if(!record.getKey().contains(bookId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(!record.getValue().getIsLostBook()) {continue;}
            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }

        //Already returned filtered records
    public TreeMap<String, BookBorrowRecord> getUserAlreadyReturnRecords(String userId, boolean alreadyReturn) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {
            if(record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", "")) && (record.getValue().getAlreadyReturned() == alreadyReturn) && (!record.getValue().getIsLostBook())) {
                newMap.put(record.getKey(), record.getValue());
            }
        }
        return newMap;

    }
    public TreeMap<String, BookBorrowRecord> getBookBorrowRecords(String bookId, boolean alreadyReturn) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.bookRecords) {
            if(record.getValue().getIsLostBook()) {continue;}
            if(!record.getKey().contains(bookId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(record.getValue().getAlreadyReturned() != alreadyReturn) {continue;}
            newMap.put(record.getKey(), record.getValue());   
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getUserBookRecords(String userId, String bookId, boolean alreadyReturn) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {
            if(record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", "")) && record.getValue().getBookId().equals(bookId) && (record.getValue().getAlreadyReturned() == alreadyReturn) && (!record.getValue().getIsLostBook())) {
                newMap.put(record.getKey(), record.getValue());
            }
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getUserBorrowDateRecords(String userId, int year, int month, int day, boolean alreadyReturn) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.userBorrowRecords) {
            if(record.getValue().getIsLostBook()) {continue;}
            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", "")) || record.getValue().getAlreadyReturned() != alreadyReturn) {continue;}
            if(record.getValue().getAlreadyReturned() != alreadyReturn) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getBorrowDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getBorrowDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getBorrowDate().getDayOfMonth()) {continue;}

            newMap.put(record.getKey(), record.getValue());
            
        }
        return newMap;
    }
    public TreeMap<String, BookBorrowRecord> getBorrowDateIsLostRecords(int year, int month, int day) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: this.lostBookRecords) {
            if(!record.getValue().getIsLostBook()) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(year != record.getValue().getBorrowDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(month != record.getValue().getBorrowDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue()); continue;}
            if(day != record.getValue().getBorrowDate().getDayOfMonth()) {continue;}
            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }

    //Utility 
    public TreeMap<String, BookBorrowRecord> getRecords(String id, MapInterface<String, BookBorrowRecord> map) {
        TreeMap<String, BookBorrowRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BookBorrowRecord> record: map) {
            if(record.getKey().contains(id.toLowerCase().replaceAll("\\s+", ""))) {
                newMap.put(record.getKey(), record.getValue());
            }
        }
        return newMap;
    }

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

    public void saveChanges() {
        saveChanges(DEFAULT_FILE_PATH, this.bookBorrowRecords);
        reloadMaps();
    }

    public void reloadMaps() {
        this.userBorrowRecords = new TreeMap<>();
        this.bookRecords = new TreeMap<>();
        this.borrowDateRecords = new TreeMap<>();
        this.alreadyReturnRecords = new TreeMap<>();
        this.returnDateRecords = new TreeMap<>();
        this.lateReturnRecords = new TreeMap<>();
        this.lostBookRecords = new TreeMap<>();

        for(MapInterface.Entry<String, BookBorrowRecord> record: this.bookBorrowRecords) {
            this.userBorrowRecords.put(IdGenerator.getKeyId(record.getValue().getUserId(), record.getKey()), record.getValue());
            this.bookRecords.put(IdGenerator.getKeyId(record.getValue().getBookId(), record.getKey()), record.getValue());
            this.borrowDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getBorrowDate()), record.getKey()), record.getValue());
            this.alreadyReturnRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getAlreadyReturned()), record.getKey()), record.getValue());
            this.returnDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getReturnDate()), record.getKey()), record.getValue());
            this.lateReturnRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getLateReturn()), record.getKey()), record.getValue());
            this.lostBookRecords.put(IdGenerator.getKeyId(String.valueOf(record.getValue().getIsLostBook()), record.getKey()), record.getValue());
        }
    }

}
