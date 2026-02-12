package com.mycompany.library_system.entity;

import java.time.YearMonth;

import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;

/**
 *
 * @author wh
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

    public Book(String id, String title, String author, String description, ListInterface<String> categories, String publisher, double price, int publishMonth, int publishYear) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = (author == null) ? "Unknown" : author;
        this.categories = (categories == null) ? new ArrayList<>() : categories;
        this.publisher = (publisher == null || publisher.isEmpty()) ? "Unknown" : publisher;
        this.price = price;
        this.publishDate = YearMonth.of(publishYear, publishMonth);
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
                "Categories  : " + categoriesToString() + "\n" +
                "Publisher   : " + publisher + "\n" +
                "Price:      : $" + price + "\n" +
                "Publish Date: " + publishDate;
    }

    public String categoriesToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= categories.size(); i++) {
            sb.append(categories.getEntry(i));
            if (i < categories.size()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}