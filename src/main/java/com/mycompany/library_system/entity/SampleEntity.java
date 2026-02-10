package com.mycompany.library_system.utility;

public class SampleEntity {
    private String id;
    private String name;
    private int score;

    public SampleEntity(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SampleEntity{name='" + name + "', score=" + score + "}";
    }
}
