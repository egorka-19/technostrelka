package com.example.main_screen.data.visitudmurtia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitUdmurtiaStopItemDto {
    public String id;
    @SerializedName("partner_poi")
    public Boolean partnerPoi;
    public String title;
    public String address;
    public String text;
    @SerializedName("image_urls")
    public List<String> imageUrls;
}
