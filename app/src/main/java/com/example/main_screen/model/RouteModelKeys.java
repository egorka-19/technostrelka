package com.example.main_screen.model;

/**
 * Стабильный ключ для избранных маршрутов: id с бэкенда / из JSON или запасной по названию.
 */
public final class RouteModelKeys {

    private RouteModelKeys() {
    }

    public static String stableKey(RouteModel r) {
        if (r == null) {
            return "";
        }
        String id = r.getId();
        if (id != null && !id.trim().isEmpty()) {
            return id.trim();
        }
        String n = r.getName();
        return "__n_" + (n != null ? String.valueOf(n.hashCode()) : "0");
    }
}
