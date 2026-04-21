package com.simats.Weekcart.models;

public class LoginResponse {
    private String access_token;
    private User user;

    public String getAccessToken() {
        return access_token;
    }

    public User getUser() {
        return user;
    }
}
