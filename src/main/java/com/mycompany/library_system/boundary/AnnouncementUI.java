package com.mycompany.library_system.boundary;


import java.util.Scanner;
import java.util.function.Supplier;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;

import com.mycompany.library_system.control.AnnouncementInfoControl;
import com.mycompany.library_system.control.AnnouncementReportControl;
import com.mycompany.library_system.entity.*;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;
import com.mycompany.library_system.utility.Pagination;
/**
 *
 * @author ZhenHao
 */
public class AnnouncementUI {

    static Scanner scanner = new Scanner(System.in);
    private static AnnouncementInfoControl aController = new AnnouncementInfoControl();
    private static Pagination pagination = new Pagination();

    public static String inputAnnouncementID(MapInterface<String, Announcement>announcementTree){
        boolean isValid = false;
        String id = "";
        do{
            System.out.print("Enter announcement ID :");
            id = scanner.nextLine();
            if(id.equals("0")) return null;
            if(!announcementTree.containsKey(id)){
                System.out.println("Announcement ID " + id + " does not exist. Please try again");
            }else{
                isValid = true;
            }
        }while(!isValid);

        return id;
    }

    public static String promptEduLevelSelection(MapInterface<String, String> eduLevels) {
        UIGenerator.titleBuilder("Select Target Education Level");

        ListInterface<String> levelsList = eduLevels.keys();

        if (levelsList.isEmpty()) {
            System.out.println("No education levels found in users.csv!");
            return "General";
        }


        for (int i = 0; i < levelsList.size(); i++) {
            System.out.printf("[%d] %s\n", (i + 1), levelsList.get(i));
        }


        int choice = -1;
        while (choice < 1 || choice > levelsList.size()) {
            System.out.print("\nSelect the target level (1-" + levelsList.size() + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 1 || choice > levelsList.size()) {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        return levelsList.get(choice - 1);
    }

    public static String inputAnnouncementTitle(){return UserPrompt.inputString("Enter Announcement Title*   >> ", 100, false);}
    public static String inputAnnouncementContent(){return UserPrompt.inputString("Enter Announcement Content*   >>", 1000, false);}
    public static String chooseTypeOfAnnouncement(){
        boolean isValid = false;
        String choice = "";
        do {
            System.out.println("Choose the type of announcement you want to make");
            System.out.println("1. Broadcast\n2. Education Level");
            choice = scanner.nextLine();

            if (choice.equals("1")) {
                return "BROADCAST";
            } else if (choice.equals("2")) {
                return "EDUCATION";
            } else {
                System.out.println("Invalid Input please try again");
            }
        } while(!isValid);

        return "GENERAL";
    }

    public static Pagination<String, Announcement> getPagination() {
        return pagination;
    }

    public static void menu(String loggedInID) {
        boolean isExit = false;

        pagination.setDisplayTree(aController.getAnnouncementList());
        pagination.setCurrentPage(1);

        do {
            UIGenerator.clearScreen();
                displayAnnouncementList();

            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Create New Announcement",
                            "[2] View Announcement details",
                            "[3] Edit Announcement",
                            "[4] Delete Announcement",
                            "[5] Search Announcements",
                            "[6] Announcement Report",
                            " ",
                            "[7] <- Previous Page          [8] Next Page ->",
                            "[0] Back to Main Menu"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { aController.addAnnouncement(loggedInID); return false; },//1
                            () -> { aController.viewAnnouncementContent(loggedInID); return false; },//2
                            () -> { aController.editAnnouncement(loggedInID); return false; },//3
                            () -> { aController.deleteAnnouncement(); return false; }, // 4
                            () -> { aController.searchAnnouncements(); return false; },//5
                            () -> { performReportWizard(aController.getAnnouncementList()); return false; },
                            () -> { pagination.previousPage(); return false; },
                            () -> { pagination.nextPage(); return false; },
                            () -> true // Exit
                    }
            );
        } while (!isExit);
    }

