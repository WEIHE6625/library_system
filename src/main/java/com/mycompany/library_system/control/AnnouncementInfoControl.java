package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.AnnouncementUI;
import com.mycompany.library_system.dao.AnnouncementDAO;
import com.mycompany.library_system.dao.AnnouncementViewDAO;
import com.mycompany.library_system.entity.*;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

import java.time.LocalDate;
import java.util.function.Supplier;

/**
 *
 * @author ZhenHao
 */
public class AnnouncementInfoControl {

    private MapInterface<String, Announcement> announcementTree;
    private MapInterface<String, Announcement> announcementTitleTree;
    private MapInterface<String, Announcement> announcementContentTree;
    private MapInterface<String, Announcement> announcementCreatedDateTree;
    private MapInterface<String, Announcement> announcementEditDateTree;

    public static final String ANNOUNCEMENT_FILE_PATH = "database/announcement.csv";
    public final String DEFAULT_USER_FILE_PATH =  "database/users.csv";
    public static final String ANNOUNCEMENTVIEW_FILE_PATH = "database/announcementViews.csv";



    public AnnouncementInfoControl(){
        System.out.println("Initializing...");
        this.announcementTree = AnnouncementDAO.readFile(ANNOUNCEMENT_FILE_PATH);
        this.announcementTitleTree = new TreeMap<>();
        this.announcementContentTree = new TreeMap<>();
        this.announcementCreatedDateTree = new TreeMap<>();
        this.announcementEditDateTree = new TreeMap<>();

        for(Announcement a : announcementTree.values()){
            indexAnnouncement(a);
        }
    }

    public MapInterface<String, Announcement> getAnnouncementList(){ return announcementTree;}

    public Announcement addAnnouncement(String currentUserID){
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Add New Announcement");
        String id = IdGenerator.generateId(announcementTree, "A", 4);
        String type = AnnouncementUI.chooseTypeOfAnnouncement();
        String title = AnnouncementUI.inputAnnouncementTitle();
        String content = AnnouncementUI.inputAnnouncementContent();
        String adminId = currentUserID;
        LocalDate createdDate = LocalDate.now();
        String editAdminId = "N/A";
        LocalDate editDate = createdDate;

        Announcement newAnnouncement = null;

        if(type.equals("EDUCATION")){
            MapInterface<String, String> eduLevels = getTargetableEduLevels();
            String selectedLevel = AnnouncementUI.promptEduLevelSelection(eduLevels);

            newAnnouncement = new AnnouncementEduLevel(id, title, content, adminId, createdDate, editAdminId, editDate, selectedLevel);
        } else if(type.equals("BROADCAST")){
            newAnnouncement = new AnnouncementBroadcast(id, title, content, adminId, createdDate, editAdminId, editDate);
        } else {
            newAnnouncement = new Announcement(id, title, content, adminId, createdDate, editAdminId, editDate);
        }



        if (newAnnouncement != null) {
            announcementTree.put(id, newAnnouncement);
            indexAnnouncement(newAnnouncement);
            saveToFile();

            AnnouncementUI.getPagination().setDisplayTree(announcementTree);
            AnnouncementUI.getPagination().setCurrentPage(1);
            System.out.println("Successfully added " + type + " announcement.");
        }

        UIGenerator.pressEnterToContinue();
        return newAnnouncement;
    }

    public void saveToFile() {

        AnnouncementDAO.writeFile(ANNOUNCEMENT_FILE_PATH, announcementTree);
    }

    public MapInterface<String, String> getTargetableEduLevels() {
        return AnnouncementDAO.getUniqueEduLevels(DEFAULT_USER_FILE_PATH);
    }

    private void indexAnnouncement(Announcement a) {
        String id = a.getId();
        announcementTitleTree.put(IdGenerator.getKeyId(a.getTitle(), id), a);
        announcementContentTree.put(IdGenerator.getKeyId(a.getContent(), id), a);
        announcementCreatedDateTree.put(IdGenerator.getKeyId(a.getCreatedDate(), id), a);
        announcementEditDateTree.put(IdGenerator.getKeyId(a.getEditDate(), id), a);
    }

