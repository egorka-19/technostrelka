package com.example.main_screen.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.adapter.ProfileFavoriteEventAdapter;
import com.example.main_screen.adapter.RouteAdapter;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.data.RouteCatalogLoader;
import com.example.main_screen.data.RouteFavoritePreferences;
import com.example.main_screen.model.RouteModel;
import com.example.main_screen.model.RouteModelKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Полноэкранный список избранных маршрутов (локально) или мероприятий (API).
 */
public class ProfileFavoritesListActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_TYPE = "list_type";
    public static final int TYPE_ROUTES = 0;
    public static final int TYPE_EVENTS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_profile_favorites_list);

        int type = getIntent().getIntExtra(EXTRA_LIST_TYPE, TYPE_ROUTES);

        View root = findViewById(R.id.profile_list_root);
        View header = findViewById(R.id.list_header);
        ImageButton back = findViewById(R.id.list_back);
        TextView title = findViewById(R.id.list_title);
        RecyclerView recycler = findViewById(R.id.list_recycler);
        TextView empty = findViewById(R.id.list_empty);

        title.setText(type == TYPE_ROUTES ? R.string.profile_my_routes : R.string.profile_my_events);
        back.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(header.getPaddingLeft(), sys.top, header.getPaddingRight(), header.getPaddingBottom());
            recycler.setPadding(recycler.getPaddingLeft(), recycler.getPaddingTop(),
                    recycler.getPaddingRight(), sys.bottom + dp(4));
            return insets;
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));

        if (type == TYPE_ROUTES) {
            RouteAdapter adapter = new RouteAdapter(this, new ArrayList<>(), true);
            recycler.setAdapter(adapter);
            loadRoutes(recycler, adapter, empty);
        } else {
            ProfileFavoriteEventAdapter adapter = new ProfileFavoriteEventAdapter();
            recycler.setAdapter(adapter);
            loadEvents(recycler, adapter, empty);
        }
    }

    private void loadRoutes(RecyclerView rv, RouteAdapter adapter, TextView empty) {
        List<String> keys = RouteFavoritePreferences.getOrderedKeys(this);
        RouteCatalogLoader.loadAll(this, routes -> {
            if (isFinishing()) {
                return;
            }
            List<RouteModel> fav = new ArrayList<>();
            for (String key : keys) {
                for (RouteModel m : routes) {
                    if (key.equals(RouteModelKeys.stableKey(m))) {
                        fav.add(m);
                        break;
                    }
                }
            }
            adapter.setRoutes(fav);
            boolean isEmpty = fav.isEmpty();
            empty.setText(R.string.profile_routes_empty);
            empty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rv.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });
    }

    private void loadEvents(RecyclerView rv, ProfileFavoriteEventAdapter adapter, TextView empty) {
        if (!TokenStore.get(this).hasAccessToken()) {
            adapter.setItems(new ArrayList<>());
            empty.setText(R.string.profile_events_login);
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            return;
        }
        empty.setText(R.string.profile_events_empty);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(this).listMyFavorites().execute();
                runOnUiThread(() -> {
                    List<EventItemDto> body =
                            resp.isSuccessful() && resp.body() != null ? resp.body() : new ArrayList<>();
                    adapter.setItems(body);
                    boolean isEmpty = body.isEmpty();
                    empty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    rv.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    adapter.setItems(new ArrayList<>());
                    empty.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                });
            }
        });
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
