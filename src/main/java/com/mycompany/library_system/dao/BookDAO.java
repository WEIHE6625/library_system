package com.mycompany.library_system.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.utility.DataProcessor;
import com.opencsv.CSVWriter;

import me.tongfei.progressbar.ProgressBar;

/**
 *
 * @author wh
 */
public class BookDAO {

    public static TreeMap<String, Book> readFile(String filePath) {
        TreeMap<String, Book> bookList = new TreeMap<>();

        // Setup CSV Reader
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip Header

            int totalLines = DataProcessor.lineCounter(filePath);

            try (ProgressBar pb = new ProgressBar("Loading Books : ", totalLines)) {

                // Read line by line
                while ((line = br.readLine()) != null) {

                    // Update the progress bar
                    pb.step();
                    // TimeUnit.MILLISECONDS.sleep(50); 

                    String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                    if (data.length >= 8) {
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

                            // Add to Map
                            Book book = new Book(id, title, author, description, categories, publisher, price, month, year);
                            bookList.put(id, book);

                        } catch (Exception e) {
                            System.out.println("Skipping invalid row: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookList;
    }


    public static void writeFile(String filePath, TreeMap<String, Book> library) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            
            // Write Header
            String[] header = { "BookID", "Title", "Authors", "Description", "Category", "Publisher", "Price ($)", "Publish Month", "Publish Year" };
            writer.writeNext(header, false);

            // Loop TreeMap and Write Data
            for (Book book : library.values()) {
                
                String[] data = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getDescription(),
                    book.categoriesToString(),
                    book.getPublisher(),
                    String.valueOf(book.getPrice()),
                    String.valueOf(book.getPublishMonth()),
                    String.valueOf(book.getPublishYear())
                };

                writer.writeNext(data, false);
            }
            
            System.out.println("Data successfully saved to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