    public void editAnnouncement(String editAdminId) {
        UIGenerator.titleBuilder("Edit Announcement");
        String id = AnnouncementUI.inputAnnouncementID(announcementTree);

        Announcement a = announcementTree.get(id);

        if (a == null) {
            System.out.println("Error: Announcement with ID " + id + " not found.");
            UIGenerator.pressEnterToContinue();
            return;
        }

        System.out.println("Editing: " + a.getTitle());
        System.out.println("Leave blank to keep current value.");

        String newTitle = AnnouncementUI.inputOptionalString("New Title", a.getTitle());
        String newContent = AnnouncementUI.inputOptionalString("New Content", a.getContent());

        a.setTitle(newTitle);
        a.setContent(newContent);
        a.setEditAdminId(editAdminId);
        a.setEditDate(LocalDate.now());

        if (a instanceof AnnouncementEduLevel) {
            MapInterface<String, String> levels = getTargetableEduLevels();
            String newLevel = AnnouncementUI.promptEduLevelSelection(levels);
            ((AnnouncementEduLevel) a).setEduLevel(newLevel);
        }


        reIndexAnnouncement(a);
        saveToFile();

        System.out.println("Announcement updated successfully!");
        UIGenerator.pressEnterToContinue();
    }

    private void reIndexAnnouncement(Announcement a) {
        indexAnnouncement(a);
    }

