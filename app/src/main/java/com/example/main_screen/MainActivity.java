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
import com.example.main_screen.bottomnav.fav.fav_fragment;
import com.example.main_screen.bottomnav.profile.ProfileFragment;
import com.example.main_screen.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new home_fragment()).commit();
        binding.bottomnav.setSelectedItemId(R.id.home);
        Map<Integer, Fragment>fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.profile, new ProfileFragment());
        fragmentMap.put(R.id.events, new ThemainscreenFragment());
        fragmentMap.put(R.id.home, new home_fragment());
        fragmentMap.put(R.id.favourite, new fav_fragment());
        fragmentMap.put(R.id.plus, new PlusFragment());
        binding.bottomnav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();
            return true;
            });
        }
    }