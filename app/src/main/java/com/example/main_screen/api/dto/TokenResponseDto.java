package com.example.main_screen.api.dto;

import com.google.gson.annotations.SerializedName;

public class TokenResponseDto {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("refresh_token")
    public String refreshToken;
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("expires_in")
    public int expiresIn;
}
