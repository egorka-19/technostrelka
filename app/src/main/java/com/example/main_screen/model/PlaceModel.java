package com.example.main_screen.model;

import java.io.Serializable;

public class PlaceModel implements Serializable {
    private String name;
    private String description;
    private int imageResourceId;
    private String imageUrl;
    private String type;
    private String age;
    private String data;
    private String place;

    /** Конструктор с локальным ресурсом (обратная совместимость). */
    public PlaceModel(String name, String description, int imageResourceId, String type, String age, String data, String place) {
        this.name = name;
        this.age = age;
        this.place = place;
        this.data = data;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.imageUrl = null;
        this.type = type;
    }

    /** Конструктор с URL изображения (для мест с фото из интернета). */
    public PlaceModel(String name, String description, int imageResourceId, String imageUrl, String type, String age, String data, String place) {
        this.name = name;
        this.age = age;
        this.place = place;
        this.data = data;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getData() {
        return data;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getType() {
        return type;
    }
}
