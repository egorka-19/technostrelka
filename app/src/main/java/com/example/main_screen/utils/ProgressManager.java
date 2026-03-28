package com.example.main_screen.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ProgressManager {
    private static final String PREFS = "technostrelka_progress";
    private static final String KEY_PROGRESS = "progress_value";
    private static final int MAX_PROGRESS = 100;
    private static final int ARTICLE_POINTS = 5;
    private static final int VIDEO_POINTS = 10;

    public static void updateProgress(Context context, String contentId, String contentType) {
        if (context == null) {
            return;
        }
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int currentProgress = sp.getInt(KEY_PROGRESS, 0);
        int newPoints = "article".equals(contentType) ? ARTICLE_POINTS : VIDEO_POINTS;
        int newProgress = Math.min(currentProgress + newPoints, MAX_PROGRESS);
        sp.edit().putInt(KEY_PROGRESS, newProgress).apply();
    }

    public static void resetProgress(Context context) {
        if (context == null) {
            return;
        }
        context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_PROGRESS, 0)
                .apply();
    }

    public static int getProgress(Context context) {
        if (context == null) {
            return 0;
        }
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getInt(KEY_PROGRESS, 0);
    }
}
