package com.mycompany.library_system.boundary;

import com.mycompany.library_system.control.UserInfoController;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.entity.User;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 *
 * @author all
 */
public class LibrarySystemUI {
    private Scanner sc = new Scanner(System.in);
    UserInfoController userInfoController = new UserInfoController();

    public void start() {
        while (true) {   // Entire program loop

            User currentUser = null;

            // ===== LOGIN LOOP =====
            while (currentUser == null) {

                UIGenerator.clearScreen();
                UIGenerator.titleBuilder("Welcome to the Library System");

                System.out.println("[0] Exit Program");
                System.out.print("Enter your user ID to login: ");
                String id = sc.nextLine();

                if ("0".equals(id)) {
                    System.out.println("Goodbye!");
                    return;   // EXIT PROGRAM
                }

                currentUser = userInfoController.login(id);

                if (currentUser == null) {
                    UIGenerator.pressEnterToContinue();
                }
            }

            // ===== MAIN MENU LOOP =====
            boolean isLogout = false;

            while (!isLogout) {
                User sessionUser = currentUser;

                UIGenerator.clearScreen();
                UIGenerator.titleBuilder("Welcome, " + sessionUser.getName());

                isLogout = UIGenerator.optionMenu(
                        new String[]{
                                "[1] User Module",
                                "[2] Book Module",
                                "[3] Borrow Module",
                                "[4] Report Module",
                                "[0] Logout"
                        },
                        "Select an option >> ",
                        new Supplier[] {
                                () -> UserUI.menu(userInfoController, sessionUser),
                                () -> {BookUI.menu(); return false; },
                                () -> {BorrowBookUI.menu(sessionUser); return false; },
                                () -> { return false; },
                                () -> { return false; }
                        }
                );
            }

            // When logout happens, loop restarts at login
            System.out.println("Logged out successfully.");
            UIGenerator.pressEnterToContinue();
        }
    }
}
