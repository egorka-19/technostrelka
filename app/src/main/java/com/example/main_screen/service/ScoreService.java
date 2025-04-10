package com.example.main_screen.service;

public class ScoreService {
    private static ScoreService instance;
    private int score = 0;

    private ScoreService() {
        // Private constructor to prevent instantiation
    }

    public static ScoreService getInstance() {
        if (instance == null) {
            instance = new ScoreService();
        }
        return instance;
    }

    public void incrementScore() {
        score += 10;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }
} 