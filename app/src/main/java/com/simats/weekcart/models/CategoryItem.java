package com.simats.weekcart.models;

import com.google.gson.annotations.SerializedName;

public class CategoryItem {

    @SerializedName("category")
    private String category;

    @SerializedName("amount")
    private float amount;

    @SerializedName("percentage")
    private float percentage;

    @SerializedName("item_count")
    private int itemCount;

    public String getCategory() {
        return category;
    }

    public float getAmount() {
        return amount;
    }

    public float getPercentage() {
        return percentage;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getCategoryColor() {
        if (category == null) return android.graphics.Color.parseColor("#9E9E9E");
        
        switch (category.toLowerCase().trim()) {
            case "fruit":
            case "fruits":
                return android.graphics.Color.parseColor("#4CAF50"); // Green
            case "vegetable":
            case "vegetables":
                return android.graphics.Color.parseColor("#8BC34A"); // Light Green
            case "dairy":
                return android.graphics.Color.parseColor("#2196F3"); // Blue
            case "protein":
            case "meat":
                return android.graphics.Color.parseColor("#F44336"); // Red
            case "grain":
            case "grains":
            case "bakery":
                return android.graphics.Color.parseColor("#FF9800"); // Orange
            case "snacks":
            case "junk":
                return android.graphics.Color.parseColor("#9C27B0"); // Purple
            case "beverages":
            case "drinks":
                return android.graphics.Color.parseColor("#00BCD4"); // Cyan
            default:
                return android.graphics.Color.parseColor("#9E9E9E"); // Grey
        }
    }
}
