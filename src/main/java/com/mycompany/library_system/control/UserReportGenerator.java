package com.mycompany.library_system.control;

import java.util.function.Supplier;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.ReportDAO;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;

public class UserReportGenerator {

    private final TreeMap<String, User> userTree;
    private final TreeMap<String, String> savedReportTree;
    private static final String REPORT_FOLDER = "database/reports/user_reports";
    private static final int COL_WIDTH = 11;

    public UserReportGenerator(TreeMap<String, User> userTree) {
        this.userTree = userTree;
        this.savedReportTree = new TreeMap<>();
        ReportDAO.createReportFolderIfNotExists(REPORT_FOLDER);
        ReportDAO.loadSavedReports(savedReportTree, REPORT_FOLDER);
    }

    public void displayReportMenu() {
        UIGenerator.clearScreen();
        boolean isExit = false;

        do {
            UIGenerator.titleBuilder("User Report Menu");
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Users by Education",
                            "[2] Users by Age Group",
                            "[3] Top 5 Borrowing Users",
                            "[4] Retrieve Saved Report",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { generateUsersByEducationReport(); return false; },
                            () -> { generateUsersByAgeGroupReport(); return false; },
                            () -> { generateTop5BorrowingUsersReport(); return false; },
                            () -> { retrieveSavedReportMenu(); return false; },
                            () -> true
                    }
            );
        } while (!isExit);
    }
    

    public void generateUsersByEducationReport() {
        String[] labels = {
                "PRIMARY",
                "SECONDARY",
                "DIPLOMA",
                "BACHELOR",
                "MASTER",
                "PHD",
                "OTHER"
        };

        int[] counts = new int[7];

        for (MapInterface.Entry<String, User> entry : userTree) {
            User user = entry.getValue();

            switch (user.getEducation()) {
                case PRIMARY_SCHOOL -> counts[0]++;
                case SECONDARY_SCHOOL -> counts[1]++;
                case DIPLOMA -> counts[2]++;
                case BACHELOR -> counts[3]++;
                case MASTER -> counts[4]++;
                case PHD -> counts[5]++;
                case OTHER -> counts[6]++;
            }
        }

        int maxEducationCount = counts[0];
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] > maxEducationCount) {
                maxEducationCount = counts[i];
            }
        }
        int standMaxEducationCount = (int) DataProcessor.getStandardScale(maxEducationCount, true);
        int chartHeight = Math.min(10, standMaxEducationCount);

        StringBuilder report = new StringBuilder();
        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "USERS BY EDUCATION REPORT");

        UIGenerator.appendCenteredLine(report, "|----|------------------|-------------|");
        UIGenerator.appendCenteredLine(report, "| No | Education        | Total Users |");
        UIGenerator.appendCenteredLine(report, "|----|------------------|-------------|");

        int totalUsers = 0;
        for (int i = 0; i < labels.length; i++) {
            String row = String.format("| %-2d | %-16s | %11d |",
                    i + 1, labels[i], counts[i]);

            UIGenerator.appendCenteredLine(report, row);
            totalUsers += counts[i];
        }

        String totalLine = "TOTAL USERS : " + totalUsers;

        UIGenerator.appendCenteredLine(report, "|----|------------------|-------------|");
        UIGenerator.appendCenteredLine(report, "_______________________________________");
        UIGenerator.appendCenteredLine(report, String.format("Total User: %25d", totalUsers));
        UIGenerator.appendCenteredLine(report, "_______________________________________");

        UIGenerator.appendCenteredLine(report, "User Education Distribution");
        report.append("\n");

        int[] scaledCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            scaledCounts[i] = (standMaxEducationCount == 0) ? 0 : 
                (int) Math.round(((double) counts[i] / standMaxEducationCount) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, labels.length, chartHeight, COL_WIDTH, standMaxEducationCount, true);
        UIGenerator.appendFooter(report);


        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(), "users_by_education", REPORT_FOLDER);
    }

    public void generateUsersByAgeGroupReport() {
        String[] labels = {"<18", "18-25", "26-40", "41-60", "60+"};
        int[] counts = new int[5];

        for (MapInterface.Entry<String, User> entry : userTree) {
            User user = entry.getValue();
            int age = user.getAge();   // make sure User class has getAge()

            if (age < 18) {
                counts[0]++;
            } else if (age <= 25) {
                counts[1]++;
            } else if (age <= 40) {
                counts[2]++;
            } else if (age <= 60) {
                counts[3]++;
            } else {
                counts[4]++;
            }
        }

        int maxAgeGroupCount = counts[0];
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] > maxAgeGroupCount) {
                maxAgeGroupCount = counts[i];
            }
        }
        int standMaxAgeGroupCount = (int) DataProcessor.getStandardScale(maxAgeGroupCount, true);
        int chartHeight = Math.min(10, standMaxAgeGroupCount);

        StringBuilder report = new StringBuilder();
        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "USERS BY AGE GROUP REPORT");

        UIGenerator.appendCenteredLine(report, "|----|------------------|-------------|");
        UIGenerator.appendCenteredLine(report, "| No | Age Group        | Total Users |");
        UIGenerator.appendCenteredLine(report, "|----|------------------|-------------|");

        int totalUsers = 0;
        for (int i = 0; i < labels.length; i++) {
            String row = String.format("| %-2d | %-16s | %11d |",
                    i + 1, labels[i], counts[i]);

            UIGenerator.appendCenteredLine(report, row);
            totalUsers += counts[i];
        }

        UIGenerator.appendCenteredLine(report, "|----|------------------|-------------|");
        UIGenerator.appendCenteredLine(report, "_______________________________________");
        UIGenerator.appendCenteredLine(report, String.format("Total User: %25d", totalUsers));
        UIGenerator.appendCenteredLine(report, "_______________________________________");

        UIGenerator.appendCenteredLine(report, "User Age Group Distribution");
        report.append("\n");

        int[] scaledCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            scaledCounts[i] = (standMaxAgeGroupCount == 0) ? 0 : 
                (int) Math.round(((double) counts[i] / standMaxAgeGroupCount) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, labels.length, chartHeight, COL_WIDTH, standMaxAgeGroupCount, true);
        UIGenerator.appendFooter(report);

        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(), "users_by_age_group", REPORT_FOLDER);
    }

    public void generateTop5BorrowingUsersReport() {
        if (userTree.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        User[] topUsers = new User[Math.min(5, userTree.size())];

        for (MapInterface.Entry<String, User> entry : userTree) {
            User current = entry.getValue();
            insertIntoTop5(topUsers, current);
        }

        String[] labels = new String[topUsers.length];
        int[] counts = new int[topUsers.length];

        StringBuilder report = new StringBuilder();
        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "TOP 5 BORROWING USERS REPORT");

        UIGenerator.appendCenteredLine(report, "|------|------------|------------------------------|----------------|");
        UIGenerator.appendCenteredLine(report, "| Rank | User ID    | User Name                    | Books Borrowed |");
        UIGenerator.appendCenteredLine(report, "|------|------------|------------------------------|----------------|");

        int validCount = 0;
        int totalBorrowedBooks = 0;
        int maxBorrowedBooks = 0;

        for (int i = 0; i < topUsers.length; i++) {
            if (topUsers[i] != null) {
                labels[i] = topUsers[i].getUserId();
                counts[i] = topUsers[i].getNumOfBooksBorrowing();
                if (counts[i] > maxBorrowedBooks) {
                    maxBorrowedBooks = counts[i];
                }

                String row = String.format("| %-4d | %-10s | %-28s | %14d |",
                        i + 1,
                        topUsers[i].getUserId(),
                        topUsers[i].getName(),
                        topUsers[i].getNumOfBooksBorrowing());

                UIGenerator.appendCenteredLine(report, row);
                validCount++;
                totalBorrowedBooks += topUsers[i].getNumOfBooksBorrowing();
            }
        }
        int standMaxBorrowedBooks = (int) DataProcessor.getStandardScale(maxBorrowedBooks, true);
        int chartHeight = Math.min(10, standMaxBorrowedBooks);

        UIGenerator.appendCenteredLine(report, "|------|------------|------------------------------|----------------|");
        UIGenerator.appendCenteredLine(report, "__________________________________________________________________");
        UIGenerator.appendCenteredLine(report, String.format("Total Top Users: %12d", validCount));
        UIGenerator.appendCenteredLine(report, String.format("Total Borrowed Books: %7d", totalBorrowedBooks));
        UIGenerator.appendCenteredLine(report, "__________________________________________________________________");

        UIGenerator.appendCenteredLine(report, "Top 5 Borrowing Users Distribution");
        report.append("\n");

        int[] scaledCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            scaledCounts[i] = (standMaxBorrowedBooks == 0) ? 0 : 
                (int) Math.round(((double) counts[i] / standMaxBorrowedBooks) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, validCount, chartHeight, COL_WIDTH, standMaxBorrowedBooks, true);
        UIGenerator.appendFooter(report);

        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(), "top_5_borrowing_users", REPORT_FOLDER);
    }

    private void insertIntoTop5(User[] topUsers, User candidate) {
        for (int i = 0; i < topUsers.length; i++) {
            if (topUsers[i] == null
                    || candidate.getNumOfBooksBorrowing() > topUsers[i].getNumOfBooksBorrowing()) {

                for (int j = topUsers.length - 1; j > i; j--) {
                    topUsers[j] = topUsers[j - 1];
                }

                topUsers[i] = candidate;
                break;
            }
        }
    }

    public void retrieveSavedReportMenu() {
        UIGenerator.clearScreen();
        boolean isExit = false;

        do {
            UIGenerator.titleBuilder("Retrieve Saved User Reports");
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Users by Education Reports",
                            "[2] Users by Age Group Reports",
                            "[3] Top 5 Borrowing Users Reports",
                            "[4] All User Reports",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[]{
                            () -> { ReportDAO.retrieveSavedReportByPrefix("users_by_education", savedReportTree, REPORT_FOLDER); return false; },
                            () -> { ReportDAO.retrieveSavedReportByPrefix("users_by_age_group", savedReportTree, REPORT_FOLDER); return false; },
                            () -> { ReportDAO.retrieveSavedReportByPrefix("top5_borrowing_users", savedReportTree, REPORT_FOLDER); return false; },
                            () -> { ReportDAO.retrieveSavedReportByPrefix("", savedReportTree, REPORT_FOLDER); return false; }
                    }
            );
        } while (!isExit);
    }
}