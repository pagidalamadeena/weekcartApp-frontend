package com.simats.frontend.models;

public class AnalyticsResponse {
    private float monthly_spending;
    private float this_week_total;
    private int this_week_count;
    private float last_week_total;
    private int last_week_count;
    private float percentage_change;
    private float avg_bill;

    public float getMonthlySpending() {
        return monthly_spending;
    }

    public float getThisWeekTotal() {
        return this_week_total;
    }

    public int getThisWeekCount() {
        return this_week_count;
    }

    public float getLastWeekTotal() {
        return last_week_total;
    }

    public int getLastWeekCount() {
        return last_week_count;
    }

    public float getPercentageChange() {
        return percentage_change;
    }

    public float getAvgBill() {
        return avg_bill;
    }
}
