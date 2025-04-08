package com.example.main_screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


import java.util.ArrayList;
import java.util.List;

import adapter.Reviews1;
import adapter.ReviewsAdapter;

public class Reviews extends AppCompatActivity {

    RecyclerView recycle1;
    ReviewsAdapter reviewsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        String categoryTitleId = getIntent().getStringExtra("categoryTitle");
        List<Reviews1> reviewsList = new ArrayList<>();
        if (categoryTitleId.equals("Кино")) {
            reviewsList.add(new Reviews1(1, "Наталия", "Интересная композиция, не новая, но динамичная. Картинка замечательная, плитка-как символ развития смотрится убедительно .Дороги важны.Ровные актёрские работы.", "natalia"));
            reviewsList.add(new Reviews1(2, "Анна", "Очень интересная композиция, рассказ в рассказе, и никакой сплошь линейности, всё время перебросы. И это так соответствует образу времени.", "anna"));
            reviewsList.add(new Reviews1(3, "Елена", "Фильм Мастер и маргарита (2024) рекомендую к просмотру с оценкой в пять баллов! ", "elenya"));
        } else if (categoryTitleId.equals("Театры")) {
            reviewsList.add(new Reviews1(4, "Александр", "Очень красивый театр, прекрасные спектакли. Всегда удивляет уровень игры актеров, постановки и декораций.", "alexandr"));
            reviewsList.add(new Reviews1(5, "Евгения", "Замечательный театр, очень хорошие, талантливые артисты, спектакли на высочайшем уровне.Очень рекомендую для посещения.", "evgenya"));
            reviewsList.add(new Reviews1(6, "Виктор", "Очень расширился и омолодился состав актеров, нравится зрелищность постановок, художник по костюмам просто волшебник. Спектакли стали современными, рекомендую!", "viktor"));
            reviewsList.add(new Reviews1(7, "Егор", "Постановка необычная, интересная. Понравилось все! Спасибо артистам - играли великолепно.", "egor"));
        }
        RecyclerView recyclerView = findViewById(R.id.recycle_reviews);
        setReviewsRecycler(reviewsList);
    }

    private void setReviewsRecycler(List<Reviews1> reviewsList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        recycle1 = findViewById(R.id.recycle_reviews);
        recycle1.setLayoutManager(layoutManager);

        reviewsAdapter = new ReviewsAdapter(this, reviewsList);
        recycle1.setAdapter(reviewsAdapter);
    }

}