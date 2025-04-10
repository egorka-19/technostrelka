package com.example.main_screen.model;

public class ViewedContent {
    private String contentId;
    private String contentType; // "article" или "video"
    private long timestamp;
    private boolean isCompleted;

    public ViewedContent() {
        // Пустой конструктор для Firebase
    }

    public ViewedContent(String contentId, String contentType, long timestamp, boolean isCompleted) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.timestamp = timestamp;
        this.isCompleted = isCompleted;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
} 