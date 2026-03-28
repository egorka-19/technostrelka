package com.example.main_screen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Остановка маршрута (точка из visitudmurtia.org и др.). */
public class RouteStop implements Serializable {
    private String title;
    private String address;
    private String text;
    private List<String> imageUrls;

    public RouteStop() {
        imageUrls = new ArrayList<>();
    }

    public RouteStop(String title, String address, String text, List<String> imageUrls) {
        this.title = title;
        this.address = address;
        this.text = text;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address != null ? address : "";
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getText() {
        return text != null ? text : "";
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }
}
