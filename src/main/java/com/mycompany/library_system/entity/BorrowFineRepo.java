package com.mycompany.library_system.entity;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookBorrowFineDAO;
import com.mycompany.library_system.utility.IdGenerator;

import java.time.LocalDate;
import java.util.Map;


public class BorrowFineRepo {
    private TreeMap<String, BorrowFineRecord> borrowFineRecords;
    private TreeMap<String, BorrowFineRecord> userFineRecords;
    private TreeMap<String, BorrowFineRecord> fineDateRecords;
    private TreeMap<String, BorrowFineRecord> fineAmountRecords;
    private TreeMap<String, BorrowFineRecord> finePayedRecords;
    private TreeMap<String, BorrowFineRecord> paymentDateRecords;

    private static final String DEFAULT_FILE_PATH = "database/borrowFineRecords.csv";

    public BorrowFineRepo(String filePath) {
        this.borrowFineRecords = BookBorrowFineDAO.readFile(filePath);
        refreshMaps();
    }

    public BorrowFineRepo() {
        this(DEFAULT_FILE_PATH);
    }


    public TreeMap<String, BorrowFineRecord> getBorrowFineRecords() {return this.borrowFineRecords;}
    public BorrowFineRecord getBorrowFineRecord(String borrowId) {return this.borrowFineRecords.get(borrowId);}

    public TreeMap<String, BorrowFineRecord> getRecords(String id, MapInterface<String, BorrowFineRecord> map) {
            TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
            for(MapInterface.Entry<String, BorrowFineRecord> record: map.entrySet()) {
                if(record.getKey().contains(id.toLowerCase().replaceAll("\\s+", ""))) {
                    newMap.put(record.getKey(), record.getValue());
                }
            }
            return newMap;
        }

    public TreeMap<String, BorrowFineRecord> getUserFineRecords(String userId) {return getRecords(userId, this.userFineRecords);}
    public TreeMap<String, BorrowFineRecord> getFineDateRecords(LocalDate fineDate) {return getRecords(String.valueOf(fineDate), this.fineDateRecords);}
    public TreeMap<String, BorrowFineRecord> getFineAmountRecords(double fineAmount) {return getRecords(String.valueOf(fineAmount), this.fineAmountRecords);}
    public TreeMap<String, BorrowFineRecord> getFineAmountRecords() {return this.fineAmountRecords;}
    public TreeMap<String, BorrowFineRecord> getFinePayedRecords(boolean finePayed) {return getRecords(String.valueOf(finePayed), this.finePayedRecords);}
    public TreeMap<String, BorrowFineRecord> getPaymentDateRecords(LocalDate paymentDate) {return getRecords(String.valueOf(paymentDate), this.paymentDateRecords);}


