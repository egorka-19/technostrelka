package com.example.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main_screen.api.TokenStore;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(() -> goNext(), 3 * 1000);
    }

    private void goNext() {
        TokenStore ts = TokenStore.get(this);
        if (ts.hasAccessToken() && ts.isRememberMeEnabled()) {
            startActivity(new Intent(Splash_screen.this, MainActivity.class));
        } else {
            if (ts.hasAccessToken()) {
                ts.clear();
            }
            startActivity(new Intent(Splash_screen.this, LoginActivity.class));
        }
        finish();
    }
}