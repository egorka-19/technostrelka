package com.example.main_screen.data.shop;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitUdmurtiaShopJsonDto {
    public int version;
    @SerializedName("scraped_at")
    public String scrapedAt;
    public String source;
    @SerializedName("license_note")
    public String licenseNote;
    public List<VisitUdmurtiaShopItemDto> products;
}
