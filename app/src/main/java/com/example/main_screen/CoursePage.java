package com.example.main_screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CoursePage extends AppCompatActivity {

    private ImageButton back_course, love, reviews, geo;
    private boolean isLoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);

        int courseImage = getIntent().getIntExtra("courseImage", 0);
        String categoryTitle = getIntent().getStringExtra("categoryTitle");
        String courseTitle = getIntent().getStringExtra("courseTitle");
        String courseDescription = getIntent().getStringExtra("courseDescription");
        String courseText = getIntent().getStringExtra("courseText");
        Double x = getIntent().getDoubleExtra("x", 0);
        Double y = getIntent().getDoubleExtra("y", 0);

        ImageView courseImageView = findViewById(R.id.main_image);
        TextView categoryTitleTextView = findViewById(R.id.maintitle);
        TextView courseTitleTextView = findViewById(R.id.Title);
        TextView courseDescriptionTextView = findViewById(R.id.Description);
        TextView courseTextTextView = findViewById(R.id.text);

        courseImageView.setImageResource(courseImage);
        categoryTitleTextView.setText(categoryTitle);
        courseTitleTextView.setText(courseTitle);
        courseDescriptionTextView.setText(courseDescription);
        courseTextTextView.setText(courseText);

        back_course = findViewById(R.id.back_course);
        love = findViewById(R.id.lovest);
        reviews = findViewById(R.id.reviews);
        geo = findViewById(R.id.geo);

        back_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        geo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoursePage.this, routes.class));
            }
        });

        isLoved = getIntent().getBooleanExtra("lovest", false);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        isLoved = sharedPref.getBoolean("isLoved", false);
        updateLoveButton();

        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoved = !isLoved;
                updateLoveButton();

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isLoved", isLoved);
                editor.apply();
            }
        });

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursePage.this, Reviews.class);
                intent.putExtra("categoryTitle", categoryTitle);
                startActivity(intent);
            }
        });
    }

    private void updateLoveButton() {
        if (isLoved) {
            love.setImageResource(R.drawable.lovest_on);
        } else {
            love.setImageResource(R.drawable.in_lovest);
        }
    }
}
