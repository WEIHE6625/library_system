package com.mycompany.library_system.entity;

import java.time.LocalDate;

/**
 *
 * @author ZhenHao
 */

public class Announcement {
    private String id;
    private String title;
    private String content;
    private String adminId;
    private LocalDate createdDate;
    private String editAdminId;
    private LocalDate editDate;

    public Announcement( String id, String title,
                        String content, String adminId,
                        LocalDate createdDate, String editAdminId, LocalDate editDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.adminId = adminId;
        this.createdDate = createdDate;
        this.editAdminId = editAdminId;
        this.editDate = editDate;
    }

    public String getId(){return id;}
    public String getTitle(){return title;}
    public String getContent(){return content;}
    public String getAdminId(){return adminId;}
    public LocalDate getCreatedDate(){return createdDate;}
    public String getEditAdminId(){return editAdminId;}
    public LocalDate getEditDate(){return editDate;}

    public void setTitle(String title){this.title = title;}
    public void setContent(String content){this.content = content;}
    public void setEditAdminId(String editAdminId){this.editAdminId = editAdminId;}
    public void setEditDate(LocalDate editDate){this.editDate = editDate;}
    @Override
    public String toString(){
        return "Announcement ID :" + id + "\n" +
                "Announcement Title :" + title + "\n" +
                "Announcement Content :" + content + "\n";
    }
}
