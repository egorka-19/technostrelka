package com.example.main_screen;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.adapter.response;

import java.util.HashMap;
import java.util.List;

public class add_response extends AppCompatActivity {
    ImageView photo_card;
    TextView name;
    SeekBar ratingSeekBar;
    TextView ratingValue;
    ImageButton sendBtn;
    HashMap<String, List<String>> productCategories;

    response responseAdapter;
    RecyclerView recycle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_response);

        }}