package com.simats.weekcart.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HealthScoreResponse {

    @SerializedName("score")
    private int score;

    @SerializedName("healthy_percent")
    private float healthyPercent;

    @SerializedName("unhealthy_percent")
    private float unhealthyPercent;

    @SerializedName("label")
    private String label;

    @SerializedName("suggestions")
    private List<String> suggestions;

    public int getScore() {
        return score;
    }

    public float getHealthyPercent() {
        return healthyPercent;
    }

    public float getUnhealthyPercent() {
        return unhealthyPercent;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }
}
