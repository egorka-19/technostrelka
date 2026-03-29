package com.example.main_screen.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.EventMapper;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.data.visitudmurtia.VisitUdmurtiaRoutesAssetLoader;
import com.example.main_screen.model.RouteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Response;

/**
 * Тот же набор маршрутов, что на вкладке «Маршруты»: встроенный JSON + события с API.
 */
public final class RouteCatalogLoader {

    public interface Callback {
        void onRoutesLoaded(List<RouteModel> routes);
    }

    private RouteCatalogLoader() {
    }

    public static void loadAll(Context context, Callback callback) {
        Context app = context.getApplicationContext();
        Handler main = new Handler(Looper.getMainLooper());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<RouteModel> out = new ArrayList<>();
            List<RouteModel> embedded = VisitUdmurtiaRoutesAssetLoader.load(app);
            if (embedded != null) {
                out.addAll(embedded);
            }
            try {
                Response<List<EventItemDto>> resp = ApiClient.get(app).listEvents(null, 100, 0).execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<RouteModel> apiRoutes = new ArrayList<>();
                    for (EventItemDto e : resp.body()) {
                        apiRoutes.add(EventMapper.eventToRoute(e));
                    }
                    if (apiRoutes.size() > 1) {
                        Collections.shuffle(apiRoutes);
                    }
                    out.addAll(apiRoutes);
                }
            } catch (Exception ignored) {
            }
            List<RouteModel> result = out;
            main.post(() -> {
                if (callback != null) {
                    callback.onRoutesLoaded(result);
                }
            });
        });
    }
}
