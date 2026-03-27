/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.library_system;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BorrowBookUI;
import com.mycompany.library_system.boundary.LibrarySystemUI;
import com.mycompany.library_system.dao.BookBorrowFineDAO;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.Pagination;
import com.mycompany.library_system.utility.UserPrompt;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;

import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.entity.BookBorrowRepo;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.entity.Fine;
import com.mycompany.library_system.control.BookBorrowControl;
import com.mycompany.library_system.control.UserInfoController;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.math.IEEE754rUtils;

import com.mycompany.library_system.adt.ArrayList;
/**
 *
 * @author all
 */
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
public class Library_system {
    

    public static void main(String[] args) {
        new LibrarySystemUI().start();
    }
    

    
    
}
