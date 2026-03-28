package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Соответствует {@code EventOut} в API. */
public class EventItemDto {
    public String id;
    public String name;
    public String slug;
    @SerializedName("img_url")
    public String imgUrl;
    @SerializedName("image_urls")
    public List<String> imageUrls;
    public String description;
    public String age;
    @SerializedName("date_caption")
    public String dateCaption;
    public String place;
    public String url;
    /** Текстовое поле с бекенда (не средний балл отзывов). */
    public String rating;
    public String schedule;
    public String status;
    public String type;
    @SerializedName("review_bucket")
    public String reviewBucket;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("ingest_key")
    public String ingestKey;
    @SerializedName("last_ingested_at")
    public String lastIngestedAt;
}
