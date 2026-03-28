package com.example.main_screen.api.dto;

public class RegisterBody {
    public String email;
    public String password;
    public String username;

    public RegisterBody(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}
