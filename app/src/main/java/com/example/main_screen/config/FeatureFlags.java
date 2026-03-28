package com.example.main_screen.config;

/**
 * Постепенный rollout: при false можно вернуть заглушки (если добавите второй источник данных).
 */
public final class FeatureFlags {
    /** Данные событий/маршрутов/чата с FastAPI backend. */
    public static final boolean USE_BACKEND_API = true;

    private FeatureFlags() {
    }
}