    public TreeMap<String, BorrowFineRecord> getUserAlreadyPaidRecords(String userId, boolean finePayed) {
        TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BorrowFineRecord> record: this.userFineRecords.entrySet()) {
            if(record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", "")) && (record.getValue().getFinePayed() == finePayed)) {
                newMap.put(record.getKey(), record.getValue());
            }
        }
        return newMap;
    }

    public TreeMap<String, BorrowFineRecord> getUserPaymentDateRecords(String userId, int year, int month, int day) {
        TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BorrowFineRecord> record: this.userFineRecords.entrySet()) {
            if(record.getValue().getFinePayed() == false) {continue;}
            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue());}
            if(year != record.getValue().getPaymentDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue());}
            if(month != record.getValue().getPaymentDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue());}
            if(day != record.getValue().getPaymentDate().getDayOfMonth()) {continue;}
            newMap.put(record.getKey(), record.getValue()); 
        }
        return newMap;
    }

    public TreeMap<String, BorrowFineRecord> getPaymentDateRecords(int year, int month, int day) {
        TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BorrowFineRecord> record: this.userFineRecords.entrySet()) {
            if(record.getValue().getFinePayed() == false) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue());}
            if(year != record.getValue().getPaymentDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue());}
            if(month != record.getValue().getPaymentDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue());}
            if(day != record.getValue().getPaymentDate().getDayOfMonth()) {continue;}
            newMap.put(record.getKey(), record.getValue()); 
        }
        return newMap;
    }

    public TreeMap<String, BorrowFineRecord> getUserFineIssuedDateRecords(String userId, int year, int month, int day) {
        TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BorrowFineRecord> record: this.userFineRecords.entrySet()) {
            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue());}
            if(year != record.getValue().getFine().getFineDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue());}
            if(month != record.getValue().getFine().getFineDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue());}
            if(day != record.getValue().getFine().getFineDate().getDayOfMonth()) {continue;}
            newMap.put(record.getKey(), record.getValue());
        }
        
        return newMap;
    }

    public TreeMap<String, BorrowFineRecord> getUserFineIssuedDateRecords(String userId, int year, int month, int day, boolean finePayed) {
        TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BorrowFineRecord> record: this.userFineRecords.entrySet()) {
            if(!record.getKey().contains(userId.toLowerCase().replaceAll("\\s+", ""))) {continue;}
            if(record.getValue().getFinePayed() != finePayed) {continue;}
            if(year == -1) {newMap.put(record.getKey(), record.getValue());}
            if(year != record.getValue().getFine().getFineDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue());}
            if(month != record.getValue().getFine().getFineDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue());}
            if(day != record.getValue().getFine().getFineDate().getDayOfMonth()) {continue;}
            newMap.put(record.getKey(), record.getValue());
        }
        
        return newMap;
    }

    public TreeMap<String, BorrowFineRecord> getFineIssuedDateRecords(int year, int month, int day) {
        TreeMap<String, BorrowFineRecord> newMap = new TreeMap<>();
        for(MapInterface.Entry<String, BorrowFineRecord> record: this.borrowFineRecords.entrySet()) {
            if(year == -1) {newMap.put(record.getKey(), record.getValue());}
            if(year != record.getValue().getFine().getFineDate().getYear()) {continue;}
            if(month == -1) {newMap.put(record.getKey(), record.getValue());}
            if(month != record.getValue().getFine().getFineDate().getMonthValue()) {continue;}
            if(day == -1) {newMap.put(record.getKey(), record.getValue());}
            if(day != record.getValue().getFine().getFineDate().getDayOfMonth()) {continue;}
            newMap.put(record.getKey(), record.getValue());
        }
        return newMap;
    }

    public void addNewRecord(String borrowId, String userId, LocalDate fineDate, double fineAmount) {
        BorrowFineRecord record = new BorrowFineRecord(borrowId, userId, new Fine(fineDate, fineAmount));
        this.borrowFineRecords.put(borrowId, record);
        this.userFineRecords.put(IdGenerator.getKeyId(userId, borrowId), record);
        this.fineDateRecords.put(IdGenerator.getKeyId(String.valueOf(fineDate), borrowId), record);
        this.fineAmountRecords.put(IdGenerator.getKeyId(fineAmount, borrowId), record);
        this.finePayedRecords.put(IdGenerator.getKeyId(String.valueOf(record.getFinePayed()), borrowId), record);
        this.paymentDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getPaymentDate()), borrowId), record);
        saveChanges();
    }


    public boolean deleteRecord(String borrowId) {
        if(isFineRecordExist(borrowId)) {
            BorrowFineRecord record = this.borrowFineRecords.get(borrowId);
            this.borrowFineRecords.remove(borrowId);
            this.userFineRecords.remove(IdGenerator.getKeyId(record.getUserId(), borrowId));
            this.fineDateRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getFine().getFineDate()), borrowId));
            this.fineAmountRecords.remove(IdGenerator.getKeyId(record.getFine().getFineAmount(), borrowId));
            this.finePayedRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getFinePayed()), borrowId));
            this.paymentDateRecords.remove(IdGenerator.getKeyId(String.valueOf(record.getPaymentDate()), borrowId));
            saveChanges();
            return true;
        }
        return false;
    }

    public boolean isFineRecordExist(String borrowId) {
        return this.borrowFineRecords.containsKey(borrowId);
    }

    public void saveChanges(String filePath, TreeMap<String, BorrowFineRecord> borrowFineRecords) {BookBorrowFineDAO.writeFile(filePath, borrowFineRecords);}
    public void saveChanges() {saveChanges(DEFAULT_FILE_PATH, this.borrowFineRecords); refreshMaps();}

    public void refreshMaps() {
        this.userFineRecords = new TreeMap<>();
        this.fineDateRecords = new TreeMap<>();
        this.fineAmountRecords = new TreeMap<>();
        this.finePayedRecords = new TreeMap<>();
        this.paymentDateRecords = new TreeMap<>();

        for(BorrowFineRecord record: borrowFineRecords.values()) {
            this.userFineRecords.put(IdGenerator.getKeyId(record.getUserId(), record.getBorrowId()), record);
            this.fineDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getFine().getFineDate()), record.getBorrowId()), record);
            this.fineAmountRecords.put(IdGenerator.getKeyId(record.getFine().getFineAmount(), record.getBorrowId()), record);
            this.finePayedRecords.put(IdGenerator.getKeyId(String.valueOf(record.getFinePayed()), record.getBorrowId()), record);
            this.paymentDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getPaymentDate()), record.getBorrowId()), record);
        }
    }
}
