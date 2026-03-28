package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Соответствует {@code ReviewOut} в API. */
public class ReviewDto {
    public String id;
    @SerializedName("event_id")
    public String eventId;
    @SerializedName("user_id")
    public String userId;
    @SerializedName("user_name")
    public String userName;
    @SerializedName("avatar_url")
    public String avatarUrl;
    public int rating;
    public String text;
    @SerializedName("review_date")
    public String reviewDate;
    @SerializedName("created_at")
    public String createdAt;
    public List<ReviewPhotoDto> photos;
}
