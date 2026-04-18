package com.simats.weekcart.models;

import com.google.gson.annotations.SerializedName;

public class ReminderItem {
    private int id;
    private String message;

    @SerializedName("reminder_date")
    private String reminderDate;

    @SerializedName("is_sent")
    private boolean isSent;

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public boolean isSent() {
        return isSent;
    }
}
