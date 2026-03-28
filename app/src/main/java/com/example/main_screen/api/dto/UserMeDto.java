package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

public class UserMeDto {
    public String id;
    public String email;
    public String username;
    @SerializedName("profile_image_url")
    public String profileImageUrl;
    @SerializedName("category_user")
    public String categoryUser;
    @SerializedName("post_text")
    public String postText;
    @SerializedName("post_name_text")
    public String postNameText;
    @SerializedName("post_images")
    public String postImages;
    @SerializedName("created_at")
    public String createdAt;
}
