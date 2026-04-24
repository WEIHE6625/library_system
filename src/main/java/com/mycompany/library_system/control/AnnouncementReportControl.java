package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.*;
import com.mycompany.library_system.dao.AnnouncementViewDAO;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.DataProcessor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author ZhenHao
 */
public class AnnouncementReportControl {
    private MapInterface<String, Announcement> announcementTree;
    private final String VIEW_LOG_PATH;
    private final String REPORT_DIR = "./database/reports/announcement_reports/";

    public AnnouncementReportControl(MapInterface<String, Announcement> tree, String viewLogPath) {
        this.announcementTree = tree;
        this.VIEW_LOG_PATH = viewLogPath;
    }

    public String generateDistributionReport() {
        StringBuilder sb = new StringBuilder();
        UIGenerator.appendHeader(sb, "ANNOUNCEMENT ANALYSIS", "CATEGORY DISTRIBUTION");

        int broadcast = 0, edu = 0, fine = 0;
        for (Announcement a : announcementTree.values()) {
            if (a instanceof AnnouncementBroadcast) broadcast++;
            else if (a instanceof AnnouncementEduLevel) edu++;
            else if (a instanceof AnnouncementFines) fine++;
        }

        String[] labels = {"BROADCAST", "EDUCATION", "FINES"};
        int[] rawValues = {broadcast, edu, fine};
        int chartSize = labels.length;

        double maxVal = 0;
        for (int v : rawValues) if (v > maxVal) maxVal = v;

        double standMaxVal = DataProcessor.getStandardScale(maxVal, false);
        int chartHeight = Math.min(10, (int) Math.round(standMaxVal));

        int[] scaledValues = new int[chartSize];
        for (int i = 0; i < chartSize; i++) {
            scaledValues[i] = (standMaxVal == 0) ? 0 : (int) Math.round((rawValues[i] / standMaxVal) * (chartHeight * 2));
        }

        UIGenerator.appendCenteredLine(sb, "--- ANNOUNCEMENT TYPE DISTRIBUTION ---");
        sb.append("\n");

        UIGenerator.appendVerticalBarChart(sb, labels, scaledValues, chartSize, chartHeight, 13, standMaxVal, false);

        sb.append("\n\n");

        sb.append("+---------------------------+------------+\n");
        sb.append(String.format("| %-25s | %-10s |\n", "CATEGORY", "COUNT"));
        sb.append("+---------------------------+------------+\n");
        sb.append(String.format("| %-25s | %-10d |\n", "Broadcasts", broadcast));
        sb.append(String.format("| %-25s | %-10d |\n", "Education Level", edu));
        sb.append(String.format("| %-25s | %-10d |\n", "Fines (Auto)", fine));
        sb.append("+---------------------------+------------+\n");
        sb.append(" Total Announcements: ").append(announcementTree.size()).append("\n");

        UIGenerator.appendFooter(sb);
        return sb.toString();
    }
    public void exportToFile(String content, String fileNamePrefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        String finalName = fileNamePrefix + "_" + timestamp + ".txt";

        File dir = new File(REPORT_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (FileWriter fw = new FileWriter(REPORT_DIR + finalName)) {
            fw.write(content);
            System.out.println("\nReport exported to: " + REPORT_DIR + finalName);
        } catch (IOException e) {
            System.out.println("Export Error: " + e.getMessage());
        }
    }

    public String generateEngagementReport() {
        StringBuilder sb = new StringBuilder();
        UIGenerator.appendHeader(sb, "ANNOUNCEMENT ANALYSIS", "USER ENGAGEMENT SUMMARY");


        MapInterface<String, AnnouncementView> viewMap = AnnouncementViewDAO.readFile(this.VIEW_LOG_PATH);

        MapInterface<String, Integer> counts = new TreeMap<>();
        for (MapInterface.Entry<String, AnnouncementView> entry : viewMap) {
            String aid = entry.getValue().getAnnouncementId();
            int current = counts.containsKey(aid) ? counts.get(aid) : 0;
            counts.put(aid, current + 1);
        }

        int chartSize = counts.size();
        String[] labels = new String[chartSize];
        int[] rawValues = new int[chartSize];

        double maxViews = 0;
        int index = 0;

        for (MapInterface.Entry<String, Integer> entry : counts) {
            labels[index] = entry.getKey();
            rawValues[index] = entry.getValue();

            if (rawValues[index] > maxViews) {
                maxViews = rawValues[index];
            }
            index++;
        }

        double standMaxViews = DataProcessor.getStandardScale(maxViews, false);
        int chartHeight = Math.min(10, (int) Math.round(standMaxViews));

        int[] scaledValues = new int[chartSize];
        for (int i = 0; i < chartSize; i++) {
            scaledValues[i] = (standMaxViews == 0) ? 0 : (int) Math.round((rawValues[i] / standMaxViews) * (chartHeight * 2));
        }

        UIGenerator.appendCenteredLine(sb, "--- VIEWS PER ANNOUNCEMENT ID ---");
        sb.append("\n");

        UIGenerator.appendVerticalBarChart(sb, labels, scaledValues, chartSize, chartHeight, 13, standMaxViews, false);
        sb.append("\n\n");

        sb.append("+----------+--------------------------------+------------+\n");
        sb.append(String.format("| %-8s | %-30s | %-10s |\n", "ID", "TITLE", "VIEWS"));
        sb.append("+----------+--------------------------------+------------+\n");

        if (counts.isEmpty()) {
            sb.append("|            No engagement data found.                   |\n");
        } else {
            for (MapInterface.Entry<String, Integer> entry : counts) {
                String aid = entry.getKey();
                Announcement a = announcementTree.get(aid);
                String title = (a != null) ? a.getTitle() : "Unknown/Deleted";

                sb.append(String.format("| %-8s | %-30s | %-10d |\n",
                        aid,
                        DataProcessor.truncate(title, 30),
                        entry.getValue()));
            }
        }
        sb.append("+----------+--------------------------------+------------+\n");
        sb.append(" Total Views: ").append(viewMap.size()).append("\n");

        UIGenerator.appendFooter(sb);
        return sb.toString();
    }

}