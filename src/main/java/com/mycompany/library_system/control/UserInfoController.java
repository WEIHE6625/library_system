package com.mycompany.library_system.control;

import java.util.function.Supplier;
import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.UserUI;
import com.mycompany.library_system.dao.UserDAO;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.DateUtil;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

public class UserInfoController {
    private TreeMap<String, User> userTree;
    private TreeMap<String, User> userNameTree;
    private TreeMap<String, User> userEmailTree;
    private TreeMap<String, User> userAgeTree;
    private TreeMap<String, User> userEducationTree;
    private TreeMap<String, User> userCreatedDateTimeTree;
    private TreeMap<String, User> userNumOfBooksBorrowing;
    private TreeMap<String, User> searchTree = new TreeMap<>();
    private static final String USER_FILE_PATH = "database/users.csv";

    public UserInfoController() {
        this.userTree = UserDAO.readFile(USER_FILE_PATH);
        this.userNameTree = new TreeMap<>();
        this.userEmailTree = new TreeMap<>();
        this.userAgeTree = new TreeMap<>();
        this.userEducationTree = new TreeMap<>();
        this.userCreatedDateTimeTree = new TreeMap<>();
        this.userNumOfBooksBorrowing = new TreeMap();

        // Loop through every book in the main bookTree
        for (User user : userTree.values()) {
            // Add the book to each tree
            userNameTree.put(IdGenerator.getKeyId(user.getName(), user.getUserId()), user);
            userEmailTree.put(IdGenerator.getKeyId(user.getEmail(), user.getUserId()), user);
            userAgeTree.put(IdGenerator.getKeyId(user.getAge(), user.getUserId()), user);
            userEducationTree.put(IdGenerator.getKeyId(user.getEducation(), user.getUserId()), user);
            userCreatedDateTimeTree.put(IdGenerator.getKeyId(user.getCreatedAt(), user.getUserId()), user);
            userNumOfBooksBorrowing.put(IdGenerator.getKeyId(user.getNumOfBooksBorrowing(), user.getUserId()), user);
        }
    }

    public TreeMap<String, User> getUserList() {
        return userTree;
    }

    public User get(String id) {
        return userTree.get(id);
    }

    public User login(String id) {
        if ("0".equals(id)) {
            return null;  // signal program exit
        }
        User currentUser = get(id);

        if (currentUser == null) {
            System.out.println("User not found.");
            return null;
        }
        System.out.println("Welcome, " +
                currentUser.getName() +
                " (" + currentUser.getRole() + ")");
        return currentUser;
    }

    public void addUser() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Add New User");
        String id = IdGenerator.generateId(userTree, "U", 4);
        String name = UserUI.inputUserName();
        String email = UserUI.inputUserEmail();
        User.Gender gender = UserUI.inputUserGender();
        int age = UserUI.inputUserAge();
        User.Education education = UserUI.inputUserEducation();
        User.UserRole role = UserUI.inputUserRole();
        String createdAt = DateUtil.getToday();

        User newUser = new User(id, name, email, gender, age, education, role, createdAt, 0);
        User user = userTree.put(id, newUser);

        userNameTree.put(IdGenerator.getKeyId(newUser.getName(), id), newUser);
        userEmailTree.put(IdGenerator.getKeyId(newUser.getEmail(), id), newUser);
        userAgeTree.put(IdGenerator.getKeyId(newUser.getAge(), id), newUser);
        userEducationTree.put(IdGenerator.getKeyId(newUser.getEducation(), id), newUser);
        userCreatedDateTimeTree.put(IdGenerator.getKeyId(newUser.getCreatedAt(), id), newUser);
        userNumOfBooksBorrowing.put(IdGenerator.getKeyId(newUser.getNumOfBooksBorrowing(), id), newUser);
        
        UserUI.getPagination().setDisplayList(userTree.values());
        UserUI.getPagination().setCurrentPage(1);
        
        System.out.println("Added user with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
    }

    public void editUser() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Edit Existing User");
        String id = UserUI.inputUserID(userTree);

