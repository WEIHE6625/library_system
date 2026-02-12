package com.mycompany.library_system.boundary;

import com.mycompany.library_system.control.BookManager;

/**
 *
 * @author wh
 */
public class MainUI {
    public static void mainMenu() {
        BookManager bookManager = new BookManager();
        BookUI.menu(bookManager);
    }
}
