package com.simats.weekcart.models;

public class User {
    private int id;
    private String full_name;
    private String email;
    private int adult_count;
    private int child_count;
    private String phone_number;
    private boolean is_notification_enabled;
    private boolean is_email_enabled;

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return full_name; // Alias for getFullName as used in ProfileActivity
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getAdultCount() {
        return adult_count;
    }

    public void setAdultCount(int adult_count) {
        this.adult_count = adult_count;
    }

    public int getChildCount() {
        return child_count;
    }

    public void setChildCount(int child_count) {
        this.child_count = child_count;
    }

    public boolean isNotificationEnabled() {
        return is_notification_enabled;
    }

    public void setNotificationEnabled(boolean notification_enabled) {
        is_notification_enabled = notification_enabled;
    }

    public boolean isEmailEnabled() {
        return is_email_enabled;
    }

    public void setEmailEnabled(boolean email_enabled) {
        is_email_enabled = email_enabled;
    }
}
