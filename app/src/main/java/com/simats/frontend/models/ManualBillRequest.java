package com.simats.frontend.models;

import java.util.List;

public class ManualBillRequest {
    private List<Item> items;

    public ManualBillRequest(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private String item_name;
        private double price;
        private double quantity;

        public Item(String item_name, double price, double quantity) {
            this.item_name = item_name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getItemName() {
            return item_name;
        }

        public double getPrice() {
            return price;
        }

        public double getQuantity() {
            return quantity;
        }
    }
}
