package com.example.main_screen.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Локальное избранное маршрутов (порядок добавления сохраняется).
 */
public final class RouteFavoritePreferences {

    private static final String PREF = "profile_route_favorites_v1";
    private static final String KEY_ORDERED = "ordered_keys";

    private RouteFavoritePreferences() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static List<String> getOrderedKeys(Context context) {
        String raw = prefs(context).getString(KEY_ORDERED, "");
        if (TextUtils.isEmpty(raw)) {
            return new ArrayList<>();
        }
        String[] parts = raw.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p != null && !p.isEmpty()) {
                out.add(p);
            }
        }
        return out;
    }

    private static void saveOrdered(Context context, List<String> keys) {
        prefs(context).edit().putString(KEY_ORDERED, TextUtils.join(",", keys)).apply();
    }

    public static boolean isFavorite(Context context, String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return getOrderedKeys(context).contains(key);
    }

    public static void add(Context context, String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        List<String> list = new ArrayList<>(getOrderedKeys(context));
        if (!list.contains(key)) {
            list.add(key);
            saveOrdered(context, list);
        }
    }

    public static void remove(Context context, String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        List<String> list = new ArrayList<>(getOrderedKeys(context));
        list.remove(key);
        saveOrdered(context, list);
    }

    public static void toggle(Context context, String key) {
        if (isFavorite(context, key)) {
            remove(context, key);
        } else {
            add(context, key);
        }
    }
}
