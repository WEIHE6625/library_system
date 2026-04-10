package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.User;

public class UserReportController {

    private final TreeMap<String, User> userTree;

    public UserReportController(TreeMap<String, User> userTree) {
        this.userTree = userTree;
    }

    public int[] getEducationCounts(String genderFilter, int length) {
        int[] counts = new int[length];

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
}