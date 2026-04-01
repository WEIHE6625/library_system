package com.mycompany.library_system.dao;

import java.io.*;

import java.time.LocalDate;
import com.mycompany.library_system.adt.ArrayList;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.Report;
import com.mycompany.library_system.utility.DataProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;

public class ReportDAO {

    public static TreeMap<String, Report> readFile(String filePath){
        TreeMap<String, Report> reportList = new TreeMap<>();
        try(CSVReader reader = new CSVReader(new FileReader(filePath))){
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try(ProgressBar pb = new ProgressBar("Loading Reports :" ,totalLines)){
                while((data = reader.readNext())!= null){
                    pb.step();

                    if (data.length ==3){
                        try{
                            String id = data[0].trim();
                            String title = data[1].trim();
                            String dateString = data[2].trim();
                            LocalDate generatedDate = LocalDate.parse(dateString);

                            Report report = new Report(id,title,generatedDate);
                            reportList.put(id, report);
                        }catch (Exception e ){
                            System.out.println("Skipping invalid row " + e.getMessage());
                        }
                    }
                }

            }
        } catch(CsvValidationException e1){
            e1.printStackTrace();
        } catch(Exception e ){
            e.printStackTrace();
        }
        return reportList;
    }


    public static void writeFile(String filePath, TreeMap<String, Report> reportTree){
        try(CSVWriter writer = new CSVWriter(new FileWriter(filePath))){
            String[] header = {"Report ID", "Report Title", "Generated Date"};
            writer.writeNext(header, false);

            for(Report report : reportTree.values()){

                String[] data = {
                    report.getReportId(),
                    report.getReportTitle(),
                    report.getGeneratedDate().toString(),
                };

                writer.writeNext(data, false);



            }

        
            System.out.println("Data successfully saved to " + filePath);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    
    public static void saveReportDetails(Report report) {
            String directoryPath = "database/reports";
            File directory = new File(directoryPath);
            
            // Create directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = report.getReportId() + ".txt";
            File file = new File(directory, fileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write the full report content (the string we built in Controller)
                writer.write(report.getContent()); 
                System.out.println("Detailed report saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error saving detailed report: " + e.getMessage());
            }
        }


    public static boolean deleteReportFile(String reportId) {
        String filePath = "database/reports/" + reportId + ".txt";
        File file = new File(filePath);

        if (file.exists()) {
            return file.delete(); // Returns true if successfully deleted
        }
        return false;
    }

    public static void loadReportContent(Report report) {
        String filePath = "database/reports/" + report.getReportId() + ".txt";
        File file = new File(filePath);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder fullContent = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    fullContent.append(line).append("\n");
                }

                // CRITICAL: You must "hand" the text to the Report object!
                report.setContent(fullContent.toString());

            } catch (IOException e) {
                System.err.println("Error reading content for " + report.getReportId());
                e.printStackTrace();
            }
        } else {
            System.out.println("Warning: .txt file not found for " + report.getReportId());
        }
    }

}