package com.mycompany.library_system.control;

import java.util.function.Supplier;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BookUI;
import com.mycompany.library_system.boundary.UserUI;
import com.mycompany.library_system.boundary.ReportUI;
import com.mycompany.library_system.dao.*;
import com.mycompany.library_system.entity.*;
import com.mycompany.library_system.utility.DateUtil;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

public class ReportInfoController{
    private TreeMap<String, Report> reportTree;
    private TreeMap<String, Report> reportTitleTree;
    private TreeMap<String, Report> generatedDateTree;
    private TreeMap<String, Report> searchTree = new TreeMap<>();
    
    private TreeMap<String, Book> bookTree;
    private TreeMap<String, User> userTree;
    private TreeMap<String, BookBorrowRecord> bookBorrowRecordTree;

    public static final String REPORT_FILE_PATH = "database/report.csv";

    public ReportInfoController(){
        System.out.println("Initializing...");
        this.reportTree = ReportDAO.readFile(REPORT_FILE_PATH);
        this.reportTitleTree = new TreeMap<>();
        this.generatedDateTree = new TreeMap<>();

        System.out.println("Matching...");
        for(Report report: reportTree.values()){
            reportTitleTree.put(IdGenerator.getKeyId(report.getReportTitle(), report.getReportId()), report);
            generatedDateTree.put(IdGenerator.getKeyId(report.getGeneratedDate(), report.getReportId()), report);

            
        }
    }


    public void generateNewReport(){
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Add new report");
        
        String id = IdGenerator.generateId(reportTree, "RP", 3);
        String selectedInputs = ReportUI.choseRequiredReports();
        String reportTitle = UserPrompt.inputString("Enter a Title for this Report: ");

        // 1. Conditional Data Loading (Efficiency)
        TreeMap<String, User> currentUsers = selectedInputs.contains("1") || selectedInputs.contains("3") ? UserDAO.readFile("database/users.csv") : null;
        TreeMap<String, BookBorrowRecord> borrowRecords = selectedInputs.contains("2") ? BookBorrowRecordDAO.readFile("database/borrow_records.csv") : null;
        TreeMap<String, BorrowFineRecord> fineRecords = selectedInputs.contains("3") ? BorrowFineRecordDAO.readFile("database/fine_records.csv") : null;
        TreeMap<String, Book> currentBooks = selectedInputs.contains("4") ? BookDAO.readFile("database/books.csv") : null;

        // 2. Data Processing (Building the Report Content)
        StringBuilder content = new StringBuilder();
        content.append("Report ID: ").append(id).append("\n");
        content.append("Generated Date: ").append(DateUtil.getCurrentDate()).append("\n\n");

        if (selectedInputs.contains("1") && currentUsers != null) {
            content.append("--- User List ---\n");
            for (User u : currentUsers.values()) content.append(u.getUserId()).append(" - ").append(u.getName()).append("\n");
        }
        
        if (selectedInputs.contains("2") && borrowRecords != null) {
            content.append("\n--- Borrow Status ---\n");
            // Logic to count or list borrows...
            content.append("Total active borrows: ").append(borrowRecords.size()).append("\n");
        }

        // ... (Add logic for Fine Status '3' and Category '4' similarly) ...

        // 3. Create the Report Object
        String currentDate = DateUtil.getCurrentDate();
        Report newReport = new Report(id, reportTitle, content.toString(), currentDate);

        // 4. Update the Controller's Trees
        reportTree.put(id, newReport);
        reportTitleTree.put(IdGenerator.getKeyId(reportTitle, id), newReport);
        generatedDateTree.put(IdGenerator.getKeyId(currentDate, id), newReport);

        // 5. Save to Database (Physical File)
        ReportDAO.saveToFile(REPORT_FILE_PATH, reportTree);

        System.out.println("\nReport generated successfully with ID: " + id);
        UserPrompt.pressEnterToContinue();


    }
}