package com.mycompany.library_system.control;

import java.time.LocalDate;
import java.util.function.Supplier;

import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.control.ReportService;
import com.mycompany.library_system.boundary.ReportUI;
import com.mycompany.library_system.boundary.UserUI;
import com.mycompany.library_system.dao.*;
import com.mycompany.library_system.entity.*;
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

        //Title
        StringBuilder titleBuilder = new StringBuilder();
        if (selectedInputs.contains("1")) titleBuilder.append("User List ");
        if (selectedInputs.contains("2")) titleBuilder.append("Borrow Status ");
        if (selectedInputs.contains("3")) titleBuilder.append("Fine Status ");
        if (selectedInputs.contains("4")) titleBuilder.append("Book Category ");
        String reportTitle = titleBuilder.toString().trim().replace(" ", ", " )+ "Report";

        //Conditional Data Loading for only when needed
        TreeMap<String, User> currentUsers = isDataNeeded(selectedInputs, "user")
                ? UserDAO.readFile("database/users.csv") : null;

        TreeMap<String, BookBorrowRecord> borrowRecords = isDataNeeded(selectedInputs, "borrow")
                ? BookBorrowDAO.readFile("database/borrowBookRecords.csv") : null;

        TreeMap<String, BorrowFineRecord> fineRecords = isDataNeeded(selectedInputs, "fine")
                ? BookBorrowFineDAO.readFile("database/borrowFineRecords.csv") : null;

        TreeMap<String, Book> currentBooks = isDataNeeded(selectedInputs, "book")
                ? BookDAO.readFile("database/book.csv") : null;
        LocalDate [] range;
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.now();
        String reportContent = "";
        switch (selectedInputs) {
            case "1":
                range = ReportUI.inputDateRange();
                fromDate = range[0];
                toDate = range[1];
                reportContent = ReportService.buildUserSummaryReport(currentUsers, fromDate, toDate);
                break;

            case "2":
                range = ReportUI.inputDateRange();
                fromDate = range[0];
                toDate = range[1];
                reportContent = ReportService.buildBorrowSummaryReport(borrowRecords, fromDate, toDate);
                break;

            case "3":
                LocalDate[] fineRange = ReportUI.inputDateRange();
                reportContent = ReportService.buildGlobalFineSummaryReport(fineRecords, fineRange[0], fineRange[1]);
                break;

            case "4":
                int[] params = ReportUI.inputCategoryExtremeParams();
                reportContent = ReportService.buildCategoryExtremeReport(currentBooks,
                        params[4], // limit
                        params[0], // startYear
                        params[1], // startMonth
                        params[2], // endYear
                        params[3]); // endMonth);
                break;

            case "12":
                String id12 = UserUI.inputUserID(currentUsers);

                LocalDate[] range12 = ReportUI.inputDateRange();
                if(!hasUserBorrow(id12, borrowRecords)){
                    UIGenerator.clearScreen();
                    System.out.println("This user has no borrow records!");
                    UIGenerator.pressEnterToContinue();
                    return;
                }
                reportContent = ReportService.buildSingleUserBorrowReport(id12, currentUsers, borrowRecords,range12[0], range12[1]);
                break;

            case "13":
                String id13 = UserUI.inputUserID(currentUsers);
                LocalDate[] fineRange13 = ReportUI.inputDateRange();
                boolean userHasFine = false;
                ListInterface<BorrowFineRecord> allFines = fineRecords.values();
                for (int i = 0; i < allFines.size(); i++) {
                    if (allFines.get(i).getUserId().equals(id13)) {
                        userHasFine = true;
                        break;
                    }
                }

                if (!userHasFine) {
                    UIGenerator.clearScreen();
                    System.out.println("Process Cancelled: This user has no fine records!");
                    UIGenerator.pressEnterToContinue();
                    return;
                }

                reportContent = ReportService.buildUserFineReport(id13, currentUsers, fineRecords, fineRange13[0],fineRange13[1]);
                break;

            case "14":
                String id14 = UserUI.inputUserID(currentUsers);
                LocalDate[] borrowRange14 = ReportUI.inputDateRange();
                if(!hasUserBorrow(id14, borrowRecords)){
                    UIGenerator.clearScreen();
                    System.out.println("This user has no borrow records!");
                    UIGenerator.pressEnterToContinue();
                    return;
                }

                reportContent = ReportService.buildUserInterestReport(id14,
                        currentUsers, borrowRecords,
                        currentBooks, borrowRange14[0], borrowRange14[1]);
                break;

            case "23":
                LocalDate[] healthRange = ReportUI.inputDateRange();
                reportContent = ReportService.buildOperationsHealthReport(borrowRecords, fineRecords
                ,healthRange[0], healthRange[1]);
                break;

            case "24":
                LocalDate[] borrowRange24 = ReportUI.inputDateRange();
                reportContent = ReportService.buildPopularCategoryReport(borrowRecords, currentBooks, borrowRange24[0], borrowRange24[1]);
                break;
            default:
                System.out.println("Invalid report selection.");
                return;
        }

        LocalDate currentDate = LocalDate.now();
        Report newReport = new Report(id, reportTitle, currentDate);
        newReport.setContent(reportContent);


        reportTree.put(id, newReport);
        reportTitleTree.put(IdGenerator.getKeyId(reportTitle, id), newReport);
        generatedDateTree.put(IdGenerator.getKeyId(currentDate, id), newReport);

        // Save to Database (Physical File)
        ReportDAO.saveReportDetails(newReport);
        ReportDAO.writeFile(REPORT_FILE_PATH, reportTree);

        System.out.println("\nReport generated successfully with ID: " + id);


    }

    public void deleteReport() {
        UIGenerator.titleBuilder("Delete Report");

        // 1. Get the ID from the user (We'll use a UI helper for this)
        String id = ReportUI.inputReportID(reportTree);

        if (id.isEmpty()) return; // User cancelled

        // 2. Confirm deletion
        String prompt = "Are you sure you want to delete report " + id  + "? (Y/N):" ;
        char confirm = UserPrompt.inputChar(prompt, 'Y' , 'N');


        if (confirm == 'Y') {
            // 3. Remove from memory (Trees)
            Report reportToDelete = reportTree.get(id);
            if (reportToDelete != null) {
                reportTitleTree.remove(IdGenerator.getKeyId(reportToDelete.getReportTitle(), id));
                generatedDateTree.remove(IdGenerator.getKeyId(reportToDelete.getGeneratedDate().toString(), id));
                reportTree.remove(id);

                // 4. Update CSV (The Index)
                ReportDAO.writeFile(REPORT_FILE_PATH, reportTree);

                // 5. Delete physical .txt file
                boolean fileDeleted = ReportDAO.deleteReportFile(id);

                if (fileDeleted) {
                    System.out.println("Report and physical file deleted successfully.");
                } else {
                    System.out.println("Report removed from list, but .txt file was not found.");
                }
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
        UIGenerator.pressEnterToContinue();
    }

    public TreeMap<String, Report> getReportTree() {
        return reportTree;
    }

    public void viewReportDetails() {
        String id = ReportUI.inputReportID(reportTree);
        if (id.isEmpty()) return;

        Report selectedReport = reportTree.get(id);

        ReportDAO.loadReportContent(selectedReport);

        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Viewing Report: " + id);
        System.out.println(selectedReport.getContent());

        UIGenerator.pressEnterToContinue();
    }

    private static boolean hasUserBorrow(String targetId, TreeMap<String, BookBorrowRecord> recordMap){
        if(recordMap == null || recordMap.isEmpty()) return false;

        ListInterface<BookBorrowRecord> allRecords = recordMap.values();

        for(int i =0 ; i< allRecords.size(); i ++){
            if(allRecords.get(i).getUserId().equalsIgnoreCase(targetId)){
                return true;
            }
        }
        return false;
    }

    private static boolean isDataNeeded(String input, String domain) {
        return switch (input) {
            case "1" -> domain.equals("user");
            case "2" -> domain.equals("borrow");
            case "3" -> domain.equals("fine");
            case "4" -> domain.equals("book");
            case "12" -> domain.equals("user") || domain.equals("borrow");
            case "13" -> domain.equals("user") || domain.equals("fine");
            case "14" -> domain.equals("user") || domain.equals("borrow") || domain.equals("book");
            case "23" -> domain.equals("borrow") || domain.equals("fine");
            case "24" -> domain.equals("borrow") || domain.equals("book");
            default -> false;
        };
    }
}