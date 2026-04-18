package com.simats.weekcart.models;

import com.google.gson.annotations.SerializedName;

public class WeeklyHealthComparison {

    @SerializedName("this_week")
    private WeekData thisWeek;

    @SerializedName("last_week")
    private WeekData lastWeek;

    @SerializedName("trend")
    private String trend;

    @SerializedName("score_diff")
    private int scoreDiff;

    public WeekData getThisWeek() {
        return thisWeek;
    }

    public WeekData getLastWeek() {
        return lastWeek;
    }

    public String getTrend() {
        return trend;
    }

    public int getScoreDiff() {
        return scoreDiff;
    }

    public static class WeekData {
        @SerializedName("score")
        private int score;

        @SerializedName("healthy_count")
        private int healthyCount;

        @SerializedName("unhealthy_count")
        private int unhealthyCount;

        @SerializedName("total_items")
        private int totalItems;

        public int getScore() {
            return score;
        }

        public int getHealthyCount() {
            return healthyCount;
        }

        public int getUnhealthyCount() {
            return unhealthyCount;
        }

        public int getTotalItems() {
            return totalItems;
        }
    }
}
