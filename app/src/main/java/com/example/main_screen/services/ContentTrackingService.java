package com.example.main_screen.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.main_screen.utils.ProgressManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Локальное отслеживание просмотров (без Firebase).
 */
public class ContentTrackingService {
    private static final String PREFS = "technostrelka_viewed_content";

    public static void trackContent(Context context, String contentId, String contentType) {
        if (context == null || contentId == null) {
            return;
        }
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String key = "id_" + contentId;
        if (sp.contains(key)) {
            return;
        }
        sp.edit().putLong(key, System.currentTimeMillis()).apply();

        Map<String, Object> contentValues = new HashMap<>();
        contentValues.put("contentId", contentId);
        contentValues.put("contentType", contentType);
        contentValues.put("timestamp", System.currentTimeMillis());
        contentValues.put("isCompleted", true);

        ProgressManager.updateProgress(context, contentId, contentType);
    }
}
