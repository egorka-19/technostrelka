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

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(ACCESS, accessToken)
                .putString(REFRESH, refreshToken)
                .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
