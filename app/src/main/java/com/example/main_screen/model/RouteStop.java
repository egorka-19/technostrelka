package com.example.main_screen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Остановка маршрута (точка из visitudmurtia.org и др.). */
public class RouteStop implements Serializable {
    /** Стабильный id для промо/учёта (из JSON или routeId + индекс). */
    private String stopId;
    private String title;
    private String address;
    private String text;
    private List<String> imageUrls;
    /** Кафе/отель и т.д. — кнопка «Получить подарок». */
    private boolean partnerPoi;

    public RouteStop() {
        imageUrls = new ArrayList<>();
    }

    public RouteStop(String title, String address, String text, List<String> imageUrls) {
        this.title = title;
        this.address = address;
        this.text = text;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public String getStopId() {
        return stopId != null ? stopId : "";
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public boolean isPartnerPoi() {
        return partnerPoi;
    }

    public void setPartnerPoi(boolean partnerPoi) {
        this.partnerPoi = partnerPoi;
    }

    /** Первое фото точки (visitudmurtia.org). */
    public String getPrimaryImageUrl() {
        List<String> u = getImageUrls();
        return u.isEmpty() ? "" : u.get(0);
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
