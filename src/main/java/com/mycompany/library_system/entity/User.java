package com.mycompany.library_system.entity;
import java.time.LocalDateTime;

public class User {
    private final String userId;
    private String name;
    private String email;
    private String role;   // MEMBER, ADMIN
    private final LocalDateTime createdAt;

    // Constructor
    public User(String userId, String name, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String toCSV() {
        return String.join(",", userId, name, email, role);
    }

    public static User fromCSV(String line) {
        String[] parts = line.split(",", -1);
        return new User(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return """
            -------- User Profile --------
            User ID : %s
            Name    : %s
            Email   : %s
            Role    : %s
            ------------------------------
            """.formatted(userId, name, email, role);
    }
}
