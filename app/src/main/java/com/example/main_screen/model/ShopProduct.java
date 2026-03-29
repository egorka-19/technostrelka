package com.example.main_screen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopProduct implements Serializable {
    private String id;
    private String lid;
    private String collection;
    private String shopCategory;
    private String name;
    private String description;
    private Integer priceRub;
    private Integer priceOldRub;
    private String place;
    private List<String> imageUrls;
    private String productUrl;

    public ShopProduct() {
        imageUrls = new ArrayList<>();
    }

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLid() {
        return lid != null ? lid : "";
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getCollection() {
        return collection != null ? collection : "";
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getShopCategory() {
        return shopCategory != null ? shopCategory : "";
    }

    public void setShopCategory(String shopCategory) {
        this.shopCategory = shopCategory;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriceRub() {
        return priceRub;
    }

    public void setPriceRub(Integer priceRub) {
        this.priceRub = priceRub;
    }

    public Integer getPriceOldRub() {
        return priceOldRub;
    }

    public void setPriceOldRub(Integer priceOldRub) {
        this.priceOldRub = priceOldRub;
    }

    public String getPlace() {
        return place != null ? place : "Ижевск";
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getProductUrl() {
        return productUrl != null ? productUrl : "";
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public int displayPriceRub() {
        return priceRub != null ? priceRub : 0;
    }
}
