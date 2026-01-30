package com.example.main_screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.main_screen.adapter.EventReviewsAdapter;
import com.example.main_screen.adapter.ProductImagePagerAdapter;
import com.example.main_screen.model.EventReviewModel;
import com.example.main_screen.model.PlaceModel;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.ViewAllModel;
import com.example.main_screen.utils.BlurUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class product_card extends AppCompatActivity {
    ViewPager2 photoPager;
    LinearLayout indicators;
    ImageView overlayBlurImage;
    TextView description, name, age, date, place, ratingValue;
    ImageButton backBtn, mapsButton;
    TextView addReviewButton;
    RecyclerView reviewsRecycler;
    EventReviewsAdapter reviewsAdapter;
    private final AtomicInteger blurSeq = new AtomicInteger(0);
    /** Список фото для ViewPager2: основное фото + фото из отзывов пользователей */
    private List<Object> photoList;

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
        overlayBlurImage = findViewById(R.id.overlay_blur_image);
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
        loadReviewPhotosFromFirebase(); // фото из отзывов в pro_card_img

        // Обновляем blur после первой отрисовки
        photoPager.post(this::updateOverlayBlurStrong);

        addReviewButton.setOnClickListener(v -> openAddReviewSheet());
    }

    private void setupPhotos() {
        photoList = new ArrayList<>();

        // Первая фотка — основная из модели (если есть)
        if (viewAllModel != null && viewAllModel.getImg_url() != null && !viewAllModel.getImg_url().isEmpty()) {
            photoList.add(viewAllModel.getImg_url());
        } else if (popularModel != null && popularModel.getImg_url() != null && !popularModel.getImg_url().isEmpty()) {
            photoList.add(popularModel.getImg_url());
        } else if (placeModel != null && placeModel.getImageUrl() != null && !placeModel.getImageUrl().isEmpty()) {
            photoList.add(placeModel.getImageUrl());
        } else {
            photoList.add(R.drawable.izo);
        }

        // Остальные фото подгружаются из отзывов пользователей (loadReviewPhotosFromFirebase)

        ProductImagePagerAdapter adapter = new ProductImagePagerAdapter(this, photoList);
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

        setupIndicators(photoList.size());
        photoPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setIndicatorActive(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // Обновляем blur только когда перелистывание завершилось,
                // чтобы не ловить "полосы" и соседние страницы.
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    updateOverlayBlurStrong();
                }
            }
        });
    }

    /**
     * Реальный сильный blur для overlay.
     * Делается на основе текущего содержимого ViewPager2 (фото).
     *
     * TODO: при переходе на Firebase-фото оставить тот же подход, но брать bitmap из текущей страницы.
     */
    private void updateOverlayBlurStrong() {
        if (overlayBlurImage == null) return;
        // Берём bitmap только текущей страницы, а не всего ViewPager2 (иначе попадают соседние фото)
        RecyclerView rv = null;
        if (photoPager.getChildCount() > 0 && photoPager.getChildAt(0) instanceof RecyclerView) {
            rv = (RecyclerView) photoPager.getChildAt(0);
        }
        if (rv == null) return;

        int position = photoPager.getCurrentItem();
        RecyclerView.ViewHolder vh = rv.findViewHolderForAdapterPosition(position);
        if (vh == null) {
            // страница ещё не прикрепилась — попробуем чуть позже
            photoPager.postDelayed(this::updateOverlayBlurStrong, 16);
            return;
        }

        View itemView = vh.itemView;
        ImageView photo = itemView.findViewById(R.id.photo);
        if (photo == null || photo.getWidth() <= 0 || photo.getHeight() <= 0) return;

        // Сильный blur лучше делать на уменьшенной копии сразу при рендере
        final float downScale = 12f;
        int w = Math.max(1, (int) (photo.getWidth() / downScale));
        int h = Math.max(1, (int) (photo.getHeight() / downScale));
        Bitmap small = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(small);
        c.scale((float) w / photo.getWidth(), (float) h / photo.getHeight());
        photo.draw(c);

        final int requestId = blurSeq.incrementAndGet();
        new Thread(() -> {
            Bitmap blurred = BlurUtils.stackBlur(small, 26); // сильнее blur
            runOnUiThread(() -> {
                // защита от гонок: если уже пришёл более новый запрос, этот результат игнорируем
                if (requestId != blurSeq.get()) {
                    return;
                }
                overlayBlurImage.setImageBitmap(blurred);
            });
        }).start();
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
        List<EventReviewModel> list = new ArrayList<>();
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reviewsRecycler.setLayoutManager(lm);
        reviewsAdapter = new EventReviewsAdapter(this, list);
        reviewsRecycler.setAdapter(reviewsAdapter);
    }

    private void loadRatingFromFirebase() {
        String eventName = getEventName();
        String category = getCategoryTypeForFirebase(getEventType());
        if (eventName == null || eventName.isEmpty()) {
            ratingValue.setText("0");
            updateReviewsEmptyState(0);
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
                    ratingValue.setText("0");
                }
                updateReviewsEmptyState(count);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                ratingValue.setText("0");
                updateReviewsEmptyState(0);
            }
        });
    }

    private void updateReviewsEmptyState(int reviewCount) {
        TextView reviewsEmpty = findViewById(R.id.reviews_empty);
        if (reviewsEmpty != null) {
            reviewsEmpty.setVisibility(reviewCount == 0 ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Подгружает фото из отзывов пользователей и добавляет их в ViewPager2 (pro_card_img).
     */
    private void loadReviewPhotosFromFirebase() {
        String eventName = getEventName();
        String category = getCategoryTypeForFirebase(getEventType());
        if (eventName == null || eventName.isEmpty() || photoList == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Reviews")
                .child(category)
                .child(eventName);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> reviewPhotoUrls = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    DataSnapshot photoUrlsSnap = userSnap.child("photoUrls");
                    if (!photoUrlsSnap.exists()) continue;
                    for (DataSnapshot urlSnap : photoUrlsSnap.getChildren()) {
                        Object val = urlSnap.getValue();
                        if (val != null) {
                            String url = String.valueOf(val);
                            if (!url.isEmpty() && !"null".equals(url)) {
                                reviewPhotoUrls.add(url);
                            }
                        }
                    }
                }

                runOnUiThread(() -> {
                    if (photoList == null) return;
                    // Первый элемент — основное фото места, остальное заменяем на фото из отзывов
                    Object mainPhoto = photoList.isEmpty() ? null : photoList.get(0);
                    photoList.clear();
                    if (mainPhoto != null) photoList.add(mainPhoto);
                    photoList.addAll(reviewPhotoUrls);
                    RecyclerView.Adapter<?> adapter = photoPager.getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    setupIndicators(photoList.size());
                    photoPager.post(product_card.this::updateOverlayBlurStrong);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // оставляем текущий список фото
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

                if (reviewsAdapter != null) {
                    reviewsAdapter.setItems(list);
                }
                updateReviewsEmptyState(list.size());
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
        if (placeModel != null) return placeModel.getType();
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

    private void openAddReviewSheet() {
        String eventName = getEventName();
        String category = getCategoryTypeForFirebase(getEventType());
        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Не удалось определить место", Toast.LENGTH_SHORT).show();
            return;
        }
        AddReviewBottomSheetFragment sheet = AddReviewBottomSheetFragment.newInstance(eventName, category);
        sheet.setOnReviewPublishedListener(() -> {
            loadRatingFromFirebase();
            loadReviewsFromFirebase();
            loadReviewPhotosFromFirebase();
        });
        FragmentManager fm = getSupportFragmentManager();
        sheet.show(fm, "AddReviewBottomSheet");
    }
}