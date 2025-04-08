package com.example.main_screen;

public class ItemData {
    private String title;
    private int imageResId;
    private String text;

    public ItemData(String title, int imageResId, String text) {
        this.title = title;
        this.imageResId = imageResId;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getText() {
        return text;
    }
}
