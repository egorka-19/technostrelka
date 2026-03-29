package com.example.main_screen.service;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class ScoreService {

    private static final String PREF = "app_score_v1";
    private static final String KEY_SCORE = "score";

    private static ScoreService instance;
    private static Context app;
    private int score = 0;

    private ScoreService() {
    }

    /** Вызвать из {@link Application#onCreate()} до использования очков. */
    public static void init(Application application) {
        app = application.getApplicationContext();
        if (instance == null) {
            instance = new ScoreService();
            instance.load();
        }
    }

    public static ScoreService getInstance() {
        if (instance == null) {
            instance = new ScoreService();
            instance.load();
        }
        return instance;
    }

    private SharedPreferences prefs() {
        if (app == null) {
            return null;
        }
        return app.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private void load() {
        SharedPreferences p = prefs();
        if (p != null) {
            score = p.getInt(KEY_SCORE, 0);
        }
    }

    private void persist() {
        SharedPreferences p = prefs();
        if (p != null) {
            p.edit().putInt(KEY_SCORE, score).apply();
        }
    }

    public void incrementScore() {
        score += 10;
        persist();
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
        persist();
    }
} 