package com.mycompany.library_system.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.DataProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;

/**
 *
 * @author weihe
 */
public class BookDAO {

    public static TreeMap<String, Book> readFile(String filePath) {
        TreeMap<String, Book> bookList = new TreeMap<>();

        // CSVReader
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip Header
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try (ProgressBar pb = new ProgressBar("Loading Books : ", totalLines)) {

                // Read line by line
                while ((data = reader.readNext()) != null) {

                    // Update the progress bar
                    pb.step();

                    if (data.length >= 10) {
                        try {
                            // Parse Fields
                            // ID
                            String id = data[0].trim();

                            // Title
                            String title = data[1].trim();
                            
                            // Author
                            String author = DataProcessor.clean(data[2]);

                            // Description
                            String description = data[3].trim();

                            // Categories (Split by comma)
                            String rawCat = DataProcessor.clean(data[4]);
                            ListInterface<String> categories = new ArrayList<>(); 
                            if (!rawCat.isEmpty()) {
                                String[] cats = rawCat.split(",");
                                for (String c : cats) categories.add(c.trim());
                            }

                            // Publisher
                            String publisher = DataProcessor.clean(data[5]);

                            // Price
                            double price = 0.0;
                            try { price = Double.parseDouble(data[6]); } catch (NumberFormatException e) {}

                            // Month and Year
                            int month = 1;
                            try { month = Integer.parseInt(data[7]); } catch (NumberFormatException e) {}
                            int year = 1800;
                            try { year = Integer.parseInt(data[8]); } catch (NumberFormatException e) {}

                            // Quantity
                            int qtyAvailable = 0;
                            int qtyTotal = 0;
                            try { qtyAvailable = Integer.parseInt(data[9]); } catch (NumberFormatException e) {}
                            try { qtyTotal = Integer.parseInt(data[10]); } catch (NumberFormatException e) {}

                            // Add to Map
                            Book book = new Book(id, title, author, description, categories, publisher, price, month, year, qtyTotal, qtyAvailable);
                            bookList.put(id, book);

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
        return bookList;
    }


    public static void writeFile(String filePath, TreeMap<String, Book> bookTree) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            
            // Write Header
            String[] header = { "BookID", "Title", "Authors", "Description", "Category", "Publisher", "Price ($)", "Publish Month", "Publish Year","Available Quantity","Total Quantity" };
            writer.writeNext(header, false);

            // Loop TreeMap and Write Data
            for (Book book : bookTree.values()) {
                
                String[] data = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getDescription(),
                    book.getCategories().toString(),
                    book.getPublisher(),
                    String.valueOf(book.getPrice()),
                    String.valueOf(book.getPublishMonth()),
                    String.valueOf(book.getPublishYear()),
                    String.valueOf(book.getQtyAvailable()),
                    String.valueOf(book.getQtyTotal())
                };

                writer.writeNext(data, false);
            }
            
            System.out.println("Data successfully saved to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
