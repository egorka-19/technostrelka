package com.example.main_screen.model;

public class UserScore {
    private int score;
    private long lastUpdated;

    public UserScore() {
        // Пустой конструктор для Firebase
    }

    public UserScore(int score, long lastUpdated) {
        this.score = score;
        this.lastUpdated = lastUpdated;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 