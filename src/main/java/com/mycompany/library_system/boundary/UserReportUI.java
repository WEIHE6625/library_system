package com.mycompany.library_system.boundary;

import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.UserReportController;
import com.mycompany.library_system.dao.ReportDAO;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;
import java.util.function.Supplier;

public class UserReportUI {

    private final UserReportController userReportController;
    private static final String REPORT_FOLDER = "database/reports/user_reports";
    private static final int COL_WIDTH = 11;

    public UserReportUI(TreeMap<String, User> userTree) {
        this.userReportController = new UserReportController(userTree);
        ReportDAO.createReportFolderIfNotExists(REPORT_FOLDER);
    }

    @SuppressWarnings("unchecked")
    public void displayReportMenu() {
        UIGenerator.clearScreen();
        boolean isExit = false;

        do {
            UIGenerator.titleBuilder("User Report Menu");
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Users by Education Level",
                            "[2] Users by Age Group",
                            "[3] Top Borrowing Users",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { handleEducationReport(); return false; },
                            () -> { handleAgeGroupReport(); return false; },
                            () -> { handleTopBorrowingUsersReport(); return false; },
                            () -> true
                    }
            );
        } while (!isExit);
    }

    private void handleEducationReport() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Users by Education Level Report");
        String[] labels = {"PRIMARY", "SECONDARY", "DIPLOMA", "BACHELOR", "MASTER", "PHD", "OTHER"};
        String gender = promptGenderFilter();
        int[] counts = userReportController.getEducationCounts(gender, labels.length);

        generateUsersByEducationReport(labels, counts, "users_by_ed_" + gender);
    }

    private void handleAgeGroupReport() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Users by Age Group Report");
        String gender = promptGenderFilter();
        System.out.println("[1] Default Age Groups");
        System.out.println("[2] Custom Age Groups");
        int choice = UserPrompt.inputInteger("Select grouping type >> ", 1, 2);

        if (choice == 1) {
            int[] groupBounds = {17, 25, 40, 60};
            String[] labels = {"<18", "18-25", "26-40", "41-60", "60+"};
            UserReportController.AgeReportData data = userReportController.getAgeGroupCounts(gender, groupBounds, labels.length);
            generateUsersByAgeGroupReport(gender, labels, data);
        } else {
            int[] customBounds = promptCustomAgeBounds();
            String[] customLabels = userReportController.buildCustomAgeLabels(customBounds);
            UserReportController.AgeReportData data = userReportController.getAgeGroupCounts(gender, customBounds, customLabels.length);
            generateUsersByAgeGroupReport(gender, customLabels, data);
        }
    }

    private void handleTopBorrowingUsersReport() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Top Borrowing Users Report");
        int topN = UserPrompt.inputInteger("Enter number of top users to display (1 - 10) >> ", 1, 10);

        User[] topUsers = userReportController.getTopBorrowingUsers(topN);
        String[] labels = new String[topUsers.length];
        int[] counts = new int[topUsers.length];

        generateTopBorrowingUsersReport(topN, labels, counts, topUsers);
    }

    private String promptGenderFilter() {
        System.out.println("[1] All");
        System.out.println("[2] Male");
        System.out.println("[3] Female");

        int choice = UserPrompt.inputInteger("Select gender filter >> ", 1, 3);
        return switch (choice) {
            case 2 -> "MALE";
            case 3 -> "FEMALE";
            default -> "ALL";
        };
    }

    private int[] promptCustomAgeBounds() {
        System.out.println("+------------------------------------------+");
        System.out.println("| Enter 4 upper bounds in ascending order. |");
        System.out.println("| Example: 17, 25, 40, 60                  |");
        System.out.println("| This will create:                        |");
        System.out.println("| Group 1: <= first bound                  |");
        System.out.println("| Group 2: previous + 1 to second bound    |");
        System.out.println("| Group 3: previous + 1 to third bound     |");
        System.out.println("| Group 4: previous + 1 to fourth bound    |");
        System.out.println("| Group 5: > fourth bound                  |");
        System.out.println("+------------------------------------------+\n");

        int b1, b2, b3, b4;
        while (true) {
            b1 = UserPrompt.inputInteger("Enter upper bound for Group 1 >> ", 1, 99);
            b2 = inputGreaterThan("Enter upper bound for Group 2 >> ", b1);
            if (b2 == -1) continue;
            b3 = inputGreaterThan("Enter upper bound for Group 3 >> ", b2);
            if (b3 == -1) continue;
            b4 = inputGreaterThan("Enter upper bound for Group 4 >> ", b3);
            if (b4 == -1) continue;
            break;
        }
        return new int[]{b1, b2, b3, b4};
    }

    private int inputGreaterThan(String prompt, int previous) {
        int value = UserPrompt.inputInteger(prompt, 1, 99);
        if (value <= previous) {
            System.out.println("Invalid input. Value must be greater than " + previous + ".");
            return -1;
        }
        return value;
    }

    public void generateUsersByEducationReport(String[] labels, int[] counts, String gender){
        StringBuilder report = new StringBuilder();

        int maxEducationCount = userReportController.getMax(counts);
        int standMaxEducationCount = (int) DataProcessor.getStandardScale(maxEducationCount, true);
        int chartHeight = Math.min(10, standMaxEducationCount);

        int totalUsers = userReportController.sum(counts);
        if (totalUsers == 0) {
            System.out.println("No user records found.");
            return;
        }

        String[] extremes = userReportController.findMaxAndMinLabels(labels, counts);
        String mostCommonEducation = extremes[0];
        String leastCommonEducation = extremes[1];
        double topPercentage = maxEducationCount * 100.0 / totalUsers;
        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "USERS BY EDUCATION LEVEL REPORT");

        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        UIGenerator.appendCenteredLine(report, "| No | Education        | Total Users |");
        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");

        int cumulativeUsers = 0;
        for (int i = 0; i < labels.length; i++) {
            cumulativeUsers += counts[i];
            String row = String.format("| %-2d | %-16s | %11d |",
                    i + 1, labels[i], counts[i]);
            UIGenerator.appendCenteredLine(report, row);
        }
        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        String totalRow = String.format("| %-2s | %-16s | %11d |",
                "", "Cumulative Users", cumulativeUsers);
        UIGenerator.appendCenteredLine(report, totalRow);

        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        report.append("\n");
        UIGenerator.appendCenteredLine(report, "=".repeat(160));
        UIGenerator.appendCenteredLine(report, "Most common education level : " + mostCommonEducation);
        UIGenerator.appendCenteredLine(report, "Least common education level : " + leastCommonEducation);
        UIGenerator.appendCenteredLine(report, String.format("Top category percentage : %.2f%%", topPercentage));
        UIGenerator.appendCenteredLine(report, "=".repeat(160));

        report.append("\n");
        UIGenerator.appendCenteredLine(report, "--User Education Level Distribution--");
        report.append("\n");

        int[] scaledCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            scaledCounts[i] = (standMaxEducationCount == 0) ? 0 :
                    (int) Math.round(((double) counts[i] / standMaxEducationCount) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, labels.length, chartHeight, COL_WIDTH, standMaxEducationCount, true);
        UIGenerator.appendFooter(report);

        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(), "users_by_education_" + gender, REPORT_FOLDER);
    }

    public void generateUsersByAgeGroupReport(String genderFilter, String[] labels, UserReportController.AgeReportData data) {
        StringBuilder report = new StringBuilder();

        int maxAgeGroupCount = userReportController.getMax(data.counts);
        int standMaxAgeGroupCount = (int) DataProcessor.getStandardScale(maxAgeGroupCount, true);
        int chartHeight = Math.min(10, Math.max(1, standMaxAgeGroupCount));

        int totalUsers = userReportController.sum(data.counts);
        if (totalUsers == 0) {
            System.out.println("No user records found.");
            return;
        }
        String[] extremes = userReportController.findMaxAndMinLabels(labels, data.counts);
        String mostCommonAgeGroup = extremes[0];
        String leastCommonAgeGroup = extremes[1];
        double avgAge = data.totalAgeSum * 1.0 / totalUsers;
        String formattedAge = String.format("%.2f", avgAge);

        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "USERS BY AGE GROUP REPORT");

        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        UIGenerator.appendCenteredLine(report, "| No | Age Group        | Total Users |");
        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");

        int cumulativeUsers = 0;
        for (int i = 0; i < labels.length; i++) {
            cumulativeUsers += data.counts[i];
            String row = String.format("| %-2d | %-16s | %11d |",
                    i + 1, labels[i], data.counts[i]);
            UIGenerator.appendCenteredLine(report, row);
        }
        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        String totalRow = String.format("| %-2s | %-16s | %11d |",
                "", "Cumulative Users", cumulativeUsers);
        UIGenerator.appendCenteredLine(report, totalRow);

        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        report.append("\n");
        UIGenerator.appendCenteredLine(report, "=".repeat(160));
        UIGenerator.appendCenteredLine(report, "Average User Age: " + formattedAge);
        UIGenerator.appendCenteredLine(report, "Most users are in the " + mostCommonAgeGroup + " group");
        UIGenerator.appendCenteredLine(report, "Fewest users are in the " + leastCommonAgeGroup + " group");
        UIGenerator.appendCenteredLine(report, "=".repeat(160));

        report.append("\n");
        UIGenerator.appendCenteredLine(report, "--User Age Group Distribution--");
        report.append("\n");

        int[] scaledCounts = new int[data.counts.length];
        for (int i = 0; i < data.counts.length; i++) {
            scaledCounts[i] = (standMaxAgeGroupCount == 0) ? 0 :
                    (int) Math.round(((double) data.counts[i] / standMaxAgeGroupCount) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, labels.length, chartHeight, COL_WIDTH, standMaxAgeGroupCount, true);
        UIGenerator.appendFooter(report);

        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(), "users_by_age_group_" + genderFilter, REPORT_FOLDER);
    }

    public void generateTopBorrowingUsersReport(int topN, String[] labels, int[] counts, User[] topUsers) {
        StringBuilder report = new StringBuilder();
        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "TOP " + topN + " BORROWING USERS REPORT");

        UIGenerator.appendCenteredLine(report, "+------+------------+------------------------------+----------------+");
        UIGenerator.appendCenteredLine(report, "| Rank | User ID    | User Name                    | Books Borrowed |");
        UIGenerator.appendCenteredLine(report, "+------+------------+------------------------------+----------------+");

        int validCount = 0;
        int totalBorrowingBooks = 0;
        int maxBorrowingBooks = 0;

        for (int i = 0; i < topUsers.length; i++) {
            if (topUsers[i] != null) {
                labels[i] = topUsers[i].getUserId();
                counts[i] = topUsers[i].getNumOfBooksBorrowing();

                if (counts[i] > maxBorrowingBooks) {
                    maxBorrowingBooks = counts[i];
                }

                String row = String.format("| %-4d | %-10s | %-28s | %14d |",
                        i + 1,
                        topUsers[i].getUserId(),
                        topUsers[i].getName(),
                        topUsers[i].getNumOfBooksBorrowing());

                UIGenerator.appendCenteredLine(report, row);
                validCount++;
                totalBorrowingBooks += topUsers[i].getNumOfBooksBorrowing();
            }
        }
        UIGenerator.appendCenteredLine(report, "+------+------------+------------------------------+----------------+");
        String totalRow = String.format("| %-4s | %-41s | %14d |",
                "", "Cumulative borrowing books", totalBorrowingBooks);
        UIGenerator.appendCenteredLine(report, totalRow);

        if (validCount == 0) {
            System.out.println("No borrowing records found.");
            return;
        }

        int standMaxBorrowedBooks = (int) DataProcessor.getStandardScale(maxBorrowingBooks, true);
        int chartHeight = Math.min(10, Math.max(1, standMaxBorrowedBooks));

        User topBorrower = topUsers[0];
        double contribution = totalBorrowingBooks == 0 ? 0.0 : maxBorrowingBooks * 100.0 / totalBorrowingBooks;

        UIGenerator.appendCenteredLine(report, "+------+------------+------------------------------+----------------+");
        report.append("\n");
        UIGenerator.appendCenteredLine(report, "=".repeat(160));
        UIGenerator.appendCenteredLine(report, "Top borrower: " + topBorrower.getUserId() + " - " + topBorrower.getName() + " (" + maxBorrowingBooks + " books)");
        UIGenerator.appendCenteredLine(report, String.format("Top borrower contribution: %.2f%%", contribution));
        UIGenerator.appendCenteredLine(report, "=".repeat(160));

        report.append("\n");
        UIGenerator.appendCenteredLine(report, "--Top " + topN + " Borrowing Users Distribution--");
        report.append("\n");

        int[] scaledCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            scaledCounts[i] = (standMaxBorrowedBooks == 0) ? 0 :
                    (int) Math.round(((double) counts[i] / standMaxBorrowedBooks) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, validCount, chartHeight, COL_WIDTH, standMaxBorrowedBooks, true);
        UIGenerator.appendFooter(report);

        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(),
                "top_" + validCount + "_borrowing_users",
                REPORT_FOLDER);
    }
}