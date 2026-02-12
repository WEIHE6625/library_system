package com.mycompany.library_system.boundary;

import com.mycompany.library_system.repo.UserRepository;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.control.BookManager;
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
    private boolean isExit = false;

    public void start() {
        int login = 0; // 0 is no access, 1 is login, -1 is exit
        do{
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Welcome to the Library System");
            login = login();
            if(login == -1) return;
        }while(login == 0);

        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Welcome to the Library System");
            System.out.println("\n=========== Main Menu ===========");
            isExit = UIGenerator.optionMenu(
                new String[] {
                "[1] User Module",
                "[2] Book Module",
                "[3] Borrow Book Module",
                "[4] Report Module",
                "[0] Exit",
            },
                "Select an option >> ",
                new Runnable[]{
                    () -> {UserUI userUI = new UserUI(currentUser); userUI.start();},
                    () -> { BookManager bookManager = new BookManager(); BookUI.menu(bookManager);},
                    () -> {},
                    () -> {},
                    () -> { isExit = true; }  // Logout
                }
            );
        } while (!isExit);

        System.out.println("Goodbye!");
    }

    public static void seedUsers() {
       UserRepository.getUserMap().put("U0001", new User("U0001", "Admin One", "admin@library.com", "admin"));
       UserRepository.getUserMap().put("U0002", new User("U0002", "User One", "user@library.com", "user"));
   }

    public int login() {
        User user;
        do {
            System.out.println("[0] <-- Back");
            System.out.print("Enter your user ID to login: ");
            String id = sc.nextLine();

            if (id.equals("0")) {
                return -1;
            } else {
                user = UserRepository.getUserMap().get(id);

                if (user != null) {
                    currentUser = user;
                    System.out.println("Welcome, " + user.getName() + " (" + user.getRole() + ")");
                    return 1;
                } else {
                    System.out.println("User not found. Try again.");
                }
            }
        } while (user == null);
        return 0;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase("admin");
    }
}
