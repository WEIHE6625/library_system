package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.User;

public class UserReportController {

    private final TreeMap<String, User> userTree;

    public UserReportController(TreeMap<String, User> userTree) {
        this.userTree = userTree;
    }

<<<<<<< HEAD
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
                            // "[4] Retrieve Saved Report",
                            "[0] Back"
                    },
                    "Select an option >> ",
                    new Supplier[] {
                            () -> { handleEducationReport(); return false; },
                            () -> { handleAgeGroupReport(); return false; },
                            () -> { handleTopBorrowingUsersReport(); return false; },
                            // () -> { retrieveSavedReportMenu(); return false; },
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
=======
    public int[] getEducationCounts(String genderFilter, int length) {
        int[] counts = new int[length];
>>>>>>> 6b78099032c896c382c780ec5dd649f82072ae9b

        for (MapInterface.Entry<String, User> entry : userTree) {
            User user = entry.getValue();

            if (matchesGender(user, genderFilter)) {
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
        }
        return counts;
    }

    public static class AgeReportData {
        public int[] counts;
        public int totalAgeSum;

        public AgeReportData(int[] counts, int totalAgeSum) {
            this.counts = counts;
            this.totalAgeSum = totalAgeSum;
        }
    }

    public AgeReportData getAgeGroupCounts(String genderFilter, int[] upperBounds, int length) {
        int[] counts = new int[length];
        int totalAge = 0;

        for (MapInterface.Entry<String, User> entry : userTree) {
            User user = entry.getValue();
            if (matchesGender(user, genderFilter)) {
                int age = user.getAge();
                int groupIndex = resolveAgeGroupIndex(age, upperBounds);
                counts[groupIndex]++;
                totalAge += age;
            }
        }
        return new AgeReportData(counts, totalAge);
    }

    public User[] getTopBorrowingUsers(int topN) {
        User[] topUsers = new User[Math.min(topN, userTree.size())];
        for (MapInterface.Entry<String, User> entry : userTree) {
            User candidate = entry.getValue();
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
        return topUsers;
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


    public String[] buildCustomAgeLabels(int[] bounds) {
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

    public int sum(int[] counts) {
        int total = 0;
        for (int count : counts) {
            total += count;
        }
        return total;
    }

    public int getMax(int[] counts) {
        int max = counts[0];
        for (int i = 1; i < counts.length; i++) {
            if (counts[i] > max) {
                max = counts[i];
            }
        }
        return max;
    }

    public String[] findMaxAndMinLabels(String[] labels, int[] counts) {
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
<<<<<<< HEAD

=======
>>>>>>> 6b78099032c896c382c780ec5dd649f82072ae9b
}