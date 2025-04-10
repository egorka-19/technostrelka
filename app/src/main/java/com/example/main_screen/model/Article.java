package com.example.main_screen.model;

import java.util.List;

public class Article {
    private String id;
    private String title;
    private List<String> imageUrls;
    private String text;
    private String category;

    public Article() {
    }

    public Article(String id, String title, List<String> imageUrls, String text, String category) {
        this.id = id;
        this.title = title;
        this.imageUrls = imageUrls;
        this.text = text;
        this.category = category;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
} 