    public static void displayAnnouncementList() {
        MapInterface<String, Announcement> tree = pagination.getDisplayTree();
        int start = pagination.getStartIndex();
        int end = pagination.getEndIndex();
        UIGenerator.titleBuilder("Announcement Management");

        if (!pagination.getSearchingWord().isEmpty()) {
            System.out.println(pagination.getSearchingWord());
        }

        String format = "| %-8s | %-14s | %-30s | %-12s |%n";
        System.out.println("+----------+----------------+--------------------------------+--------------+");
        System.out.printf(format, "ID", "Type", "Title", "Date");
        System.out.println("+----------+----------------+--------------------------------+--------------+");

        if (tree == null || tree.isEmpty()) {
            System.out.println("|                No announcements found.                          |");
        } else {
            int currentIndex = 0;
            for (Announcement a : tree.values()) {
                if (currentIndex >= start && currentIndex <= end) {
                    String type = a.getClass().getSimpleName().replace("Announcement", "");
                    System.out.printf(format, a.getId(), type, a.getTitle(), a.getCreatedDate());
                }

                currentIndex++;

                if (currentIndex > end) break;
            }
        }
        System.out.println("+----------+----------------+--------------------------------+--------------+");
        System.out.printf(" Page %d of %d (Total: %d)%n",
                pagination.getCurrentPage(), pagination.getTotalPages(), tree.size());
    }

    public static String inputOptionalString(String prompt, String currentVal) {
        System.out.print(prompt + " [" + currentVal + "]: ");
        String input = scanner.nextLine();
        return input.isEmpty() ? currentVal : input;
    }

    public static String chooseTypeForSearch() {
        System.out.println("\nSelect Announcement Type to Filter:");
        System.out.println("[1] Fine");
        System.out.println("[2] Broadcast");
        System.out.println("[3] Education Level");

        int choice = UserPrompt.inputInteger("Select type >> ", 1, 3);

        switch (choice) {
            case 1: return "AnnouncementFines";
            case 2: return "AnnouncementBroadcast";
            case 3: return "AnnouncementEduLevel";
            default: return "";
        }
    }


    public static void studentMenu(User loggedInUser) {
        aController.checkAndNotifyFines(loggedInUser);
        MapInterface<String, Announcement> userView = aController.getFilteredAnnouncementsForUser(loggedInUser);

        pagination.setDisplayTree(userView);
        pagination.setCurrentPage(1);
        boolean isExit = false;
        do {
            UIGenerator.clearScreen();
            displayAnnouncementList();

            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] View Announcement Details",
                            "[2] Prev Page",
                            "[3] Next Page",
                            "[0] Back"
                    },
                    "Select >> ",
                    new Supplier[] {
                            () -> { aController.viewAnnouncementContent(loggedInUser.getUserId()); return false; },
                            () -> { pagination.previousPage(); return false; },
                            () -> { pagination.nextPage(); return false; },
                            () -> true
                    }
            );
        } while (!isExit);
    }

    public static void displayAnnouncementDetails(Announcement a) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Announcement Content");

        System.out.println("ID      : " + a.getId());
        System.out.println("Title   : " + a.getTitle());
        System.out.println("Date    : " + a.getCreatedDate());
        System.out.println("--------------------------------------------------");
        System.out.println(a.getContent());
        System.out.println("--------------------------------------------------");

        UIGenerator.pressEnterToContinue();
    }


    public static void performReportWizard(MapInterface<String, Announcement> tree) {
        AnnouncementReportControl reportCtrl = new AnnouncementReportControl(tree, AnnouncementInfoControl.ANNOUNCEMENTVIEW_FILE_PATH);

        System.out.println("\nSelect Report Type:");
        System.out.println("[1] Distribution Report");
        System.out.println("[2] Engagement Report");
        int type = UserPrompt.inputInteger("Choice >> ", 1, 2);

        String report = "";
        String prefix = "";

        if (type == 1) {
            report = reportCtrl.generateDistributionReport();
            prefix = "Distribution_Analysis";
        } else {
            report = reportCtrl.generateEngagementReport();
            prefix = "Engagement_Analysis";
        }

        System.out.println(report);
        String choice = UserPrompt.inputString("Export to file? (y/n) >> ", 1, false);
        if (choice.equalsIgnoreCase("y")) {
            reportCtrl.exportToFile(report, prefix);
        }
    }

}
