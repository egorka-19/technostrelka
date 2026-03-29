package com.example.main_screen;

import android.app.Application;

import com.example.main_screen.service.ScoreService;
import com.example.main_screen.ui.ThemePreferences;

public class MainApp extends Application {

    @Override
    public void onCreate() {
        ThemePreferences.applyStoredNightMode(this);
        ScoreService.init(this);
        super.onCreate();
    }
}
