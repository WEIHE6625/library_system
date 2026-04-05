package com.mycompany.library_system.entity;

public class User {
    private final String userId;
    private String name;
    private String email;
    private Gender gender;
    private int age;
    private Education education;
    private UserRole role;   // MEMBER, ADMIN
    private int numOfBooksBorrwing;
    private final String createdAt;

    // Constructor
    public User(String userId, String name, String email, Gender gender, int age, Education education, UserRole role, String createdAt, int numOfBooksBorrwing) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.education = education;
        this.role = role;
        this.numOfBooksBorrwing = numOfBooksBorrwing;
        this.createdAt = createdAt;
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum UserRole {
        ADMIN,
        MEMBER
    }

    public enum Education {
        PRIMARY_SCHOOL,
        SECONDARY_SCHOOL,
        DIPLOMA,
        BACHELOR,
        MASTER,
        PHD,
        OTHER
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Education getEducation() {
        return education;
    }

    public void setEducation(Education education) {
        this.education = education;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getNumOfBooksBorrowing(){
        return numOfBooksBorrwing;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setNumberOfBooksBorrowing(int numOfBooksBorrwing) {
        this.numOfBooksBorrwing = numOfBooksBorrwing;
    }

    public String adminEditToString() {
        return """
            ---------------- User Profile ----------------
            Name                   : %s
            Email                  : %s
            Gender                 : %s
            Age                    : %s
            Education              : %s
            Role                   : %s
            ----------------------------------------------
            """.formatted(name, email, gender, age, education, role);
    }
    public String memberEditToString() {
        return """
            ---------------- User Profile ----------------
            Name                   : %s
            Email                  : %s
            Gender                 : %s
            Age                    : %s
            Education              : %s
            ----------------------------------------------
            """.formatted(name, email, gender, age, education);
    }
    @Override
    public String toString() {
        return """
            ---------------- User Profile ----------------
            User ID                : %s
            Name                   : %s
            Email                  : %s
            Gender                 : %s
            Age                    : %s
            Education              : %s
            Role                   : %s
            Create Time            : %s
            No. of Books Borrowing : %s
            ----------------------------------------------
            """.formatted(userId, name, email, gender, age, education, role, createdAt, numOfBooksBorrwing);
    }
}
