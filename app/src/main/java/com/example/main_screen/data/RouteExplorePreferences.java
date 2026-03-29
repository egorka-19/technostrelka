package com.example.main_screen.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Какие остановки маршрута пользователь уже раскрывал и завершён ли маршрут целиком (для прогресса профиля).
 */
public final class RouteExplorePreferences {

    private static final String PREF = "route_explore_v1";
    private static final String KEY_COMPLETED = "completed_route_keys";

    private RouteExplorePreferences() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    private static String openedKey(String routeKey) {
        return "opened_" + routeKey.hashCode() + "_" + routeKey.length();
    }

    private static Set<Integer> loadOpened(Context context, String routeKey) {
        Set<Integer> out = new HashSet<>();
        if (TextUtils.isEmpty(routeKey)) {
            return out;
        }
        String raw = prefs(context).getString(openedKey(routeKey), "");
        if (TextUtils.isEmpty(raw)) {
            return out;
        }
        for (String p : raw.split(",")) {
            if (TextUtils.isEmpty(p)) {
                continue;
            }
            try {
                out.add(Integer.parseInt(p.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return out;
    }

    private static void saveOpened(Context context, String routeKey, Set<Integer> indices) {
        if (TextUtils.isEmpty(routeKey)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Integer i : indices) {
            if (i == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(i);
        }
        prefs(context).edit().putString(openedKey(routeKey), sb.toString()).apply();
    }

    private static boolean isRouteCompleted(Context context, String routeKey) {
        if (TextUtils.isEmpty(routeKey)) {
            return true;
        }
        Set<String> done = prefs(context).getStringSet(KEY_COMPLETED, null);
        return done != null && done.contains(routeKey);
    }

    private static void markRouteCompleted(Context context, String routeKey) {
        if (TextUtils.isEmpty(routeKey)) {
            return;
        }
        Set<String> prev = prefs(context).getStringSet(KEY_COMPLETED, null);
        Set<String> next = new HashSet<>();
        if (prev != null) {
            next.addAll(prev);
        }
        next.add(routeKey);
        prefs(context).edit().putStringSet(KEY_COMPLETED, next).apply();
    }

    /**
     * Пользователь раскрыл остановку с индексом {@code stopIndex}.
     *
     * @return true, если после этого впервые открыты все остановки маршрута (можно начислить прогресс).
     */
    public static boolean recordStopOpened(Context context, String routeKey, int stopIndex, int totalStops) {
        if (totalStops <= 0 || stopIndex < 0 || stopIndex >= totalStops || TextUtils.isEmpty(routeKey)) {
            return false;
        }
        if (isRouteCompleted(context, routeKey)) {
            return false;
        }
        Set<Integer> opened = loadOpened(context, routeKey);
        opened.add(stopIndex);
        saveOpened(context, routeKey, opened);
        if (opened.size() < totalStops) {
            return false;
        }
        markRouteCompleted(context, routeKey);
        return true;
    }
}
