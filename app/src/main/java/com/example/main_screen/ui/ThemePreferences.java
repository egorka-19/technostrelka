package com.example.main_screen.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Сохранение и применение тёмной темы (принудительно светлая / тёмная, без системного режима).
 */
public final class ThemePreferences {

    private static final String PREF = "app_theme_prefs";
    private static final String KEY_DARK = "dark_enabled";
    private static final String KEY_LAST_NAV_ITEM_ID = "last_bottom_nav_item_id";

    private ThemePreferences() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static boolean isDarkMode(Context context) {
        return prefs(context).getBoolean(KEY_DARK, false);
    }

    /** Вызывать из {@link android.app.Application#onCreate()} до отображения Activity. */
    public static void applyStoredNightMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode(context) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setDarkMode(Context context, boolean dark) {
        prefs(context).edit().putBoolean(KEY_DARK, dark).apply();
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    /** Сохраняет выбранный пункт нижнего меню, чтобы после recreate() остаться на той же вкладке. */
    public static void saveLastNavItemId(Context context, int menuItemId) {
        prefs(context).edit().putInt(KEY_LAST_NAV_ITEM_ID, menuItemId).apply();
    }

    public static int getLastNavItemId(Context context, int defaultMenuItemId) {
        return prefs(context).getInt(KEY_LAST_NAV_ITEM_ID, defaultMenuItemId);
    }
}
