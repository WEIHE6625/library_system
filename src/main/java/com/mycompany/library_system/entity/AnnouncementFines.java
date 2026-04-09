package com.mycompany.library_system.entity;

import java.time.LocalDate;
/**
 *
 * @author ZhenHao
 */
public class AnnouncementFines extends Announcement {
    private String targetUserId;

    public AnnouncementFines(String id, String title, String content, String adminId, LocalDate createdDate, String editAdminId, LocalDate editDate, String targetUserId) {
        super(id, title, content, adminId, createdDate, editAdminId, editDate);
        this.targetUserId = targetUserId; // Now it correctly assigns the ID!
    }

    public String getTargetUserId() {
        return targetUserId;
    }
}