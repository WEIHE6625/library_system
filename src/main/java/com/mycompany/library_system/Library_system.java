/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.library_system;

import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;

/**
 *
 * @author all
 */
public class Library_system {

    public static void main(String[] args) {
        TreeMap<String, Book> library = BookDAO.readFile("database/book.csv");
        System.out.println(library.get("BK102612").toString());
    }  
}