    public void deleteAnnouncement() {
        UIGenerator.titleBuilder("Delete Announcement");
        String id = AnnouncementUI.inputAnnouncementID(announcementTree);

        Announcement a = announcementTree.get(id);
        if (a == null) {
            System.out.println("Error: ID not found.");
            UIGenerator.pressEnterToContinue();
            return;
        }
        String confirm = UserPrompt.inputString("Are you sure you want to detete Announcement " + id + "? (y/n) >> ", 1, false);

        if (confirm.equalsIgnoreCase("Y")) {
            announcementTree.remove(id);
            announcementTitleTree.remove(IdGenerator.getKeyId(a.getTitle(), id));

            saveToFile();
            System.out.println("Announcement deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
        UIGenerator.pressEnterToContinue();
    }

    private MapInterface<String, String> searchCriteriaTree = new TreeMap<>();

    public void applyAnnouncementSearch(String criteria, String searchWord) {
        searchCriteriaTree.put(criteria, searchWord.toLowerCase().trim());

        MapInterface<String, Announcement> finalResults = announcementTree;

        for (MapInterface.Entry<String, String> entry : searchCriteriaTree) {
            String activeCriteria = entry.getKey();
            String activeValue = entry.getValue();

            MapInterface<String, Announcement> currentMatches = new TreeMap<>();

            for (Announcement a : announcementTree.values()) {
                boolean isMatch = false;

                switch (activeCriteria) {
                    case "Title":
                        isMatch = a.getTitle().toLowerCase().contains(activeValue);
                        break;
                    case "AdminID":
                        isMatch = a.getAdminId().toLowerCase().contains(activeValue);
                        break;
                    case "Type":
                        isMatch = a.getClass().getSimpleName().toLowerCase().contains(activeValue);
                        break;
                }

                if (isMatch) {
                    currentMatches.put(a.getId(), a);
                }
            }


            MapInterface<String, Announcement> intersection = new TreeMap<>();
            for (Announcement m : currentMatches.values()) {
                if (finalResults.containsKey(m.getId())) {
                    intersection.put(m.getId(), m);
                }
            }

            finalResults = intersection;
        }

        updatePagination(finalResults);
    }

    private void updatePagination(MapInterface<String, Announcement> filteredResults) {
        AnnouncementUI.getPagination().setDisplayTree(filteredResults);

        StringBuilder displaySearch = new StringBuilder();
        for (MapInterface.Entry<String, String> entry : searchCriteriaTree) {
            displaySearch.append("[").append(entry.getKey()).append(": ").append(entry.getValue()).append("] ");
        }

        AnnouncementUI.getPagination().setSearchingWord(displaySearch.toString().trim());

        AnnouncementUI.getPagination().setCurrentPage(1);

    }

    public void resetSearch() {
        this.searchCriteriaTree = new TreeMap<>();
        AnnouncementUI.getPagination().setDisplayTree(this.announcementTree);
        AnnouncementUI.getPagination().clearSearchingWord();
    }

    public void searchAnnouncements() {
        boolean isBack = false;
        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Search Announcements");
            AnnouncementUI.displayAnnouncementList();

            isBack = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Show All (Reset)",
                            "[2] Search by Title",
                            "[3] Filter by Type",
                            "[4] Search by Admin ID",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { resetSearch(); return false; },
                            () -> {
                                String input = UserPrompt.inputString("Enter Title: ", 50, false);
                                applyAnnouncementSearch("Title", input);
                                return false;
                            },
                            () -> {
                                String input = AnnouncementUI.chooseTypeForSearch();
                                applyAnnouncementSearch("Type", input);
                                return false;
                            },
                            () -> {
                                String input = UserPrompt.inputString("Enter Admin ID: ", 10, false);
                                applyAnnouncementSearch("AdminID", input);
                                return false;
                            },
                            () -> true
                    }
            );
        } while (!isBack);
    }

    public MapInterface<String, Announcement> getFilteredAnnouncementsForUser(User user) {
        MapInterface<String, Announcement> visibleAnnouncements = new TreeMap<>();

        String studentLevel = user.getEducation().name();

        for (Announcement a : announcementTree.values()) {
            if (a instanceof AnnouncementBroadcast) {
                visibleAnnouncements.put(a.getId(), a);

            } else if (a instanceof AnnouncementEduLevel) {
                String targetLevel = ((AnnouncementEduLevel) a).getEduLevel();
                if (targetLevel.equalsIgnoreCase(studentLevel)) {
                    visibleAnnouncements.put(a.getId(), a);
                }
            }
            if (a instanceof AnnouncementFines) {
                AnnouncementFines f = (AnnouncementFines) a;
                if (f.getTargetUserId().equals(user.getUserId())) {
                    visibleAnnouncements.put(a.getId(), a);
                }
            }
        }

        return visibleAnnouncements;
    }

    public void viewAnnouncementContent(String loggedInUserId) {
        MapInterface<String, Announcement> allowedTree = AnnouncementUI.getPagination().getDisplayTree();

        if (allowedTree == null || allowedTree.isEmpty()) {
            System.out.println("No announcements available to view.");
            return;
        }

        String id = AnnouncementUI.inputAnnouncementID(allowedTree);

        if (id == null) return;


        Announcement a = announcementTree.get(id);

        if (a != null) {
            AnnouncementUI.displayAnnouncementDetails(a);
        }
        AnnouncementViewDAO.recordView(ANNOUNCEMENTVIEW_FILE_PATH, id, loggedInUserId);
    }

    public void generateAutomaticAnnouncement(String id, String title, String content, String adminId,
                                              LocalDate createdDate, String editAdminId, LocalDate editDate, String targetUserId) {


        Announcement fineNotice = new AnnouncementFines(id, title, content, adminId, createdDate, editAdminId, editDate, targetUserId);


        announcementTree.put(fineNotice.getId(), fineNotice);


        AnnouncementDAO.writeFile(ANNOUNCEMENT_FILE_PATH, announcementTree);
    }

    private double calculateLiveFine(LocalDate borrowDate, LocalDate currentDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(borrowDate, currentDate);
        double incrementDays = Fine.getDaysToIncrementFine();
        double weeklyAmount = Fine.getWeeklyFineAmount();

        return ((int) (days / incrementDays)) * weeklyAmount;
    }

    public void checkAndNotifyFines(User student) {
        BorrowFineRepo fineRepo = new BorrowFineRepo();
        TreeMap<String, BorrowFineRecord> unpaidFines = fineRepo.getUserAlreadyPaidRecords(student.getUserId(), false);

        if (unpaidFines == null || unpaidFines.isEmpty()) {
            return;
        }

        for (BorrowFineRecord record : unpaidFines.values()) {
            Fine fineData = record.getFine();

            if (fineData == null || fineData.getFineDate() == null) continue;

            double liveFineAmount = calculateLiveFine(fineData.getFineDate(), LocalDate.now());

            if (liveFineAmount > 0) {
                if (!hasBeenNotified(student.getUserId(), record.getBorrowId(), liveFineAmount)) {

                    String title = "Overdue Fine: " + record.getBorrowId();
                    String content = String.format(
                            "You have an unpaid fine for Borrow ID %s.\n" +
                                    "Current Total: RM %.2f\n" +
                                    "Issued on: %s\n\nPlease visit the counter to settle.",
                            record.getBorrowId(), liveFineAmount, fineData.getFineDate()
                    );

                    String id = IdGenerator.generateId(announcementTree, "A", 4);
                    String adminId = "SYSTEM";
                    LocalDate currentDate = LocalDate.now();

                    generateAutomaticAnnouncement(id, title, content, adminId, currentDate, null, null, student.getUserId());
                }
            }
        }
    }

    private boolean hasBeenNotified(String userId, String borrowId, double amount) {
        for (Announcement a : announcementTree.values()) {


            if (a instanceof AnnouncementFines) {
                AnnouncementFines f = (AnnouncementFines) a;

                if (f.getTargetUserId().equals(userId) &&
                        f.getTitle().contains(borrowId) &&
                        f.getContent().contains(String.format("%.2f", amount))) {

                    return true;
                }
            }
        }
        return false;
    }





}


