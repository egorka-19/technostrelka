package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/** Тело {@code POST /events/{id}/reviews} — {@code ReviewCreate}. */
public class ReviewUpsertBody {
    public int rating;
    public String text;
    @SerializedName("photo_urls")
    public List<String> photoUrls;

    public ReviewUpsertBody(int rating, String text, List<String> photoUrls) {
        this.rating = rating;
        this.text = text != null ? text : "";
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
    }
}
