package com.example.main_screen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import com.example.main_screen.bottomnav.events.ThemainscreenFragment;
import com.example.main_screen.bottomnav.home.home_fragment;
import com.example.main_screen.bottomnav.plus.PlusFragment;
import com.example.main_screen.bottomnav.profile.ProfileFragment;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.databinding.ActivityMainBinding;
import com.example.main_screen.ui.ThemePreferences;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (!TokenStore.get(this).hasAccessToken()) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }

        binding.bottomnav.setItemActiveIndicatorEnabled(false);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.events, new ThemainscreenFragment());
        fragmentMap.put(R.id.home, new home_fragment());
        fragmentMap.put(R.id.plus, new PlusFragment());

        int startItemId = ThemePreferences.getLastNavItemId(this, R.id.events);
        Fragment startFragment = fragmentMap.get(startItemId);
        if (startFragment == null) {
            startItemId = R.id.events;
            startFragment = fragmentMap.get(R.id.events);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(binding.fragmentContainer.getId(), startFragment)
                .commit();
        binding.bottomnav.setSelectedItemId(startItemId);

        binding.bottomnav.setOnItemSelectedListener(item -> {
            ThemePreferences.saveLastNavItemId(MainActivity.this, item.getItemId());
            Fragment fragment = fragmentMap.get(item.getItemId());
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.fragmentContainer.getId(), fragment)
                        .commit();
            }
            return true;
        });
    }
}