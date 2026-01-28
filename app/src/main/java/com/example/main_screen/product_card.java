package com.example.main_screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.main_screen.adapter.EventReviewsAdapter;
import com.example.main_screen.adapter.ProductImagePagerAdapter;
import com.example.main_screen.model.EventReviewModel;
import com.example.main_screen.model.PlaceModel;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.ViewAllModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class product_card extends AppCompatActivity {
    ViewPager2 photoPager;
    LinearLayout indicators;
    TextView description, name, age, date, place, ratingValue;
    ImageButton backBtn, mapsButton;
    TextView addReviewButton;
    RecyclerView reviewsRecycler;
    EventReviewsAdapter reviewsAdapter;

    ViewAllModel viewAllModel = null;
    PopularModel popularModel = null;
    PlaceModel placeModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_card);

        // Initialize views
        photoPager = findViewById(R.id.pro_card_img);
        indicators = findViewById(R.id.photo_indicators);
        description = findViewById(R.id.description);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        date = findViewById(R.id.date);
        place = findViewById(R.id.place);
        ratingValue = findViewById(R.id.rating_value);
        backBtn = findViewById(R.id.back_btn);
        mapsButton = findViewById(R.id.maps_button);
        addReviewButton = findViewById(R.id.add_review_button);
        reviewsRecycler = findViewById(R.id.reviews_recycler);

        // Чтобы кнопку не перекрывал системный UI (navigation bar)
        View root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) addReviewButton.getLayoutParams();
            lp.bottomMargin = sysBars.bottom + dp(14);
            addReviewButton.setLayoutParams(lp);
            return insets;
        });

        // Set up back button
        backBtn.setOnClickListener(v -> finish());

        // Set up maps button
        mapsButton.setOnClickListener(v -> {
            String url = "";
            if (viewAllModel != null) {
                url = viewAllModel.getUrl();
            } else if (popularModel != null) {
                url = popularModel.getUrl();
            }
            
            if (!url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        // Get data from intent
        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel) {
            viewAllModel = (ViewAllModel) object;
        } else if (object instanceof PopularModel) {
            popularModel = (PopularModel) object;
        } else if (object instanceof PlaceModel) {
            placeModel = (PlaceModel) object;
        }

        setupPhotos(); // TODO: заменить список фото на Firebase
        setupReviews(); // TODO: заменить заглушки на Firebase

        // Load data into views
        if (viewAllModel != null) {
            loadViewAllModelData();
        } else if (popularModel != null) {
            loadPopularModelData();
        } else if (placeModel != null) {
            loadPlaceModelData();
        }

        loadRatingFromFirebase(); // рейтинг как на карточках (по отзывам)
        loadReviewsFromFirebase(); // отзывы (если есть структура)

        addReviewButton.setOnClickListener(v -> {
            // TODO: открыть экран добавления отзыва и сохранять в Firebase
            Toast.makeText(this, "TODO: Добавление отзыва", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupPhotos() {
        List<Object> images = new ArrayList<>();

        // TODO: Здесь заменить на список фотографий из Firebase для конкретного места
        // Первая фотка — основная из модели (если есть)
        if (viewAllModel != null && viewAllModel.getImg_url() != null && !viewAllModel.getImg_url().isEmpty()) {
            images.add(viewAllModel.getImg_url());
        } else if (popularModel != null && popularModel.getImg_url() != null && !popularModel.getImg_url().isEmpty()) {
            images.add(popularModel.getImg_url());
        } else if (placeModel != null && placeModel.getImageUrl() != null && !placeModel.getImageUrl().isEmpty()) {
            images.add(placeModel.getImageUrl());
        } else {
            images.add(R.drawable.izo);
        }

        // Рандомные/временные фотки из проекта
        images.add(R.drawable.izo);
        images.add(R.drawable.card);

        ProductImagePagerAdapter adapter = new ProductImagePagerAdapter(this, images);
        photoPager.setAdapter(adapter);
        photoPager.setOffscreenPageLimit(3);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(dp(6)));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.92f + r * 0.08f);
            page.setAlpha(0.75f + r * 0.25f);
        });
        photoPager.setPageTransformer(transformer);

        setupIndicators(images.size());
        photoPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setIndicatorActive(position);
            }
        });
    }

    private void setupIndicators(int count) {
        indicators.removeAllViews();
        for (int i = 0; i < count; i++) {
            View bar = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(18), dp(4));
            lp.setMargins(dp(4), 0, dp(4), 0);
            bar.setLayoutParams(lp);
            bar.setBackgroundResource(i == 0 ? R.drawable.indicator_bar_active : R.drawable.indicator_bar_inactive);
            indicators.addView(bar);
        }
    }

    private void setIndicatorActive(int index) {
        int childCount = indicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = indicators.getChildAt(i);
            child.setBackgroundResource(i == index ? R.drawable.indicator_bar_active : R.drawable.indicator_bar_inactive);
        }
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private void setupReviews() {
        // Заглушки, если Firebase ещё не настроен под отзывы
        // TODO: убрать заглушки, когда отзывы будут приходить из Firebase
        List<EventReviewModel> list = new ArrayList<>();
        list.add(new EventReviewModel(
                "Суяков Егор",
                "", // TODO: avatarUrl из Firebase
                "12.03.24",
                5.0f,
                "Постановка необычная, интересная. Понравилось все! Спасибо артистам - играли великолепно."
        ));
        list.add(new EventReviewModel(
                "Наталия",
                "", // TODO: avatarUrl из Firebase
                "11.03.24",
                5.0f,
                "Интересная композиция, не новая, но динамичная. Картинка замечательная."
        ));

        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reviewsRecycler.setLayoutManager(lm);
        reviewsAdapter = new EventReviewsAdapter(this, list);
        reviewsRecycler.setAdapter(reviewsAdapter);
    }

    private void loadRatingFromFirebase() {
        String eventName = getEventName();
        String category = getCategoryTypeForFirebase(getEventType());
        if (eventName == null || eventName.isEmpty()) {
            ratingValue.setText("5.0"); // TODO
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Reviews")
                .child(category)
                .child(eventName);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double total = 0;
                int count = 0;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    Object ratingObj = userSnap.child("rating").getValue();
                    if (ratingObj != null) {
                        try {
                            double r = Double.parseDouble(String.valueOf(ratingObj));
                            total += r;
                            count++;
                        } catch (Exception ignored) {
                        }
                    }
                }
                if (count > 0) {
                    double avg = total / count;
                    ratingValue.setText(String.format(java.util.Locale.US, "%.1f", avg));
                } else {
                    // TODO: если отзывов нет — дефолт 5.0
                    ratingValue.setText("5.0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // TODO: если не удалось загрузить — дефолт 5.0
                ratingValue.setText("5.0");
            }
        });
    }

    private void loadReviewsFromFirebase() {
        // Пытаемся подгрузить отзывы, если такая структура уже есть в Firebase.
        // TODO: согласовать финальную структуру отзывов в Firebase (avatarUrl/userName/date/text/rating)
        String eventName = getEventName();
        String category = getCategoryTypeForFirebase(getEventType());
        if (eventName == null || eventName.isEmpty()) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Reviews")
                .child(category)
                .child(eventName);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<EventReviewModel> list = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String userName = String.valueOf(userSnap.child("userName").getValue());
                    String avatarUrl = String.valueOf(userSnap.child("avatarUrl").getValue());
                    String date = String.valueOf(userSnap.child("date").getValue());
                    String text = String.valueOf(userSnap.child("text").getValue());

                    float rating = 5.0f;
                    Object ratingObj = userSnap.child("rating").getValue();
                    if (ratingObj != null) {
                        try {
                            rating = Float.parseFloat(String.valueOf(ratingObj));
                        } catch (Exception ignored) {}
                    }

                    // Если ключи отсутствуют — пропускаем (чтобы не показывать "null")
                    if (userName == null || "null".equals(userName) || text == null || "null".equals(text)) {
                        continue;
                    }
                    if (date == null || "null".equals(date)) date = "";
                    if (avatarUrl != null && "null".equals(avatarUrl)) avatarUrl = "";

                    list.add(new EventReviewModel(userName, avatarUrl, date, rating, text));
                }

                if (!list.isEmpty() && reviewsAdapter != null) {
                    reviewsAdapter.setItems(list);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // оставляем заглушки
            }
        });
    }

    private String getEventName() {
        if (viewAllModel != null) return viewAllModel.getName();
        if (popularModel != null) return popularModel.getName();
        if (placeModel != null) return placeModel.getName();
        return null;
    }

    private String getEventType() {
        if (viewAllModel != null) return viewAllModel.getType();
        if (popularModel != null) return popularModel.getType();
        return null;
    }

    private String getCategoryTypeForFirebase(String type) {
        if (type == null) return "Other";
        switch (type) {
            case "Кино":
            case "Cinema":
                return "Cinema";
            case "Театр":
            case "Theater":
                return "Theater";
            case "Парк":
            case "Park":
                return "Park";
            case "Ресторан":
            case "Restaurant":
                return "Restaraunt";
            case "Музей":
            case "Museum":
                return "Museum";
            default:
                return "Other";
        }
    }

    private void loadViewAllModelData() {
        name.setText(viewAllModel.getName());
        description.setText(viewAllModel.getDescription());
        age.setText(nonEmpty(viewAllModel.getAge(), "Нет ограничений по возрасту"));
        date.setText(nonEmpty(viewAllModel.getData(), "пн-чт 06:30–23:00; пт 06:30–00:00; сб 08:00–00:00; вс 08:00–23:00"));
        place.setText(nonEmpty(viewAllModel.getPlace(), "Ижевск, ул. Милиционная, 5"));
    }

    private void loadPopularModelData() {
        name.setText(popularModel.getName());
        description.setText(popularModel.getDescription());
        age.setText(nonEmpty(popularModel.getAge(), "Нет ограничений по возрасту"));
        // На карточке используется schedule (fallback data) — делаем так же
        String schedule = popularModel.getSchedule();
        if (schedule == null || schedule.trim().isEmpty()) {
            schedule = popularModel.getData();
        }
        date.setText(nonEmpty(schedule, "пн-чт 06:30–23:00; пт 06:30–00:00; сб 08:00–00:00; вс 08:00–23:00"));
        place.setText(nonEmpty(popularModel.getPlace(), "Ижевск, ул. Милиционная, 5"));
    }

    private void loadPlaceModelData() {
        name.setText(placeModel.getName());
        description.setText(placeModel.getDescription());
        age.setText(nonEmpty(placeModel.getAge(), "Нет ограничений по возрасту"));
        date.setText(nonEmpty(placeModel.getData(), "пн-чт 06:30–23:00; пт 06:30–00:00; сб 08:00–00:00; вс 08:00–23:00"));
        place.setText(nonEmpty(placeModel.getPlace(), "Ижевск, ул. Милиционная, 5"));
    }

    private String nonEmpty(String value, String fallback) {
        if (value == null) return fallback;
        String v = value.trim();
        return v.isEmpty() ? fallback : v;
    }
}