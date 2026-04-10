package com.mycompany.library_system.dao;

import com.mycompany.library_system.utility.UserPrompt;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportDAO {

    public static void promptSaveReport(String reportContent, String reportPrefix, String reportFolder) {
        char choice = UserPrompt.inputChar("Do you want to save this report? (Y/N) >> ");

        if (choice == 'Y' || choice == 'y') {
            saveReport(reportContent, reportPrefix, reportFolder);
        } else {
            System.out.println("Report not saved.");
        }
    }

    private static void saveReport(String reportContent, String reportPrefix, String reportFolder) {
        createReportFolderIfNotExists(reportFolder);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = reportPrefix + "_" + timestamp + ".txt";
        String fullPath = reportFolder + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath))) {
            writer.write(reportContent);
            System.out.println("Report saved successfully to: " + fullPath);
        } catch (IOException e) {
            System.out.println("Failed to save report: " + e.getMessage());
        }
    }

    public static void createReportFolderIfNotExists(String reportFolder) {
        File folder = new File(reportFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}