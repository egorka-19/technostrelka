package com.example.main_screen.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.main_screen.api.dto.RatingDto;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.model.ViewAllModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

/**
 * В списке событий API отдаёт поле {@code rating} (текст с афиши), часто пустое.
 * Средний балл по отзывам — только в {@code GET .../rating-summary}; на карточке «Подробнее» он уже запрашивается.
 * Здесь подгружаем те же сводки для списков на главной и в сетке.
 */
public final class ReviewRatingPrefetch {

    private static final int MAX_PARALLEL = 8;
    private static final int PER_REQUEST_SEC = 12;

    private ReviewRatingPrefetch() {
    }

    public static void prefetchForPopularModels(Context context, List<PopularModel> items, Runnable onUiThread) {
        if (items == null || items.isEmpty()) {
            postMain(onUiThread);
            return;
        }
        Context app = context.getApplicationContext();
        Executors.newSingleThreadExecutor().execute(() -> {
            ExecutorService pool = Executors.newFixedThreadPool(MAX_PARALLEL);
            List<Future<?>> futures = new ArrayList<>();
            for (PopularModel m : items) {
                futures.add(pool.submit(() -> applyPopular(app, m)));
            }
            for (Future<?> f : futures) {
                try {
                    f.get(PER_REQUEST_SEC, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }
            }
            pool.shutdown();
            postMain(onUiThread);
        });
    }

    public static void prefetchForViewAllModels(Context context, List<ViewAllModel> items, Runnable onUiThread) {
        if (items == null || items.isEmpty()) {
            postMain(onUiThread);
            return;
        }
        Context app = context.getApplicationContext();
        Executors.newSingleThreadExecutor().execute(() -> {
            ExecutorService pool = Executors.newFixedThreadPool(MAX_PARALLEL);
            List<Future<?>> futures = new ArrayList<>();
            for (ViewAllModel m : items) {
                futures.add(pool.submit(() -> applyViewAll(app, m)));
            }
            for (Future<?> f : futures) {
                try {
                    f.get(PER_REQUEST_SEC, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }
            }
            pool.shutdown();
            postMain(onUiThread);
        });
    }

    private static void applyPopular(Context app, PopularModel m) {
        try {
            String id = m.getServerId();
            if (id == null || id.isEmpty()) {
                return;
            }
            Response<RatingDto> resp = ApiClient.get(app).getEventRatingSummary(id).execute();
            if (!resp.isSuccessful() || resp.body() == null) {
                return;
            }
            RatingDto d = resp.body();
            if (d.count > 0) {
                m.setRating(String.format(Locale.US, "%.1f", d.average));
            } else {
                m.setRating("—");
            }
        } catch (Exception ignored) {
        }
    }

    private static void applyViewAll(Context app, ViewAllModel m) {
        try {
            String id = m.getServerId();
            if (id == null || id.isEmpty()) {
                return;
            }
            Response<RatingDto> resp = ApiClient.get(app).getEventRatingSummary(id).execute();
            if (!resp.isSuccessful() || resp.body() == null) {
                return;
            }
            RatingDto d = resp.body();
            if (d.count > 0) {
                m.setRating(String.format(Locale.US, "%.1f", d.average));
            } else {
                m.setRating("—");
            }
        } catch (Exception ignored) {
        }
    }

    private static void postMain(Runnable r) {
        if (r == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(r);
    }
}
