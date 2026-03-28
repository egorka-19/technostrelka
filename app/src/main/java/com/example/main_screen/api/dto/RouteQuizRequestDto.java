package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class RouteQuizRequestDto {
    public Map<String, Object> answers;
    @SerializedName("update_profile_category")
    public boolean updateProfileCategory = true;

    public RouteQuizRequestDto(Map<String, Object> answers, boolean updateProfileCategory) {
        this.answers = answers;
        this.updateProfileCategory = updateProfileCategory;
    }
}
