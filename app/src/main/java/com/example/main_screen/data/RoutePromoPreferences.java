package com.example.main_screen.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.SecureRandom;
import java.util.Locale;

/**
 * Локально: уникальный суффикс промокода и флаги «уже использован» по id точки.
 * При появлении бэкенда можно синхронизировать те же ключи через API.
 */
public final class RoutePromoPreferences {

    private static final String PREF = "route_promo_prefs";
    private static final String KEY_SUFFIX = "user_promo_suffix_v1";

    private RoutePromoPreferences() {
    }

    public static String getFullPromoCode(Context context) {
        return "СердцеУдмуртии_" + getOrCreateUserSuffix(context);
    }

    /** Короткий код для диктовки: 6 цифр. */
    public static String getOrCreateUserSuffix(Context context) {
        Context app = context.getApplicationContext();
        SharedPreferences sp = app.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String existing = sp.getString(KEY_SUFFIX, null);
        if (existing != null && !existing.isEmpty()) {
            return existing;
        }
        SecureRandom r = new SecureRandom();
        int n = 100_000 + r.nextInt(900_000);
        String suffix = String.format(Locale.US, "%06d", n);
        sp.edit().putString(KEY_SUFFIX, suffix).apply();
        return suffix;
    }

    public static boolean isRedeemed(Context context, String stopId) {
        if (stopId == null || stopId.isEmpty()) {
            return false;
        }
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getBoolean("redeemed_" + stopId, false);
    }

    public static void markRedeemed(Context context, String stopId) {
        if (stopId == null || stopId.isEmpty()) {
            return;
        }
        context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("redeemed_" + stopId, true)
                .apply();
    }
}
