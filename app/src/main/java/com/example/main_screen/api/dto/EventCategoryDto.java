package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

public class EventCategoryDto {
    public String id;
    public String name;
    public String type;
    @SerializedName("sort_order")
    public int sortOrder;
}
