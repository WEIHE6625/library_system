/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.library_system;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BorrowBookUI;
import com.mycompany.library_system.boundary.LibrarySystemUI;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.entity.BookBorrowRepo;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.control.BookBorrowControl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.mycompany.library_system.adt.ArrayList;
/**
 *
 * @author all
 */
public class Library_system {
    

    public static void main(String[] args) {
        new LibrarySystemUI().start();
    }
    

    
    
}
