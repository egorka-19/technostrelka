package com.example.main_screen.api.dto;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Разбор ответа {@code GET /home-categories}: массив или объект с массивом
 * ({@code data}, {@code categories}, {@code items}, …), поля {@code type}/{@code slug}/…,
 * числовой {@code id}, {@code sort_order}.
 */
public final class HomeCategoriesJsonParser {

    private static final String[] WRAPPER_KEYS = {
            "categories", "data", "items", "results", "home_categories", "rows", "list", "content", "payload"
    };

    private static final String[] TYPE_KEYS = {
            "type", "category_type", "event_type", "slug", "code", "key", "value"
    };

    private static final String[] NAME_KEYS = {
            "name", "title", "label", "display_name", "text", "caption"
    };

    private static final String[] SORT_KEYS = {
            "sort_order", "sortOrder", "order", "position", "idx"
    };

    private HomeCategoriesJsonParser() {
    }

    public static List<EventCategoryDto> parse(@Nullable JsonElement root) {
        if (root == null || root.isJsonNull()) {
            return Collections.emptyList();
        }
        JsonArray arr = asArray(root);
        if (arr == null) {
            return Collections.emptyList();
        }
        List<EventCategoryDto> out = new ArrayList<>();
        for (JsonElement el : arr) {
            if (el == null || !el.isJsonObject()) {
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            EventCategoryDto c = new EventCategoryDto();
            c.id = primitiveToString(o, "id");
            c.name = firstNonEmptyString(o, NAME_KEYS);
            c.title = null;
            c.type = firstNonEmptyString(o, TYPE_KEYS);
            c.sortOrder = firstInt(o, SORT_KEYS, 0);

            if (c.type == null || c.type.isEmpty()) {
                if (c.id != null && !c.id.isEmpty()) {
                    c.type = c.id;
                } else {
                    continue;
                }
            }
            out.add(c);
        }
        return out;
    }

    @Nullable
    private static JsonArray asArray(JsonElement root) {
        if (root.isJsonArray()) {
            return root.getAsJsonArray();
        }
        if (!root.isJsonObject()) {
            return null;
        }
        JsonObject obj = root.getAsJsonObject();
        for (String key : WRAPPER_KEYS) {
            if (!obj.has(key)) {
                continue;
            }
            JsonElement inner = obj.get(key);
            if (inner != null && inner.isJsonArray()) {
                return inner.getAsJsonArray();
            }
        }
        return null;
    }

    @Nullable
    private static String firstNonEmptyString(JsonObject o, String[] keys) {
        for (String key : keys) {
            String s = primitiveToString(o, key);
            if (s != null && !s.isEmpty()) {
                return s;
            }
        }
        return null;
    }

    @Nullable
    private static String primitiveToString(JsonObject o, String key) {
        if (!o.has(key) || o.get(key).isJsonNull()) {
            return null;
        }
        JsonElement e = o.get(key);
        if (!e.isJsonPrimitive()) {
            return null;
        }
        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isNumber()) {
            Number n = p.getAsNumber();
            double dv = n.doubleValue();
            long lv = n.longValue();
            if (dv == lv) {
                return String.valueOf(lv);
            }
            return n.toString();
        }
        if (p.isBoolean()) {
            return String.valueOf(p.getAsBoolean());
        }
        String s = p.getAsString();
        return s != null ? s.trim() : null;
    }

    private static int firstInt(JsonObject o, String[] keys, int defaultValue) {
        for (String key : keys) {
            if (!o.has(key) || o.get(key).isJsonNull()) {
                continue;
            }
            JsonElement e = o.get(key);
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) {
                return e.getAsInt();
            }
            if (e.isJsonPrimitive()) {
                try {
                    return Integer.parseInt(e.getAsString().trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return defaultValue;
    }
}
