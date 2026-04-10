package com.mycompany.library_system.entity;

import java.time.LocalDate;
/**
 *
 * @author ZhenHao
 */
public class AnnouncementEduLevel extends Announcement{
    private String eduLevel;
    public AnnouncementEduLevel(String id, String title,
                                String content, String adminId,
                                LocalDate createdDate, String editAdminId,
                                LocalDate editDate, String eduLevel){
        super(id, title, content, adminId, createdDate, editAdminId, editDate);
        this.eduLevel = eduLevel;
    }

    public String getEduLevel() {return eduLevel;}
    public void setEduLevel(String eduLevel) {this.eduLevel = eduLevel;}
}
