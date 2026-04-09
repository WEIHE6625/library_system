package com.mycompany.library_system.entity;

import java.time.LocalDate;

/**
 *
 * @author ZhenHao
 */

public class AnnouncementBroadcast extends Announcement{
    public AnnouncementBroadcast(String id, String title, String content,
                                 String adminId, LocalDate createdDate, String editAdminId, LocalDate editDate) {
        super(id, title, content, adminId, createdDate, editAdminId, editDate);

    }

    @Override
    public String toString(){
        return "GLOBAL ANNOUCNEMENT" + "\n" + super.toString();
    }
}
