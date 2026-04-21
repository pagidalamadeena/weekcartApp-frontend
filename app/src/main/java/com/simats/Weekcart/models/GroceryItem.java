package com.simats.Weekcart.models;

import com.google.gson.annotations.SerializedName;

public class GroceryItem {

    @SerializedName("id")
    private int id;

    @SerializedName("item_name")
    private String itemName;

    @SerializedName("is_purchased")
    private boolean isPurchased;

    public GroceryItem(String itemName, boolean isPurchased) {
        this.itemName = itemName;
        this.isPurchased = isPurchased;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }
}
