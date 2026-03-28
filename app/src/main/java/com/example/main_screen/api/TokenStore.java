package com.example.main_screen.api;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * JWT access/refresh tokens for FastAPI backend.
 */
public final class TokenStore {
    private static final String PREF = "technostrelka_auth";
    private static final String ACCESS = "access_token";
    private static final String REFRESH = "refresh_token";
    /** Если false — при следующем холодном старте токены сбрасываются (нужен вход снова). */
    private static final String REMEMBER_ME = "remember_me";

    private final SharedPreferences prefs;

    public static TokenStore get(Context context) {
        return new TokenStore(context.getApplicationContext());
    }

    private TokenStore(Context appContext) {
        this.prefs = appContext.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public boolean hasAccessToken() {
        String t = prefs.getString(ACCESS, null);
        return t != null && !t.isEmpty();
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS, null);
    }

    public String getRefreshToken() {
        return prefs.getString(REFRESH, null);
    }

    /**
     * Сохранить сессию. {@code rememberMe == true} — оставаться в аккаунте после перезапуска приложения.
     */
    public void saveTokens(String accessToken, String refreshToken, boolean rememberMe) {
        prefs.edit()
                .putString(ACCESS, accessToken != null ? accessToken : "")
                .putString(REFRESH, refreshToken != null ? refreshToken : "")
                .putBoolean(REMEMBER_ME, rememberMe)
                .apply();
    }

    /** То же, что {@link #saveTokens(String, String, boolean)} с «запомнить» = true (регистрация и старый код). */
    public void saveTokens(String accessToken, String refreshToken) {
        saveTokens(accessToken, refreshToken, true);
    }

    /**
     * Можно ли восстанавливать сохранённую сессию при запуске.
     * Для старых установок без ключа — по умолчанию true, чтобы не выкидывать пользователей.
     */
    public boolean isRememberMeEnabled() {
        return prefs.getBoolean(REMEMBER_ME, true);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
