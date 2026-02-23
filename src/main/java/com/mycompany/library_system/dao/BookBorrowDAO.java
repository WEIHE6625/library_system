package com.mycompany.library_system.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.adt.TreeMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;
import java.time.LocalDate;

public class BookBorrowDAO {
    public static TreeMap<String, BookBorrowRecord> readFile(String filePath) {
        TreeMap<String, BookBorrowRecord> bookBorrowMap = new TreeMap<>();

        // CSVReader
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip Header
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try (ProgressBar pb = new ProgressBar("Loading Borrow Records : ", totalLines)) {

                // Read line by line
                while ((data = reader.readNext()) != null) {
                    pb.step();

                    if (data.length == 8) {
                        try {
                            String borrowId = data[0].trim();
                            String bookId = data[1].trim();
                            String userId = data[2].trim();
                            LocalDate borrowDate = LocalDate.parse(data[3].trim());
                            boolean alreadyReturn = Boolean.parseBoolean(data[4].trim());
                            LocalDate returnDate = null;
                            boolean lateReturn = false;
                            if(alreadyReturn) {
                                returnDate = LocalDate.parse(data[5].trim());
                                lateReturn = Boolean.parseBoolean(data[6].trim());
                            }
                            boolean isLostBook = Boolean.parseBoolean(data[7].trim()); 
                            
                            bookBorrowMap.put(borrowId, new BookBorrowRecord(borrowId, bookId, userId, borrowDate, alreadyReturn, returnDate, lateReturn, isLostBook));

                        } catch (Exception e) {
                            System.out.println("Skipping invalid row: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e1) {
            e1.printStackTrace();
        }
        return bookBorrowMap;
    }


    public static void writeFile(String filePath, TreeMap<String, BookBorrowRecord> bookBorrowMap) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
        
            
            String[] header = { "Borrow ID", "Book ID", "User ID", "Borrow Date (yyyy-MM-dd)", "Already Return ?", "Return Date (yyyy-MM-dd)", "Late Return ?", "Lost Book ?"};
            writer.writeNext(header, false);

            // Loop TreeMap and Write Data
            for (BookBorrowRecord record : bookBorrowMap.values()) {
                
                String[] data = {
                    record.getBorrowId(),
                    record.getBookId(),
                    record.getUserId(),
                    String.valueOf(record.getBorrowDate()),
                    String.valueOf(record.getAlreadyReturned()),
                    String.valueOf(record.getReturnDate()),
                    String.valueOf(record.getLateReturn()),
                    String.valueOf(record.getIsLostBook())
                };

                writer.writeNext(data, false);
            }
            
            System.out.println("Data successfully saved to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
