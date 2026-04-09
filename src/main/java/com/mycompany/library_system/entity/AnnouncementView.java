package com.mycompany.library_system.entity;

import java.time.LocalDateTime;
/**
 *
 * @author ZhenHao
 */
public class AnnouncementView {
    private String announcementId;
    private String userId;
    private LocalDateTime viewTimestamp;

    public AnnouncementView(String announcementId, String userId, LocalDateTime viewTimestamp) {
        this.announcementId = announcementId;
        this.userId = userId;
        this.viewTimestamp = viewTimestamp;
    }

    public String getAnnouncementId() { return announcementId; }
    public String getUserId() { return userId; }
    public LocalDateTime getViewTimestamp() { return viewTimestamp; }
}