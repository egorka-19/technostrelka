package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewPhotoDto {
    public String id;
    public String url;
    @SerializedName("sort_order")
    public int sortOrder;
}
