package com.example.main_screen.data.visitudmurtia;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Корень JSON из assets/visit_udmurtia_routes.json */
public class VisitUdmurtiaRouteJsonDto {
    public int version;
    @SerializedName("scraped_at")
    public String scrapedAt;
    public String source;
    @SerializedName("license_note")
    public String licenseNote;
    public List<VisitUdmurtiaRouteItemDto> routes;
}
