/**
 *
 * @author Isaac Kuan Way Houng
 */
package com.mycompany.library_system.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.entity.Fine;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.adt.TreeMap;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;

public class BookBorrowFineDAO {
    public static TreeMap<String, BorrowFineRecord> readFile(String filePath) {
        TreeMap<String, BorrowFineRecord> borrowFineRecords = new TreeMap<>();

        // CSVReader
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip Header
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try (ProgressBar pb = new ProgressBar("Loading Fine Records : ", totalLines)) {

                // Read line by line
                while ((data = reader.readNext()) != null) {
                    pb.step();

                    if (data.length == 6) {
                        try {
                            String borrowId = data[0].trim();
                            String userId = data[1].trim();
                            LocalDate fineDate = LocalDate.parse(data[2].trim());
                            double fineAmount = Double.parseDouble(data[3].trim()); 
                            boolean finePayed = Boolean.parseBoolean(data[4].trim());
                            LocalDate paymentDate = null;

                            if(finePayed == true) {
                                paymentDate = LocalDate.parse(data[5].trim());
                            }

                            borrowFineRecords.put(borrowId, new BorrowFineRecord(borrowId, userId, new Fine(fineDate, fineAmount), finePayed, paymentDate));


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
        return borrowFineRecords;
    }


    public static void writeFile(String filePath, TreeMap<String, BorrowFineRecord> borrowFineMap) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {            
            String[] header = { "Borrow ID", "User ID", "Fine Issued Date (yyyy-MM-dd)", "Fine amount (RM)", "Fine Payed ?", "Payment Date (yyyy-MM-dd)"};
            writer.writeNext(header, false);

            // Loop TreeMap and Write Data
            for (BorrowFineRecord record : borrowFineMap.values()) {
                
                String[] data = {
                    record.getBorrowId(),
                    record.getUserId(),
                    String.valueOf(record.getFine().getFineDate()),
                    String.valueOf(record.getFine().getFineAmount()),
                    String.valueOf(record.getFinePayed()),
                    String.valueOf(record.getPaymentDate())
                };

                writer.writeNext(data, false);
            }
            
            System.out.println("Data successfully saved to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
