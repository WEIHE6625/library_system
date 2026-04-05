package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;

import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.utility.UserPrompt;
import com.mycompany.library_system.utility.Pagination;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.control.UserInfoController;

public class UserUI {
    static Scanner sc = new Scanner(System.in);
    static Pagination<String, User> pagination = new Pagination<>();

    public static String inputUserID(MapInterface<String, User> userList) {
        boolean isValid = false;
        String id = "";
        do {
            System.out.print("Enter User ID*      >> ");
            id = sc.nextLine();
            if (!userList.containsKey(id)) {
                System.out.println("User with ID " + id + " does not exist. Please try again.");
            }else{
                isValid = true;
            }
        } while (!isValid);
        return id;
    }

    public static String inputUserName() {
        return UserPrompt.inputString("Enter User Name*   >> ", 100, false);
    }

    public static String inputUserEmail() {
        return UserPrompt.inputEmail("Enter User Email*   >> ", 40, false);
    }

    public static User.Gender inputUserGender() {
        return UserPrompt.inputEnum("Enter User Gender*   >> ", User.Gender.class, false);
    }

    public static int inputUserAge() {
        return UserPrompt.inputInteger("Enter User Age*   >> ", 0, 100);
    }

    public static User.Education inputUserEducation() {
        return UserPrompt.inputEnum("Enter User Education* >> ", User.Education.class, false);
    }

    public static User.UserRole inputUserRole() {
        return UserPrompt.inputEnum("Enter User Role* >> ", User.UserRole.class, false);
    }

    private static boolean isAdmin(User currentUser) {
        return currentUser.getRole() == User.UserRole.ADMIN;
    }

    @SuppressWarnings("unchecked")
    public static boolean menu(UserInfoController uController, User currentUser) {
        boolean isExit = false;
        boolean[] isLogout = {false}; // flag to signal outer logout

        pagination.clearSearchingWord();
        pagination.setDisplayTree(uController.getUserList());

        do {
            UIGenerator.clearScreen();

            if (isAdmin(currentUser)) {
                userListPage();
                isExit = UIGenerator.optionMenu(
                        new String[]{
                                "[1] Add New User",
                                "[2] View User Information",
                                "[3] Edit User Information",
                                "[4] Sort Users",
                                "[5] Search Users",
                                "[6] Delete User",
                                "[7] Display User Report",
                                " ",
                                "[8] <- Previous Page          [9] Next Page ->",
                                "[0] Back to Main Menu"
                        },
                        "Select an option >> ",
                        new Supplier[]{
                                () -> { uController.addUser(); return false; },
                                () -> { uController.viewUserAccount(); return false; },
                                () -> { uController.editUser(); return false; },
                                () -> { uController.sortUsers(); return false; },
                                () -> { uController.searchUsers(); return false; },
                                () -> {
                                    boolean deletedSelf = uController.deleteUser(currentUser);
                                    if (deletedSelf) {
                                        isLogout[0] = true; // signal outer logout
                                        return true;    // exit this menu
                                    }
                                    return false;       // otherwise stay in menu
                                },
                                () -> { uController.displayUserReport(); return false; },
                                () -> { pagination.previousPage(); return false; },
                                () -> { pagination.nextPage(); return false; },
                                () -> true // Back to Main Menu
                        }
                );
            } else { // MEMBER
                UIGenerator.titleBuilder("My Account Settings");
                isExit = UIGenerator.optionMenu(
                        new String[]{
                                "[1] Edit My Information",
                                "[2] View My Account",
                                "[3] Delete My Account",
                                "[0] Back to Main Menu"
                        },
                        "Select an option >> ",
                        new Supplier[]{
                                () -> { uController.editOwnAccount(currentUser); return false; },
                                () -> { uController.viewOwnAccount(currentUser); return false; },
                                () -> {
                                    boolean deletedSelf = uController.deleteOwnAccount(currentUser);
                                    if (deletedSelf) {
                                        isLogout[0] = true; // signal outer logout
                                        return true;    // exit this menu
                                    }
                                    return false;       // otherwise stay in menu
                                },
                                () -> true // Back to Main Menu
                        }
                );
            }

        } while (!isExit);
        return isLogout[0]; // outer loop now knows if user deleted account
    }

    public static void userListPage() {
        UIGenerator.titleBuilder("User List");
        System.out.println(pagination.getSearchingWord());
        ListInterface<User> displayList = pagination.getDisplayTree().values();

        if (displayList == null || displayList.isEmpty()) {
            System.out.println("User records not found.");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = pagination.getStartIndex();
        int endIndex = pagination.getEndIndex();

        System.out.printf("%-8s %-30s %-30s %-8s %-5s %-18S %-7s %-22s %-5s\n", "User ID", "User Name", "Email", "Gender", "Age", "Education", "Role", "Created Date & Time", "No. of Books Borrowing");
        System.out.printf("%-8s %-30s %-30s %-8s %-5s %-18s %-7s %-22s %-5s\n", "-------", "---------", "-----", "------", "---", "----------", "----", "------------------", "----------------------");

        for (int i = startIndex; i <= endIndex; i++) {
            int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);

            User u = displayList.get(actualIndex);
            System.out.printf("%-8s %-30s %-30s %-8s %-5s %-18s %-7s %-22s %-5s\n", u.getUserId(), u.getName(), u.getEmail(), u.getGender(), u.getAge(), u.getEducation(), u.getRole(), u.getCreatedAt(), u.getNumOfBooksBorrowing());
        }

        System.out.println("-".repeat(160));
        System.out.printf("Page: %d/%d\n\n", pagination.getCurrentPage(), pagination.getTotalPages());
    }

    public static Pagination<String, User> getPagination() {
        return pagination;
    }
}
