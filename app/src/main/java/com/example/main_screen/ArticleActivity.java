package com.example.main_screen;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.main_screen.model.LearningItem;

public class ArticleActivity extends AppCompatActivity {
    private ImageView articleImage;
    private TextView articleTitle;
    private TextView articleContent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Инициализация элементов интерфейса
        initViews();

        // Настройка тулбара
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Получение данных из Intent
        LearningItem article = (LearningItem) getIntent().getSerializableExtra("article");
        if (article != null) {
            displayArticle(article);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        articleImage = findViewById(R.id.article_image);
        articleTitle = findViewById(R.id.article_title);
        articleContent = findViewById(R.id.article_content);
    }

    private void displayArticle(LearningItem article) {
        // Установка изображения
        Glide.with(this)
                .load(article.getImageResourceId())
                .into(articleImage);

        // Установка заголовка
        articleTitle.setText(article.getTitle());

        // Установка содержимого
        articleContent.setText(article.getDescription());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 