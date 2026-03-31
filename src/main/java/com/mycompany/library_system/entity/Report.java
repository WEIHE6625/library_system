package com.mycompany.library_system.entity;

import java.time.LocalDate;

public class Report{
    private String reportId;
    private String reportTitle;
    private LocalDate generatedDate;

    public Report(String reportId, String reportTitle, LocalDate generatedDate){
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.generatedDate = generatedDate
    }

    public Report(String reportId, String reportTitle) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.generatedDate = LocalDate.now()
    }

    public String getReportId(){
        return this.reportId;
    }

    public String getReportTitle(){
        return this.reportTitle;
    }

    public LocalDate getGeneratedDate(){
        return this.generatedDate
    }



    @Override
    public String toString(){
        return  "Report ID " + reportId + "\n" + 
                "Report Title " + reportTitle + "\n" +
                "Generated Date " + generatedDate;
        
    }
    }