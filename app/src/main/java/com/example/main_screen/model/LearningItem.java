package com.example.main_screen.model;

import java.io.Serializable;

public class LearningItem implements Serializable {
    private String id;
    private String title;
    private String description;
    private int imageResourceId;
    private String type;
    private String contentUrl;

    public LearningItem(String id, String title, String description, int imageResourceId, String type, String contentUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.type = type;
        this.contentUrl = contentUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
} 