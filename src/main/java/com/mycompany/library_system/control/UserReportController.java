package com.mycompany.library_system.control;

import java.util.function.Supplier;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.dao.ReportDAO;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

public class UserReportController {

    private final TreeMap<String, User> userTree;
    private final TreeMap<String, String> savedReportTree;
    private static final String REPORT_FOLDER = "database/reports/user_reports";
    private static final int COL_WIDTH = 11;

    public UserReportController(TreeMap<String, User> userTree) {
        this.userTree = userTree;
        this.savedReportTree = new TreeMap<>();
        ReportDAO.createReportFolderIfNotExists(REPORT_FOLDER);
        ReportDAO.loadSavedReports(savedReportTree, REPORT_FOLDER);
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
                            "[4] Retrieve Saved Report",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { handleEducationReport(); return false; },
                            () -> { handleAgeGroupReport(); return false; },
                            () -> { handleTopBorrowingUsersReport(); return false; },
                            () -> { retrieveSavedReportMenu(); return false; },
                            () -> true
                    }
            );
        } while (!isExit);
    }

    private void handleEducationReport() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Users by Education Level Report");

        String genderFilter = promptGenderFilter();

        generateUsersByEducationReport(genderFilter);
    }

    private void handleAgeGroupReport() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Users by Age Group Report");

        String genderFilter = promptGenderFilter();

        System.out.println("[1] Default Age Groups");
        System.out.println("[2] Custom Age Groups");
        int choice = UserPrompt.inputInteger("Select grouping type >> ", 1, 2);

        if (choice == 1) {
            int[] groupBounds = {17, 25, 40, 60}; // <18, 18-25, 26-40, 41-60, 60+
            String[] labels = {"<18", "18-25", "26-40", "41-60", "60+"};
            generateUsersByAgeGroupReport(genderFilter, labels, groupBounds);
        } else {
            int[] customBounds = promptCustomAgeBounds();
            String[] customLabels = buildCustomAgeLabels(customBounds);
            generateUsersByAgeGroupReport(genderFilter, customLabels, customBounds);
        }
    }

    private void handleTopBorrowingUsersReport() {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Top Borrowing Users Report");

        if (userTree.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        int maxTopN = userTree.size();
        int topN = UserPrompt.inputInteger("Enter number of top users to display (1 - 10) >> ", 1, 10);

        generateTopBorrowingUsersReport(topN);
    }


    public void generateUsersByEducationReport(String genderFilter) {
        String[] labels = {
                "PRIMARY",
                "SECONDARY",
                "DIPLOMA",
                "BACHELOR",
                "MASTER",
                "PHD",
                "OTHER"
        };

        int[] counts = new int[labels.length];

        for (MapInterface.Entry<String, User> entry : userTree) {
            User user = entry.getValue();

            if (!matchesGender(user, genderFilter)) {
                continue;
            }

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

        int totalUsers = sum(counts);
        if (totalUsers == 0) {
            System.out.println("No user records found.");
            return;
        }


        int maxEducationCount = getMax(counts);
        int standMaxEducationCount = (int) DataProcessor.getStandardScale(maxEducationCount, true);
        int chartHeight = Math.min(10, standMaxEducationCount);

        String[] extremes = findMaxAndMinLabels(labels, counts);
        String mostCommonEducation = extremes[0];
        String leastCommonEducation = extremes[1];
        double topPercentage = maxEducationCount * 100.0 / totalUsers;

        StringBuilder report = new StringBuilder();
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
        ReportDAO.promptSaveReport(report.toString(), "users_by_education_" + genderFilter, REPORT_FOLDER);
    }

    public void generateUsersByAgeGroupReport(String genderFilter, String[] labels, int[] upperBounds) {
        int[] counts = new int[labels.length];
        int totalAge = 0;
        int totalUsers = 0;

        for (MapInterface.Entry<String, User> entry : userTree) {
            User user = entry.getValue();
            if (!matchesGender(user, genderFilter)) {
                continue;
            }

            int age = user.getAge();
            int groupIndex = resolveAgeGroupIndex(age, upperBounds);
            counts[groupIndex]++;
            totalUsers++;
            totalAge += age;
        }

        if (totalUsers == 0) {
            System.out.println("No user records found.");
            return;
        }

        int maxAgeGroupCount = getMax(counts);
        int standMaxAgeGroupCount = (int) DataProcessor.getStandardScale(maxAgeGroupCount, true);
        int chartHeight = Math.min(10, Math.max(1, standMaxAgeGroupCount));

        String[] extremes = findMaxAndMinLabels(labels, counts);
        String mostCommonAgeGroup = extremes[0];
        String leastCommonAgeGroup = extremes[1];
        double avgAge = totalAge * 1.0 / totalUsers;
        String formattedAge = String.format("%.2f", avgAge);

        StringBuilder report = new StringBuilder();
        UIGenerator.appendHeader(report, "USER ANALYSIS REPORT", "USERS BY AGE GROUP REPORT");

        UIGenerator.appendCenteredLine(report, "+----+------------------+-------------+");
        UIGenerator.appendCenteredLine(report, "| No | Age Group        | Total Users |");
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
        UIGenerator.appendCenteredLine(report, "Average User Age: " + formattedAge);
        UIGenerator.appendCenteredLine(report, "Most users are in the " + mostCommonAgeGroup + " group");
        UIGenerator.appendCenteredLine(report, "Fewest users are in the " + leastCommonAgeGroup + " group");
        UIGenerator.appendCenteredLine(report, "=".repeat(160));

        report.append("\n");
        UIGenerator.appendCenteredLine(report, "--User Age Group Distribution--");
        report.append("\n");

        int[] scaledCounts = new int[counts.length];
        for (int i = 0; i < counts.length; i++) {
            scaledCounts[i] = (standMaxAgeGroupCount == 0) ? 0 :
                (int) Math.round(((double) counts[i] / standMaxAgeGroupCount) * (chartHeight * 2));
        }
        UIGenerator.appendVerticalBarChart(report, labels, scaledCounts, labels.length, chartHeight, COL_WIDTH, standMaxAgeGroupCount, true);
        UIGenerator.appendFooter(report);

        System.out.println(report);
        ReportDAO.promptSaveReport(report.toString(), "users_by_age_group_" + genderFilter, REPORT_FOLDER);
    }

    public void generateTopBorrowingUsersReport(int topN) {
        if (userTree.isEmpty()) {
            System.out.println("No user records found.");
            return;
        }

        User[] topUsers = new User[Math.min(topN, userTree.size())];

        for (MapInterface.Entry<String, User> entry : userTree) {
            User current = entry.getValue();
            insertIntoTopUsers(topUsers, current);
        }

        String[] labels = new String[topUsers.length];
        int[] counts = new int[topUsers.length];

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

    private boolean matchesGender(User user, String genderFilter) {
        if ("ALL".equalsIgnoreCase(genderFilter)) {
            return true;
        }

        if (user.getGender() == null) {
            return false;
        }

        return user.getGender().toString().equalsIgnoreCase(genderFilter)
                || user.getGender().name().equalsIgnoreCase(genderFilter)
                || user.getGender().toString().replace(" ", "_").equalsIgnoreCase(genderFilter);
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

    private String[] buildCustomAgeLabels(int[] bounds) {
        String[] labels = new String[5];
        labels[0] = "<=" + bounds[0];
        labels[1] = (bounds[0] + 1) + "-" + bounds[1];
        labels[2] = (bounds[1] + 1) + "-" + bounds[2];
        labels[3] = (bounds[2] + 1) + "-" + bounds[3];
        labels[4] = (bounds[3] + 1) + "+";
        return labels;
    }

    private int resolveAgeGroupIndex(int age, int[] upperBounds) {
        for (int i = 0; i < upperBounds.length; i++) {
            if (age <= upperBounds[i]) {
                return i;
            }
        }
        return upperBounds.length;
    }

    private void insertIntoTopUsers(User[] topUsers, User candidate) {
        for (int i = 0; i < topUsers.length; i++) {
            if (topUsers[i] == null || candidate.getNumOfBooksBorrowing() > topUsers[i].getNumOfBooksBorrowing()) {

                for (int j = topUsers.length - 1; j > i; j--) {
                    topUsers[j] = topUsers[j - 1];
                }

                topUsers[i] = candidate;
                break;
            }
        }
    }

    private int inputGreaterThan(String prompt, int previous) {
        int value = UserPrompt.inputInteger(prompt, 1, 99);

        if (value <= previous) {
            System.out.println("Invalid input. Value must be greater than " + previous + ".");
            return -1;
        }

        return value;
    }

    private int sum(int[] counts) {
        int total = 0;
        for (int count : counts) {
            total += count;
        }
        return total;
    }

    private int getMax(int[] counts) {
        int max = counts[0];
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] > max) {
                max = counts[i];
            }
        }
        return max;
    }

    private String[] findMaxAndMinLabels(String[] labels, int[] counts) {
        if (labels == null || counts == null || labels.length == 0 || labels.length != counts.length) {
            return new String[]{"N/A", "N/A"};
        }

        int max = counts[0];
        int min = counts[0];

        for (int i = 1; i < counts.length; i++) {
            if (counts[i] > max) {
                max = counts[i];
            }
            if (counts[i] < min) {
                min = counts[i];
            }
        }

        StringBuilder maxLabels = new StringBuilder();
        StringBuilder minLabels = new StringBuilder();

        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == max) {
                if (!maxLabels.isEmpty()) {
                    maxLabels.append(", ");
                }
                maxLabels.append(labels[i]);
            }

            if (counts[i] == min) {
                if (!minLabels.isEmpty()) {
                    minLabels.append(", ");
                }
                minLabels.append(labels[i]);
            }
        }

        return new String[]{maxLabels.toString(), minLabels.toString()};
    }

    private String safeCut(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen - 3) + "...";
    }

    @SuppressWarnings("unchecked")
    public void retrieveSavedReportMenu() {
        UIGenerator.clearScreen();
        boolean isExit = false;

        do {
            UIGenerator.titleBuilder("Retrieve Saved User Reports");
            isExit = UIGenerator.optionMenu(
                    new String[]{
                            "[1] Users by Education Reports",
                            "[2] Users by Age Group Reports",
                            "[3] Top Borrowing Users Reports",
                            "[4] All User Reports",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[]{
                            () -> { ReportDAO.retrieveSavedReportByPrefix("users_by_education", savedReportTree, REPORT_FOLDER); return false; },
                            () -> { ReportDAO.retrieveSavedReportByPrefix("users_by_age_group", savedReportTree, REPORT_FOLDER); return false; },
                            () -> { ReportDAO.retrieveSavedReportByPrefix("top_borrowing_users", savedReportTree, REPORT_FOLDER); return false; },
                            () -> { ReportDAO.retrieveSavedReportByPrefix("", savedReportTree, REPORT_FOLDER); return false; }
                    }
            );
        } while (!isExit);
    }
}