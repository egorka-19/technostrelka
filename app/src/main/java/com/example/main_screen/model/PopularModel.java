package com.example.main_screen.model;

import java.io.Serializable;

public class PopularModel implements Serializable {
    private String name;
    private String img_url;
    private String description;
    private String age;
    private String data;
    private String place;
    private String url;
    private String rating; // Рейтинг (например, "5.0")
    private String schedule; // Часы работы (например, "пн-чт 06:30-23:00; пт 06:30-00:00; сб 08:00-00:00; вс 08:00-23:00")
    private String status; // Статус ("Открыто" или "Закрыто")
    private String type; // Тип категории (Кино, Театр, Парк, Ресторан, Музей и т.д.)

    public PopularModel() {
    }

    public PopularModel(String name, String img_url, String description, String age, String data, String place, String url) {
        this.name = name;
        this.img_url = img_url;
        this.description = description;
        this.age = age;
        this.data = data;
        this.place = place;
        this.url = url;
        this.rating = "5.0"; // По умолчанию
        this.schedule = ""; // По умолчанию пусто
        this.status = ""; // По умолчанию пусто
        this.type = ""; // По умолчанию пусто
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRating() {
        return rating != null ? rating : "5.0";
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSchedule() {
        return schedule != null ? schedule : "";
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getStatus() {
        return status != null ? status : "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public void setType(String type) {
        this.type = type;
    }
}
