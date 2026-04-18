package com.simats.weekcart.models;

public class RegisterRequest {
    private String full_name;
    private String phone_number;
    private String email;
    private String password;
    private int adult_count;
    private int child_count;

    public RegisterRequest(String full_name, String phone_number, String email, String password, int adult_count,
            int child_count) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.adult_count = adult_count;
        this.child_count = child_count;
    }
}
