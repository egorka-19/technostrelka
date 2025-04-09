package com.example.main_screen.model;

import java.io.Serializable;

public class PlaceModel implements Serializable {
    private String name;
    private String description;
    private int imageResourceId;
    private String type;

    public PlaceModel(String name, String description, int imageResourceId, String type) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getType() {
        return type;
    }
} 