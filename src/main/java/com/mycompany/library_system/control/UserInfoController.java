package com.mycompany.library_system.control;

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

    public User editUser() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Edit Existing User");
        String id = UserUI.inputUserID(userTree);

        System.out.println("\nEditing User with ID: " + id);
        System.out.println(userTree.get(id).toString());

        User oldUser = userTree.get(id);
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
                    new Runnable[]{
                            () -> userTree.get(id).setName(UserUI.inputUserName()),
                            () -> userTree.get(id).setEmail(UserUI.inputUserEmail()),
                            () -> userTree.get(id).setGender(UserUI.inputUserGender()),
                            () -> userTree.get(id).setAge(UserUI.inputUserAge()),
                            () -> userTree.get(id).setEducation(UserUI.inputUserEducation()),
                            () -> userTree.get(id).setRole(UserUI.inputUserRole()),
                            () -> {}  // Back to Main Menu
                    }
            );
        }while (!isExit);

        System.out.println("Edited User with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
        return oldUser;
    }

    public boolean deleteUser(User currentUser) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete Existing User");
        String id = UserUI.inputUserID(userTree);
        User user = userTree.remove(id);

        if (user != null) {
            System.out.println("User with ID " + id + " has been deleted.");
            saveToFile();

            if (id.equals(currentUser.getUserId())) {
                System.out.println("You deleted your own account. Logging out...");
                UIGenerator.pressEnterToContinue();
                return true;
            }
        } else {
            System.out.println("User with ID " + id + " not found.");
        }
        UIGenerator.pressEnterToContinue();
        return false;
    }

//    public void viewUser() {
//        UIGenerator.clearScreen();
//        UIGenerator.titleBuilder("View User Details");
//        String id = UserUI.inputUserID(userTree);
//        User user = userTree.get(id);
//        System.out.println(user.toString());
//        UIGenerator.pressEnterToContinue();
//    }

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
                    new Runnable[]{
                            () -> currentUser.setName(UserUI.inputUserName()),
                            () -> currentUser.setEmail(UserUI.inputUserEmail()),
                            () -> currentUser.setGender(UserUI.inputUserGender()),
                            () -> currentUser.setAge(UserUI.inputUserAge()),
                            () -> currentUser.setEducation(UserUI.inputUserEducation()),
                            () -> {}
                    }
            );
        } while (!isExit);

        saveToFile();
    }

    public void deleteOwnAccount(User currentUser) {

        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete My Account");

        userTree.remove(currentUser.getUserId());

        System.out.println("Your account has been deleted.");
        saveToFile();

        UIGenerator.pressEnterToContinue();
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
                    new Runnable[] {
                            () -> { UserUI.getPagination().setDisplayList(userTree.values()); },
                            () -> { UserUI.getPagination().setDisplayList(userNameTree.values()); },
                            () -> { UserUI.getPagination().setDisplayList(userEmailTree.values()); },
                            () -> { UserUI.getPagination().setDisplayList(userAgeTree.values()); },
                            () -> { UserUI.getPagination().setDisplayList(userEducationTree.values()); },
                            () -> { UserUI.getPagination().setDisplayList(userCreatedDateTimeTree.values()); },
                            () -> { UserUI.getPagination().setDisplayList(userNumOfBooksBorrowing.values()); },
                            () -> {}
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
                    new Runnable[] {
                            () -> { UserUI.getPagination().setIsAscending(true); },
                            () -> { UserUI.getPagination().setIsAscending(false); },
                            () -> { }  // Back
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
                    new Runnable[] {
                            () -> { searchOption[0] = -1; searchTree = userTree; },
                            () -> { searchOption[0] = 0; searchTree = userNameTree; },
                            () -> { searchOption[0] = 1; searchTree = userEmailTree; },
                            () -> { searchOption[0] = 2; searchTree = userAgeTree; },
                            () -> { searchOption[0] = 3; searchTree = userEducationTree; },
                            () -> { searchOption[0] = 4; searchTree = userNumOfBooksBorrowing; },
                            () -> {}
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
