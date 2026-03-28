package com.example.main_screen.utils;

import android.net.Uri;

import com.example.main_screen.BuildConfig;

/**
 * Сервер часто кладёт в JSON абсолютные URL с {@code localhost} / {@code 127.0.0.1}
 * (из {@code PUBLIC_BASE_URL}). Телефон не достучится до них — подменяем хост на тот же, что в {@code API_BASE_URL}.
 */
public final class MediaUrlUtils {

    private MediaUrlUtils() {
    }

    /** Например {@code http://192.168.0.134:8000} без завершающего слэша. */
    public static String apiOrigin() {
        String base = BuildConfig.API_BASE_URL;
        if (base == null) {
            return "";
        }
        String b = base.trim();
        if (b.endsWith("/api/v1/")) {
            b = b.substring(0, b.length() - "/api/v1/".length());
        } else if (b.endsWith("/api/v1")) {
            b = b.substring(0, b.length() - "/api/v1".length());
        }
        while (b.endsWith("/")) {
            b = b.substring(0, b.length() - 1);
        }
        return b;
    }

    public static String resolveForApiClient(String url) {
        if (url == null) {
            return "";
        }
        String u = url.trim();
        if (u.isEmpty()) {
            return "";
        }
        String origin = apiOrigin();
        if (origin.isEmpty()) {
            return u;
        }

        boolean absolute = u.startsWith("http://") || u.startsWith("https://");
        if (!absolute) {
            if (u.startsWith("/")) {
                return origin + u;
            }
            return origin + "/" + u;
        }

        Uri uri = Uri.parse(u);
        String host = uri.getHost();
        if (host != null && isLoopbackHost(host)) {
            return replaceOrigin(uri, origin);
        }
        return u;
    }

    private static boolean isLoopbackHost(String host) {
        String h = host.toLowerCase();
        return "localhost".equals(h)
                || "127.0.0.1".equals(h)
                || "[::1]".equals(h)
                || "::1".equals(h);
    }

    private static String replaceOrigin(Uri uri, String newOrigin) {
        String path = uri.getEncodedPath();
        if (path == null) {
            path = "";
        }
        String query = uri.getEncodedQuery();
        String frag = uri.getEncodedFragment();
        StringBuilder sb = new StringBuilder(newOrigin);
        if (path.isEmpty()) {
            return sb.toString();
        }
        if (path.startsWith("/")) {
            sb.append(path);
        } else {
            sb.append('/').append(path);
        }
        if (query != null) {
            sb.append('?').append(query);
        }
        if (frag != null) {
            sb.append('#').append(frag);
        }
        return sb.toString();
    }
}
