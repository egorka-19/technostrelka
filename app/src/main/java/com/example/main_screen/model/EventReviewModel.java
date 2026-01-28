package com.example.main_screen.model;

public class EventReviewModel {
    private final String userName;
    private final String userAvatarUrl;
    private final String date;
    private final float rating;
    private final String text;

    public EventReviewModel(String userName, String userAvatarUrl, String date, float rating, String text) {
        this.userName = userName;
        this.userAvatarUrl = userAvatarUrl;
        this.date = date;
        this.rating = rating;
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public String getDate() {
        return date;
    }

    public float getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }
}

