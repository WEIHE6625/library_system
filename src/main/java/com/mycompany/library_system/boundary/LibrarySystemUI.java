package com.mycompany.library_system.boundary;

import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.repo.UserRepository;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.dao.UserDAO;
import java.util.Scanner;

/**
 *
 * @author wh
 */
public class LibrarySystemUI {
    private Scanner sc = new Scanner(System.in);
    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    public void start() {
        System.out.println("=== Welcome to the Library System ===");

        login();

        boolean exit = false;
        while (!exit) {
            showMainMenu();
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" ->  {
                    UserUI userUI = new UserUI(currentUser);
                    userUI.start();
                }
//                case "2" -> user_module();
//                case "3" -> handleBorrowModule();
//                case "4" -> {
//                    if (isAdmin()) handleReportModule();
//                    else System.out.println("Invalid choice!");
//                }
                case "0" -> exit = true;
                default -> System.out.println("Invalid choice!");
            }
        }

        System.out.println("Goodbye!");
    }

//    private void seedUsers() {
//        UserRepository.getUserMap().put("U0001", new User("U0001", "Admin One", "admin@library.com", "admin"));
//        UserRepository.getUserMap().put("U0002", new User("U0002", "User One", "user@library.com", "user"));
//    }

    private void login() {
        User user;
        do {
            System.out.print("Enter your user ID to login: ");
            String id = sc.nextLine();
            user = UserRepository.getUserMap().get(id);

            if (user != null) {
                currentUser = user;
                System.out.println("Welcome, " + user.getName() + " (" + user.getRole() + ")");
            } else {
                System.out.println("User not found. Try again.");
            }
        } while (user == null);
    }

    private boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase("admin");
    }

    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. User Module");
        System.out.println("2. Book Module");
        System.out.println("3. Borrow Book Module");
        if (isAdmin()) System.out.println("4. Report Module");
        System.out.println("0. Exit");
    }

    public static void main(String[] args) {
        TreeMap<String, Book> library = BookDAO.readFile("database/book.csv");
        System.out.println(library.get("BK102612").toString());
        new LibrarySystemUI().start();
    }
}
