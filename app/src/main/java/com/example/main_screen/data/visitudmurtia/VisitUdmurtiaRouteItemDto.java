package com.example.main_screen.data.visitudmurtia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitUdmurtiaRouteItemDto {
    public String id;
    public String name;
    public String slug;
    public String category;
    public String place;
    @SerializedName("listing_image_url")
    public String listingImageUrl;
    @SerializedName("cover_image_url")
    public String coverImageUrl;
    @SerializedName("image_urls")
    public List<String> imageUrls;
    public String description;
    public String url;
    public String goal;
    @SerializedName("days_range")
    public String daysRange;
    @SerializedName("people_count")
    public String peopleCount;
    public String duration;
    public String difficulty;
    public List<VisitUdmurtiaStopItemDto> stops;
}
