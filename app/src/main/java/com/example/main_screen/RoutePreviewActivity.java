package com.example.main_screen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.main_screen.data.RouteFavoritePreferences;
import com.example.main_screen.model.RouteModel;
import com.example.main_screen.model.RouteModelKeys;
import com.example.main_screen.utils.MediaUrlUtils;
import com.google.android.material.button.MaterialButton;

/**
 * Экран превью маршрута: обложка, градиент, название, город, описание, кнопка «Начать».
 */
public class RoutePreviewActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTE = "route";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_route_preview);

        RouteModel route = readRouteExtra();
        if (route == null) {
            finish();
            return;
        }

        ImageView hero = findViewById(R.id.hero_image);
        ImageButton back = findViewById(R.id.back_button);
        ImageButton favorite = findViewById(R.id.favorite_route_button);
        TextView title = findViewById(R.id.route_title);
        TextView place = findViewById(R.id.route_place);
        TextView description = findViewById(R.id.route_description);
        MaterialButton start = findViewById(R.id.start_button);
        ViewGroup root = findViewById(R.id.route_preview_root);

        title.setText(nonEmpty(route.getName(), ""));
        String city = nonEmpty(route.getPlace(), "Ижевск");
        place.setText(city);
        String full = nonEmpty(route.getDescription(), getString(R.string.route_itinerary_placeholder));
        description.setText(shortenDescription(full));

        String img = MediaUrlUtils.resolveForApiClient(route.getImageUrl());
        if (!TextUtils.isEmpty(img)) {
            Glide.with(this).load(img).centerCrop().error(R.drawable.izo).into(hero);
        } else {
            hero.setImageResource(R.drawable.izo);
        }

        back.setOnClickListener(v -> finish());

        final String routeKey = RouteModelKeys.stableKey(route);
        applyFavoriteIcon(favorite, RouteFavoritePreferences.isFavorite(this, routeKey));
        favorite.setOnClickListener(v -> {
            RouteFavoritePreferences.toggle(this, routeKey);
            applyFavoriteIcon(favorite, RouteFavoritePreferences.isFavorite(this, routeKey));
        });

        start.setOnClickListener(v -> {
            Intent next = new Intent(this, RouteItineraryActivity.class);
            next.putExtra(EXTRA_ROUTE, route);
            startActivity(next);
        });

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams backLp = (ViewGroup.MarginLayoutParams) back.getLayoutParams();
            backLp.topMargin = sys.top + dp(4);
            back.setLayoutParams(backLp);

            ViewGroup.MarginLayoutParams favLp = (ViewGroup.MarginLayoutParams) favorite.getLayoutParams();
            favLp.topMargin = sys.top + dp(4);
            favorite.setLayoutParams(favLp);

            ViewGroup.MarginLayoutParams btnLp = (ViewGroup.MarginLayoutParams) start.getLayoutParams();
            btnLp.bottomMargin = sys.bottom + dp(12);
            start.setLayoutParams(btnLp);
            return insets;
        });
    }

    @Nullable
    private RouteModel readRouteExtra() {
        Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getSerializableExtra(EXTRA_ROUTE, RouteModel.class);
        }
        Object o = intent.getSerializableExtra(EXTRA_ROUTE);
        return o instanceof RouteModel ? (RouteModel) o : null;
    }

    private static String nonEmpty(String s, String fallback) {
        if (s == null || s.trim().isEmpty()) {
            return fallback;
        }
        return s;
    }

    /** Укороченный анонс на превью (полный текст — на экране маршрута). */
    private static String shortenDescription(String text) {
        if (text == null) {
            return "";
        }
        String t = text.trim();
        final int max = 360;
        if (t.length() <= max) {
            return t;
        }
        int cut = max;
        int lastSpace = t.lastIndexOf(' ', max);
        if (lastSpace > max / 2) {
            cut = lastSpace;
        }
        return t.substring(0, cut).trim() + "…";
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private static void applyFavoriteIcon(ImageButton btn, boolean favorite) {
        if (favorite) {
            btn.setColorFilter(Color.parseColor("#FF0033"), PorterDuff.Mode.SRC_IN);
        } else {
            btn.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
        }
    }
}
