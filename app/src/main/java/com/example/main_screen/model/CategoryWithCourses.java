package com.example.main_screen.model;

import java.util.List;

public class CategoryWithCourses {
    private String categoryTitle;
    private List<Course> courses;

    public CategoryWithCourses(String categoryTitle, List<Course> courses) {
        this.categoryTitle = categoryTitle;
        this.courses = courses;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public List<Course> getCourses() {
        return courses;
    }
}