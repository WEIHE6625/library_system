package com.mycompany.library_system.entity;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;

/**
 *
 * @author weihe
 */
public class Book {
    
    private String id;
    private String title;
    private String description;
    private String author;
    private ListInterface<String> categories;
    private String publisher;
    private double price;
    private YearMonth publishDate;
    private int qtyTotal;     // Total copies owned by library
    private int qtyAvailable; // Copies currently on shelf

    public Book(String id, String title, String author, String description, ListInterface<String> categories, String publisher, double price, int publishMonth, int publishYear, int qtyTotal, int qtyAvailable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = (author == null) ? "Unknown" : author;
        this.categories = (categories == null) ? new ArrayList<>() : categories;
        this.publisher = (publisher == null || publisher.isEmpty()) ? "Unknown" : publisher;
        this.price = price;
        this.publishDate = YearMonth.of(publishYear, publishMonth);
        this.qtyTotal = qtyTotal;
        this.qtyAvailable = qtyAvailable;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAuthor() { return author; }
    public ListInterface<String> getCategories() { return categories; }
    public String getPublisher() { return publisher; }
    public double getPrice() { return price; }
    // Get the publish date in different type
    public YearMonth getPublishDate() { return publishDate; }
    public int getPublishYear() { return publishDate.getYear(); }
    public int getPublishMonth() { return publishDate.getMonthValue(); }

    public int getQtyTotal() { return qtyTotal; }
    public int getQtyAvailable() { return qtyAvailable; }
    

    public String setTitle(String title) { return this.title = title; }
    public String setDescription(String description) { return this.description = description; }
    public String setAuthor(String author) { return this.author = author; }

    public boolean setCategories(ListInterface<String> categories) { 
        this.categories = categories;
        return true;
    }

    public String setPublisher(String publisher) { return this.publisher = publisher; }
    public double setPrice(double price) { return this.price = price; }
    public YearMonth setPublishDate(int year, int month) { 
        this.publishDate = YearMonth.of(year, month);
        return this.publishDate;
    }

    public void addQty(int qty) {
        qtyTotal += qty;
        qtyAvailable += qty;
    }

    public boolean reduceQty(int qty) {
        
        if((qtyAvailable - qty) <= 0) {
            qtyTotal -= qty;
            qtyAvailable -= qty;
            return true;
        }else{
            return false;
        }
    }

    public boolean hasCategory(String category) {
        if (categories.contains(category)) return true;
        else return false;
    }

    @Override
    public String toString() {
        return  "BookID      : " + id + "\n" +
                "Title       : " + title + "\n" +
                "Author      : " + author + "\n" +
                "Description : " + description + "\n" +
                "Categories  : " + categories.toString() + "\n" +
                "Publisher   : " + publisher + "\n" +
                "Price:      : $" + price + "\n" +
                "Publish Date: " + publishDate.format(DateTimeFormatter.ofPattern("MM-yyyy")) + "\n" +
                "-".repeat(30) + "\n" +
                "Available Quantity : " + qtyAvailable + "\n" +
                "Total Quantity     : " + qtyTotal;
    }
}