package com.mycompany.library_system.dao;

import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.utility.UserPrompt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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

    public static void saveReport(String reportContent, String reportPrefix, String reportFolder) {
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

    public static void loadSavedReports(TreeMap<String, String> savedReportTree, String reportFolder) {
        savedReportTree.clear();

        File folder = new File(reportFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().endsWith(".txt")) {
                savedReportTree.put(files[i].getName(), files[i].getPath());
            }
        }
    }

    public static void createReportFolderIfNotExists(String reportFolder) {
        File folder = new File(reportFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void displayReportFileContent(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to read report: " + e.getMessage());
        }
    }
}