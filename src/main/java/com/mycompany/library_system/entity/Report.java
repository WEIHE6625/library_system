package com.mycompany.library_system.entity;

import java.time.LocalDate;

public class Report{
    private String reportId;
    private String reportTitle;
    private LocalDate generatedDate;

    private String content;


    //Constructor for loading report from CSV
    public Report(String reportId, String reportTitle, LocalDate generatedDate){
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.generatedDate = generatedDate;
    }

    //Constructor for creating new report with current date
    public Report(String reportId, String reportTitle) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.generatedDate = LocalDate.now();
    }

    //Full constructor
    public Report(String reportId, String reportTitle, String content, LocalDate generatedDate){
        this.reportId = reportId;
        this.reportTitle =  reportTitle;
        this.generatedDate = generatedDate;
        this.content = content;
    }

    public String getReportId(){
        return this.reportId;
    }

    public String getReportTitle(){
        return this.reportTitle;
    }

    public LocalDate getGeneratedDate(){
        return this.generatedDate;
    }

    public String getContent(){
        return this.content;
    }

    public void setContent(String content){
        this.content = content;
    }


    @Override
    public String toString(){
        return  "Report ID " + reportId + "\n" + 
                "Report Title " + reportTitle + "\n" +
                "Generated Date " + generatedDate;
        
    }
    }