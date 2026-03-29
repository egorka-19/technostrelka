package com.example.main_screen;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.adapter.ViewAllAdapters;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.EventMapper;
import com.example.main_screen.api.ReviewRatingPrefetch;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.model.ViewAllModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class ViewAllActivity extends AppCompatActivity {
    List<ViewAllModel> viewAllModelList;
    ViewAllAdapters viewAllAdapters;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_all);
        String type = getIntent().getStringExtra("type");
        recyclerView = findViewById(R.id.view_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        viewAllModelList = new ArrayList<>();
        viewAllAdapters = new ViewAllAdapters(this, viewAllModelList);
        recyclerView.setAdapter(viewAllAdapters);

        final String apiType;
        if (type != null && type.equalsIgnoreCase("IT")) {
            apiType = "IT";
        } else if (type != null && type.equalsIgnoreCase("Искусство")) {
            apiType = "Искусство";
        } else if (type != null && type.equalsIgnoreCase("История")) {
            apiType = "История";
        } else {
            apiType = type;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(ViewAllActivity.this)
                        .listEvents(apiType, 100, 0)
                        .execute();
                runOnUiThread(() -> {
                    viewAllModelList.clear();
                    if (resp.isSuccessful() && resp.body() != null) {
                        for (EventItemDto e : resp.body()) {
                            viewAllModelList.add(EventMapper.toViewAll(e));
                        }
                        if (viewAllModelList.size() > 1) {
                            Collections.shuffle(viewAllModelList);
                        }
                    } else {
                        Toast.makeText(ViewAllActivity.this, "Не удалось загрузить события", Toast.LENGTH_SHORT).show();
                    }
                    viewAllAdapters.notifyDataSetChanged();
                    ReviewRatingPrefetch.prefetchForViewAllModels(ViewAllActivity.this, viewAllModelList,
                            () -> viewAllAdapters.notifyDataSetChanged());
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ViewAllActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
