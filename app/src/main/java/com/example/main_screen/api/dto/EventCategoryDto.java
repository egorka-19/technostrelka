package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Запись из GET /home-categories. Для фильтрации событий ({@code GET /events?type=…})
 * используется поле {@link #type}; подпись на главном экране — {@link #name} или {@link #title},
 * иначе показываем {@link #type}.
 */
public class EventCategoryDto {
    public String id;
    public String name;
    /** Значение query-параметра {@code type} для списка событий. */
    public String type;
    /** Альтернативное имя для UI, если {@code name} пустой. */
    @SerializedName("title")
    public String title;
    @SerializedName("sort_order")
    public Integer sortOrder;
}
