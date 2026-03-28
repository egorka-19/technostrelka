package com.example.main_screen.data.visitudmurtia;

import android.content.Context;
import android.text.TextUtils;

import com.example.main_screen.model.RouteModel;
import com.example.main_screen.model.RouteStop;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Загрузка встроенного каталога маршрутов с visitudmurtia.org (скрипт {@code scripts/scrape_visit_udmurtia_routes.py}).
 */
public final class VisitUdmurtiaRoutesAssetLoader {

    private static final String ASSET = "visit_udmurtia_routes.json";

    private VisitUdmurtiaRoutesAssetLoader() {
    }

    public static List<RouteModel> load(Context context) {
        if (context == null) {
            return Collections.emptyList();
        }
        try (InputStream is = context.getAssets().open(ASSET);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            VisitUdmurtiaRouteJsonDto root = new Gson().fromJson(reader, VisitUdmurtiaRouteJsonDto.class);
            if (root == null || root.routes == null || root.routes.isEmpty()) {
                return Collections.emptyList();
            }
            List<RouteModel> out = new ArrayList<>(root.routes.size());
            for (VisitUdmurtiaRouteItemDto dto : root.routes) {
                RouteModel m = toRouteModel(dto);
                if (m != null) {
                    out.add(m);
                }
            }
            return out;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static RouteModel toRouteModel(VisitUdmurtiaRouteItemDto dto) {
        if (dto == null || TextUtils.isEmpty(dto.name)) {
            return null;
        }
        String cover = nz(dto.coverImageUrl);
        String listing = nz(dto.listingImageUrl);
        String img = !cover.isEmpty() ? cover : listing;
        if (img.isEmpty() && dto.imageUrls != null) {
            for (String u : dto.imageUrls) {
                if (!TextUtils.isEmpty(u)) {
                    img = u.trim();
                    break;
                }
            }
        }
        RouteModel m = new RouteModel(
                dto.name,
                img,
                nz(dto.description),
                nz(dto.category),
                nz(dto.goal),
                nz(dto.daysRange),
                nz(dto.peopleCount),
                nz(dto.duration),
                nz(dto.difficulty)
        );
        m.setId(nz(dto.id));
        m.setUrl(nz(dto.url));
        m.setPlace(nz(dto.place));

        if (dto.stops != null && !dto.stops.isEmpty()) {
            List<RouteStop> stops = new ArrayList<>();
            for (VisitUdmurtiaStopItemDto s : dto.stops) {
                if (s == null) {
                    continue;
                }
                List<String> imgs = s.imageUrls != null ? new ArrayList<>(s.imageUrls) : new ArrayList<>();
                stops.add(new RouteStop(nz(s.title), nz(s.address), nz(s.text), imgs));
            }
            m.setStops(stops);
        }
        return m;
    }

    private static String nz(String s) {
        return s != null ? s : "";
    }
}