        System.out.println("\nEditing User with ID: " + id);
        System.out.println(userTree.get(id).toString());

        boolean isExit = false;
        do {
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Change User Name",
                            "[2] Change User Email",
                            "[3] Change User Gender",
                            "[4] Change User Age",
                            "[5] Change User Education",
                            "[6] Change User Role",
                            "[0] Back to Main Menu"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { userTree.get(id).setName(UserUI.inputUserName()); return false; },
                            () -> { userTree.get(id).setEmail(UserUI.inputUserEmail()); return false; },
                            () -> { userTree.get(id).setGender(UserUI.inputUserGender()); return false; },
                            () -> { userTree.get(id).setAge(UserUI.inputUserAge()); return false; },
                            () -> { userTree.get(id).setEducation(UserUI.inputUserEducation()); return false; },
                            () -> { userTree.get(id).setRole(UserUI.inputUserRole()); return false; },
                            () -> true
                    }
            );
        }while (!isExit);

        System.out.println("Edited User with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
    }

    public boolean deleteUser(User currentUser) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete Existing User");
        String id = UserUI.inputUserID(userTree);
        User user = userTree.get(id);

        if (user == null) {
            System.out.println("User with ID " + id + " not found.");
            UIGenerator.pressEnterToContinue();
            return false;
        }

        char confirm = UserPrompt.inputChar("Are you sure you want to delete user " + id + "? (Y/N): ", 'Y', 'N');
        if (confirm != 'Y') {
            System.out.println("User deletion canceled.");
            UIGenerator.pressEnterToContinue();
            return false;
        }

        // Proceed with deletion
        userTree.remove(id);
        System.out.println("User with ID " + id + " has been deleted.");

        UserUI.getPagination().setDisplayList(userTree.values());
        UserUI.getPagination().setCurrentPage(1);

        saveToFile();

        // Check if admin deleted themselves
        if (id.equals(currentUser.getUserId())) {
            System.out.println("You deleted your own account. Logging out...");
            UIGenerator.pressEnterToContinue();
            return true;
        }

        UIGenerator.pressEnterToContinue();
        return false;
    }


    public void viewOwnAccount(User currentUser) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("My Account Details");
        System.out.println(currentUser);
        UIGenerator.pressEnterToContinue();
    }

    public void editOwnAccount(User currentUser) {

        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Edit My Account");

        boolean isExit = false;

        do {
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Change Name",
                            "[2] Change Email",
                            "[3] Change Gender",
                            "[4] Change Age",
                            "[5] Change Education",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { currentUser.setName(UserUI.inputUserName()); return false; },
                            () -> { currentUser.setEmail(UserUI.inputUserEmail()); return false; },
                            () -> { currentUser.setGender(UserUI.inputUserGender()); return false; },
                            () -> { currentUser.setAge(UserUI.inputUserAge()); return false; },
                            () -> { currentUser.setEducation(UserUI.inputUserEducation()); return false; },
                            () -> true
                    }
            );
        } while (!isExit);

        saveToFile();
    }

    public boolean deleteOwnAccount(User currentUser) {

        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete My Account");

        char confirm = UserPrompt.inputChar("Are you sure you want to delete your account? (Y/N): ", 'Y', 'N');

        if (confirm != ('Y')) {  // if user does not confirm
            System.out.println("Account deletion canceled.");
            UIGenerator.pressEnterToContinue();
            return false;  // do nothing
        }

        userTree.remove(currentUser.getUserId());

        System.out.println("Your account has been deleted.");

        UserUI.getPagination().setDisplayList(userTree.values());
        UserUI.getPagination().setCurrentPage(1);

        saveToFile();

        UIGenerator.pressEnterToContinue();
        return true;
    }

    public void sortUsers() {
        boolean isBack = false;

        do {
            isBack = false;

            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("View Sorted Books");
            isBack = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Sort by User ID",
                            "[2] Sort by User Name",
                            "[3] Sort by User Email",
                            "[4] Sort by User Age",
                            "[5] Sort by User Education",
                            "[6] Sort by User Created Date & Time",
                            "[7] Sort by Number of Books Borrowing",
                            "[0] Back to Main Menu"
                    },
                    "Select a sorting option >> ",
                    new Supplier[] {
                            () -> { UserUI.getPagination().setDisplayList(userTree.values()); return false; },
                            () -> { UserUI.getPagination().setDisplayList(userNameTree.values()); return false; },
                            () -> { UserUI.getPagination().setDisplayList(userEmailTree.values()); return false; },
                            () -> { UserUI.getPagination().setDisplayList(userAgeTree.values()); return false; },
                            () -> { UserUI.getPagination().setDisplayList(userEducationTree.values()); return false; },
                            () -> { UserUI.getPagination().setDisplayList(userCreatedDateTimeTree.values()); return false; },
                            () -> { UserUI.getPagination().setDisplayList(userNumOfBooksBorrowing.values()); return false; },
                            () -> true
                    }
            );

            if(isBack) {
                return;
            }

            isBack = UIGenerator.optionMenu(
                    new String[]{
                            "\n[1] Ascending Order",
                            "[2] Descending Order",
                            "[0] Back"
                    },
                    "Select a sorting option >> ",
                    new Supplier[] {
                            () -> { UserUI.getPagination().setIsAscending(true); return false; },
                            () -> { UserUI.getPagination().setIsAscending(false); return false; },
                            () -> false
                    }
            );

        } while (isBack);
        UserUI.getPagination().setCurrentPage(1);
    }

    public void searchUsers() {
        boolean isBack = false;
        final int searchOption[] = {0,0};

        do {
            isBack = false;

            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("View Sorted Users");
            isBack = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Show All Users",
                            "[2] Search by User Name",
                            "[3] Search by Email",
                            "[4] Search by Age",
                            "[5] Search by Education",
                            "[6] Search by Number Of Books Borrowing",
                            "[0] Back to Main Menu"
                    },
                    "Select a searching option >> ",
                    new Supplier[] {
                            () -> { searchOption[0] = -1; searchTree = userTree; return false; },
                            () -> { searchOption[0] = 0; searchTree = userNameTree; return false; },
                            () -> { searchOption[0] = 1; searchTree = userEmailTree; return false; },
                            () -> { searchOption[0] = 2; searchTree = userAgeTree; return false; },
                            () -> { searchOption[0] = 3; searchTree = userEducationTree; return false; },
                            () -> { searchOption[0] = 4; searchTree = userNumOfBooksBorrowing; return false; },
                            () -> true
                    }
            );

            // if(isBack) {
            //     return;
            // }

            // isBack = UIGenerator.optionMenu(
            //     new String[]{
            //         "\n[1] Actual Value",
            //         "[2] Range",
            //         " ",
            //         "[0] Back"
            //     },
            //     "Select a sorting option >> ",
            //     new Runnable[] {
            //         () -> { searchOption[1] = 0; },
            //         () -> { searchOption[1] = 1; },
            //         () -> { }  // Back
            //     }
            // );

        } while (isBack);

        String search;
        ListInterface<User> searchList = new ArrayList<>();
        // If user search String
        if(searchOption[0] == -1) {
            searchList = userTree.values();
        }else{
            if(searchOption[0] == 0 || searchOption[0] == 1 || searchOption[0] == 3 ) {
                search = UserPrompt.inputString("Enter the word seaching : ", 999, false).toLowerCase().replaceAll("\\s+", "");
            }
            else {
                search = IdGenerator.getKeyId(UserPrompt.inputInteger("Enter the number seaching : ", 0, 999), "");
//                search = Integer.toString(UserPrompt.inputInteger("Enter the number seaching : ", 0, 999));
            }
            UserUI.getPagination().setSearchingWord(search);
            searchList = searchTree.getRange(search, search+Character.MAX_VALUE);
        }

        UserUI.getPagination().setDisplayList(searchList);
        UserUI.getPagination().setCurrentPage(1);
    }

    public void saveToFile() {
        UserDAO.writeFile(USER_FILE_PATH, userTree);
    }
}
