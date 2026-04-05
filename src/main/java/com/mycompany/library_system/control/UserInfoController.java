package com.mycompany.library_system.control;

import java.util.function.Supplier;
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
    private UserReportGenerator userReportGenerator;
    private static final String USER_FILE_PATH = "database/users.csv";

    public UserInfoController() {
        this.userTree = UserDAO.readFile(USER_FILE_PATH);
        this.userNameTree = new TreeMap<>();
        this.userEmailTree = new TreeMap<>();
        this.userAgeTree = new TreeMap<>();
        this.userEducationTree = new TreeMap<>();
        this.userCreatedDateTimeTree = new TreeMap<>();
        this.userNumOfBooksBorrowing = new TreeMap<>();
        this.userReportGenerator = new UserReportGenerator(userTree);

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
        userTree.put(id, newUser);

        userNameTree.put(IdGenerator.getKeyId(newUser.getName(), id), newUser);
        userEmailTree.put(IdGenerator.getKeyId(newUser.getEmail(), id), newUser);
        userAgeTree.put(IdGenerator.getKeyId(newUser.getAge(), id), newUser);
        userEducationTree.put(IdGenerator.getKeyId(newUser.getEducation(), id), newUser);
        userCreatedDateTimeTree.put(IdGenerator.getKeyId(newUser.getCreatedAt(), id), newUser);
        userNumOfBooksBorrowing.put(IdGenerator.getKeyId(newUser.getNumOfBooksBorrowing(), id), newUser);

        UserUI.getPagination().setDisplayTree(userTree);
        UserUI.getPagination().setCurrentPage(1);

        System.out.println("Added user with ID: " + id);
        saveToFile();
        UIGenerator.pressEnterToContinue();
    }

    public void viewUserAccount() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("View Account Details");
        String id = UserUI.inputUserID(userTree);
        User user = userTree.get(id);
        System.out.println(user);
        UIGenerator.pressEnterToContinue();
    }

    public void editUser() {
        UIGenerator.clearScreen();
        String id = UserUI.inputUserID(userTree);
        User user = userTree.get(id);
        boolean isExit = false;

        do {
            UIGenerator.titleBuilder("Edit Existing User");
            System.out.println("\nEditing User with ID: " + id);
            System.out.println(userTree.get(id).adminEditToString());
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
                            () -> {
                                String oldName = user.getName();
                                String newName = UserUI.inputUserName();
                                user.setName(newName);
                                userNameTree.remove(IdGenerator.getKeyId(oldName, id));
                                userNameTree.put(IdGenerator.getKeyId(newName, id), user);
                                return false;
                            },
                            () -> {
                                String oldEmail = user.getEmail();
                                String newEmail = UserUI.inputUserEmail();
                                user.setEmail(newEmail);
                                userEmailTree.remove(IdGenerator.getKeyId(oldEmail, id));
                                userEmailTree.put(IdGenerator.getKeyId(newEmail, id), user);
                                return false;
                            },
                            () -> { userTree.get(id).setGender(UserUI.inputUserGender()); return false; },
                            () -> {
                                String oldAge = String.valueOf(user.getAge());
                                int newAge = UserUI.inputUserAge();
                                user.setAge(newAge);
                                userAgeTree.remove(IdGenerator.getKeyId(oldAge, id));
                                userAgeTree.put(IdGenerator.getKeyId(String.valueOf(newAge), id), user);

                                return false;
                            },
                            () -> {
                                String oldEducation = String.valueOf(user.getEducation());
                                User.Education newEducation = UserUI.inputUserEducation();
                                user.setEducation(newEducation);
                                userEducationTree.remove(IdGenerator.getKeyId(oldEducation, id));
                                userEducationTree.put(IdGenerator.getKeyId(String.valueOf(newEducation), id), user);

                                return false;
                            },
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

        if (user.getNumOfBooksBorrowing() > 0){
            System.out.println("Cannot delete user: " + id);
            System.out.println("Reason: This user still has " + user.getNumOfBooksBorrowing() + " borrowed book(s) to return.");
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
        userNameTree.remove(IdGenerator.getKeyId(user.getName(), id));
        userEmailTree.remove(IdGenerator.getKeyId(user.getEmail(), id));
        userAgeTree.remove(IdGenerator.getKeyId(user.getAge(), id));
        userEducationTree.remove(IdGenerator.getKeyId(user.getEducation(), id));
        userCreatedDateTimeTree.remove(IdGenerator.getKeyId(user.getCreatedAt(), id));
        userNumOfBooksBorrowing.remove(IdGenerator.getKeyId(user.getNumOfBooksBorrowing(), id));
        System.out.println("User with ID " + id + " has been deleted.");

        UserUI.getPagination().setDisplayTree(userTree);
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

        String id = currentUser.getUserId();
        boolean isExit = false;

        do {
            UIGenerator.titleBuilder("Edit My Account");
            System.out.println(userTree.get(id).memberEditToString());
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
                            () -> {
                                String oldName = currentUser.getName();
                                String newName = UserUI.inputUserName();
                                currentUser.setName(newName);
                                userNameTree.remove(IdGenerator.getKeyId(oldName, id));
                                userNameTree.put(IdGenerator.getKeyId(newName, id), currentUser);
                                return false;
                            },
                            () -> {
                                String oldEmail = currentUser.getEmail();
                                String newEmail = UserUI.inputUserEmail();
                                currentUser.setEmail(newEmail);
                                userEmailTree.remove(IdGenerator.getKeyId(oldEmail, id));
                                userEmailTree.put(IdGenerator.getKeyId(newEmail, id), currentUser);
                                return false;
                            },
                            () -> { currentUser.setGender(UserUI.inputUserGender()); return false; },
                            () -> {
                                String oldAge = String.valueOf(currentUser.getAge());
                                int newAge = UserUI.inputUserAge();
                                currentUser.setAge(newAge);
                                userAgeTree.remove(IdGenerator.getKeyId(oldAge, id));
                                userAgeTree.put(IdGenerator.getKeyId(String.valueOf(newAge), id), currentUser);
                                return false;
                            },
                            () -> {
                                String oldEducation = String.valueOf(currentUser.getEducation());
                                User.Education newEdu = UserUI.inputUserEducation();
                                currentUser.setEducation(newEdu);
                                userEducationTree.remove(IdGenerator.getKeyId(oldEducation, id));
                                userEducationTree.put(IdGenerator.getKeyId(String.valueOf(newEdu), id), currentUser);
                                return false;
                            },
                            () -> true
                    }
            );
        } while (!isExit);

        saveToFile();
    }

    public boolean deleteOwnAccount(User currentUser) {

        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Delete My Account");

        if (currentUser.getNumOfBooksBorrowing() > 0){
            System.out.println("You cannot delete your account while you still have books borrowed.");
            System.out.println("Current books on loan: " + currentUser.getNumOfBooksBorrowing());
            UIGenerator.pressEnterToContinue();
            return false;
        }

        char confirm = UserPrompt.inputChar("Are you sure you want to delete your account? (Y/N): ", 'Y', 'N');
        if (confirm != ('Y')) {
            System.out.println("Account deletion canceled.");
            UIGenerator.pressEnterToContinue();
            return false;
        }

        String id = currentUser.getUserId();

        userTree.remove(currentUser.getUserId());
        userNameTree.remove(IdGenerator.getKeyId(currentUser.getName(), id));
        userEmailTree.remove(IdGenerator.getKeyId(currentUser.getEmail(), id));
        userAgeTree.remove(IdGenerator.getKeyId(currentUser.getAge(), id));
        userEducationTree.remove(IdGenerator.getKeyId(currentUser.getEducation(), id));
        userCreatedDateTimeTree.remove(IdGenerator.getKeyId(currentUser.getCreatedAt(), id));
        userNumOfBooksBorrowing.remove(IdGenerator.getKeyId(currentUser.getNumOfBooksBorrowing(), id));

        System.out.println("Your account has been deleted.");

        UserUI.getPagination().setDisplayTree(userTree);
        UserUI.getPagination().setCurrentPage(1);

        saveToFile();

        UIGenerator.pressEnterToContinue();
        return true;
    }

    public void sortUsers() {
        UserUI.getPagination().clearSearchingWord();
        boolean isBack = false;

        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("View Sorted Users");
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
                            () -> { UserUI.getPagination().setDisplayTree(userTree); return false; },
                            () -> { UserUI.getPagination().setDisplayTree(userNameTree); return false; },
                            () -> { UserUI.getPagination().setDisplayTree(userEmailTree); return false; },
                            () -> { UserUI.getPagination().setDisplayTree(userAgeTree); return false; },
                            () -> { UserUI.getPagination().setDisplayTree(userEducationTree); return false; },
                            () -> { UserUI.getPagination().setDisplayTree(userCreatedDateTimeTree); return false; },
                            () -> { UserUI.getPagination().setDisplayTree(userNumOfBooksBorrowing); return false; },
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
            if(isBack) {
                return;
            }
        } while (isBack);

        String search = new String();
        // If user search String
        if(searchOption[0] >= 0) {
            if(searchOption[0] == 0) {
                search = UserPrompt.inputString("Enter the word seaching : ", 999, false).toLowerCase().replaceAll("\\s+", "");
                UserUI.getPagination().setSearchingWord(search);
            }
            else {
                int input = UserPrompt.inputInteger("Enter the number seaching : ", 0, 999);
                search = IdGenerator.getKeyId(input, "");
                UserUI.getPagination().setSearchingWord(String.valueOf(input));
            }
        }

        UserUI.getPagination().setDisplayTree(searchTree.getRange(search, search+Character.MAX_VALUE));
        UserUI.getPagination().setCurrentPage(1);
    }

    public void displayUserReport() {
        userReportGenerator.displayReportMenu();
    }

    public void saveToFile() {
        UserDAO.writeFile(USER_FILE_PATH, userTree);
    }
}
