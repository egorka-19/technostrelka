package com.example.main_screen;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.main_screen.model.RouteModel;
import com.example.main_screen.model.RouteStop;

/**
 * Заготовка экрана прохождения маршрута (таймлайн, аудио). Наполнение — по следующему ТЗ.
 */
public class RouteItineraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_route_itinerary);

        RouteModel route = readRoute();
        String titleText = route != null && route.getName() != null && !route.getName().isEmpty()
                ? route.getName()
                : getString(R.string.road);

        TextView title = findViewById(R.id.itinerary_title);
        title.setText(titleText);

        TextView body = findViewById(R.id.itinerary_body);
        if (route != null && route.getStops() != null && !route.getStops().isEmpty()) {
            CharSequence stopsText = buildStopsText(route.getStops());
            if (stopsText != null && stopsText.length() > 0) {
                body.setText(stopsText);
            } else {
                body.setText(R.string.route_itinerary_placeholder);
            }
        } else {
            body.setText(R.string.route_itinerary_placeholder);
        }

        ImageButton back = findViewById(R.id.itinerary_back);
        back.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.itinerary_root), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });
    }

    private static CharSequence buildStopsText(java.util.List<RouteStop> stops) {
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (RouteStop s : stops) {
            if (s == null) {
                continue;
            }
            String t = s.getTitle();
            if (t != null && !t.isEmpty()) {
                sb.append(n++).append(". ").append(t).append('\n');
            }
            String addr = s.getAddress();
            if (addr != null && !addr.isEmpty()) {
                sb.append("   ").append(addr).append('\n');
            }
            String txt = s.getText();
            if (txt != null && !txt.isEmpty()) {
                sb.append('\n').append(txt).append("\n\n");
            }
        }
        String out = sb.toString().trim();
        return out.isEmpty() ? "" : out;
    }

    @Nullable
    private RouteModel readRoute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(RoutePreviewActivity.EXTRA_ROUTE, RouteModel.class);
        }
        Object o = getIntent().getSerializableExtra(RoutePreviewActivity.EXTRA_ROUTE);
        return o instanceof RouteModel ? (RouteModel) o : null;
    }
}
