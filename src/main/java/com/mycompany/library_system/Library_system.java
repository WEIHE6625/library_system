/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.library_system;

import com.mycompany.library_system.boundary.LibrarySystemUI;

/**
 *
 * @author all
 */
public class Library_system {

    public static void main(String[] args) {
        LibrarySystemUI.seedUsers();
        new LibrarySystemUI().start();
    }
}
