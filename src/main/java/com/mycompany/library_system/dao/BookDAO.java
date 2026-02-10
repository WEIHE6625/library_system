package com.mycompany.library_system.dao;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Month;

import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.entity.Book;

/**
 *
 * @author wh
 */
public class BookDAO {

    public static TreeMap<String, Book> readFile(String filePath) {
        TreeMap<String, Book> library = new TreeMap<>();

        // Setup CSV Reader
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Skip Header

            // Read line by line
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                // if(looptime == 102611 || looptime == 102700){
                //     System.out.println("Debug Breakpoint Reached at line: " + looptime);
                // }
                if (data.length >= 8) {
                    try {
                        // Parse Fields
                        // ID
                        String id = data[0].trim();

                        // Title
                        String title = data[1].trim();
                        
                        // Author
                        String author = data[2].trim();

                        // Description
                        String description = data[3].trim();

                        // Categories (Split by comma)
                        String rawCat = data[4].trim();
                        ListInterface<String> categories = new ArrayList<>(); 
                        if (!rawCat.isEmpty()) {
                            String[] cats = rawCat.split(",");
                            for (String c : cats) categories.add(c.trim());
                        }

                        // Publisher
                        String publisher = data[5].trim();

                        // Price
                        double price = 0.0;
                        try { price = Double.parseDouble(data[6]); } catch (NumberFormatException e) {}

                        // Month and Year
                        String monthString = data[7].trim();
                        int monthNumber = 1;
                        try {
                            Month month = Month.valueOf(monthString.toUpperCase());
                            monthNumber = month.getValue(); // getValue() returns 1-12
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid month name: " + monthString);
                        }
                        int year = 1800;
                        try { year = Integer.parseInt(data[8]); } catch (NumberFormatException e) {}

                        // Add to Map
                        Book book = new Book(id, title, author, description, categories, publisher, price, monthNumber, year);
                        library.put(id, book);

                    } catch (Exception e) {
                        System.out.println("Skipping invalid row: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return library;
    }


    public static void writeFile(String filePath, TreeMap<String, Book> library) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            
            // Write Header
            String[] header = { "BookID", "Title", "Authors", "Description", "Category", "Publisher", "Price ($)", "Publish Month", "Publish Year" };
            writer.writeNext(header);

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

                writer.writeNext(data);
            }
            
            System.out.println("Data successfully saved to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
