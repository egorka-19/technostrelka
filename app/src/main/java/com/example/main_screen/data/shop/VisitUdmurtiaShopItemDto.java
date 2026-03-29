package com.example.main_screen.data.shop;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitUdmurtiaShopItemDto {
    public String id;
    public String lid;
    public String collection;
    @SerializedName("shop_category")
    public String shopCategory;
    public String name;
    public String description;
    @SerializedName("price_rub")
    public Integer priceRub;
    @SerializedName("price_old_rub")
    public Integer priceOldRub;
    public String place;
    @SerializedName("image_urls")
    public List<String> imageUrls;
    @SerializedName("product_url")
    public String productUrl;
}
