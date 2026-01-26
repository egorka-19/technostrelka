package com.example.main_screen.model;

import java.io.Serializable;

public class RouteModel implements Serializable {
    private String name;
    private String imageUrl;
    private String description;
    private String category; // "С детьми", "Романтические", "Исторические" и т.д.
    private String goal; // Цель поездки: "Отдых", "Экскурсия", "Активный отдых", "Культура"
    private String daysRange; // "1", "2-3", "3-5", ">7"
    private String peopleCount; // Количество человек
    private String duration; // Продолжительность маршрута
    private String difficulty; // Сложность

    public RouteModel() {
    }

    public RouteModel(String name, String imageUrl, String description, String category, 
                     String goal, String daysRange, String peopleCount, String duration, String difficulty) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.goal = goal;
        this.daysRange = daysRange;
        this.peopleCount = peopleCount;
        this.duration = duration;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDaysRange() {
        return daysRange;
    }

    public void setDaysRange(String daysRange) {
        this.daysRange = daysRange;
    }

    public String getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(String peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
