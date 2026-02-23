package com.mycompany.library_system.entity;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.dao.BookBorrowFineDAO;
import com.mycompany.library_system.utility.IdGenerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowFineRepo {
    private TreeMap<String, BorrowFineRecord> borrowFineRecords;
    private TreeMap<String, BorrowFineRecord> fineDateRecords;
    private TreeMap<String, BorrowFineRecord> fineAmountRecords;
    private TreeMap<String, BorrowFineRecord> finePayedRecords;
    private TreeMap<String, BorrowFineRecord> paymentDateRecords;

    private static final String DEFAULT_FILE_PATH = "database/borrowFineRecords.csv";

    public BorrowFineRepo(String filePath) {
        this.borrowFineRecords = BookBorrowFineDAO.readFile(filePath);
        this.fineDateRecords = new TreeMap<>();
        this.fineAmountRecords = new TreeMap<>();
        this.finePayedRecords = new TreeMap<>();
        this.paymentDateRecords = new TreeMap<>();

        for(BorrowFineRecord record: borrowFineRecords.values()) {
            this.fineDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getFine().getFineDate()), record.getBorrowId()), record);
            this.fineAmountRecords.put(IdGenerator.getKeyId(record.getFine().getFineAmount(), record.getBorrowId()), record);
            this.finePayedRecords.put(IdGenerator.getKeyId(String.valueOf(record.getFinePayed()), record.getBorrowId()), record);
            this.paymentDateRecords.put(IdGenerator.getKeyId(String.valueOf(record.getPaymentDate()), record.getBorrowId()), record);
        }
    }

    public BorrowFineRepo() {
        this(DEFAULT_FILE_PATH);
    }


    public TreeMap<String, BorrowFineRecord> getBorrowFineRecords() {return this.borrowFineRecords;}
    public BorrowFineRecord getBorrowFineRecord(String borrowId) {return this.borrowFineRecords.get(borrowId);}

    public ArrayList<BorrowFineRecord> getRecords(String id, MapInterface<String, BorrowFineRecord> map) {
            ArrayList<BorrowFineRecord> list = new ArrayList<>();
            for(MapInterface.Entry<String, BorrowFineRecord> record: map.entrySet()) {
                if(record.getKey().contains(id.toLowerCase().replaceAll("\\s+", ""))) {
                    list.add(record.getValue());
                }
            }
            return list;
        }

    public ArrayList<BorrowFineRecord> getFineDateRecords(LocalDate fineDate) {return getRecords(String.valueOf(fineDate), this.fineDateRecords);}
    public ArrayList<BorrowFineRecord> getFineAmountRecords(double fineAmount) {return getRecords(String.valueOf(fineAmount), this.fineAmountRecords);}
    public ArrayList<BorrowFineRecord> getFinePayedRecords(boolean finePayed) {return getRecords(String.valueOf(finePayed), this.finePayedRecords);}
    public ArrayList<BorrowFineRecord> getPaymentDateRecords(LocalDate paymentDate) {return getRecords(String.valueOf(paymentDate), this.paymentDateRecords);}

    public void addNewRecord(String borrowId, LocalDate fineDate, double fineAmount) {
        BorrowFineRecord record = new BorrowFineRecord(borrowId, new Fine(fineDate, fineAmount));
        this.borrowFineRecords.put(borrowId, record);
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
    public void saveChanges() {saveChanges(DEFAULT_FILE_PATH, this.borrowFineRecords);}
}
