package com.mycompany.library_system.boundary;

import com.mycompany.library_system.control.UserService;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.adt.TreeMap;
import java.util.Scanner;

public class UserUI {
    private TreeMap<String, User> userMap;
    private Scanner sc = new Scanner(System.in);
    private User currentUser;
    private UserService service = new UserService();

    public UserUI(User currentUser) {
        this.currentUser = currentUser;
    }

    public void start() {
        boolean exit = false;

        while (!exit) {
            showUserMenu();
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> viewProfileUI();
                case "2" -> updateProfileUI();
                case "3" -> deleteAccountUI();
                case "4" -> {
                    if (isAdmin()) addUserUI();
                    else System.out.println("Only admin allow to add user!");
                }
                case "0" -> exit = true;
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private boolean isAdmin() {
        return currentUser.getRole().equalsIgnoreCase("admin");
    }

    private void showUserMenu() {
        System.out.println("\n--- User Module ---");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Delete Account");
        if (isAdmin()) System.out.println("4. Add User");
        System.out.println("0. Back to Main Menu");
    }

    // --- User actions ---

    // View profile (for normal user) or view any user (admin)
    private void viewProfileUI() {
        if (isAdmin()) {
            System.out.print("Enter User ID to view (0 to cancel): ");
            String userId = sc.nextLine();

            if (userId.equals("0")) return;

            User user = service.getUser(userId);
            if (user != null) {
                System.out.println(user);
            } else {
                System.out.println("User not found!");
            }
        } else {
            System.out.println(currentUser);
        }
    }

    // Update profile (normal user can update self, admin can update anyone)
    private void updateProfileUI() {
        try {
            String userId;

            if (isAdmin()) {
                System.out.print("Enter User ID to update (0 to cancel): ");
                userId = sc.nextLine();
                if (userId.equals("0")) return;
            } else {
                userId = currentUser.getUserId();
            }

            User user = service.getUser(userId);
            if (user == null) {
                System.out.println("User not found");
                return;
            }

            int choice;
            do {
                System.out.println("\n=== Update User Menu ===");
                System.out.println("1. Update Name");
                System.out.println("2. Update Email");
                System.out.println("0. Save & Exit");
                System.out.print("Choose option: ");

                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        System.out.print("Enter new name: ");
                        user.setName(sc.nextLine());
                        break;
                    case 2:
                        System.out.print("Enter new email: ");
                        user.setEmail(sc.nextLine());
                        break;
//                    case 3:
//                        if (!isAdmin()) {
//                            System.out.println("❌ Only admin can change role");
//                        } else {
//                            System.out.print("Enter new role: ");
//                            user.setRole(sc.nextLine());
//                        }
//                        break;
                    case 0:
                        service.updateUser(user);
                        System.out.println("✅ User updated successfully");
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            } while (choice != 0);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Delete account (normal user can delete self, admin can delete anyone)
    private void deleteAccountUI() {
        try {
            String userId;
            if (isAdmin()) {
                System.out.print("Enter User ID to delete (0 to cancel): ");
                userId = sc.nextLine();

                if (userId.equals("0")) {
                    System.out.println("Delete cancelled.");
                    return;
                }
            } else {
                userId = currentUser.getUserId();
                System.out.print("Are you sure you want to delete your account? (Y/N): ");
                String confirm = sc.nextLine();
                if (!confirm.equalsIgnoreCase("Y")) return;
            }

            service.deleteUser(userId);
            System.out.println("User deleted successfully!");

            // Self-delete → logout
            if (!isAdmin() && userId.equals(currentUser.getUserId())) {
                System.out.println("Your account has been deleted. Logging out...");
                System.exit(0);
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add user (admin only)
    private void addUserUI() {
        try {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Role (admin/user): ");
            String role = sc.nextLine();

            service.addUser(name, email, role);

            // SUCCESS MESSAGE
            System.out.println("\nUser added successfully!");
            System.out.println("Press Enter to return to User Menu...");
            sc.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("\nFailed to add user: " + e.getMessage());
            System.out.println("Press Enter to try again...");
            sc.nextLine();
        }
    }
}
