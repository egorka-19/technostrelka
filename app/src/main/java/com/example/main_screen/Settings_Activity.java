package com.example.main_screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.main_screen.bottomnav.home.home_fragment;
import com.example.main_screen.bottomnav.profile.ProfileFragment;
import com.example.main_screen.databinding.ActivitySettingsBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.yandex.mapkit.MapKitFactory;

public class Settings_Activity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ImageButton back_button = (ImageButton) findViewById(R.id.back_btn);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageButton logoutbtn = (ImageButton)findViewById(R.id.logout_btn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings_Activity.this, LoginActivity.class));
            }
        });
        ImageButton helpme_btn = (ImageButton) findViewById(R.id.helpme_btn);
        helpme_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/flachka"));
                startActivity(browserIntent);
            }
        });
    }
}