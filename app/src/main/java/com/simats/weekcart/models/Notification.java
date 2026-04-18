package com.simats.weekcart.models;

public class Notification {
    private int id;
    private String message;
    private boolean is_read;
    private String created_at;

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return is_read;
    }
}
